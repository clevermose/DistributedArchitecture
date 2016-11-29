package com.whs.da.rpc.tcp;

import java.util.concurrent.ConcurrentHashMap;

import com.whs.da.rpc.SayHelloService;
import com.whs.da.rpc.SayHelloServiceImpl;

/**
 * 服务容器
 * @author haiswang
 *
 */
public class ServiceContainer {
    
    //所有提供的RPC服务的接口与实现的映射
    private ConcurrentHashMap<Class<?>, Object> services = new ConcurrentHashMap<>();
    
    //锁
    private static Byte[] lock = new Byte[0];
    
    //实例对象
    public static ServiceContainer INSTANCE = null;
    
    /**
     * 初始化单例对象
     */
    public static void initServiceContainer() {
        if(null == INSTANCE) {
            synchronized (lock) {
                if(null == INSTANCE) {
                    INSTANCE = new ServiceContainer();
                }
            }
        }
    }
    
    /**
     * 私有构造函数
     */
    private ServiceContainer() {}
    
    /**
     * 加载RPC服务
     */
    public void loadRPCService() {
        services.put(SayHelloService.class, new SayHelloServiceImpl());
    }
    
    /**
     * 获取服务实现对象
     * @param clazz
     * @return
     */
    public Object getServiceImplObject(Class<?> clazz) {
        return services.get(clazz);
    }
}
