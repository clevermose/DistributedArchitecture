package com.whs.da.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

/**
 * Java的序列化与反序列化
 * @author haiswang
 *
 */
public class JavaSerialize<T> {

    /**
     * @param args
     */
    public static void main(String[] args) {
        
        Person p1 = new Person();
        p1.setName("wanghaisheng");
        p1.setAge(28);
        
        JavaSerialize<Person> javaSerialize = new JavaSerialize<Person>();
        byte[] bytes = javaSerialize.doSerialize(p1);
        System.out.println("序列化后的字节 : " + Arrays.toString(bytes));
        
        p1 = javaSerialize.doDeSerialize(bytes);
        System.out.println("反序列化的对象 : " + p1);
    }
    
    /**
     * 序列化
     * @param person
     * @return
     */
    public byte[] doSerialize(T person) {
        
        if(null == person) {
            return null;
        }
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] returnBytes = null;
        
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)){
            oos.writeObject(person);
            returnBytes = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
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
        if(null==bytes) {
            return null;
        }
        
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        T returnObj = null;
        try(ObjectInputStream ois = new ObjectInputStream(bais)) {
            returnObj = (T)ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } 
        
        return returnObj;
    }
    
    

}
