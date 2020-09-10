package com.aj.filesdispatch.Entities;

import com.aj.filesdispatch.ApplicationActivity;

import java.io.Serializable;

import static com.aj.filesdispatch.ApplicationActivity.OptnlAvatarName;
import static com.aj.filesdispatch.ApplicationActivity.OptnlUserName;
import static com.aj.filesdispatch.Activities.FindConnection.AVATAR;
import static com.aj.filesdispatch.Activities.FindConnection.BUDDY_NAME;

public class UserInfo implements Serializable {
    private String UserName;
    private int AvatarName;
    private String AppVersion;
    private int ByteReceiverSpeed;

    public UserInfo(int byteReceiverSpeed) {
        ByteReceiverSpeed = byteReceiverSpeed;
        UserName = ApplicationActivity.defaultPreference.getString(BUDDY_NAME, OptnlUserName);
        AvatarName = ApplicationActivity.sharedPreferences.getInt(AVATAR,OptnlAvatarName);
    }

    public int getByteReceiverSpeed() {
        return ByteReceiverSpeed;
    }

    public String getUserName() {
        return UserName;
    }

    public int getAvatarName() {
        return AvatarName;
    }

    public String getAppVersion() {
        return AppVersion;
    }
}
