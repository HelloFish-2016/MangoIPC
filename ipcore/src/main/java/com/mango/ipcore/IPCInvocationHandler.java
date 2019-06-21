package com.mango.ipcore;

import com.google.gson.Gson;
import com.mango.ipcore.callback.IPCRequestError;
import com.mango.ipcore.utils.ParamsConvert;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Author:mango
 * Time:2019/6/20 14:18
 * Version:1.0.0
 * Desc:TODO()
 */

public class IPCInvocationHandler implements InvocationHandler {

    private Gson mGson;
    private String className;
    private String serviceName;
    private IPCRequestError listener;

    public IPCInvocationHandler(IPCRequestError listener, String serviceName, String className) {
        this.className = className;
        this.serviceName = serviceName;
        this.listener = listener;
        mGson = new Gson();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        /**
         * 当代理对象执行方法时，会走到这里
         * 然后构造请求
         * 转发给服务端
         */
        IPCRequest ipcRequest = new IPCRequest();
        ipcRequest.setType(IPCRequest.LOAD_METHOD);
        ipcRequest.setClassName(className);
        ipcRequest.setMethodName(method.getName());
        ipcRequest.setParameters(ParamsConvert.serializationParams(args));
        IPCResponse ipcResponse = IPCMango.getDefault().sendRequest(ipcRequest,serviceName);
        if (ipcResponse != null && ipcResponse.isSuccess()) {
            Class<?> returnType = method.getReturnType();
            if (returnType != void.class && returnType != Void.class) {
                return mGson.fromJson(ipcResponse.getResult(), returnType);
            }
        } else {
            listener.sendResult(ipcResponse);
        }
        return null;
    }


}
