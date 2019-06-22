package com.mango.ipcore;

import android.content.Context;

import com.mango.ipcore.annotate.RequestHandler;
import com.mango.ipcore.callback.IPCRequestError;
import com.mango.ipcore.utils.ParamsConvert;

import java.lang.reflect.Proxy;

/**
 * Author:mango
 * Time:2019/6/19 16:06
 * Version:1.0.0
 * Desc:TODO()
 */
public class IPCMango {

    private static class LOADOBJECT{
        static final IPCMango INSTANCE = new IPCMango();
    }

    public static IPCMango getDefault() {
        return LOADOBJECT.INSTANCE;
    }

    private IPCMango() {}

    /**
     * 服务端调用
     * 注册处理客户端请求的类
     * @param clazz
     */
    public void register(Class<?> clazz){
        IPCCache.getDefault().register(clazz);
    }

    /**
     * 服务端调用
     * 注销处理客户端请求的类
     * @param clazz
     */
    public void unRegister(Class<?> clazz){
        IPCCache.getDefault().unRegister(clazz);
    }

    /**
     * 客户端调用
     * 绑定默认RemoteService
     * bindservice
     */
    public void bind(Context context, String packageName){
        bind(context,packageName,"com.mango.ipcore.handler.RemoteService");
    }

    /**
     * 客户端调用
     * 绑定自定义Service
     * bindservice
     */
    public void bind(Context context, String packageName, String serviceName){
        IPCTransport.getDefault().bind(context,packageName,serviceName);
    }

    /**
     * 客户端调用
     * 解绑默认服务RemoteService
     * unBind
     */
    public void unBind(Context context){
        unBind(context,"com.mango.ipcore.handler.RemoteService");
    }

    /**
     * 客户端调用
     * 解绑自定义服务
     * unBind
     */
    public void unBind(Context context,String serviceName){
        IPCTransport.getDefault().unBind(context,serviceName);
    }

    /**
     * 客户端调用
     * 绑定RemoteService，必须通过该方法获取实例再发送请求
     * 获取 远程服务端处理客户端请求 的对象
     * @param inter       远程 处理客户端请求的类实现的接口
     * @param params      获取实例方法的参数
     * @return 代理对象实例
     */
    public <T> T loadRequestHandler(Class inter, Object... params){
        return loadRequestHandler("com.mango.ipcore.handler.RemoteService",inter,null,params);
    }

    /**
     * 客户端调用
     * 绑定RemoteService，必须通过该方法获取实例再发送请求
     * @param inter
     * @param listener load失败后通过该接口回调
     * @param params
     * @return <T>
     */
    public <T> T loadRequestHandler(Class inter, IPCRequestError listener, Object... params){
        return loadRequestHandler("com.mango.ipcore.handler.RemoteService",inter,listener,params);
    }

    public <T> T loadRequestHandler(String serviceName, Class inter, IPCRequestError listener, Object... params){

        if (!checkService(serviceName)) {
            if (listener != null) {
                listener.sendResult(new IPCResponse("","未绑定远程Service",false));
            }
            return null;
        }

        //获取处理客户端请求的对象的Key，以此在服务端找出对应的处理者
        String className;
        RequestHandler annotation = (RequestHandler) inter.getAnnotation(RequestHandler.class);
        if (annotation == null) {
            className = inter.getName();
        } else {
            className = annotation.value();
        }
        IPCRequest ipcRequest = buildRequest(IPCRequest.LOAD_INSTANCE,className,"getDefault",params);
        /**
         * 发消息给服务端, 来实例化 处理客户端请求 的对象, 要求实例该对象的方法名为 getDefault
         * 如果构造成功 就通过动态代理构造一个实现该接口的对象并返回
         * 以接收客户端后续请求
         */
        IPCResponse ipcResponse = sendRequest(ipcRequest,serviceName);
        if (ipcResponse.isSuccess()) {
            return (T) Proxy.newProxyInstance(getClass().getClassLoader(),
                    new Class[]{inter},
                    new IPCInvocationHandler(listener,serviceName, className));
        } else {
            if (listener != null) {
                listener.sendResult(ipcResponse);
            }
        }
        return null;
    }


    /**
     * 客户端调用
     * 自定义远程Service 通过该方法发送请求，自己在Service里定义处理逻辑
     * 发送请求给远程Service
     * @param request
     * @return
     */
    public IPCResponse sendRequest(IPCRequest request,String serviceName){
        if (!checkService(serviceName)) {
            return new IPCResponse("","未绑定远程Service",false);
        }
        return IPCTransport.getDefault().sendRequest(request,serviceName);
    }


    public boolean checkService(String serviceName){
        return IPCCache.getDefault().getRemoteService(serviceName) == null ? false:true;
    }

    public IPCRequest buildRequest(int type,String className,String methodName,Object... parameters){
        IPCRequest request = new IPCRequest();
        request.setType(type);
        request.setClassName(className);
        request.setMethodName(methodName);
        request.setParameters(ParamsConvert.serializationParams(parameters));
        return request;
    }
}
