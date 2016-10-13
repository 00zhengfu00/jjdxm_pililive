package com.dou361.live.ui.activity;

import android.content.Context;
import android.hardware.Camera;
import android.media.AudioManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dou361.baseutils.utils.UIUtils;
import com.dou361.customui.ui.AlertView;
import com.dou361.live.R;
import com.dou361.live.bean.LiveRoom;
import com.dou361.live.module.TestRoomLiveRepository;
import com.dou361.live.ui.adapter.RoomPanlAdapter;
import com.dou361.live.ui.config.StatusConfig;
import com.dou361.live.ui.fragment.RoomPanlFragment;
import com.dou361.live.ui.fragment.TransparentFragment;
import com.dou361.live.ui.listener.OnLiveListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseImageView;
import com.pili.pldroid.streaming.CameraStreamingManager;
import com.pili.pldroid.streaming.CameraStreamingSetting;
import com.pili.pldroid.streaming.StreamingProfile;
import com.pili.pldroid.streaming.widget.AspectFrameLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

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
 * 创建日期：2016/10/4 14:17
 * <p>
 * 描 述：开始直播
 * <p>
 * <p>
 * 修订历史：
 * <p>
 * ========================================
 */
public class StartLiveActivity extends BaseActivity implements CameraStreamingManager.StreamingStateListener {

    @BindView(R.id.cameraPreview_afl)
    AspectFrameLayout afl;
    @BindView(R.id.cameraPreview_surfaceView)
    GLSurfaceView cameraPreviewFrameView;
    @BindView(R.id.start_container)
    RelativeLayout startContainer;
    @BindView(R.id.countdown_txtv)
    TextView countdownView;
    @BindView(R.id.tv_stop_username)
    TextView tv_stop_username;
    @BindView(R.id.eiv_stop_avatar)
    EaseImageView eiv_stop_avatar;
    @BindView(R.id.finish_frame)
    View liveEndLayout;
    @BindView(R.id.cover_image)
    ImageView coverImage;
    @BindView(R.id.vp_panl)
    ViewPager vp_panl;

    public static final int MSG_UPDATE_COUNTDOWN = 1;

    public static final int COUNTDOWN_DELAY = 1000;

    public static final int COUNTDOWN_START_INDEX = 3;
    public static final int COUNTDOWN_END_INDEX = 1;
    protected boolean isShutDownCountdown = false;

    boolean isStarted;
    RoomPanlFragment fragment;

