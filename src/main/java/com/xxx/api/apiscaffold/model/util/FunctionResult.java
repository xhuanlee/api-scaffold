package com.xxx.api.apiscaffold.model.util;

/**
 * @author lixianghuan@allcomchina.com
 * @date 2020/4/15 11:31
 * @extra code change the world
 * @description
 */
public class FunctionResult {
    private boolean success;
    private String message;

    public FunctionResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static FunctionResult ok() {
        return new FunctionResult(true, null);
    }

    public static FunctionResult ok(String message) {
        return new FunctionResult(true, message);
    }

    public static FunctionResult fail(String message) {
        return new FunctionResult(false, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

}
