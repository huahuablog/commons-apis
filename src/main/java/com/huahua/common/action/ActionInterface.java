package com.huahua.common.action;

import ch.qos.logback.classic.Logger;

public interface ActionInterface {

    int doInit(Logger var1);
    int doExec(Object... var1);
    Object doSave();
    void doRestore(Object var1);
}
