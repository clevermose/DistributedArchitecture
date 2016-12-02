package com.whs.da.service.exception;

public class ServiceParameterException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public ServiceParameterException(String errorMsg) {
        super(errorMsg);
    }
    
}
