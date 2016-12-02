package com.whs.da.service.discovery.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.I0Itec.zkclient.ZkClient;

import com.whs.da.service.Configuration;
import com.whs.da.service.ServiceUtils;
import com.whs.da.service.discovery.ZKServerNode;
import com.whs.da.service.discovery.intf.ServiceDiscovery;
import com.whs.da.service.loadbalance.ServiceServerNode;

/**
 * 基于ZK的服务发现
 * @author haiswang
 *
 */
public class ZKServiceDiscovery implements ServiceDiscovery {
    
    //Zookeeper的机器列表
    private Set<ZKServerNode> zkServerNodes = null;
    
    private ZkClient zkClient;
    
    public ZKServiceDiscovery(Set<ZKServerNode> zkServerNodes) {
        this.zkServerNodes = zkServerNodes;
    }
    
    /**
     * 初始化ZKClient
     */
    public void initZKClient() {
        StringBuffer servers = new StringBuffer();
        Iterator<ZKServerNode> iter = zkServerNodes.iterator();
        
        ZKServerNode tmpNode = iter.next();
        servers.append(tmpNode.getZkIp()).append(":").append(tmpNode.getPort());
        
        while(iter.hasNext()) {
            tmpNode = iter.next();
            servers.append(",").append(tmpNode.getZkIp()).append(":").append(tmpNode.getPort());
        }
        
        zkClient = new ZkClient(servers.toString());
    }
    
    @Override
    public Map<String, List<ServiceServerNode>> discovery() {
        
        boolean isRootPathExists = zkClient.exists(Configuration.ROOTPATH);
        if(!isRootPathExists) {
            System.err.println("服务探测失败,指定Root路径不存在,Root Path : " + Configuration.ROOTPATH);
            return null;
        }
        
        List<String> serviceNames = zkClient.getChildren(Configuration.ROOTPATH);
        Map<String, List<ServiceServerNode>> servicesInfos = new HashMap<String, List<ServiceServerNode>>();
        List<ServiceServerNode> serviceServerNodes = null;
        for (String serviceName : serviceNames) {
            System.out.println("发现服务 ,服务名称 : " + serviceName);
            List<String> serverNames = zkClient.getChildren(Configuration.ROOTPATH + "/" + serviceName + "/" + Configuration.ZK_PROVIDER_NODE);
            serviceServerNodes = new ArrayList<>();
            for (String serverName : serverNames) {
                ServiceServerNode serverNode = ServiceUtils.paraseServerNode(serverName);
                serviceServerNodes.add(serverNode);
            }
            
            System.out.println("服务机器列表 : " + Arrays.toString(serviceServerNodes.toArray()));
            
            servicesInfos.put(serviceName, serviceServerNodes);
        }
        
        return servicesInfos;
    }
}
