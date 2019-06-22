package com.mango.client;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mango.ipcore.IPCMango;
import com.mango.ipcore.IPCRequest;
import com.mango.ipcore.IPCResponse;

import java.util.List;

public class MainActivity extends Activity {

    private IData iData;
    /**
     * 远程服务的包名和类名
     */
    private String mRemotePackageName = "com.mango.ipc";
    private String mRemoteServiceName = "com.mango.ipcore.handler.RemoteService";
    private String mRemoteServiceName2 = "com.mango.ipc.MyRemoteService";

    private TextView hint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IPCMango.getDefault().bind(this,mRemotePackageName,mRemoteServiceName);
        IPCMango.getDefault().bind(this,mRemotePackageName,mRemoteServiceName2);
        hint = (TextView) findViewById(R.id.hint);
    }

    public void sendData(View v){
        //如果服务端会执行耗时操作，这里需要放在子线程
        //发送请求给框架提供的RemoteService
        if (iData == null) {
            iData = IPCMango.getDefault()
                        .loadRequestHandler(mRemoteServiceName,IData.class,"伙计");
        }
        if (iData != null) {
            List list = iData.sendData("小米");
            Object student = iData.getStudent("阿菜");
            hint.setText("");
            hint.append("list="+list+"\n");
            hint.append("student="+student+"\n");
        }
    }

    public void sendMyData(View view){
        //发送请求给服务端自定义的远程Service
        IPCResponse ipcResponse = IPCMango.getDefault().sendRequest(new IPCRequest(), mRemoteServiceName2);
        hint.append("ipcResponse="+ipcResponse+"\n");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IPCMango.getDefault().unBind(this,mRemoteServiceName);
        IPCMango.getDefault().unBind(this,mRemoteServiceName2);
    }

}
