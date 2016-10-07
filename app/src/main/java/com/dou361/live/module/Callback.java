package com.dou361.live.module;

import com.alibaba.fastjson.JSON;
import com.dou361.baseutils.utils.LogUtils;
import com.dou361.live.bean.CallBean;
import com.dou361.live.utils.SystemConfig;

import retrofit2.Call;
import retrofit2.Response;


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
 * 创建日期：2016/3/8
 * <p>
 * 描 述：封装网络请求，过滤联网失败的回调
 * <p>
 * <p>
 * 修订历史：
 * <p>
 * ========================================
 */
public abstract class Callback implements retrofit2.Callback<String> {

    @Override
    public void onResponse(Call<String> call, Response<String> response) {
        onComplete();
        String result = response.body();
        CallBean mCallBean = null;
        try {
            mCallBean = JSON.parseObject(result, CallBean.class);
            LogUtils.logTagName("dou361").log("response result---->>" + result);
        } catch (Exception e) {
            LogUtils.logTagName("dou361").log(e);
        }
        if (mCallBean != null) {
            int code = mCallBean.getCode();
            String message = mCallBean.getMessage();
            String succuss = mCallBean.getData();
            if (code == SystemConfig.Req_Success) {
                onSuccuss(succuss);
            } else {
                onLogicError(code, message);
            }
        } else {
            onLogicError(-1, "未知错误！");
        }
    }

    /**
     * 联网失败回调
     */

    @Override
    public void onFailure(Call<String> call, Throwable t) {
        onComplete();
    }

    /**
     * 联网成功回调
     */
    public abstract void onSuccuss(String data);

    /**
     * 逻辑错误回调
     */
    public abstract void onLogicError(int code, String message);


    /**
     * 联网完成回调
     */
    public void onComplete() {
    }
}
