package com.weather.forecast.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static final String YMD_FORMAT= "yyyy.MM.dd.HH.mm";

    public static String format(String format, Date date) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            return simpleDateFormat.format(date);
        } catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }
}
