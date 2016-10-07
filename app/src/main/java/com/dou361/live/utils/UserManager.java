package com.dou361.live.utils;

import com.dou361.baseutils.utils.SPUtils;
import com.dou361.baseutils.utils.UIUtils;
import com.dou361.live.bean.UserBean;

import org.litepal.crud.DataSupport;

public class UserManager {

    private static UserManager instance;

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    private UserBean mUserBean;

    private UserManager() {
        mUserBean = DataSupport.findFirst(UserBean.class);
        if(mUserBean == null){
            mUserBean = new UserBean();
            mUserBean.setNickname("13510394044");
            mUserBean.setUsername("13510394044");
            mUserBean.setHeadportrait("http://g.hiphotos.baidu.com/image/pic/item/f3d3572c11dfa9ecfc13ccc066d0f703918fc12c.jpg");
        }
    }

    public UserBean getUserBean() {
        return mUserBean;
    }

    public boolean isLogin() {
        return (boolean) SPUtils.getData(UIUtils.getContext(), StatusConfig.ISLOGIN,
                false);
    }

    public void setLogin(boolean flag) {
        SPUtils.putData(UIUtils.getContext(), StatusConfig.ISLOGIN,
                flag);
    }

    public void add(UserBean user) {
        if(user==null){
            return;
        }
        DataSupport.deleteAll(UserBean.class);
        user.save();
        mUserBean = user;
    }


    public void remove() {
        DataSupport.deleteAll(UserBean.class);
        mUserBean = null;
    }

    public boolean isMySelf(String username) {
        if (mUserBean != null) {
            return mUserBean.getUsername().equals(username);
        }
        return false;
    }
}
