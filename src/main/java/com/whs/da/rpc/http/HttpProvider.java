package com.whs.da.rpc.http;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableAutoConfiguration
@RequestMapping(value="/RPC")
public class HttpProvider {
    
    @RequestMapping("/method/invoke/{serviceName}/{methodName}/{arguments}")
    public void rpcInvoke(@PathVariable String serviceName, @PathVariable String methodName, @PathVariable String arguments) {
        System.out.println("serviceName : " + serviceName);
        System.out.println("methodName : " + methodName);
        System.out.println("arguments : " + arguments);
        
        
    }
}
