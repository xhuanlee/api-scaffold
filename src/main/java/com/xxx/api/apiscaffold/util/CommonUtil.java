package com.xxx.api.apiscaffold.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author lixianghuan@allcomchina.com
 * @date 2020/4/17 17:44
 * @extra code change the world
 * @description
 */
public class CommonUtil {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String format(Date date) {
        return date == null ? null : SIMPLE_DATE_FORMAT.format(date);
    }

}
