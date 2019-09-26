package com.huahua.common.pojo;


import com.conn.db.base.annotation.DBColumnAnnotation;

public class RestBean {
    @DBColumnAnnotation(columnName="api_called_name")
    private  String api_path;
    @DBColumnAnnotation(columnName="is_valid")
    private String valid_flag;
    @DBColumnAnnotation(columnName="req_method")
    private String request_method;
    @DBColumnAnnotation(columnName="class_name")
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
