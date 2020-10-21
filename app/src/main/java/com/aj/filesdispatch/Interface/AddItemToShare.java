package com.aj.filesdispatch.Interface;

import com.aj.filesdispatch.Entities.FileItem;

import java.util.List;

public interface AddItemToShare{
    void onItemAdded(FileItem item);

    void onMultiItemAdded(List<FileItem> fileViewItems);
}
