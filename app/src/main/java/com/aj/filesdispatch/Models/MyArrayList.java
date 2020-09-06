package com.aj.filesdispatch.Models;

import androidx.annotation.Nullable;

import com.aj.filesdispatch.Models.FileData;
import com.aj.filesdispatch.Models.FileViewItem;
import com.aj.filesdispatch.Models.SentFileItem;

import java.io.File;
import java.util.ArrayList;

class MyArrayList extends ArrayList<SentFileItem> {

    public boolean contains(FileData filePack) {
        while(this.listIterator().hasNext()){
            if(this.listIterator().next().getFileName().equals(filePack.getFileName())){
                return true;
            }
        }
        return false;
    }
    public void cast(ArrayList<FileViewItem> viewItems){
        for (FileViewItem item:viewItems){
            //this.add(((SentFileItem) (FileData) item));
        }
    }
}
