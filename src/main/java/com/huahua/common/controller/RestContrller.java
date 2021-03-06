package com.huahua.common.controller;

import com.alibaba.fastjson.JSONObject;
import com.huahua.common.pojo.RestBean;
import com.huahua.common.service.RestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 *返回对象null的，代表程序执行错误
 */

public class RestContrller {
    private static Logger log= LoggerFactory.getLogger(RestContrller.class);
    private RestService restService;
//构造器，日志注入
    public RestContrller(){
        restService=new RestService(log);
    }
//加载所有的接口apis
    public Map<String, RestBean> loadRestAPI(){
        Map<String,RestBean> map=new HashMap<String, RestBean>();
        try {
            for (RestBean bean:restService.getAllRestAPI()){
                map.put(bean.getApi_path(),bean);
            }
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("load Rest APIs error:",e);
            return null;
        }
    }
//通过类名反射，加载类并创建返回该类的一个实例
    public RestAuthController getControllerByClassName(String className){
        RestAuthController rc=null;
        try {
            rc= (RestAuthController) Class.forName(className).newInstance();
        } catch (InstantiationException e) {
            log.error("instance RestClass:{},fail:{}",className,e);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            log.error("instance RestClass:{},fail:{}",className,e);
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            log.error("instance RestClass:{},fail:{}",className,e);
            e.printStackTrace();
        }
    return rc;
    }
/**
    获取类信息(className和secretkey)
 @param params tokenID、path
 @return String
 */

    public  String getContrllerAndSecretKeyByToken(String...params){

        JSONObject jsonObj=new JSONObject();
        try {
            Map<String,String> map=restService.getRestAPIController(params);
            jsonObj.put("controller",map.get("controller"));
            jsonObj.put("secretKey",map.get("secretKey"));
            return jsonObj.toString();
        } catch (Exception e) {
            log.error("query controller and secretKey error，tokenID&path,MSG:{}",params,e);
            e.printStackTrace();
            return null;
        }

    }



}
