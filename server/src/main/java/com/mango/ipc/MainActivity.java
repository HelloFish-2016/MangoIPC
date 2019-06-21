package com.mango.ipc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.mango.ipcore.IPCMango;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IPCMango.getDefault().register(DataManager.class);
        Log.e("MainActivity",""+Thread.currentThread().getName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IPCMango.getDefault().unRegister(DataManager.class);
    }
}
