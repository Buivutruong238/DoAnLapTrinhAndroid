package com.nhom08.doanlaptrinhandroid.Interface_enum;

public interface OnMyFinishListener<T> {
    void onFinish(T result);
    void onError(Throwable error, Object bonusOfCoder);
}