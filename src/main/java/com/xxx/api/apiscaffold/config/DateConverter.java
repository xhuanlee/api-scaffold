package com.xxx.api.apiscaffold.config;

import org.springframework.core.convert.converter.Converter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author lixianghuan@allcomchina.com
 * @date 2018/8/3 09:23
 * @extra code change the world
 * @description
 */
public class DateConverter implements Converter<String, Date> {

    private static final List<String> dateFormats = new ArrayList<>(4);

    static {
        dateFormats.add("yyyy-MM");
        dateFormats.add("yyyy-MM-dd");
        dateFormats.add("yyyy-MM-dd HH");
        dateFormats.add("yyyy-MM-dd HH:mm");
        dateFormats.add("yyyy-MM-dd HH:mm:ss");
    }

    @Override
    public Date convert(String s) {
        if (s == null || s.trim() == "") {
            return null;
        }

        String dateString = s.trim();
        if (dateString.matches("^\\d{4}-\\d{1,2}$")) {
            return format(dateString, dateFormats.get(0));
        } else if (dateString.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
            return format(dateString, dateFormats.get(1));
        } else if (dateString.matches("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}$")) {
            return format(dateString, dateFormats.get(2));
        } else if (dateString.matches("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}$")) {
            return format(dateString, dateFormats.get(3));
        } else if (dateString.matches("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
            return format(dateString, dateFormats.get(4));
        } else {
            throw new RuntimeException(String.format("无效的日期格式: %s", dateString));
        }
    }

    private Date format(String dateString, String format) {
        if (dateString == null || format == null || dateString.trim() == "" || format.trim() == "") {
            return null;
        }

        Date date = null;
        SimpleDateFormat sdf;

        try {
            sdf = new SimpleDateFormat(format);
            date = sdf.parse(dateString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return date;
    }

}
