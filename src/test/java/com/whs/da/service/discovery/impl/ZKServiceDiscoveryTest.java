package com.whs.da.service.discovery.impl;

import java.util.HashSet;
import java.util.Set;

import com.whs.da.service.discovery.ZKServerNode;

public class ZKServiceDiscoveryTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        
        ZKServerNode zkServerNode = new ZKServerNode("10.249.73.143", 2181);
        Set<ZKServerNode> zkServerNodes = new HashSet<>();
        zkServerNodes.add(zkServerNode);
        
        ZKServiceDiscovery zkServiceDiscovery = new ZKServiceDiscovery(zkServerNodes);
        zkServiceDiscovery.initZKClient();
        zkServiceDiscovery.discovery();
    }

}
