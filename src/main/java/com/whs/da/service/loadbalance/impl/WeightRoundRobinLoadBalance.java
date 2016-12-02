package com.whs.da.service.loadbalance.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.whs.da.service.loadbalance.ServiceServerNode;
import com.whs.da.service.loadbalance.intf.ServiceLoadBalance;

/**
 * 加权轮询法实现负载均衡
 * 可以依据服务器的配置不同,以加权的方式,均衡各个server的负载
 * @author haiswang
 *
 */
public class WeightRoundRobinLoadBalance implements ServiceLoadBalance {
    
    private Integer currentPos;
    
    public WeightRoundRobinLoadBalance() {
        currentPos = 0;
    }
    
    @Override
    public ServiceServerNode handleServiceNode(Map<ServiceServerNode, Integer> serverNodes) {
        //这边将所有的节点Copy出来,避免线程安全问题(服务的上线与下线会修改这个集合,出现遍历异常问题)
        Map<ServiceServerNode, Integer> serverMap = new HashMap<ServiceServerNode, Integer>();
        serverMap.putAll(serverNodes);
        
        List<ServiceServerNode> serverNodeList = new ArrayList<>();
        for (Map.Entry<ServiceServerNode, Integer> entry : serverMap.entrySet()) {
            int weight = entry.getValue();
            for(int i=0; i<weight; i++) {
                serverNodeList.add(entry.getKey());
            }
        }
        
        ServiceServerNode returnServerNode = null;
        
        synchronized (currentPos) {
            if(currentPos >= serverNodeList.size()) {
                currentPos = 0;
            }
            
            returnServerNode = serverNodeList.get(currentPos);
            currentPos++;
        }
        
        return returnServerNode;
    }

}
