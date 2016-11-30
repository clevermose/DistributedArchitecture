package com.whs.da.service.loadbalance.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.whs.da.service.loadbalance.ServerNode;
import com.whs.da.service.loadbalance.intf.ServiceLoadBalance;

/**
 * 加权随机法实现负载均衡
 * @author haiswang
 *
 */
public class WeightRandom implements ServiceLoadBalance {

    @Override
    public ServerNode handleServiceNode(Map<ServerNode, Integer> serverNodes) {
        
        //这边将所有的节点Copy出来,避免线程安全问题(服务的上线与下线会修改这个集合,出现遍历异常问题)
        Map<ServerNode, Integer> serverMap = new HashMap<ServerNode, Integer>();
        serverMap.putAll(serverNodes);
        
        List<ServerNode> serverNodeList = new ArrayList<>();
        for (Map.Entry<ServerNode, Integer> entry : serverMap.entrySet()) {
            int weight = entry.getValue();
            for(int i=0; i<weight; i++) {
                serverNodeList.add(entry.getKey());
            }
        }
        
        Random random = new Random();
        int randomPos = random.nextInt(serverNodeList.size());
        
        return serverNodeList.get(randomPos);
    }
}
