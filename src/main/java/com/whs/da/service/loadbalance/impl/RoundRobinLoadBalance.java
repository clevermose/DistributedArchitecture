package com.whs.da.service.loadbalance.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.whs.da.service.loadbalance.ServerNode;
import com.whs.da.service.loadbalance.intf.ServiceLoadBalance;

/**
 * 轮询法实现负载均衡
 * 问题：
 * 1：悲观锁降低了吞吐量
 * @author haiswang
 *
 */
public class RoundRobinLoadBalance implements ServiceLoadBalance {
    
    //当前的节点的index
    private Integer currentPos;
    
    public RoundRobinLoadBalance() {
        currentPos = 0;
    }
    
    @Override
    public ServerNode handleServiceNode(Map<ServerNode, Integer> serverNodes) {
        
        //这边将所有的节点Copy出来,避免线程安全问题(服务的上线与下线会修改这个集合,出现遍历异常问题)
        Map<ServerNode, Integer> serverMap = new HashMap<ServerNode, Integer>();
        serverMap.putAll(serverNodes);
        
        Set<ServerNode> nodes = serverMap.keySet();
        ArrayList<ServerNode> serverNodeList = new ArrayList<>();
        serverNodeList.addAll(nodes);
        
        ServerNode returnNode = null;
        
        //同一时刻只能有一个线程更新位置变量
        synchronized (currentPos) {
            if(currentPos > serverNodeList.size()) {
                currentPos = 0;
            }
            
            returnNode = serverNodeList.get(currentPos);
            currentPos++;
        }
        
        return returnNode;
    }
}
