package com.huahua.common.jetty;

public class ServiceInfo {
    private int port;
    private String path;
    //构造器
    public ServiceInfo(int port,String path){
    super();
    this.path=path;
    this.port=port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    @Override
    public int hashCode(){
       final int prime=31;
       int result=1;
       result=prime*result+((path==null)?0:path.hashCode());
       result=prime*result+port;
       return result;
    }


    //通过port和path判断该服务是否相等。
    @Override
    public boolean equals(Object obj){
        if(this==obj){
            return true;
        }
        if(obj==null){
            return false;
        }
        if(getClass()!=obj.getClass()){
            return false;
        }
        ServiceInfo other= (ServiceInfo) obj;
       if(path==null){
           if(other.path!=null){
               return false;
           }
       }else if(!path.equals((other.path))){
           return false;
       }
       if(port!=other.port)
           return false;
       return true;
    }
}
