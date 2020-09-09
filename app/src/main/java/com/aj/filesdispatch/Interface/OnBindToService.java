package com.aj.filesdispatch.Interface;

import com.aj.filesdispatch.Entities.UserInfo;

public interface OnBindToService {
    void setTotalProgress(int totalProgress);
    void setTotalSize(int total);
    void getConnectedDeviceInfo(UserInfo info);
}