package com.mango.ipcore.handler;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.mango.ipcore.IPCCache;
import com.mango.ipcore.IPCRequest;
import com.mango.ipcore.IPCResponse;
import com.mango.ipcore.IRemoteService;
import com.mango.ipcore.utils.ParamsConvert;

import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.mango.ipcore.IPCRequest.LOAD_INSTANCE;
import static com.mango.ipcore.IPCRequest.LOAD_METHOD;

/**
 * Author:mango
 * Time:2019/6/19 15:59
 * Version:1.0.0
 * Desc:TODO()
 */
public class RemoteService extends Service {

    /**
     * 后续开发 缓存客户端请求
     * 重连后将缓存数据返回
     */
    private final Deque<IPCRequest> runningRequest = new ArrayDeque<>();

    private Lock mLock = new ReentrantLock();

    private IPCCache mIpcCache ;
    {
        mIpcCache = IPCCache.getDefault();
    }

    @Override
    public IBinder onBind(Intent intent) {

        return new IRemoteService.Stub(){

            @Override
            public IPCResponse sendRequest(IPCRequest request) throws RemoteException {
                mLock.lock();
                executed(request);
                switch (request.getType()) {
                    case LOAD_INSTANCE:
                        try {
                            /**
                             * 找到对应的处理客户端请求的Class和构造方法
                             * 然后通过反射实例化对象 保存起来
                             */
                            Class<?> clazz = mIpcCache.getClass(request.getClassName());
                            Method method = mIpcCache.getMethod(clazz, request.getMethodName());
                            if (mIpcCache.getObject(request.getClassName())  == null) {
                                Object object = method.invoke(null, ParamsConvert.unSerializationParams(request.getParameters()));
                                mIpcCache.putObject(request.getClassName(),object);
                            }
                            return new IPCResponse(null,"初始化处理对象成功",true);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return new IPCResponse(e.getMessage(),"初始化处理对象失败",false);
                        }finally {
                            finished(request);
                            mLock.unlock();
                        }
                    case LOAD_METHOD:
                        try {
                            /**
                             * 找到对应的处理客户端请求的Class和执行请求的方法
                             */
                            Class<?> cl = mIpcCache.getClass(request.getClassName());
                            Method me = mIpcCache.getMethod(cl, request.getMethodName());
                            Object object = mIpcCache.getObject(request.getClassName());

                            Object[] params = ParamsConvert.unSerializationParams(request.getParameters());
                            Object result = me.invoke(object, params);
                            String r = ParamsConvert.mGson.toJson(result);
                            return new IPCResponse(r,"执行方法成功",true);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return new IPCResponse(e.getMessage(),"执行方法失败",false);
                        }finally {
                            finished(request);
                            mLock.unlock();
                        }
                    default:
                }
                return new IPCResponse(null,"未知类型请求，请指定type",false);
            }
        };
    }

    void executed(IPCRequest request){
        runningRequest.offer(request);
    }

    void finished(IPCRequest request){
        runningRequest.remove(request);
    }

}
