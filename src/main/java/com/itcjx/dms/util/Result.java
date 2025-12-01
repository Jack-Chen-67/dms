package com.itcjx.dms.util;

import java.util.HashMap;
import java.util.Map;

public class Result<T> {
    private int code;//状态码
    private String msg;//提示信息
    private T data;// 数据
    private Long timestamp;//时间戳

    private Map<String, Object> extra;

    public Result(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    // 成功且返回数据
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    // 成功但不返回数据
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    //失败,自定义状态码和消息
    public static <T> Result<T> error(int code, String msg) {
        return new Result<>(code, msg, null);
    }

    //失败,使用预定义的状态码和消息
    public static <T> Result<T> error(ErrorCode errorCode) {
        return new Result<>(errorCode.getCode(), errorCode.getMsg(), null);
    }

    //失败,使用预定义的状态码和消息,并附加错误信息
    public static <T> Result<T> error(ErrorCode errorCode, String detailMsg) {
        return new Result<>(errorCode.getCode(), errorCode.getMsg() + ": " + detailMsg, null);
    }

    //预定义的状态码和消息
    public enum ErrorCode {
        //通用状态码
        PARAM_ERROR(400, "参数错误"),
        UNAUTHORIZED(401, "未授权"),
        FORBIDDEN(403, "禁止访问"),
        NOT_FOUND(404, "资源不存在"),
        SERVER_ERROR(500, "服务器内部错误"),
        //业务状态码
        BUSINESS_ERROR(1001, "业务异常"),
        DUPLICATE_USERNAME(1002, "用户名已存在"),
        ADD_FAILED(1003, "添加失败"),
        UPDATE_FAILED(1004, "更新失败"),
        DATABASE_ERROR(1005, "数据库操作失败"),
        DELETE_FAILED(1006, "删除失败");

        private final int code;
        private final String msg;

        ErrorCode(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    public Result<T> addExtra(String key, Object value) {
        if (this.extra == null) {
            this.extra = new HashMap<>();
        }
        this.extra.put(key, value);
        return this;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

