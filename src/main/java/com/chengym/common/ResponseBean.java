package com.chengym.common;

/**
 * Desc:   通用返回值
 */
public class ResponseBean<T> {
    /**
     * 返回码 0成功，其他失败
     */
    private String code;
    /**
     * 描述信息
     */
    private String message;
    /**
     * 返回结果
     */
    private T result;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ResponseBean{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", result=" + result +
                '}';
    }
}
