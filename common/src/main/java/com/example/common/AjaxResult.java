package com.example.common;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一返回对象
 */
@Data
public class AjaxResult implements Serializable {
    @Serial
    private static final long serialVersionUID = -3166356399586188236L;
    private int code;    // 状态码
    private String msg;  // 状态码描述信息
    private Object data; // 返回数据

    /**
     * 成功时返回的统一对象
     */
    public static AjaxResult success(String msg, Object data) {
        AjaxResult result = new AjaxResult();
        result.setCode(200);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    /**
     * 成功时返回的统一对象
     */
    public static AjaxResult success(Object data) {
        AjaxResult result = new AjaxResult();
        result.setCode(200);
        result.setMsg("");
        result.setData(data);
        return result;
    }

    /**
     * 失败时返回的统一对象
     */
    public static AjaxResult fail(int code, String msg, Object data) {
        AjaxResult result = new AjaxResult();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    /**
     * 失败时返回的统一对象
     */
    public static AjaxResult fail(int code, String msg) {
        AjaxResult result = new AjaxResult();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(null);
        return result;
    }

}
