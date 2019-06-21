package com.mango.ipcore;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Author: mango
 * Time: 2019/6/12 20:45
 * Version:
 * Desc: TODO()
 */
public class IPCResponse implements Parcelable {

    //方法返回结果
    private String result;
    //本次请求执行情况的描述信息
    private String message;
    //是否执行成功
    private boolean success;

    public IPCResponse(String result, String message, boolean success) {
        this.result = result;
        this.message = message;
        this.success = success;
    }

    protected IPCResponse(Parcel in) {
        result = in.readString();
        message = in.readString();
        success = in.readByte() != 0;
    }

    public static final Creator<IPCResponse> CREATOR = new Creator<IPCResponse>() {
        @Override
        public IPCResponse createFromParcel(Parcel in) {
            return new IPCResponse(in);
        }

        @Override
        public IPCResponse[] newArray(int size) {
            return new IPCResponse[size];
        }
    };

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(result);
        dest.writeString(message);
        dest.writeByte((byte) (success ? 1 : 0));
    }

    @Override
    public String toString() {
        return "IPCResponse{" +
                "result='" + result + '\'' +
                ", message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}
