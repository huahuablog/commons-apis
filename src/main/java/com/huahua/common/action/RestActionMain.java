package com.huahua.common.action;

public class RestActionMain {
    /**
     *
     *应用程序启动入口main()方法,
     * 启动端口：8084
     * 访问路径：localhost:8084/path
     */

    public static void main(String[] args) {
        RestAction action=new RestAction();
        action.doInit(null);

    }
}
