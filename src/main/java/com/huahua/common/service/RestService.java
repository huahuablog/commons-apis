package com.huahua.common.service;

import com.conn.db.base.dao.DBBaseDao;
import com.conn.db.base.service.DBBaseService;
import com.huahua.common.pojo.RestBean;
import com.java.util.StringUtil;
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
    public Map<String,String> getRestAPIController(String...params) throws Exception {
        String query_sql="select t3.class_name,t2.secret_key from sys_api_token_rel t1 \n" +
                "inner join sys_api_token_secret t2 \n" +
                "on  t1.token_id=t2.token_id\n" +
                "inner join sys_api_control t3 \n" +
                "on t3.id=t1.api_id \n" +
                "where t2.valid='0'\n" +
                "and t3.api_called_name=?\n";
        Object[] objects ;
        if(StringUtil.isNotEmpty(params[0])){
            objects=new Object[params.length];
            query_sql=query_sql+" and t1.token_id=?\n ";
            objects[0]=params[1];
            objects[1]=params[0];
        }else{
            objects=new Object[params.length-1];
            objects[0]=params[1];
        }
        String controller=null;
        String secretKey=null;
        Map<String,String> map= new HashMap<String,String>();
        ResultSet rs= this.query("test",query_sql,objects);
        while (rs.next()){
            map.put("controller",rs.getString("class_name")) ;
            map.put("secretKey",rs.getString("secret_key"));
        }
        return map;
    }

}
