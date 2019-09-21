package com.huahua.common.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class RestAuthContrl {
    protected Logger log;
    public RestAuthContrl(){
        log= LoggerFactory.getLogger(this.getClass());
    }

    private String tokenID;
    private String path;

    public String getTokenID() {
        return tokenID;
    }

    public void setTokenID(String tokenID) {
        this.tokenID = tokenID;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public abstract String doRequest(Map<String,String> paramMap,String body);
}
