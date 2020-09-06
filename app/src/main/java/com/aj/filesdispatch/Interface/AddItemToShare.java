package com.aj.filesdispatch.Interface;

import com.aj.filesdispatch.Models.FileViewItem;

import java.util.List;

public interface AddItemToShare {
    void onItemAdded(FileViewItem item);
    void onMultiItemAdded(List<FileViewItem> fileViewItems);
}
