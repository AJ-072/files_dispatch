package com.aj.filesdispatch;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.GridLayoutManager;

class DispatchLayoutManager extends GridLayoutManager {
    public DispatchLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public DispatchLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public DispatchLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }


}
