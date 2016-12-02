package com.whs.da.service.loadbalance.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.whs.da.service.loadbalance.ServiceServerNode;
import com.whs.da.service.loadbalance.intf.ServiceLoadBalance;

/**
 * 随机法实现负载均衡
 * 相对于RoundRobinLoadBalance这边不需要加锁,所以吞吐量会有所提升
 * @author haiswang
 *
 */
public class RandomLoadBalance implements ServiceLoadBalance {

    @Override
    public ServiceServerNode handleServiceNode(Map<ServiceServerNode, Integer> serverNodes) {
        
        //这边将所有的节点Copy出来,避免线程安全问题(服务的上线与下线会修改这个集合,出现遍历异常问题)
        Map<ServiceServerNode, Integer> serverMap = new HashMap<ServiceServerNode, Integer>();
        serverMap.putAll(serverNodes);
        
        Set<ServiceServerNode> nodes = serverMap.keySet();
        ArrayList<ServiceServerNode> serverNodeList = new ArrayList<>();
        serverNodeList.addAll(nodes);
        
        //产生一个随机数
        Random random = new Random();
        int randomPos = random.nextInt(serverNodeList.size());
        
        return serverNodeList.get(randomPos);
    }

}
