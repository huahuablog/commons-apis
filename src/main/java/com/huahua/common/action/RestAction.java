package com.huahua.common.action;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;

public class RestAction extends HttpServlet implements ActionInterface{


    private Logger log= LoggerFactory.getLogger(this.getClass());

    //初始化
    public int doInit(ch.qos.logback.classic.Logger var1) {
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
}
