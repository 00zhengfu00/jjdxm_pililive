package com.dou361.live.bean;

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
 * 描 述：
 * <p/>
 * <p/>
 * 修订历史：
 * <p/>
 * ========================================
 */
public class MessageBean {
    int id;
    int dirid;//直播id
    String username;//评论人手机号
    String headportrait;//评论人头像地址url
    String nickname;//评论人昵称
    String comments;//评论内容
    String replayer;//被回复人手机号
    String reheadportrait;//被回复人头像地址url
    String repname;//被回复人昵称
    String comtime;//发评论的时间

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

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getReplayer() {
        return replayer;
    }

    public void setReplayer(String replayer) {
        this.replayer = replayer;
    }

    public String getReheadportrait() {
        return reheadportrait;
    }

    public void setReheadportrait(String reheadportrait) {
        this.reheadportrait = reheadportrait;
    }

    public String getRepname() {
        return repname;
    }

    public void setRepname(String repname) {
        this.repname = repname;
    }

    public String getComtime() {
        return comtime;
    }

    public void setComtime(String comtime) {
        this.comtime = comtime;
    }
}
