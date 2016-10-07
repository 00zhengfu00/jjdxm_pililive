package com.dou361.live.ui.activity;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.dou361.baseutils.utils.RegexUtils;
import com.dou361.baseutils.utils.StringUtils;
import com.dou361.baseutils.utils.UIUtils;
import com.dou361.customui.ui.LoadingTieDialog;
import com.dou361.live.R;
import com.dou361.live.bean.UserBean;
import com.dou361.live.module.ApiServiceUtils;
import com.dou361.live.module.Callback;
import com.dou361.live.utils.UserManager;

import butterknife.Bind;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {


    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.et_name)
    EditText etName;
    @Bind(R.id.et_pwd)
    EditText etPwd;
    @Bind(R.id.btn_login)
    Button btnLogin;
    @Bind(R.id.btn_register)
    Button btnRegister;
    private String userName;
    private String userPwd;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_login);
        tvTitle.setText("惠购房直播");
    }

    @OnClick({R.id.btn_login, R.id.btn_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                if (checkLogin()) {
                    login();
                }
                break;
            case R.id.btn_register:
                startActivity(RegisterActivity.class);
                break;
        }
    }


    /**
     * 登录
     */
    private void login() {
        LoadingTieDialog.showTie((Activity) mContext);
        ApiServiceUtils.login(userName, userPwd, new Callback() {
            @Override
            public void onSuccuss(String data) {
                UserBean userBean = JSON.parseObject(data, UserBean.class);
                UserManager.getInstance().add(userBean);
                UIUtils.showToastLong("登录成功！");
                UserManager.getInstance().setLogin(true);
                startActivity(MainActivity.class);
                finishAll();
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

    private boolean checkLogin() {
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
}

