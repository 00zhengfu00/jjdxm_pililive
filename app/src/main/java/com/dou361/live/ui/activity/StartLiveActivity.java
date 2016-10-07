package com.dou361.live.ui.activity;

import android.app.Activity;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.dou361.baseutils.utils.StringUtils;
import com.dou361.baseutils.utils.UIUtils;
import com.dou361.customui.ui.LoadingTieDialog;
import com.dou361.live.R;
import com.dou361.live.bean.LiveId;
import com.dou361.live.bean.LiveRoom;
import com.dou361.live.module.ApiServiceUtils;
import com.dou361.live.module.Callback;
import com.dou361.live.utils.UserManager;
import com.dou361.live.utils.Utils;
import com.pili.pldroid.streaming.CameraStreamingManager;
import com.pili.pldroid.streaming.CameraStreamingSetting;
import com.pili.pldroid.streaming.StreamingProfile;
import com.pili.pldroid.streaming.widget.AspectFrameLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import butterknife.Bind;
import butterknife.OnClick;

public class StartLiveActivity extends BaseLiveActivity implements CameraStreamingManager.StreamingStateListener {

    private static final String TAG = StartLiveActivity.class.getSimpleName();
    @Bind(R.id.start_container)
    RelativeLayout startContainer;
    @Bind(R.id.ll_back)
    LinearLayout llBack;
    @Bind(R.id.ll_sw)
    LinearLayout llSw;
    @Bind(R.id.et_title)
    EditText titleEdit;
    @Bind(R.id.countdown_txtv)
    TextView countdownView;
    @Bind(R.id.cameraPreview_afl)
    AspectFrameLayout afl;
    @Bind(R.id.cameraPreview_surfaceView)
    GLSurfaceView cameraPreviewFrameView;

    public static final int MSG_UPDATE_COUNTDOWN = 1;
    public static final int COUNTDOWN_DELAY = 1000;
    public static final int COUNTDOWN_START_INDEX = 3;
    public static final int COUNTDOWN_END_INDEX = 1;
    protected boolean isShutDownCountdown = false;

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
    private CameraStreamingManager mCameraStreamingManager;
    private JSONObject mJSONObject;
    private StreamingProfile mProfile;
    private LiveRoom mLiveRoom;
    private CameraStreamingSetting setting;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_start_live);
        mLiveRoom = new LiveRoom();
        mLiveRoom.setNickname(UserManager.getInstance().getUserBean().getNickname());
        mLiveRoom.setUsername(UserManager.getInstance().getUserBean().getUsername());
        llSw.setVisibility(View.VISIBLE);
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
        mCameraStreamingManager = new CameraStreamingManager(StartLiveActivity.this, afl, cameraPreviewFrameView, CameraStreamingManager.EncodingType.SW_VIDEO_WITH_SW_AUDIO_CODEC);  // soft codec
        mCameraStreamingManager.prepare(setting, mProfile);
        mCameraStreamingManager.setStreamingStateListener(StartLiveActivity.this);
    }

    @OnClick({R.id.ll_sw, R.id.btn_start})
    public void onClisck(View v) {
        switch (v.getId()) {
            case R.id.ll_sw:
                /**切换摄像头*/
                if (mCameraStreamingManager != null) {
                    mCameraStreamingManager.switchCamera();
                }
                break;
            case R.id.btn_start:
                /**开始直播*/
                if (TextUtils.isEmpty(titleEdit.getText())) {
                    Toast.makeText(this, "直播标题不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                Utils.hideKeyboard(titleEdit);
                startLive();
                break;
        }
    }

    private void startLive() {
        ApiServiceUtils.startLive(titleEdit.getText().toString(), new Callback() {
            @Override
            public void onSuccuss(String data) {
                LiveId mLiveId = JSON.parseObject(data, LiveId.class);
                if (mLiveId != null && mLiveId.getId() != null) {
                    String[] arr = mLiveId.getId().split("_");
                    if (arr != null && arr.length == 2) {
                        try {
                            int id = Integer.valueOf(arr[1]);
                            roomId = "hgf_" + id;
                            mLiveRoom.setDirid(id);
                            mLiveRoom.setHlsip(data);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (mLiveRoom == null || mLiveRoom.getHlsip() == null) {
                    UIUtils.showToastLong("创建直播失败！");
                } else {
                    isPlay = true;
                    liveId = mLiveRoom.getDirid();
                    startContainer.setVisibility(View.INVISIBLE);
                    Utils.hideKeyboard(titleEdit);
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
                }
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

    public void handleUpdateCountdown(final int count) {
        if (countdownView != null) {
            countdownView.setVisibility(View.VISIBLE);
            countdownView.setText(String.format("%d", count));
            ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0f, 1.0f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(COUNTDOWN_DELAY);
            scaleAnimation.setFillAfter(false);
            scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    countdownView.setVisibility(View.GONE);
                    if (count == COUNTDOWN_END_INDEX && mCameraStreamingManager != null && !isShutDownCountdown) {
                        try {
                            showToast("直播开始！");
                            mJSONObject = new JSONObject(mLiveRoom.getHlsip());
                            StreamingProfile.Stream stream = new StreamingProfile.Stream(mJSONObject);
                            mProfile.setStream(stream);  // You can invoke this before startStreaming, but not in initialization phase.
                            mCameraStreamingManager.setStreamingProfile(mProfile);
                            mCameraStreamingManager.startStreaming();
                            isPull = true;
                            onMessageListInit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            showToast("推流地址解析失败！");
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
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraStreamingManager != null) {
            mCameraStreamingManager.destroy();
        }
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
}
