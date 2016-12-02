package com.whs.da.service.regist;

import java.util.HashSet;
import java.util.Set;

import com.whs.da.service.discovery.ZKServerNode;
import com.whs.da.service.loadbalance.ServiceServerNode;

public class ServiceRegistTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        
        ZKServerNode zkServerNode = new ZKServerNode("10.249.73.143", 2181);
        Set<ZKServerNode> zkServerNodes = new HashSet<>();
        zkServerNodes.add(zkServerNode);
        
        ServiceRegist serviceRegist = new ServiceRegist(zkServerNodes);
        ServiceServerNode serviceServerNode = new ServiceServerNode("127.0.0.1",7799);
        
        serviceRegist.initZKClient();
        
        try {
            serviceRegist.registService("test-service", serviceServerNode);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        while(true) {
            try {
                Thread.currentThread().sleep(10000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
