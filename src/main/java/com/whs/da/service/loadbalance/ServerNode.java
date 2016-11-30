package com.whs.da.service.loadbalance;

/**
 * service的provider的机器信息
 * @author haiswang
 *
 */
public class ServerNode {
    
    private String ip;
    
    private int port;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
    @Override
    public String toString() {
        return "["+ip+" ,"+port+"]";
    }
    
    @Override
    public int hashCode() {
        int hashCode = ip.hashCode() ^ (ip.hashCode() >>> 32);
        hashCode = 31 * hashCode + (port ^ (port >>> 32));
        return hashCode;
    }
}
