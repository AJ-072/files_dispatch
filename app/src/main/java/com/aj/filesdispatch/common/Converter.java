package com.aj.filesdispatch.common;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Converter {
    public static String SizeInGMK(long sizeInByte) {
        DecimalFormat size_format = new DecimalFormat();
        size_format.setMaximumFractionDigits(2);
        if (sizeInByte >= 1000000000)
            return size_format.format((Float.parseFloat(String.valueOf(sizeInByte))) / 1000000000) + "GB";
        else if (sizeInByte >= 1000000)
            return size_format.format((Float.parseFloat(String.valueOf(sizeInByte))) / 1000000) + "MB";
        else if (sizeInByte >= 1000)
            return size_format.format((Float.parseFloat(String.valueOf(sizeInByte))) / 1000) + "KB";
        else
            return size_format.format(Float.parseFloat(String.valueOf(sizeInByte))) + "B";
    }

    public static String GetDate(long dateInLong) {
        if (dateInLong==0){
            return SimpleDateFormat.getDateInstance(DateFormat.MEDIUM).format(new Date());
        }
        return SimpleDateFormat.getDateInstance(DateFormat.SHORT).format(new Date(dateInLong * 1000));
    }

    public static String GetDateMed(long dateInLong) {
        return SimpleDateFormat.getDateInstance(DateFormat.MEDIUM).format(new Date(dateInLong * 1000));
    }


    public static String getFileDes(File file) {
        if (file.isDirectory()) {
            return GetDate(file.lastModified() / 1000) + " | " + Objects.requireNonNull(file.list()).length + (Objects.requireNonNull(file.list()).length == 1 ? " item " : " items");
        } else
            return GetDate(file.lastModified() / 1000) + " | " + SizeInGMK(file.length());
    }
}
