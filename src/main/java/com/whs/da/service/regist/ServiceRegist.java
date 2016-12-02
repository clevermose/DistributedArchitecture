package com.whs.da.service.regist;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;

import com.whs.da.service.Configuration;
import com.whs.da.service.discovery.ZKServerNode;
import com.whs.da.service.loadbalance.ServiceServerNode;

/**
 * 服务的注册
 * @author haiswang
 *
 */
public class ServiceRegist {
    
    //Zookeeper的机器列表
    private Set<ZKServerNode> zkServerNodes = null;
    
    private ZkClient zkClient = null;
    
    /**
     * 构造函数
     * @param zkServerNodes
     */
    public ServiceRegist(Set<ZKServerNode> zkServerNodes) {
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
    
    /**
     * 注册服务
     * @param serviceName
     * @param serverNode
     * @return
     */
    public void registService(String serviceName, ServiceServerNode serverNode) throws Exception {
        //根节点
        boolean isNodeExists = zkClient.exists(Configuration.ROOTPATH);
        if(!isNodeExists) {
            zkClient.createPersistent(Configuration.ROOTPATH);
        }
        
        //服务节点
        isNodeExists = zkClient.exists(Configuration.ROOTPATH + "/" + serviceName);
        if(!isNodeExists) {
            zkClient.createPersistent(Configuration.ROOTPATH + "/" + serviceName);
        }
        
        //provider节点
        isNodeExists = zkClient.exists(Configuration.ROOTPATH + "/" + serviceName + "/" + Configuration.ZK_PROVIDER_NODE);
        if(!isNodeExists) {
            zkClient.createPersistent(Configuration.ROOTPATH + "/" + serviceName + "/" + Configuration.ZK_PROVIDER_NODE);
        }
        
        //创建服务的节点
        zkClient.create(Configuration.ROOTPATH + "/" + serviceName + "/" + Configuration.ZK_PROVIDER_NODE + "/" + serverNode.toTxt(), null, CreateMode.EPHEMERAL);
    }
    
    /**
     * 注册服务
     * @param serviceName
     * @param serverNodes
     * @return 注册失败的节点
     */
    public Set<ServiceServerNode> registService(String serviceName, Set<ServiceServerNode> serverNodes) {
        
        Set<ServiceServerNode> failedRegistNodes = new HashSet<>();
        
        for (ServiceServerNode serverNode : serverNodes) {
            try {
                registService(serviceName, serverNode);
            } catch (Exception e) {
                e.printStackTrace();
                failedRegistNodes.add(serverNode);
            }
        }
        
        return failedRegistNodes;
    }
}
