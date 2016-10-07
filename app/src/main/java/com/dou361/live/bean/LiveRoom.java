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
 * 创建日期：2016/8/8 22:18
 * <p>
 * 描 述：直播间对象
 * <p>
 * <p>
 * 修订历史：
 * <p>
 * ========================================
 */
public class LiveRoom {

    int id;
    int dirid;//直播id
    String username;//主播手机号
    String headportrait;//主播头像地址url
    String nickname;//主播昵称
    String picture;//直播封面地址url
    String hlsip;//推流地址url
    String playurl;//播放地址url
    int audienceNum;//观看人数

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDirid() {
        return dirid;
    }

    public void setDirid(int dirid) {
        this.dirid = dirid;
    }

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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getHlsip() {
        return hlsip;
    }

    public void setHlsip(String hlsip) {
        this.hlsip = hlsip;
    }

    public int getAudienceNum() {
        return audienceNum;
    }

    public void setAudienceNum(int audienceNum) {
        this.audienceNum = audienceNum;
    }

    public String getPlayurl() {
        return playurl;
    }

    public void setPlayurl(String playurl) {
        this.playurl = playurl;
    }
}
