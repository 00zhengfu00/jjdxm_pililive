package com.dou361.live.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.dou361.baseutils.utils.LogUtils;
import com.dou361.baseutils.utils.MediaUtils;
import com.dou361.customui.ui.AlertView;
import com.dou361.live.R;
import com.dou361.live.bean.MessageBean;
import com.dou361.live.module.ApiServiceUtils;
import com.dou361.live.module.Callback;
import com.dou361.live.ui.widget.BarrageLayout;
import com.dou361.live.ui.widget.GiftLayout;
import com.dou361.live.ui.widget.GlideCircleTransform;
import com.dou361.live.ui.widget.PeriscopeLayout;
import com.dou361.live.ui.widget.RoomMessagesView;
import com.dou361.live.utils.UserManager;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by wei on 2016/6/12.
 */
public abstract class BaseLiveActivity extends BaseActivity {
    protected static final String TAG = "LiveActivity";

    @Bind(R.id.message_view)
    RoomMessagesView messageView;
    @Bind(R.id.periscope_layout)
    PeriscopeLayout periscopeLayout;
    /**
     * 底部操作栏
     */
    @Bind(R.id.bottom_bar)
    View bottomBar;
    /**
     * 弹幕
     */
    @Bind(R.id.barrage_layout)
    BarrageLayout barrageLayout;
    /**
     * 礼物
     */
    @Bind(R.id.gift_layout)
    GiftLayout giftLayout;
    @Bind(R.id.horizontal_recycle_view)
    RecyclerView horizontalRecyclerView;

    /**
     * 房间id
     */
    protected String roomId = "";
    /**
     * 直播id
     */
    protected int liveId = 0;
    /**
     * 是否在播放或者直播
     */
    protected boolean isPlay;
    /**
     * 是否开始推流
     */
    protected boolean isPull;
    /**
     * 聊天界面是否初始化
     */
    protected boolean isMessageListInited;
    /**
     * 聊天室成员列表
     */
    List<String> memberList = new ArrayList<>();
    /**
     * 亮屏管理
     */
    private PowerManager.WakeLock wakeLock;
    private AutoPlayRunnable mAutoPlayRunnable;
    private AutoPerscopeRunnable mAutoPerscopeRunnable;

