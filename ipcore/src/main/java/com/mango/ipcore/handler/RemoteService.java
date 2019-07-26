package com.mango.ipcore.handler;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

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
            public IPCResponse sendRequest(IPCRequest request) {
                mLock.lock();
                IPCRequest rq = request;
                executed(rq);
                switch (rq.getType()) {
                    case LOAD_INSTANCE:
                        try {
                            /**
                             * 找到对应的处理客户端请求的Class和构造方法
                             * 然后通过反射实例化对象 保存起来
                             */
                            Class<?> clazz = mIpcCache.getClass(rq.getClassName());
                            Method method = mIpcCache.getMethod(clazz, rq.getMethodName());
                            if (mIpcCache.getObject(rq.getClassName())  == null) {
                                Object object = method.invoke(null, ParamsConvert.unSerializationParams(rq.getParameters()));
                                mIpcCache.putObject(rq.getClassName(),object);
                            }
                            return new IPCResponse(null,"初始化处理对象成功",true);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return new IPCResponse(e.getMessage(),"初始化处理对象失败",false);
                        }finally {
                            finished(rq);
                            mLock.unlock();
                        }
                    case LOAD_METHOD:
                        try {
                            /**
                             * 找到对应的处理客户端请求的Class和执行请求的方法
                             */
                            Class<?> cl = mIpcCache.getClass(rq.getClassName());
                            Method me = mIpcCache.getMethod(cl, rq.getMethodName());
                            Object object = mIpcCache.getObject(rq.getClassName());

                            Object[] params = ParamsConvert.unSerializationParams(rq.getParameters());
                            Object result = me.invoke(object, params);
                            String r = ParamsConvert.mGson.toJson(result);
                            return new IPCResponse(r,"执行方法成功",true);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return new IPCResponse(e.getMessage(),"执行方法失败",false);
                        }finally {
                            finished(rq);
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
