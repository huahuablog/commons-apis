package com.huahua.common.action;


import com.huahua.common.controller.RestContrller;
import com.huahua.common.pojo.RestBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RestAction extends HttpServlet implements ActionInterface{


    private Logger log= LoggerFactory.getLogger(this.getClass());
    private static RestContrller restContrller;
    private static Map<String,RestBean> map_api;


    //初始化
    public int doInit(ch.qos.logback.classic.Logger var1) {
        restContrller=new RestContrller();

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

    private boolean jettyServerInit(){
        map_api=restContrller.loadRestAPI();
        if(map_api==null){
            return false;
        }
        List<String> list_path=new ArrayList<String>(map_api.keySet());

        return false;
    }
}
