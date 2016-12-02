package com.whs.da.service.discovery;

/**
 * Zookeeper的server信息
 * @author haiswang
 *
 */
public class ZKServerNode {
    
    private String zkIp;
    
    private int port;
    
    public ZKServerNode() {}
    
    public ZKServerNode(String zkIp, int port) {
        this.zkIp = zkIp;
        this.port = port;
    }

    public String getZkIp() {
        return zkIp;
    }

    public void setZkIp(String zkIp) {
        this.zkIp = zkIp;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
    @Override
    public String toString() {
        return "ZK server node : ["+zkIp+" ,"+port+"]";
    }
    
    @Override
    public int hashCode() {
        int hashCode = zkIp.hashCode() ^ (zkIp.hashCode() >>> 32);
        hashCode = 31 * hashCode + (port ^ (port >>> 32));
        return hashCode;
    }
}
