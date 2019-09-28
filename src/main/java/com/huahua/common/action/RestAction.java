package com.huahua.common.action;


import com.alibaba.fastjson.JSONObject;
import com.conn.db.base.entity.Constants;
import com.huahua.common.controller.RestAuthController;
import com.huahua.common.controller.RestContrller;
import com.huahua.common.jetty.JettyServer;
import com.huahua.common.pojo.RestBean;
import com.huahua.common.pojo.RestConst;
import com.huahua.common.util.AuthVerifyUtil;
import com.huahua.common.util.IOUtil;
import com.huahua.common.util.Utility;
import com.java.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestAction extends HttpServlet implements ActionInterface{


    private Logger log= LoggerFactory.getLogger(this.getClass());
    private static RestContrller restContrller;
    private static Map<String,RestBean> MAP_API;
    private static Map<String, HashMap<String,Long>> IP_MAP = new HashMap<String, HashMap<String, Long>>();
    private static Map<String, HashMap<String,Long>> TOKENID_MAP = new HashMap<String, HashMap<String, Long>>();
/**************************过滤servlet请求处理*********************************************/


    //处理get请求
@Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    String path=request.getRequestURI();
    //手动更新REST API
    if(path.equals(RestConst.REST_API_UPDATE)){
        String tokenId=request.getHeader("tokenId");
        if(StringUtil.isNotEmpty(tokenId)||!tokenId.equals(RestConst.REST_API_UPDATE_TOKENID)){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        if(!jettyServerInit()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("update Rest API Fail;pls check");
            System.exit(-1);
        }
    }else{//处理其他get请求
        //拿到该URi对应的一个api资源，map没有重复
        RestBean bean=MAP_API.get(path);
        if(!bean.getRequest_method().equals("GET")){
            //不是GET请求，直接交给servler自己去处理，我们不管
            super.doGet(request,response);
        }else{
            //get请求，进来，获取参数并处理
            String params=request.getQueryString();
            this.doRequest(bean,request,response,path);
        }
    }
}
    //处理post请求
@Override
protected  void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String path=request.getRequestURI();
    RestBean bean=MAP_API.get(path);
    if(!bean.getRequest_method().equals("POST")){
        //若不是post请求，交给servlet自己处理，我们不管
        super.doPost(request,response);
    }else{
        //POST请求方法
        String params=request.getQueryString();
        ServletInputStream in=request.getInputStream();
        String input_in= IOUtil.readline(in,RestConst.ENCODE);
        this.doRequest(bean,request,response,input_in);
    }

}
    //处理put请求
@Override
protected  void doPut(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
super.doPut(request,response);
}
    //处理delete请求
@Override
protected  void doDelete(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
super.doDelete(request,response);
}

/**
实际处理请求的方法
 @param bean 封装的bean类
 @param request 请求
 @param response 响应
 @param params 请求携带的参数
 */

