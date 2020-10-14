package com.aj.filesdispatch.Entities;

import com.aj.filesdispatch.Enums.Action;

import java.io.Serializable;
import java.util.List;

public class MsgData implements Serializable {
    private List<SentFileItem> filesToSend;
    private byte[] bytes;
    private Action Action;
    public int count=-1;
    private int length;

    public int getLength() {
        return length;
    }

    public MsgData(List<SentFileItem> file, byte[] bytes, com.aj.filesdispatch.Enums.Action action, int length) {
        filesToSend = file;
        this.bytes = bytes;
        Action = action;
        this.length = length;
    }

    public List<SentFileItem> getFileList() {
        return filesToSend;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public Action getAction() {
        return Action;
    }

}
