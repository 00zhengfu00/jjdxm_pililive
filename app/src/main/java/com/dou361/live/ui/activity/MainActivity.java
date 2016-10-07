package com.dou361.live.ui.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dou361.live.R;
import com.dou361.live.bean.UserBean;
import com.dou361.live.ui.fragment.LiveSquareFragment;
import com.dou361.live.ui.fragment.MyProfileFragment;
import com.jaeger.library.StatusBarUtil;

import org.litepal.crud.DataSupport;

import butterknife.Bind;

public class MainActivity extends BaseActivity {

    private int index;
    // 当前fragment的index
    private int currentTabIndex;
    private Fragment[] fragments;

    @Bind(R.id.unread_msg_number)
    TextView unreadLabel;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.btn_publish)
    Button publishBtn;
    private Button[] mTabs;
    private UserBean mUserBean;

    @Override
    protected void setStatusBarColor() {
        StatusBarUtil.setColorNoTranslucent(activity, 0xff3040A0);
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_main);
//        StatusBarUtil.setColor(activity, R.color.global_bg_color_08);
        mTabs = new Button[2];
        mTabs[0] = (Button) findViewById(R.id.btn_square);
        mTabs[1] = (Button) findViewById(R.id.btn_setting);
        // 把第一个tab设为选中状态
        mTabs[0].setSelected(true);
        tvTitle.setText("广场");
        fragments = new Fragment[]{new LiveSquareFragment(), new MyProfileFragment()};
        // 添加显示第一个fragment
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragments[0])
                .commit();
        mUserBean = DataSupport.findFirst(UserBean.class);
    }

    /**
     * button点击事件
     *
     * @param view
     */
    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_square:
                index = 0;
                tvTitle.setText("广场");
                break;
            case R.id.btn_setting:
                index = 1;
                tvTitle.setText("我");
                break;
            case R.id.btn_publish:
                startActivity(new Intent(this, StartLiveActivity.class));
                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        mTabs[currentTabIndex].setSelected(false);
        // 把当前tab设为选中状态
        mTabs[index].setSelected(true);
        currentTabIndex = index;
    }

}
