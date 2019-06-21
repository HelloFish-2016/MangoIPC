package com.mango.ipcore;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Author:mango
 * Time:2019/6/19 16:10
 * Version:1.0.0
 * Desc:TODO()
 */

public class IPCTransport {

    private static class LOADOBJECT{
        static final IPCTransport INSTANCE = new IPCTransport();
    }

    public static IPCTransport getDefault() {
        return LOADOBJECT.INSTANCE;
    }

    private IPCTransport() {}

    private Map<String,IPCServiceConnection> mConnection = new HashMap<>();

    private class IPCServiceConnection implements ServiceConnection {
        String serviceName;

        public IPCServiceConnection(String serviceName) {
            this.serviceName = serviceName;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IRemoteService ipcService = IRemoteService.Stub.asInterface(service);
            Log.e(TAG,"onServiceConnected ipcService="+ipcService);
            IPCCache.getDefault().putRemoteService(serviceName,ipcService);
            mConnection.put(serviceName,this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG,"onServiceDisconnected ");
            IPCCache.getDefault().putRemoteService(serviceName,null);
            mConnection.remove(serviceName);
        }
    }

    /**
     * 绑定服务
     */
    public void bind(Context context , String packageName, String serviceName){
        IRemoteService service = IPCCache.getDefault().getRemoteService(serviceName);
        if (service != null) {
            return;
        }

        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName,serviceName));
        if (!mConnection.containsKey(serviceName) || mConnection.get(serviceName) == null) {
            IPCServiceConnection connection = new IPCServiceConnection(serviceName);
            context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        } else {
            context.bindService(intent,mConnection.get(serviceName),Context.BIND_AUTO_CREATE);
        }
    }

    public void unBind(Context context,String serviceName){
        if (!mConnection.containsKey(serviceName) || mConnection.get(serviceName) == null) {
            return;
        }
        context.unbindService(mConnection.get(serviceName));
        mConnection.remove(serviceName);
        IPCCache.getDefault().putRemoteService(serviceName,null);
    }

    public IPCResponse sendRequest(IPCRequest request,String serviceName){
        try {
            IRemoteService service = IPCCache.getDefault().getRemoteService(serviceName);
            return service.sendRequest(request);
        } catch (RemoteException e) {
            e.printStackTrace();
            return new IPCResponse(e.getMessage(),"请求远程Service出现异常",false);
        }
    }

}
