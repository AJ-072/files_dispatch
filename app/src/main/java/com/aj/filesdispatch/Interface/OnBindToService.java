package com.aj.filesdispatch.Interface;

import com.aj.filesdispatch.Models.SentFileItem;
import com.aj.filesdispatch.Models.UserInfo;

public interface OnBindToService {
    void setTotalProgress(int totalProgress);
    void setTotalSize(int total);
    void getConnectedDeviceInfo(UserInfo info);
}