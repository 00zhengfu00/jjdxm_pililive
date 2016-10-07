package com.dou361.live.ui.activity;

import com.dou361.baseutils.utils.SPUtils;
import com.dou361.baseutils.utils.SystemUtils;
import com.dou361.baseutils.utils.UIUtils;
import com.dou361.live.R;
import com.dou361.live.utils.StatusConfig;
import com.dou361.live.utils.UserManager;


/**
 * ========================================
 * <p>
 * 版 权：深圳市晶网科技控股有限公司 版权所有 （C） 2015
 * <p>
 * 作 者：陈冠明
 * <p>
 * 个人网站：http://www.dou361.com
 * <p>
 * 版 本：1.0
 * <p>
 * 创建日期：2015/11/17 10:34
 * <p>
 * 描 述：最早启动页面
 * <p>
 * <p>
 * 修订历史：
 * <p>
 * ========================================
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected void initView() {
        setContentView(R.layout.activity_splash);
        /** 应用上一次启动的版本号 */
        String preVersion = (String) SPUtils.getData(mContext,
                StatusConfig.PREVERSION, "");
        if (!preVersion.equals(SystemUtils.getVersionName())) {
            /** 如果应用更新版本了，则重新调用向导页面 */
            SPUtils.putData(mContext, StatusConfig.PREVERSION,
                    SystemUtils.getVersionName());
            SPUtils.remove(mContext, StatusConfig.ISFIRST);
        }
        UIUtils.postDelayed(new Runnable() {
            @Override
            public void run() {
                goToActivity();
            }
        }, 500);

    }

    /**
     * 发一个延迟消息进行页面跳转
     */
    private void goToActivity() {
        if (UserManager.getInstance().isLogin()) {
            startActivity(MainActivity.class);
            onBackPressed();
        } else {
            startActivity(GuideActivity.class);
            onBackPressed();
        }
    }
}
