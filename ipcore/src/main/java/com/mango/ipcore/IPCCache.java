package com.mango.ipcore;

import android.text.TextUtils;

import com.mango.ipcore.annotate.RequestHandler;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Author:mango
 * Time:2019/6/19 16:08
 * Version:1.0.0
 * Desc:TODO()
 */
public class IPCCache {

    private static final IPCCache ourInstance = new IPCCache();

    public static IPCCache getDefault() {
        return ourInstance;
    }

    private IPCCache() {}

    /**
     * 保存服务端处理客户端请求的Class和内部的方法
     */
    private Map<String , Class<?>> mClazzs = new HashMap<>();
    private Map<Class<?> , HashMap<String,Method>> mMethods = new HashMap<>();
    /**
     * 保存服务端处理客户端请求的实例
     */
    private Map<String , WeakReference<Object>> mInstance = new HashMap<>();

    /**
     * Key   远程Service的全限定名称
     * Value 远程Service实例
     */
    private Map<String , WeakReference<IRemoteService>> mIPCService = new HashMap<>();


    /**
     * 缓存DataManager.class
     * 缓存class里面的方法
     * @param clazz
     */
    public void register(Class<?> clazz){
        //获取类注解
        RequestHandler annotation = clazz.getAnnotation(RequestHandler.class);
        String className;
        if (annotation == null) {
            className = clazz.getName();
        } else {
            className = annotation.value();
        }
        mClazzs.put(className,clazz);
        //缓存Method
        HashMap<String,Method> method = new HashMap<>();
        Method[] methods = clazz.getMethods();
        for (Method m : methods) {
            method.put(m.getName(),m);
        }
        mMethods.put(clazz,method);
    }

    public void unRegister(Class<?> clazz){
        //获取类注解
        RequestHandler annotation = clazz.getAnnotation(RequestHandler.class);
        String className;
        if (annotation == null) {
            className = clazz.getName();
        } else {
            className = annotation.value();
        }
        mClazzs.remove(className);
        mMethods.remove(clazz);
    }

    public Class<?> getClass(String className){
        if (TextUtils.isEmpty(className)) {
            return null;
        }
        Class<?> clazz = mClazzs.get(className);
        if (clazz == null) {
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return clazz;
    }

    public Method getMethod(Class<?> clazz,String methodName){
        HashMap<String, Method> methods = mMethods.get(clazz);
        return methods == null ? null : methods.get(methodName);
    }

    public void putObject(String classType,Object object){
        if (object == null) {
            mInstance.remove(classType);
        } else {
            mInstance.put(classType,new WeakReference<>(object));
        }
    }

    public Object getObject(String classType){
        return mInstance.containsKey(classType) ? mInstance.get(classType).get() : null;
    }

    public void putRemoteService(String serviceName, IRemoteService service){
        if (service == null) {
            mIPCService.remove(serviceName);
        } else {
            mIPCService.put(serviceName,new WeakReference<>(service));
        }
    }

    public IRemoteService getRemoteService(String serviceName){
        return mIPCService.containsKey(serviceName) ? mIPCService.get(serviceName).get() : null;
    }
}
