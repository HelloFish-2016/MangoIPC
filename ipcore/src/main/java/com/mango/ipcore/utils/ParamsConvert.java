package com.mango.ipcore.utils;

import com.google.gson.Gson;
import com.mango.ipcore.IPCParameter;

/**
 * Author:mango
 * Time:2019/6/20 18:55
 * Version:1.0.0
 * Desc:TODO()
 */

public class ParamsConvert {

    public static Gson mGson = new Gson();

    public static Object[] unSerializationParams(IPCParameter[] parameters) {
        Object[] objects;
        if (parameters == null || parameters.length == 0) {
            objects = new Object[0];
        } else {
            objects = new Object[parameters.length];
            for (int i=0; i<parameters.length; i++) {
                IPCParameter pa = parameters[i];
                objects[i] = mGson.fromJson(pa.getValue(),pa.getType());
            }
        }
        return objects;
    }

    public static IPCParameter[] serializationParams(Object[] params) {
        IPCParameter[] p;
        if (params == null) {
            p = new IPCParameter[0];
        } else {
            p = new IPCParameter[params.length];
            for (int i=0; i<params.length; i++) {
                Object o = params[i];
                p[i] = new IPCParameter(o.getClass(),mGson.toJson(o));
            }
        }
        return p;
    }
}
