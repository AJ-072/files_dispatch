package com.aj.filesdispatch;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;

class RequestSendingPermissions extends BottomSheetDialog {
    public RequestSendingPermissions(@NonNull Context context) {
        super(context);
    }

    public RequestSendingPermissions(@NonNull Context context, int theme) {
        super(context, theme);
    }

    protected RequestSendingPermissions(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
}
