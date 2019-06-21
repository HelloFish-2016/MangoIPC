package com.mango.ipc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.mango.ipcore.IPCRequest;
import com.mango.ipcore.IPCResponse;
import com.mango.ipcore.IRemoteService;

/**
 * Author:mango
 * Time:2019/6/21 10:45
 * Version:1.0.0
 * Desc:TODO()
 */

public class MyRemoteService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new IRemoteService.Stub(){

            @Override
            public IPCResponse sendRequest(IPCRequest request) throws RemoteException {

                return new IPCResponse("hello","执行方法成功",true);
            }
        };
    }
}