    /**
     * 初始化消息列表
     */
    protected void onMessageListInit() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageView.init(roomId);
                messageView.setMessageViewListener(new RoomMessagesView.MessageViewListener() {
                    @Override
                    public void onMessageSend(String content) {
                        if (messageView.isDanmuShow) {
                            barrageLayout.addBarrage(content, UserManager.getInstance().getUserBean().getUsername());
                        }
                        MessageBean msg = new MessageBean();
                        msg.setComments(content);
                        msg.setUsername(UserManager.getInstance().getUserBean().getUsername());
                        msg.setNickname(UserManager.getInstance().getUserBean().getUsername());
                        msg.setDirid(liveId);
                        if (messageView != null) {
                            msg.setReplayer(messageView.getATId());
                            msg.setRepname(messageView.getATText());
                            messageView.sendMsg(msg);
                            messageView.refresh();
                        }
                        ApiServiceUtils.sendMessage(messageView.getATId(), liveId, content, new Callback() {
                            @Override
                            public void onSuccuss(String data) {
                                messageView.refreshSelectLast();
                            }

                            @Override
                            public void onLogicError(int code, String message) {
                                showToast("消息发送失败！");
                            }
                        });
                    }

                    @Override
                    public void onHiderBottomBar() {
                        bottomBar.setVisibility(View.VISIBLE);
                        messageView.setShowInputView(false);
                    }

                    @Override
                    public void onMessageOnclick(int id, String sid) {
                        if (sid != null && !sid.equals(UserManager.getInstance().getUserBean().getUsername())) {
                            messageView.setShowInputView(true);
                            bottomBar.setVisibility(View.INVISIBLE);
                            messageView.setReplyer();
                        } else {
                            if (messageView != null) {
                                messageView.clearAT();
                            }
                        }

                    }

                    @Override
                    public void onLoadMore() {
                        if (liveId <= 0 || messageView == null) {
                            return;
                        }
                        Observable
                                .create(new Observable.OnSubscribe<List<MessageBean>>() {
                                    @Override
                                    public void call(Subscriber<? super List<MessageBean>> subscriber) {
                                        List<MessageBean> temp = ApiServiceUtils.getMessageList(liveId, messageView.getMessageCount() + 1);
                                        subscriber.onNext(temp);
                                    }
                                })
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<List<MessageBean>>() {
                                    @Override
                                    public void call(List<MessageBean> temp) {
                                        if (messageView != null) {
                                            messageView.loadMoreMessageList(temp);
                                            messageView.refresh();
                                        }
                                    }
                                });
                    }


                });
                messageView.setVisibility(View.VISIBLE);
                bottomBar.setVisibility(View.VISIBLE);
                isMessageListInited = true;
                showMemberList();
            }
        });
    }


    protected void addMessageListener() {
        if (liveId <= 0) {
            return;
        }
        Observable
                .create(new Observable.OnSubscribe<List<MessageBean>>() {
                    @Override
                    public void call(Subscriber<? super List<MessageBean>> subscriber) {
                        List<MessageBean> temp = ApiServiceUtils.getMessageList(liveId, 1);
                        subscriber.onNext(temp);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<MessageBean>>() {
                    @Override
                    public void call(List<MessageBean> temp) {
                        if (messageView != null) {
                            messageView.refreshMessageList(temp);
                            messageView.refresh();
                            messageView.refreshSelectLast();
                        }
                    }
                });

    }

    /**
     * 显示房间观看成员
     */
    private void showMemberList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(BaseLiveActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        horizontalRecyclerView.setLayoutManager(layoutManager);
        horizontalRecyclerView.setAdapter(new AvatarAdapter(BaseLiveActivity.this, memberList));
        new Thread(new Runnable() {
            @Override
            public void run() {
                memberList.add("主播1");
                memberList.add("主播2");
                memberList.add("主播3");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        horizontalRecyclerView.getAdapter().notifyDataSetChanged();
                    }
                });
            }
        }).start();


    }

    /**
     * 进入房间
     */
    private void onRoomMemberAdded(String name) {
        if (!memberList.contains(name))
            memberList.add(name);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                horizontalRecyclerView.getAdapter().notifyDataSetChanged();
            }
        });
    }

    /**
     * 退出房间
     */
    private void onRoomMemberExited(String name) {
        memberList.remove(name);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                horizontalRecyclerView.getAdapter().notifyDataSetChanged();
            }
        });
    }

    @OnClick({R.id.ll_back, R.id.root_layout, R.id.comment_image, R.id.present_image, R.id.chat_image})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_back:
                /**退出直播*/
                if (isPlay) {
                    showAlertDialog();
                } else {
                    finish();
                }
                break;
            case R.id.root_layout:
                //根布局点赞
                periscopeLayout.addHeart();
                break;
            case R.id.comment_image:
                //私信
                break;
            case R.id.present_image:
                //弹幕
                //TODO 发送消息
