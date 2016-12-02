package com.whs.da.service;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.util.StringUtils;

import com.whs.da.service.exception.ServiceParameterException;
import com.whs.da.service.loadbalance.ServiceServerNode;

public class ServiceUtils {
    
    /**
     * 
     * @param serverString
     * @return
     */
    public static ServiceServerNode paraseServerNode(String serverString) throws ServiceParameterException {
        
        boolean isEmpty = StringUtils.isEmpty(serverString);
        if(isEmpty) {
            throw new ServiceParameterException("bad paramter ,value : " + serverString);
        }
        
        serverString = serverString.trim();
        String[] infos = serverString.split(":");
        if(2 != infos.length) {
            throw new ServiceParameterException("bad paramter ,value : " + serverString);
        }
        
        //其实这边需要验证一下IP地址的正确性
        String ip = infos[0];
        String portString = infos[1];
        
        Integer port = null;
        boolean isDigists = NumberUtils.isDigits(portString);
        if(!isDigists) {
            throw new ServiceParameterException("bad paramter ,value : " + serverString);
        }
        
        try {
            port = Integer.parseInt(infos[1]);
        } catch (NumberFormatException e) {
            throw new ServiceParameterException("bad paramter ,value : " + serverString);
        }
        
        return new ServiceServerNode(ip, port);
    }
}
