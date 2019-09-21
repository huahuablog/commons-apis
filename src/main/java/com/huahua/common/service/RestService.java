package com.huahua.common.service;

import com.conn.db.base.dao.DBBaseDao;
import com.conn.db.base.service.DBBaseService;
import com.huahua.common.pojo.RestBean;
import org.slf4j.Logger;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;

public class RestService extends DBBaseService<DBBaseDao> {
    public RestService(Logger log){
        super(log);
    }

    /**
     * 获取以开发好的所有的API接口类
     * */
    public List<RestBean> getAllRestAPI() throws Exception {
        String query_sql="select apiname, requestmethod,validflag,controllername,empname from sys_api_control ";
        return this.query(null,query_sql,RestBean.class,new Object[]{});
    }

    /***
     *通过tokenID 和path获取contrller
     */
    public String getRestAPIController(String tokenID,String path) throws Exception {
        String query_sql="";
        Object[] objects=new Object[2];
        objects[0]=tokenID;
        objects[1]=path;
        ResultSet rs= this.query(null,query_sql,objects);
        while (rs.next()){
            rs.getObject()
        }
    }
}
