package com.dou361.live.ui.activity;

import android.view.View;

import com.dou361.live.R;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.pili.pldroid.player.widget.PLVideoView;

public class PlayLiveActivity extends BaseLiveActivity implements PLMediaPlayer.OnErrorListener, PLMediaPlayer.OnPreparedListener, PLMediaPlayer.OnInfoListener, PLMediaPlayer.OnCompletionListener, PLMediaPlayer.OnVideoSizeChangedListener {


    private PLVideoTextureView mVideoView;
    private String liveUrl;

    @Override
    protected void initView() {
        setContentView(R.layout.activity_play_live);

//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

        mVideoView = (PLVideoTextureView) findViewById(R.id.PLVideoTextureView);

        liveId = getIntent().getIntExtra("liveId", 0);
        roomId = getIntent().getStringExtra("roomId");
        liveUrl = getIntent().getStringExtra("liveUrl");

        View loadingView = findViewById(R.id.LoadingView);
        mVideoView.setBufferingIndicator(loadingView);
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnVideoSizeChangedListener(this);
        mVideoView.setOnErrorListener(this);

        mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);
        mVideoView.setMirror(true);
        mVideoView.setVideoPath(liveUrl);
        mVideoView.start();
        isPlay = true;
        onMessageListInit();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView != null) {
            mVideoView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView != null) {
            mVideoView.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }


    @Override
    public boolean onError(PLMediaPlayer plMediaPlayer, int errorCode) {
        switch (errorCode) {
            case PLMediaPlayer.ERROR_CODE_INVALID_URI:
                showToast("Invalid URL !");
                break;
            case PLMediaPlayer.ERROR_CODE_404_NOT_FOUND:
                showToast("404 resource not found !");
                break;
            case PLMediaPlayer.ERROR_CODE_CONNECTION_REFUSED:
                showToast("Connection refused !");
                break;
            case PLMediaPlayer.ERROR_CODE_CONNECTION_TIMEOUT:
                showToast("Connection timeout !");
                break;
            case PLMediaPlayer.ERROR_CODE_EMPTY_PLAYLIST:
                showToast("Empty playlist !");
                break;
            case PLMediaPlayer.ERROR_CODE_STREAM_DISCONNECTED:
                showToast("Stream disconnected !");
                break;
            case PLMediaPlayer.ERROR_CODE_IO_ERROR:
                showToast("Network IO Error !");
                break;
            case PLMediaPlayer.ERROR_CODE_UNAUTHORIZED:
                showToast("Unauthorized Error !");
                break;
            case PLMediaPlayer.ERROR_CODE_PREPARE_TIMEOUT:
                showToast("Prepare timeout !");
                break;
            case PLMediaPlayer.ERROR_CODE_READ_FRAME_TIMEOUT:
                showToast("Read frame timeout !");
                break;
            case PLMediaPlayer.MEDIA_ERROR_UNKNOWN:
            default:
                showToast("unknown error !");
                break;
        }
        // Todo pls handle the error status here, retry or call finish()
//        finish();
        // If you want to retry, do like this:
        // mVideoView.setVideoPath(mVideoPath);
        // mVideoView.start();
        // Return true means the error has been handled
        // If return false, then `onCompletion` will be called
        return true;
    }

    @Override
    public void onPrepared(PLMediaPlayer plMediaPlayer) {

    }

    @Override
    public boolean onInfo(PLMediaPlayer plMediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onCompletion(PLMediaPlayer plMediaPlayer) {
        showToast("Play Completed !");
        finish();
    }

    @Override
    public void onVideoSizeChanged(PLMediaPlayer plMediaPlayer, int i, int i1) {

    }
}
