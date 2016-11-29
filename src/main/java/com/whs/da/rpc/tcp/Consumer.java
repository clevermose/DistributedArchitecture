package com.whs.da.rpc.tcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.UnknownHostException;

import com.whs.da.rpc.SayHelloService;

/**
 * RPC服务的调用者
 * @author haiswang
 *
 */
public class Consumer {
    
    //RPC服务端的IP
    private String ip;
    
    //PRC服务端的端口
    private int port;
    
    //RPC服务端
    private Socket rpcServer = null;
    
    //连接I/O的流
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;
    
    /**
     * 构造函数
     * @param ip
     * @param port
     */
    public Consumer(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
    
    /**
     * 调用Method
     * @param clazz
     * @param methodName
     * @param parameters
     * @param parameterTypes
     * @return
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws UnknownHostException
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public Object callMethod(Class<?> clazz, String methodName, Object[] parameters, Class<?> ... parameterTypes) throws NoSuchMethodException, SecurityException, UnknownHostException, ClassNotFoundException, IOException {
        Method invokeMethod = clazz.getMethod(methodName, parameterTypes);
        String method = invokeMethod.getName();
        return rpcInvoke(clazz.getName(), method, parameters, parameterTypes);
    }
    
    /**
     * RPC调用
     * @param clazz
     * @param methodName
     * @param parameterTypes
     * @throws IOException 
     * @throws UnknownHostException 
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchMethodException 
     */
    public Object rpcInvoke(String className, String methodName, Object[] parameters, Class<?> ... parameterTypes) throws UnknownHostException, IOException, ClassNotFoundException {
        rpcServer = new Socket(ip, port);
        oos = new ObjectOutputStream(rpcServer.getOutputStream());
        oos.writeUTF(className);
        oos.writeUTF(methodName);
        oos.writeObject(parameterTypes);
        oos.writeObject(parameters);
        ois = new ObjectInputStream(rpcServer.getInputStream());
        return ois.readObject();
    }
    
    /**
     * 关闭客户端连接
     * @throws IOException
     */
    public void close() throws IOException {
        if(null != ois) {
            ois.close();
        }
        
        if(null != oos) {
            oos.close();
        }
        
        if(null != rpcServer) {
            rpcServer.close();
        }
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        Consumer consumer = new Consumer("127.0.0.1", 8899);
        Object result = null;
        
        try {
            result = consumer.callMethod(SayHelloService.class, "sayHello", new Object[]{"hello"}, String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.out.println("rpc result : " + result);
        
        try {
            consumer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