private void doRequest(RestBean bean,HttpServletRequest request,HttpServletResponse response,String params) throws IOException {
    //跨域控制
    response.addHeader("Access-Control-Allow-Origin", "*");
    //请求方法控制
    response.addHeader("Access-Control-Allow-Methods", "GET,POST");
    //设置文本类型和编码格式
    response.setContentType("text/html;charset=UTF-8");
    response.setCharacterEncoding(RestConst.ENCODE);
    // 响应头设置
    response.setHeader("Access-Control-Allow-Headers", "*");
    String req_param=request.getQueryString();
    String req_mothed=request.getMethod();

    String className=null;
    String tokenID=request.getHeader("tokenId");
    if(StringUtil.isNotEmpty(tokenID)&&!bean.getValid_flag().equals("0")){
        //需要tokenID验证，但tokenID为空
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("tokenId can not be null");
        log.error("URI:{},tokenId is null",request.getRequestURI());
        return;
    }
    //解析url参数
    Map<String,String> paramsMap=new HashMap<String, String>();
    if(StringUtil.isNotEmpty(req_param)){
        String paramsList[]=req_param.split("&");
        for(int i=0;i<paramsList.length;i++){
            String item[]=paramsList[i].split("=");
            paramsMap.put(item[0],item[1]);
        }
    }
    String str=restContrller.getContrllerAndSecretKeyByToken(tokenID,request.getRequestURI());
    JSONObject jsonObj=JSONObject.parseObject(str);
    //判断该API使用哪种验证方式
    switch (Integer.parseInt(bean.getValid_flag())){
        case 1://tokenID验证
            className=jsonObj.getString("controller");
            if(StringUtil.isNotEmpty(className)){
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                log.error("URL:{},tokenId:{},the apis can not called",request.getRequestURI(),tokenID);
                return;
            }
            break;
        case 2://MD5验证
            AuthVerifyUtil authMD5=new AuthVerifyUtil();
            String secretKey=jsonObj.getString("secretKey");
            if(!authMD5.verifyMD5Sign(secretKey,tokenID,request.getRequestURI(),paramsMap,params)){
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Authority Verify Fail,");
                log.error("URL:{},tokenID:{},auth verify MD5 fail",request.getRequestURI(),tokenID);
                return;
            }
        default:
            className=bean.getController_name();
            break;
    }
    RestAuthController restAuth=restContrller.getControllerByClassName(className);
    //----------------访问频率限制-------------------
    this.limitRequest(request,response,bean,restAuth,paramsMap);

}
/**
    限制客户端或token用户访问pinlv
 @param request 请求
 @param response 响应
 @param bean 封装的bean类
 @param restAuth 封装的bean类
 @param paramsMap 请求的所有参数
 */

    private void limitRequest(HttpServletRequest request,HttpServletResponse response,RestBean bean,RestAuthController restAuth,Map<String,String> paramsMap) throws IOException {
        long thisTime=System.currentTimeMillis();
        long interval=5;
        String tokenID=request.getHeader("tokenId");
        String requestIP= Utility.getRemoteRequestIP(request);
        String path=request.getRequestURI();
        if(bean.getValid_flag().equals("0")){//没有tokenID
            if(!IP_MAP.containsKey(requestIP)){
                HashMap<String,Long> urlMap=new HashMap<String, Long>();
                // 此次调用的接口url和时间写入map
                urlMap.put(request.getRequestURI(), thisTime);
                IP_MAP.put(requestIP,urlMap);
                this.jettyResponse(response,restAuth,tokenID,path,paramsMap);
            } else {
                if (!IP_MAP.get(requestIP).containsKey(request.getRequestURI())) {
                    IP_MAP.get(requestIP).put(request.getRequestURI(), thisTime);
                    this.jettyResponse(response,restAuth,tokenID,path,paramsMap);
                } else {
                    long lastTime = IP_MAP.get(requestIP).get(request.getRequestURI());
                    if (thisTime - lastTime >= interval * 1000) {
                        // 此次调用的接口url和时间写入map
                        IP_MAP.get(requestIP).put(request.getRequestURI(), thisTime);
                        this.jettyResponse(response,restAuth,tokenID,path,paramsMap);
                    } else {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("No Frequent Access/禁止频繁访问");
                        log.warn("No Frequent Access/禁止频繁访问，API:{},IP:{},lastTime:{}",request.getRequestURI(),requestIP,lastTime);
                        return;
                    }
                }
            }
        }else{
            //2.使用tokenID限制调用频率
            if (!TOKENID_MAP.containsKey(tokenID)) {
                // tokenID首次调用
                HashMap<String, Long> urlMap = new HashMap<String, Long>();
                // 此次调用的接口url和时间写入map
                urlMap.put(request.getRequestURI(), thisTime);
                TOKENID_MAP.put(tokenID, urlMap);
                this.jettyResponse(response,restAuth,tokenID,path,paramsMap);
            } else {
                //tokenID首次调用某接口
                if (!TOKENID_MAP.get(tokenID).containsKey(request.getRequestURI())) {
                    // 记录首次调用时间
                    TOKENID_MAP.get(tokenID).put(request.getRequestURI(), thisTime);
                    this.jettyResponse(response,restAuth,tokenID,path,paramsMap);
                } else {
                    long lastTime = TOKENID_MAP.get(tokenID).get(request.getRequestURI());
                    if (thisTime - lastTime >= interval * 1000) {
                        // 此次调用的接口url和时间写入map
                        TOKENID_MAP.get(tokenID).put(request.getRequestURI(), thisTime);
                        this.jettyResponse(response,restAuth,tokenID,path,paramsMap);
                    } else {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.getWriter().write("No Frequent Access/禁止频繁访问");
                        log.warn("No Frequent Access/禁止频繁访问，API:{},token:{},lastTime:{}",request.getRequestURI(),tokenID,lastTime);
                        return;
                    }
                }

            }

        }
    }
/**
 *处理请求后response响应
 * @param response 响应
 * @param restAuth 封装的bean类
 * @param tokenID
 * @param path 请求的URI
 * @param paramsMap 请求的参数
 *
 */
private void jettyResponse(HttpServletResponse response,RestAuthController restAuth,String tokenID,String path,Map<String,String> paramsMap) throws IOException {
    restAuth.setTokenID(tokenID);
    restAuth.setPath(path);
    response.setStatus(HttpServletResponse.SC_OK);
    response.setCharacterEncoding(Constants.ENCODE);
    response.getWriter().write(
            restAuth.doRequest(paramsMap, path));
}

/****************************jetty初始化*************************************/
    //初始化
    public int doInit(Logger var1) {
        restContrller=new RestContrller();
        if(!this.jettyServerInit()){
            System.exit(-1);
        }
        return 0;
    }

    //任务执行
    public int doExec(Object... var1) {
        return 0;
    }

    public Object doSave() {
        return null;
    }

    public void doRestore(Object var1) {

    }


    //jetty服务器容器初始化
    private boolean jettyServerInit(){
        //获取所有api服务
        MAP_API=restContrller.loadRestAPI();
        if(MAP_API==null){
            return false;
        }
        List<String> list_path=new ArrayList<String>(MAP_API.keySet());
        //添加所有api服务
        int result= JettyServer.addServlet(RestConst.REST_PORT,list_path,this);
        if(result==-1){
            this.log.error("init jetty server fail,path list:{},port:{}",list_path,RestConst.REST_PORT);
            return false;
        }
        return true;
    }

}
