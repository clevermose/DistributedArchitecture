package com.whs.da.rpc;

/**
 * 服务的实现
 * @author haiswang
 *
 */
public class SayHelloServiceImpl implements SayHelloService {

    @Override
    public String sayHello(String helloArg) {
        if("hello".equals(helloArg)) {
            return "hello";
        } else {
            return "bye bye";
        }
    }
}
