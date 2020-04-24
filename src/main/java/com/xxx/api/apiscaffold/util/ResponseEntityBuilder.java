package com.xxx.api.apiscaffold.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lixianghuan@allcomchina.com
 * @date 2020/3/23 15:12
 * @extra code change the world
 * @description
 */
public class ResponseEntityBuilder {

    public static final String ENTRY = "entry";
    public static final String LIST = "list";
    public static final String TOTAL = "total";

    private int statusCode = HttpStatus.OK.value();
    private int code;
    private String message;
    private Map<String, Object> data;

    private ResponseEntityBuilder() {
        this.code = 200;
    }

    private ResponseEntityBuilder(int code) {
        this.code = code;
    }

    private ResponseEntityBuilder(Map<String, Object> data) {
        this.code = 200;
        this.data = data;
    }

    private ResponseEntityBuilder(String message) {
        this.code = 200;
        this.message = message;
    }

    private ResponseEntityBuilder(int code, Map<String, Object> data) {
        this.code = code;
        this.data = data;
    }

    private ResponseEntityBuilder(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private ResponseEntityBuilder(int code, Map<String, Object> data, String message) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static ResponseEntityBuilder builder() {
        return new ResponseEntityBuilder();
    }

    public static ResponseEntityBuilder builder(int code) {
        return new ResponseEntityBuilder(code);
    }

    public static ResponseEntityBuilder builder(int code, String message) {
        return new ResponseEntityBuilder(code, message);
    }

    public static ResponseEntityBuilder builder(int code, Map<String, Object> data) {
        return new ResponseEntityBuilder(code, data);
    }

    public static ResponseEntityBuilder builder(int code, Map<String, Object> data, String message) {
        return new ResponseEntityBuilder(code, data, message);
    }

    public ResponseEntity build() {
        return ResponseEntity.status(statusCode).body(this);
    }

    public ResponseEntityBuilder statusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public ResponseEntityBuilder code(int code) {
        this.code = code;
        return this;
    }

    public ResponseEntityBuilder message(String message) {
        this.message = message;
        return this;
    }

    public ResponseEntityBuilder data(Map<String, Object> data) {
        this.data = data;
        return this;
    }

    public ResponseEntityBuilder addData(String key, Object value) {
        if (StringUtils.isEmpty(key) || value == null) {
            return this;
        }
        createMapIfNull();
        this.data.put(key, value);
        return this;
    }

    public ResponseEntityBuilder entry(Object entry) {
        return addData(ENTRY, entry);
    }

    public ResponseEntityBuilder list(List<? extends Object> list) {
        return addData(LIST, list);
    }

    public ResponseEntityBuilder total(long total) {
        return addData(TOTAL, total);
    }

    public static ResponseEntityBuilder setEntry(Object entry) {
        return new ResponseEntityBuilder().entry(entry);
    }

    public static ResponseEntityBuilder setList(List<? extends Object> list) {
        return new ResponseEntityBuilder().list(list);
    }

    public static ResponseEntity ok(Map<String, Object> data) {
        return new ResponseEntityBuilder(data).build();
    }

    public static ResponseEntity ok(String key, Object value) {
        if (StringUtils.isEmpty(key) || value == null) {
            return new ResponseEntityBuilder().build();
        }
        return new ResponseEntityBuilder().addData(key, value).build();
    }

    public static ResponseEntity ok(String message) {
        return new ResponseEntityBuilder(message).code(HttpStatus.OK.value()).build();
    }

    public static ResponseEntity notFound() {
        return new ResponseEntityBuilder(HttpStatus.NOT_FOUND.value(), getErrorMessage(HttpStatus.NOT_FOUND.value())).build();
    }

    public static ResponseEntity badRequest() {
        return new ResponseEntityBuilder(HttpStatus.BAD_REQUEST.value(), getErrorMessage(HttpStatus.BAD_REQUEST.value())).build();
    }

    public static ResponseEntity error(int code) {
        return new ResponseEntityBuilder(code, getErrorMessage(code)).build();
    }

    public static ResponseEntity error(String message) {
        return new ResponseEntityBuilder(HttpStatus.BAD_REQUEST.value(), message).build();
    }

    public static ResponseEntity error(int code, String message) {
        return new ResponseEntityBuilder(code, message).build();
    }

    public ResponseEntity error(int code, String message, Map<String, Object> data) {
        return new ResponseEntityBuilder(code, data, message).build();
    }

    private void createMapIfNull() {
        if (data == null) {
            data = new HashMap<>();
        }
    }

    public static String getErrorMessage(int code) {
        switch (code) {
            case 400:
                return "请求数据错误";
            case 403:
                return "缺少权限";
            case 404:
                return "没有数据";
            case 500:
                return "请求数据异常";
            case 502:
                return "服务不可用";
            default:
                return null;
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseEntityBuilder{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
