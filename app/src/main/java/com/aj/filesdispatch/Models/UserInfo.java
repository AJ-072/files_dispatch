package com.aj.filesdispatch.Models;

import com.aj.filesdispatch.ApplicationActivity;

import java.io.Serializable;

import static com.aj.filesdispatch.ApplicationActivity.OptnlAvatarName;
import static com.aj.filesdispatch.ApplicationActivity.OptnlUserName;

public class UserInfo implements Serializable {
    private String UserName;
    private String AvatarName;
    private String AppVersion;
    private int ByteReceiverSpeed;

    public UserInfo( int byteReceiverSpeed) {
        ByteReceiverSpeed = byteReceiverSpeed;
        UserName = ApplicationActivity.defaultPreference.getString("username", OptnlUserName);
        AvatarName = ApplicationActivity.sharedPreferences.getString("avatarNum", OptnlAvatarName);
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
