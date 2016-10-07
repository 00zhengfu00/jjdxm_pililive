package com.dou361.live.ui.activity;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;

import com.dou361.baseutils.utils.RegexUtils;
import com.dou361.baseutils.utils.StringUtils;
import com.dou361.baseutils.utils.UIUtils;
import com.dou361.customui.ui.LoadingTieDialog;
import com.dou361.live.R;
import com.dou361.live.module.ApiServiceUtils;
import com.dou361.live.module.Callback;

import butterknife.Bind;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity {

    @Bind(R.id.et_name)
    EditText etName;
    @Bind(R.id.et_pwd)
    EditText etPwd;
    private String userName;
    private String userPwd;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_register);

    }

    @OnClick({R.id.btn_register, R.id.btn_login})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                if (checkData()) {
                    register();
                }
                break;
            case R.id.btn_login:
                finish();
                break;
        }
    }

    /**
     * 检查所有数据
     */
    private boolean checkData() {
        userName = etName.getText().toString();
        userPwd = etPwd.getText().toString();
        if (userName.length() == 0) {
            UIUtils.showToastShort("请输入手机号！");
            return false;
        }
        if (userName.length() != 11) {
            UIUtils.showToastShort("请输入11位手机号！");
            return false;
        }
        if (!RegexUtils.matcherMobileNo(userName)) {
            UIUtils.showToastShort("输入的手机号格式不正确！");
            return false;
        }
        if (userPwd.length() == 0) {
            UIUtils.showToastShort("密码不能为空！");
            return false;
        }
        if (userPwd.length() < 6 || userPwd.length() > 16) {
            UIUtils.showToastShort("6-16位密码，数字、下划线、字母组合！");
            return false;
        }
        if (!RegexUtils.matcherPassword(userPwd)) {
            UIUtils.showToastShort("密码不能有非法字符！");
            return false;
        }
        return true;
    }

    /**
     * 获取验证码
     */
    private void register() {
        LoadingTieDialog.showTie((Activity) mContext);
        ApiServiceUtils.register(userName, userPwd, new Callback() {
            @Override
            public void onSuccuss(String data) {
                UIUtils.showToastShort("注册成功！");
                onBackPressed();
            }

            @Override
            public void onLogicError(int code, String message) {
                UIUtils.showToastLong(StringUtils.getString(message));
            }

            @Override
            public void onComplete() {
                LoadingTieDialog.dismssTie((Activity) mContext);
            }
        });
    }
}
