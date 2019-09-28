package com.huahua.demo.controller;

import com.huahua.common.controller.RestAuthController;

import java.util.Map;

public  class DemoController extends RestAuthController {

    /**
     *继承RestAuthController类，同时实现doRequest()方法；由它分发请求的信息给你处理
     */


    @Override
    public String doRequest(Map<String, String> paramMap, String body) {
        return "来自您的响应！";
    }
}
