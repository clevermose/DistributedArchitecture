package com.whs.da.rpc.tcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * RPC服务的提供者
 * @author haiswang
 *
 */
public class TcpProvider {
    
    //RPC服务监听端口
    private int port;
    
    //rpc服务器对象
    private ServerSocket rpcServer = null;
    
    //请求处理线程池
    private ExecutorService handleRequestThreadPool = null;
    
    //处理RPC调用的线程池大小
    private static final int DEFAULT_HANDLE_THREAD_POOL_SIZE = 20;
    private int handleThreadPoolSize;
    
    /**
     * 构造函数
     * @param port
     */
    public TcpProvider(int port) {
        this(port, DEFAULT_HANDLE_THREAD_POOL_SIZE);
    }
    
    /**
     * 构造函数
     * @param port
     * @param handleThreadPoolSize
     */
    public TcpProvider(int port, int handleThreadPoolSize) {
        this.handleThreadPoolSize = handleThreadPoolSize;
        this.port = port;
    }
    
    /**
     * 初始化RPC服务器
     * @throws IOException 
     */
    public void initRPCServer() throws IOException {
        rpcServer = new ServerSocket(port);
        handleRequestThreadPool = Executors.newFixedThreadPool(handleThreadPoolSize);
        //初始化服务容器
        ServiceContainer.initServiceContainer();
        //加载RPC服务
        ServiceContainer.INSTANCE.loadRPCService();
    }
    
    
    /**
     * 启动RPC服务端
     */
    public void start() {
        
        System.out.println("rpc server start.");
        
        while(true) {
            Socket client = null;
            try {
                client = rpcServer.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            System.out.println("Get request : " + client);
            
            //请求交给线程池执行
            handleRequestThreadPool.execute(new RPCRequestHandler(client));
        }
    }
    
    public static void main(String[] args) {
        TcpProvider rpcProvider = new TcpProvider(8899);
        try {
            rpcProvider.initRPCServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        rpcProvider.start();
    }
}

/**
 * RPC请求的处理类
 * @author haiswang
 *
 */
class RPCRequestHandler implements Runnable {
    
    private Socket socket = null;
    
    public RPCRequestHandler(Socket socket) {
        this.socket = socket;
    }
    
    @Override
    public void run() {
        
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        
        try {
            ois = new ObjectInputStream(socket.getInputStream());
            String serviceName = ois.readUTF();
            String methodName = ois.readUTF();
            Class<?>[] parameterTypes = (Class<?>[])ois.readObject();
            Object[] arguments = (Object[])ois.readObject();
            Object result = handle(serviceName, methodName, parameterTypes, arguments);
            oos = new ObjectOutputStream(socket.getOutputStream());
            //返回RPC执行结果
            writeResponse(oos, result);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            if(null != ois) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            if(null != oos) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            if(null != socket) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * 处理RPC request
     * @param serviceName
     * @param methodName
     * @param parameterTypes
     * @param arguments
     * @return
     * @throws ClassNotFoundException 
     * @throws SecurityException 
     * @throws NoSuchMethodException 
     * @throws InvocationTargetException 
     * @throws IllegalArgumentException 
     * @throws IllegalAccessException 
     */
    public Object handle(String serviceName, String methodName, Class<?>[] parameterTypes, Object[] arguments) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?> serviceInterfaceClass = Class.forName(serviceName);
        Object serviceImplObj = ServiceContainer.INSTANCE.getServiceImplObject(serviceInterfaceClass);
        Method method = serviceInterfaceClass.getMethod(methodName, parameterTypes);
        return method.invoke(serviceImplObj, arguments);
    }
    
    /**
     * 
     * @param oos
     * @param result
     * @throws IOException 
     */
    public void writeResponse(ObjectOutputStream oos, Object result) throws IOException {
        oos.writeObject(result);
    }
}
