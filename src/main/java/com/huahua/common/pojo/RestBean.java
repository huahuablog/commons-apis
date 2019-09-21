package com.huahua.common.pojo;


import com.conn.db.base.annotation.DBColumnAnnotation;

public class RestBean {
    @DBColumnAnnotation(columnName="apiName")
    private  String api_path;
    @DBColumnAnnotation(columnName="validFlag")
    private String valid_flag;
    @DBColumnAnnotation(columnName="requestMethod")
    private String request_method;
    @DBColumnAnnotation(columnName="controllerName")
    private String controller_name;

    public String getApi_path() {
        return api_path;
    }

    public void setApi_path(String api_path) {
        this.api_path = api_path;
    }

    public String getValid_flag() {
        return valid_flag;
    }

    public void setValid_flag(String valid_flag) {
        this.valid_flag = valid_flag;
    }

    public String getRequest_method() {
        return request_method;
    }

    public void setRequest_method(String request_method) {
        this.request_method = request_method;
    }

    public String getController_name() {
        return controller_name;
    }

    public void setController_name(String controller_name) {
        this.controller_name = controller_name;
    }
}
