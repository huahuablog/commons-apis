package com.huahua.common.controller;

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
    public RestAuthContrl getControllerByClassName(String className){
        RestAuthContrl rc=null;
        try {
            rc= (RestAuthContrl) Class.forName(className).newInstance();
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
//通过tokenID和path，获取类信息
    public  String getContrllerByToken(String token,String path){
        String className=null;

        return null;
    }



}
