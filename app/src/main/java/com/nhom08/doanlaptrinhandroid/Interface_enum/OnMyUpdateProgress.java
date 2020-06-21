package com.nhom08.doanlaptrinhandroid.Interface_enum;

public interface OnMyUpdateProgress<progress,value> {
    void onUpdateProgress(progress percent);
    void onUpdateValue(value value);
}
