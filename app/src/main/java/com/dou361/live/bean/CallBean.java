package com.dou361.live.bean;

/**
 * ========================================
 * <p>
 * 版 权：dou361.com 版权所有 （C） 2015
 * <p>
 * 作 者：陈冠明
 * <p>
 * 个人网站：http://www.dou361.com
 * <p>
 * 版 本：1.0
 * <p>
 * 创建日期：2016/8/5
 * <p>
 * 描 述：粗略解析请求返回的内容
 * <p>
 * <p>
 * 修订历史：
 * <p>
 * ========================================
 */
public class CallBean {

    /**返回码*/
    int Code = -1;
    /**返回码对应的消息*/
    String Message;
    /**请求成功返回的数据*/
    String Data;

    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        Code = code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getData() {
        return Data;
    }

    public void setData(String data) {
        Data = data;
    }
}
