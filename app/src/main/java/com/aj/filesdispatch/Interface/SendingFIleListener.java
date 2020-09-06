package com.aj.filesdispatch.Interface;

import com.aj.filesdispatch.Models.SentFileItem;

import java.util.List;

public interface SendingFIleListener {
    void onFileRemoved(int position);

    void onFileListChanged(List<SentFileItem> fileItems);

    void onFileProgress(int position);

    void onErrorOccur(int position);
}
