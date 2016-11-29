package com.whs.da.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

/**
 * Hessian的序列化与反序列化,该序列化也需要对象实现Java的序列化接口
 * 
 * @author haiswang
 *
 */
public class HessianSerialize<T> {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Person p1 = new Person();
        p1.setName("wanghaisheng");
        p1.setAge(28);
        
        HessianSerialize<Person> hessianSerialize = new HessianSerialize<>();
        byte[] bytes = hessianSerialize.doSerialize(p1);
        System.out.println("序列化后的字节 : " + Arrays.toString(bytes));
        
        p1 = hessianSerialize.doDeSerialize(bytes);
        System.out.println("反序列化的对象 : " + p1);
    }
    
    /**
     * 序列化
     * @param object
     * @return
     */
    public byte[] doSerialize(T object) {
        if(null==object) {
            return null;
        }
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        HessianOutput ho = new HessianOutput(baos);
        byte[] returnBytes = null;
        try {
            ho.writeObject(object);
            returnBytes = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(null!=baos) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            if(null!=ho) {
                try {
                    ho.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return returnBytes;
    }
    
    /**
     * 反序列化
     * @param bytes
     * @return
     */
    @SuppressWarnings("unchecked")
    public T doDeSerialize(byte[] bytes) {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        HessianInput hi = new HessianInput(bais);
        T returnObj = null;
        try {
            returnObj = (T)hi.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } 
        
        return returnObj;
    }
}
