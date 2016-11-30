package com.whs.da.service.loadbalance.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.whs.da.service.loadbalance.ServerNode;
import com.whs.da.service.loadbalance.intf.ServiceLoadBalance;

/**
 * Hash法实现负载均衡
 * 能够保证一个client的多次请求映射到一个server上 ,根据这一的特性可以在client和server端提供有状态的会话
 * @author haiswang
 *
 */
public class HashLoadBalance implements ServiceLoadBalance {
    
    //调用服务的Client的Ip地址
    private String clientIp = null;
    
    
    public HashLoadBalance(String clientIp) {
        this.clientIp = clientIp;
    }
    
    @Override
    public ServerNode handleServiceNode(Map<ServerNode, Integer> serverNodes) {
        
      //这边将所有的节点Copy出来,避免线程安全问题(服务的上线与下线会修改这个集合,出现遍历异常问题)
        Map<ServerNode, Integer> serverMap = new HashMap<ServerNode, Integer>();
        serverMap.putAll(serverNodes);
        
        Set<ServerNode> nodes = serverMap.keySet();
        ArrayList<ServerNode> serverNodeList = new ArrayList<>();
        serverNodeList.addAll(nodes);
        
        //通过client ip的hashcode 定位具体Server
        int ipHashCode = clientIp.hashCode();
        int serverPos = ipHashCode % serverNodeList.size();
        
        return serverNodeList.get(serverPos);
    }

}