    private String liveId;
    private String roomId;
    private String anchorId;
    private StreamingProfile mProfile;
    private CameraStreamingManager mCameraStreamingManager;
    private JSONObject mJSONObject;
    private CameraStreamingSetting setting;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_COUNTDOWN:
                    handleUpdateCountdown(msg.arg1);
                    break;
            }
        }
    };

    @Override
    public boolean openSliding() {
        return false;
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_start_live);

        RoomPanlAdapter adapter = new RoomPanlAdapter(getSupportFragmentManager());
        adapter.addFragment(new TransparentFragment());
        fragment = new RoomPanlFragment();
        Bundle mBundle = new Bundle();
        LiveRoom liveRoom = new LiveRoom();
        liveId = TestRoomLiveRepository.getLiveRoomId(EMClient.getInstance().getCurrentUser());
        roomId = TestRoomLiveRepository.getChatRoomId(EMClient.getInstance().getCurrentUser());
        anchorId = EMClient.getInstance().getCurrentUser();
        liveRoom.setId(liveId);
        liveRoom.setChatroomId(roomId);
        liveRoom.setAnchorId(anchorId);
        liveRoom.setAvatar(R.mipmap.live_avatar_girl09);
        mBundle.putSerializable(StatusConfig.LiveRoom, liveRoom);
        mBundle.putInt(StatusConfig.ROOM_STYLE, StatusConfig.ROOM_STYLE_LIVE);
        fragment.setArguments(mBundle);
        adapter.addFragment(fragment);
        vp_panl.setAdapter(adapter);
        vp_panl.setCurrentItem(adapter.getCount() - 1);
        fragment.setOnLiveListener(new OnLiveListener() {
            @Override
            public void onCamreClick(View view) {
                /**
                 * 切换摄像头
                 */
                mCameraStreamingManager.switchCamera();
            }

            @Override
            public void onLightClick(View view) {
                /**
                 * 打开或关闭闪关灯
                 */
                boolean succeed = mCameraStreamingManager.turnLightOff();
                if (succeed) {
                    view.setSelected(!view.isSelected());
                }
            }

            @Override
            public void onVoiceClick(View view) {
                /**
                 * 打开或关闭声音
                 */
                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                if (audioManager.isMicrophoneMute()) {
                    audioManager.setMicrophoneMute(false);
                    view.setSelected(false);
                } else {
                    audioManager.setMicrophoneMute(true);
                    view.setSelected(true);
                }
            }
        });
        initEnv();
    }

    public void initEnv() {
        StreamingProfile.AudioProfile aProfile = new StreamingProfile.AudioProfile(44100, 96 * 1024);
        StreamingProfile.VideoProfile vProfile = new StreamingProfile.VideoProfile(30, 1000 * 1024, 48);
        StreamingProfile.AVProfile avProfile = new StreamingProfile.AVProfile(vProfile, aProfile);

        mProfile = new StreamingProfile();
        mProfile.setVideoQuality(StreamingProfile.VIDEO_QUALITY_HIGH1)
                .setAudioQuality(StreamingProfile.AUDIO_QUALITY_MEDIUM2)
                .setEncodingSizeLevel(StreamingProfile.VIDEO_ENCODING_HEIGHT_480)
                .setEncoderRCMode(StreamingProfile.EncoderRCModes.QUALITY_PRIORITY)
                .setAVProfile(avProfile)
                .setStreamStatusConfig(new StreamingProfile.StreamStatusConfig(3))
                .setSendingBufferProfile(new StreamingProfile.SendingBufferProfile(0.2f, 0.8f, 3.0f, 20 * 1000));
        setting = new CameraStreamingSetting();
        setting.setContinuousFocusModeEnabled(true)
                .setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT) // 前置摄像头
                .setContinuousFocusModeEnabled(true) // 自动对焦
                .setFocusMode(CameraStreamingSetting.FOCUS_MODE_CONTINUOUS_VIDEO)//对焦频率
                .setResetTouchFocusDelayInMs(3000)//手动对焦后恢复自动对焦时间
                .setCameraPrvSizeLevel(CameraStreamingSetting.PREVIEW_SIZE_LEVEL.MEDIUM)
                .setCameraPrvSizeRatio(CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_16_9);
        afl.setShowMode(AspectFrameLayout.SHOW_MODE.FULL);
        mCameraStreamingManager = new CameraStreamingManager(mContext, afl, cameraPreviewFrameView, CameraStreamingManager.EncodingType.SW_VIDEO_WITH_SW_AUDIO_CODEC);  // soft codec
        mCameraStreamingManager.prepare(setting, mProfile);
        mCameraStreamingManager.setStreamingStateListener(this);
    }

    @Override
    public void onStateChanged(int state, Object info) {
        switch (state) {
            case CameraStreamingManager.STATE.PREPARING:

                break;
            case CameraStreamingManager.STATE.READY:
                // start streaming when READY
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (mCameraStreamingManager != null) {
                            mCameraStreamingManager.startStreaming();
                        }
                    }
                }).start();
                if (fragment != null) {
                    fragment.addPeriscope();
                }
                break;
            case CameraStreamingManager.STATE.CONNECTING:
                break;
            case CameraStreamingManager.STATE.STREAMING:
                // The av packet had been sent.
                break;
            case CameraStreamingManager.STATE.SHUTDOWN:
                // The streaming had been finished.
                break;
            case CameraStreamingManager.STATE.IOERROR:
                // Network connect error.
                break;
            case CameraStreamingManager.STATE.SENDING_BUFFER_EMPTY:
                break;
            case CameraStreamingManager.STATE.SENDING_BUFFER_FULL:
                break;
            case CameraStreamingManager.STATE.AUDIO_RECORDING_FAIL:
                // Failed to record audio.
                break;
            case CameraStreamingManager.STATE.OPEN_CAMERA_FAIL:
                // Failed to open camera.
                break;
            case CameraStreamingManager.STATE.DISCONNECTED:
                // The socket is broken while streaming
                break;
        }
    }

    @Override
    public boolean onStateHandled(int i, Object o) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mCameraStreamingManager != null) {
            mCameraStreamingManager.stopStreaming();
        }
        super.onBackPressed();
    }

    /**
     * 弹出提示框
     */
    private void showAlertDialog() {
        new AlertView("是否关闭直播间？", null, null, null, new String[]{"是", "否"}, mContext, AlertView.Style.Alert, new AlertView.OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                if (position == 0) {
                    /**
                     * 关闭直播显示直播成果
                     */
                    if (mCameraStreamingManager != null) {
                        mCameraStreamingManager.stopStreaming();
                    }
                    if (!isStarted) {
                        onBackPressed();
                        return;
                    }
                    showConfirmCloseLayout();
                }
            }
        }).setCancelable(true)
                .show();
    }

    @OnClick({R.id.live_close_confirm, R.id.btn_start, R.id.btn_close})
    public void onLiveClick(View v) {
        switch (v.getId()) {
            case R.id.live_close_confirm:
                onBackPressed();
                break;
            case R.id.btn_start:
                /**
                 * 开始直播
                 */
                if (liveId == null) {
                    new EaseAlertDialog(this, "只有存在的liveId才能开启直播").show();
                    return;
                }
                startContainer.setVisibility(View.INVISIBLE);
                //Utils.hideKeyboard(titleEdit);
                new Thread() {
                    public void run() {
                        int i = COUNTDOWN_START_INDEX;
                        do {
                            Message msg = Message.obtain();
                            msg.what = MSG_UPDATE_COUNTDOWN;
                            msg.arg1 = i;
                            handler.sendMessage(msg);
                            i--;
                            try {
                                Thread.sleep(COUNTDOWN_DELAY);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } while (i >= COUNTDOWN_END_INDEX);
                    }
                }.start();
                break;
            case R.id.btn_close:
                showAlertDialog();
                break;
        }
    }

    private void showConfirmCloseLayout() {
        //显示封面
        coverImage.setVisibility(View.VISIBLE);
        liveEndLayout.setVisibility(View.VISIBLE);
        List<LiveRoom> liveRoomList = TestRoomLiveRepository.getLiveRoomList();
        for (LiveRoom liveRoom : liveRoomList) {
            if (liveRoom.getId().equals(liveId)) {
                coverImage.setImageResource(liveRoom.getCover());
                Glide.with(mContext)
                        .load(liveRoom.getCover())
                        .placeholder(R.color.placeholder)
                        .into(eiv_stop_avatar);
            }
        }
        tv_stop_username.setText(EMClient.getInstance().getCurrentUser());
    }

    public void handleUpdateCountdown(final int count) {
        if (countdownView != null) {
            countdownView.setVisibility(View.VISIBLE);
            countdownView.setText(String.format("%d", count));
            ScaleAnimation scaleAnimation =
                    new ScaleAnimation(1.0f, 0f, 1.0f, 0f, Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(COUNTDOWN_DELAY);
            scaleAnimation.setFillAfter(false);
            scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    countdownView.setVisibility(View.GONE);
                    fragment.joinChatRoom();
                    if (count == COUNTDOWN_END_INDEX && mCameraStreamingManager != null && !isShutDownCountdown) {
                        try {
                            UIUtils.showToastCenterShort("直播开始！");
                            mJSONObject = new JSONObject(liveId);
                            StreamingProfile.Stream stream = new StreamingProfile.Stream(mJSONObject);
                            mProfile.setStream(stream);  // You can invoke this before startStreaming, but not in initialization phase.
                            mCameraStreamingManager.setStreamingProfile(mProfile);
                            //这里没有打开只是简单测试功能没有去推流的
                            mCameraStreamingManager.startStreaming();
                            isStarted = true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            UIUtils.showToastCenterShort("推流地址解析失败！");
                        }
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            if (!isShutDownCountdown) {
                countdownView.startAnimation(scaleAnimation);
            } else {
                countdownView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCameraStreamingManager != null) {
            mCameraStreamingManager.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCameraStreamingManager != null) {
            mCameraStreamingManager.resume();
        }
        if (fragment != null) {
            fragment.onResume();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (fragment != null) {
            fragment.onStop();
        }
    }

    @Override
    protected void onDestroy() {
        if (fragment != null) {
            fragment.destroy();
        }
        super.onDestroy();
        if (mCameraStreamingManager != null) {
            mCameraStreamingManager.destroy();
        }
    }
}
