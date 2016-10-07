package com.dou361.live.ui.activity;

import android.widget.Button;

import com.dou361.live.R;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * ========================================
 * <p/>
 * 版 权：深圳市晶网科技控股有限公司 版权所有 （C） 2015
 * <p/>
 * 作 者：陈冠明
 * <p/>
 * 个人网站：http://www.dou361.com
 * <p/>
 * 版 本：1.0
 * <p/>
 * 创建日期：2015/11/17 11:57
 * <p/>
 * 描 述：开启应用的向导页面
 * <p/>
 * <p/>
 * 修订历史：
 * <p/>
 * ========================================
 */
public class GuideActivity extends BaseActivity {


    @Bind(R.id.btn_login)
    Button btnLogin;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_guide);

    }

    @OnClick(R.id.btn_login)
    public void onClick() {
        startActivity(LoginActivity.class);
        onBackPressed();
    }

}
