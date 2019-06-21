package com.mango.ipc;

import com.mango.ipcore.annotate.RequestHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:mango
 * Time:2019/6/19 17:00
 * Version:1.0.0
 * Desc:注解值和客户端接口中的注解值保持一致
 * 需要提供一个公共静态且方法名为getDefault的方法 返回实例
 */
@RequestHandler("IData")
public class DataManager implements IData {

    private static final DataManager ourInstance = new DataManager();

    public static DataManager getDefault(String params) {
        return ourInstance;
    }

    private DataManager() {}


    @Override
    public List sendData(String params) {
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        return list;
    }

    @Override
    public Student getStudent(String name) {
        return new Student(name);
    }
}
