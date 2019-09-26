package com.huahua.common.service;

import com.conn.db.base.dao.DBBaseDao;
import com.conn.db.base.service.DBBaseService;
import com.huahua.common.pojo.RestBean;
import org.slf4j.Logger;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestService extends DBBaseService<DBBaseDao> {
    public RestService(Logger log){
        super(log);
    }

    /**
     * 获取以开发好的所有的API接口类
     * */
    public List<RestBean> getAllRestAPI() throws Exception {
        String query_sql="select api_called_name, req_method,is_valid,class_name,create_name from sys_api_control ";
        return this.query("test",query_sql,RestBean.class,new Object[]{});
    }

    /***
     *通过tokenID 和path获取contrller和secretKey
     */
    public Map<String,String> getRestAPIController(String tokenID,String path) throws Exception {
        String query_sql="select t3.class_name,t2.secret_key from sys_api_token_rel t1 \n" +
                "inner join sys_api_token_secret t2 \n" +
                "on  t1.token_id=t2.token_id\n" +
                "inner join sys_api_control t3 \n" +
                "on t3.id=t1.api_id \n" +
                "where t2.valid='0'\n" +
                "and t1.token_id=?\n" +
                "and t3.api_called_name=?";
        Object[] objects=new Object[2];
        objects[0]=tokenID;
        objects[1]=path;
        String controller=null;
        String secretKey=null;
        Map<String,String> map= new HashMap<String,String>();
        ResultSet rs= this.query(null,query_sql,objects);
        while (rs.next()){
            map.put("controller",rs.getString("class_name")) ;
            map.put("secretKey",rs.getString("secret_key"));
        }
        return map;
    }

}
