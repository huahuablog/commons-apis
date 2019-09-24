package com.huahua.common.jetty;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlet.ServletMapping;

import javax.servlet.http.HttpServlet;
import java.util.*;

public class JettyServer {

private static Map<Integer, Server> MAP_SERVER=new HashMap<Integer, Server>();
private static Set<ServiceInfo> SERVICES=new HashSet<ServiceInfo>();


    //移除指定端口的http服务，除了传入的servlet服务
    public static synchronized int removeServlet(int port,HttpServlet servlet){
        try {
            ServletContextHandler handler = (ServletContextHandler) MAP_SERVER.get(port).getHandler();
            ServletHandler servletHandler = handler.getServletHandler();
            List<ServletHolder> servletHolders = new ArrayList<ServletHolder>();
            Set<String> names = new HashSet<String>();
            //ServletHandler.getServlets()返回的是一个ServletHolder[]数组，Holder封装了servlet的相关信息
            for (ServletHolder holder : servletHandler.getServlets()) {
                if (servlet.getClass().toString().equals(holder.getServlet().getClass().toString())) {
                    //如果请求的servlet与原来保存的MAP_SERVER中port对应的servlet相同，则记录其holder的名称，白名单
                    names.add(holder.getName());
                }else {
                    //如果不相同，则单独记录保存，黑名单
                    servletHolders.add(holder);
                }
            }
            //遍历servletHandler,把已存在的请求的servlet剔除，不存在的servlet单独mapping保存
            List<ServletMapping> list_mapping=new ArrayList<ServletMapping>();
            for(ServletMapping mapping:servletHandler.getServletMappings()){
                if(!names.contains(mapping.getServletName())){
                    list_mapping.add(mapping);
                }
            }
            //将筛选好的ServletHolders和ServletMapping注入ServletHandler（黑名单），并将其初始化为空
            servletHandler.setServletMappings(list_mapping.toArray(new ServletMapping[0]));
            servletHandler.setServlets(servletHolders.toArray(new ServletHolder[0]));

        }catch (Throwable throwable){
            throwable.printStackTrace();
            return -1;
        }
        return 0;
    }

    //移除http服务（本地已保存有的情况，移除本地服务）
    public static synchronized int removeServlet(int port,String path,HttpServlet servlet){
        try {
            ServiceInfo serviceInfo=new ServiceInfo(port,path);
            //先把本地保存的端口跟路径移除，再把其对应的端口服务移除
            if(SERVICES.contains(serviceInfo)){
                SERVICES.remove(serviceInfo);
                return removeServlet(serviceInfo.getPort(),servlet);
            }
        }catch (Throwable throwable){
            throwable.printStackTrace();
            return -1;
        }

        return 0;
    }

    //添加http服务
    public static synchronized  int addServlet(int port, List<String> list_path, HttpServlet servlet){
        try {
            //把jetty端口服务启动起来
            startServer(port);
            //默认先删除已存在的Servlet
            if(removeServlet(port,servlet)!=0){
                return -1;
            }
            //清除，初始化后，重新添加服务,并本地保存SERVICES
            ServletHolder servletHolder=new ServletHolder(servlet);
            ServletContextHandler servletContextHandler= (ServletContextHandler) MAP_SERVER.get(port).getHandler();
            for(String path: list_path){
                ServiceInfo serviceInfo=new ServiceInfo(port,path);
                servletContextHandler.addServlet(servletHolder,path);
                SERVICES.add(serviceInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return  -1;
        }
        return 0;
    }
    //添加http服务（本地存在的情况，清除存在的服务）
    public static synchronized int addServlet(int port,String path,HttpServlet servlet){
        try {
            startServer(port);
            ServiceInfo serviceInfo=new ServiceInfo(port,path);
            //若存在，先删除，在添加
            if(SERVICES.contains(serviceInfo)){
                if(removeServlet(port,path,servlet)!=0){
                    return -1;
                }
            }
            ServletHolder servletHolder=new ServletHolder(servlet);
            ServletContextHandler handler= (ServletContextHandler) MAP_SERVER.get(port).getHandler();
            handler.addServlet(servletHolder,path);
            SERVICES.add(serviceInfo);
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    //启动server服务
    private static synchronized  void startServer(int port) throws Exception {
        //是否存在服务
        if (!MAP_SERVER.containsKey(port)){
            Server server=new Server(port);
            //允许暂停和关闭服务
            server.setStopAtShutdown(true);
            ServerConnector connector=new ServerConnector(server);
            connector.setPort(port);
            //不允许重复使用端口地址，解决重复启动jetty不报端口冲突的问题
            connector.setReuseAddress(false);
            //设置超时时间,5分钟
            connector.setIdleTimeout(3000000);
            server.setConnectors(new Connector[]{connector});

            ServletContextHandler handler=new ServletContextHandler();
            server.setHandler(handler);
            MAP_SERVER.put(port,server);
            server.start();

        }

    }
    //
}
