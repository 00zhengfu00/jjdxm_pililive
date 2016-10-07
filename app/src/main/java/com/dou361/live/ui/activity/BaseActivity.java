package com.dou361.live.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.Toast;

import com.dou361.customui.ui.LoadingPage;
import com.dou361.customui.ui.SwipeBackLayout;
import com.dou361.live.R;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by wei on 2016/5/30.
 */
public abstract class BaseActivity extends AppCompatActivity {


    /**
     * 记录处于前台的Activity
     */
    private static BaseActivity mForegroundActivity;
    /**
     * 记录所有活动的Activity
     */
    private static final List<BaseActivity> mActivities = new LinkedList<BaseActivity>();
    /**
     * 当前Activity
     */
    protected Activity activity;
    /**
     * 当前上下文
     */
    protected Context mContext;
    /**
     * 当前调用类的类标识
     */
    protected String TAG = this.getClass().getSimpleName();
    /**
     * 判断当前Activity是否是显示false为onResume()之后 true为onPause()之后
     */
    protected boolean mFgState;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        /**
         * 只有继承了BaseActivity，Activity才会加入集合管理中
         */
        addActivity(this);
        initView();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setStatusBarColor();
        ButterKnife.bind(this);
    }

    protected void setStatusBarColor(){
        StatusBarUtil.setColorNoTranslucent(activity, 0xff3EB2FB);
    }

    @Override
    public void onBackPressed() {
        /** 点击返回键时关闭当前Activity */
        finishActivity(this);
        try {
            super.onBackPressed();
        } catch (Exception e) {

        }
//        overridePendingTransition(0, R.anim.base_slide_right_out);

    }

    @Override
    protected void onResume() {
        mForegroundActivity = this;
        activity = this;
        mContext = this;
        mFgState = false;
        super.onResume();
    }

    @Override
    protected void onPause() {
        mForegroundActivity = null;
        mFgState = true;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        /** 主动调用gc回收 */
        System.gc();
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    /**
     * 初始化对象
     */
    protected void init() {
        activity = this;
        mContext = this;
    }

    /**
     * 初始化View
     */
    protected abstract void initView();

    /**
     * 关闭所有Activity
     */
    public static void finishAll() {
        List<BaseActivity> copy;
        synchronized (mActivities) {
            copy = new ArrayList<BaseActivity>(mActivities);
        }
        for (BaseActivity activity : copy) {
            activity.finish();
        }
        mActivities.clear();
    }

    /**
     * 添加页面
     */
    public static void addActivity(BaseActivity activity) {
        for (int i = 0; i < mActivities.size(); i++) {
            if (mActivities.get(i) == activity) {
                mActivities.remove(i);
                break;
            }
        }
        mActivities.add(activity);
    }

    /**
     * 添加页面
     */
    public static void finishActivity(BaseActivity activity) {
        for (int i = 0; i < mActivities.size(); i++) {
            if (mActivities.get(i) == activity) {
                activity.finish();
                mActivities.remove(i);
                break;
            }
        }

    }

    /**
     * 关闭所有Activity，除了参数传递的Activity
     */
    public static void finishAll(BaseActivity except) {
        List<BaseActivity> copy;
        synchronized (mActivities) {
            copy = new ArrayList<BaseActivity>(mActivities);
        }
        for (BaseActivity activity : copy) {
            if (activity != except)
                activity.finish();
        }
    }

    /**
     * 是否有启动的Activity
     */
    public static boolean hasActivity() {
        return mActivities.size() > 0;
    }

    /**
     * 是否有其他启动的Activity除掉当前的
     */
    public static boolean hasElseActivity(BaseActivity activity) {
        int i = 0;
        for (; i < mActivities.size(); i++) {
            if (mActivities.get(i) == activity) {
                break;
            }
        }
        return mActivities.size() != i;
    }

    /**
     * 获取当前处于前台的activity
     */
    public static BaseActivity getForegroundActivity() {
        return mForegroundActivity;
    }

    /**
     * 获取当前处于栈顶的activity，无论其是否处于前台
     */
    public static BaseActivity getCurrentActivity() {
        List<BaseActivity> copy;
        synchronized (mActivities) {
            copy = new ArrayList<BaseActivity>(mActivities);
        }
        if (copy.size() > 0) {
            return copy.get(copy.size() - 1);
        }
        return null;
    }

    /**
     * 退出应用
     */
    public void exitApp() {
        finishAll();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    protected <T> LoadingPage.LoadResult checkData(List<T> list) {
        if (list == null) {
            return LoadingPage.LoadResult.ERROR;
        }
        if (list.size() > 0) {
            return LoadingPage.LoadResult.SUCCESS;
        } else {
            return LoadingPage.LoadResult.EMPTY;
        }
    }


    protected void showToast(final String toastContent) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(BaseActivity.this, toastContent, Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void showLongToast(final String toastContent) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(BaseActivity.this, toastContent, Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void startActivity(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
//        overridePendingTransition(R.anim.base_slide_right_in, R.anim.base_slide_remain);
    }
}
