package com.aj.filesdispatch;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

class logg {
    File file;
    BufferedWriter writer;
    @RequiresApi(api = Build.VERSION_CODES.O)
    logg(){
        file= new File(Environment.getExternalStorageDirectory().getPath()+"//log"+ LocalDateTime.now()+".txt");
        try {
            file.createNewFile();
            writer= new BufferedWriter(new FileWriter(file));
            writer.write("Files Dispatch");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void d(String Tag,String Msg) throws IOException {
        writer.append("tag "+ "msg");
    }

}
