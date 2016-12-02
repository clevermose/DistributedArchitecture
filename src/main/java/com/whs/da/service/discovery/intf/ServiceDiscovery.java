package com.whs.da.service.discovery.intf;

import java.util.List;
import java.util.Map;

import com.whs.da.service.loadbalance.ServiceServerNode;

/**
 * 服务发现
 * @author haiswang
 *
 */
public interface ServiceDiscovery {
    
    /**
     * 探测服务
     * @return
     */
    public Map<String, List<ServiceServerNode>> discovery();
}
