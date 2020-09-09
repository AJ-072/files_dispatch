package com.aj.filesdispatch.Models;

import com.aj.filesdispatch.ApplicationActivity;

import java.io.Serializable;

import static com.aj.filesdispatch.ApplicationActivity.OptnlAvatarName;
import static com.aj.filesdispatch.ApplicationActivity.OptnlUserName;
import static com.aj.filesdispatch.Activities.FindConnection.AVATAR;
import static com.aj.filesdispatch.Activities.FindConnection.BUDDY_NAME;

public class UserInfo implements Serializable {
    private String UserName;
    private String AvatarName;
    private String AppVersion;
    private int ByteReceiverSpeed;

    public UserInfo(int byteReceiverSpeed) {
        ByteReceiverSpeed = byteReceiverSpeed;
        UserName = ApplicationActivity.defaultPreference.getString(BUDDY_NAME, OptnlUserName);
        AvatarName = ApplicationActivity.sharedPreferences.getString(AVATAR, String.valueOf(OptnlAvatarName));
    }

    public int getByteReceiverSpeed() {
        return ByteReceiverSpeed;
    }

    public String getUserName() {
        return UserName;
    }

    public String getAvatarName() {
        return AvatarName;
    }

    public String getAppVersion() {
        return AppVersion;
    }
}
