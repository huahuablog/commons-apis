package com.huahua.common.action;


import org.slf4j.Logger;

public interface ActionInterface {

    int doInit(Logger var1);
    int doExec(Object... var1);
    Object doSave();
    void doRestore(Object var1);
}
