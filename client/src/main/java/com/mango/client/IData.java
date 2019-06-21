package com.mango.client;

import com.mango.ipcore.annotate.RequestHandler;

import java.util.List;

/**
 * 注解值需要和服务端实现该接口的类注解值保持一致
 * 接口名不需要和服务端一致，只需要接口里的方法和服务端一致就行
 */
@RequestHandler("IData")
public interface IData {

    List sendData(String params);

    Object getStudent(String name);

}
