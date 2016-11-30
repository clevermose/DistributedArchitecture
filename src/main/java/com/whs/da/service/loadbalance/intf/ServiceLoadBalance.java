package com.whs.da.service.loadbalance.intf;

import java.util.Map;

import com.whs.da.service.loadbalance.ServerNode;

/**
 * 服务的负载均衡接口
 * @author haiswang
 *
 */
public interface ServiceLoadBalance {
    
    /**
     * 均衡负载的实现方法
     * @param serverNodes
     * @return
     */
    public ServerNode handleServiceNode(Map<ServerNode, Integer> serverNodes);
    
}
