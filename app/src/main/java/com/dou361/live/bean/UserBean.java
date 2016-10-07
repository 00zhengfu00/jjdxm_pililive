package com.dou361.live.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * ========================================
 * <p/>
 * 版 权：dou361.com 版权所有 （C） 2015
 * <p/>
 * 作 者：陈冠明
 * <p/>
 * 个人网站：http://www.dou361.com
 * <p/>
 * 版 本：1.0
 * <p/>
 * 创建日期：2016/7/29
 * <p/>
 * 描 述：用户对象
 * <p/>
 * <p/>
 * 修订历史：
 * <p/>
 * ========================================
 */
public class UserBean extends DataSupport implements Serializable {
    //手机号
    String username;
    //用户头像地址url
    String headportrait;
    //昵称
    String nickname;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHeadportrait() {
        return headportrait;
    }

    public void setHeadportrait(String headportrait) {
        this.headportrait = headportrait;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