//                showLeftGiftVeiw(UserManager.getInstance().getUserBean().getNickname(), UserManager.getInstance().getUserBean().getHeadportrait());
                giftLayout.showLeftGiftVeiw(activity, UserManager.getInstance().getUserBean().getNickname(), UserManager.getInstance().getUserBean().getHeadportrait());
                break;
            case R.id.chat_image:
                //聊天按钮
                messageView.setShowInputView(true);
                bottomBar.setVisibility(View.INVISIBLE);
                break;
        }
    }


    /**
     * 弹出提示框
     */
    private void showAlertDialog() {
        new AlertView("是否关闭直播间？", null, null, null, new String[]{"是", "否"}, mContext, AlertView.Style.Alert, new AlertView.OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                if (position == 0) {
                    if (isPull) {
                        closeLive();
                    } else {
                        finish();
                    }
                }
            }
        }).setCancelable(true)
                .show();
    }


    /**
     * 关闭直播
     */
    private void closeLive() {
        ApiServiceUtils.closeLive(liveId, new Callback() {
            @Override
            public void onSuccuss(String data) {

            }

            @Override
            public void onLogicError(int code, String message) {
                LogUtils.log(message);
            }

            @Override
            public void onComplete() {
                finish();
            }
        });
    }

    @Override
    protected void setStatusBarColor() {
        StatusBarUtil.setColorNoTranslucent(activity, 0xff3040A0);
    }


    @Override
    public void onBackPressed() {
        if (isPlay) {
            showAlertDialog();
            return;
        }
        if (giftLayout != null) {
            giftLayout.release();
        }
        super.onBackPressed();
        if (wakeLock != null) {
            wakeLock.release();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaUtils.muteAudioFocus(mContext, true);
        if (mAutoPlayRunnable != null) {
            mAutoPlayRunnable.stop();
        }
        if (mAutoPerscopeRunnable != null) {
            mAutoPerscopeRunnable.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wakeLock != null) {
            wakeLock.acquire();
        }
        if (isMessageListInited) {
            messageView.refresh();
        }
        MediaUtils.muteAudioFocus(mContext, false);
        if (mAutoPlayRunnable != null) {
            mAutoPlayRunnable.start();
        }
        if (mAutoPerscopeRunnable != null) {
            mAutoPerscopeRunnable.start();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "liveTAG");
        wakeLock.acquire();
        mAutoPlayRunnable = new AutoPlayRunnable();
        mAutoPerscopeRunnable = new AutoPerscopeRunnable();
    }

    /**
     * 用作消息处理
     */
    private Handler mHandler = new Handler();
    /**
     * 用作视频点赞处理
     */
    private Handler mHandler2 = new Handler();

    /**
     * 消息轮询
     */
    private class AutoPlayRunnable implements Runnable {
        private int AUTO_PLAY_INTERVAL = 3000;
        private boolean mShouldAutoPlay;

        public AutoPlayRunnable() {
            mShouldAutoPlay = false;
        }

        public void start() {
            if (!mShouldAutoPlay) {
                mShouldAutoPlay = true;
                mHandler.removeCallbacks(this);
                mHandler.postDelayed(this, AUTO_PLAY_INTERVAL);
            }
        }

        public void stop() {
            if (mShouldAutoPlay) {
                mHandler.removeCallbacks(this);
                mShouldAutoPlay = false;
            }
        }

        @Override
        public void run() {
            if (mShouldAutoPlay) {
                mHandler.removeCallbacks(this);
                addMessageListener();
                mHandler.postDelayed(this, AUTO_PLAY_INTERVAL);
            }
        }
    }

    /**
     * 点赞轮询
     */
    private class AutoPerscopeRunnable implements Runnable {
        private boolean mShouldAutoPlay;

        public AutoPerscopeRunnable() {
            mShouldAutoPlay = false;
        }

        public void start() {
            if (!mShouldAutoPlay) {
                mShouldAutoPlay = true;
                mHandler2.removeCallbacks(this);
                mHandler2.postDelayed(this, new Random().nextInt(400) + 1000);
            }
        }

        public void stop() {
            if (mShouldAutoPlay) {
                mHandler2.removeCallbacks(this);
                mShouldAutoPlay = false;
            }
        }

        @Override
        public void run() {
            if (mShouldAutoPlay) {
                mHandler2.removeCallbacks(this);
                if (periscopeLayout != null && periscopeLayout.isShown()) {
                    periscopeLayout.addHeart();
                }
                mHandler2.postDelayed(this, new Random().nextInt(400) + 1000);
            }
        }
    }


    private static class AvatarAdapter extends RecyclerView.Adapter<AvatarViewHolder> {
        List<String> namelist;
        Context context;

        public AvatarAdapter(Context context, List<String> namelist) {
            this.namelist = namelist;
            this.context = context;
        }

        @Override
        public AvatarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new AvatarViewHolder(LayoutInflater.from(context).inflate(R.layout.avatar_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(AvatarViewHolder holder, int position) {
            String name = "test_avatar" + new Random().nextInt(9);
            int id = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
            //暂时使用测试数据
            Glide.with(context).load(id).placeholder(R.drawable.live_default_avatar)
                    .transform(new GlideCircleTransform(context)).into(holder.Avatar);
        }


        @Override
        public int getItemCount() {
            return namelist.size();
        }
    }

    static class AvatarViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.avatar)
        ImageView Avatar;

        public AvatarViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
