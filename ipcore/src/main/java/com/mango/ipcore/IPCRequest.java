package com.mango.ipcore;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * Author: mango
 * Time: 2019/6/12 20:45
 * Version:
 * Desc: TODO(封装请求信息)
 */
public class IPCRequest implements Parcelable {

    //获取对象
    public static final int LOAD_INSTANCE = 1;
    //执行方法
    public static final int LOAD_METHOD = 2;
    //请求类型
    private int type;
    //服务端处理请求的类名
    private String className;
    //服务端处理请求的方法名
    private String methodName;
    //服务端处理请求的方法的参数
    private IPCParameter[] parameters;

    public IPCRequest() {}

    public IPCRequest(int type, String className, String methodName, IPCParameter[] parameters) {
        this.type = type;
        this.className = className;
        this.methodName = methodName;
        this.parameters = parameters;
    }

    protected IPCRequest(Parcel in) {
        type = in.readInt();
        className = in.readString();
        methodName = in.readString();
        Parcelable[] parcelables = in.readParcelableArray(IPCParameter.class.getClassLoader());
        if (parcelables != null) {
            parameters = Arrays.copyOf(parcelables, parcelables.length, IPCParameter[].class);
        }
    }

    public static final Creator<IPCRequest> CREATOR = new Creator<IPCRequest>() {
        @Override
        public IPCRequest createFromParcel(Parcel in) {
            return new IPCRequest(in);
        }

        @Override
        public IPCRequest[] newArray(int size) {
            return new IPCRequest[size];
        }
    };

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public IPCParameter[] getParameters() {
        return parameters;
    }

    public void setParameters(IPCParameter[] parameters) {
        this.parameters = parameters;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeString(className);
        dest.writeString(methodName);
        dest.writeParcelableArray(parameters,flags);
    }

    @Override
    public String toString() {
        return "IPCRequest{" +
                "type=" + type +
                ", className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", parameters=" + Arrays.toString(parameters) +
                '}';
    }
}
