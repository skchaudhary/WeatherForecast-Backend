package com.weather.forecast.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class DateUtil {
    public static final String YMD_FORMAT= "yyyy.MM.dd.HH.mm";

    public static String format(String format, Timestamp date) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            return simpleDateFormat.format(date);
        } catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }
}
