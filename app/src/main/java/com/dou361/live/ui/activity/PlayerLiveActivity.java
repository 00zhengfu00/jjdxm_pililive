package com.dou361.live.ui.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dou361.baseutils.utils.UIUtils;
import com.dou361.customui.ui.AlertView;
import com.dou361.live.R;
import com.dou361.live.bean.LiveRoom;
import com.dou361.live.ui.adapter.RoomPanlAdapter;
import com.dou361.live.ui.config.StatusConfig;
import com.dou361.live.ui.fragment.RoomPanlFragment;
import com.dou361.live.ui.fragment.TransparentFragment;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.pili.pldroid.player.widget.PLVideoView;

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
 * 描 述：直播播放页面
 * <p>
 * <p>
 * 修订历史：
 * <p>
 * ========================================
 */
public class PlayerLiveActivity extends BaseActivity implements PLMediaPlayer.OnPreparedListener, PLMediaPlayer.OnInfoListener, PLMediaPlayer.OnCompletionListener, PLMediaPlayer.OnVideoSizeChangedListener, PLMediaPlayer.OnErrorListener {

    @BindView(R.id.plv_player)
    PLVideoTextureView mVideoView;
    @BindView(R.id.loading_layout)
    RelativeLayout loadingLayout;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.cover_image)
    ImageView coverView;
    @BindView(R.id.loading_text)
    TextView loadingText;
    @BindView(R.id.vp_panl)
    ViewPager vp_panl;
    RoomPanlFragment fragment;

    @Override
    public boolean openSliding() {
        return false;
    }

    @Override
    protected void initView() {
        setContentView(R.layout.activity_player_live);
        LiveRoom liveRoom = (LiveRoom) getIntent().getSerializableExtra(StatusConfig.LiveRoom);

        int coverRes = liveRoom.getCover();
        coverView.setImageResource(coverRes);

        RoomPanlAdapter adapter = new RoomPanlAdapter(getSupportFragmentManager());
        adapter.addFragment(new TransparentFragment());
        fragment = new RoomPanlFragment();
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(StatusConfig.LiveRoom, liveRoom);
        mBundle.putInt(StatusConfig.ROOM_STYLE, StatusConfig.ROOM_STYLE_PLAYER);
        fragment.setArguments(mBundle);
        adapter.addFragment(fragment);
        vp_panl.setAdapter(adapter);
        vp_panl.setCurrentItem(adapter.getCount() - 1);


        mVideoView.setBufferingIndicator(loadingLayout);
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnVideoSizeChangedListener(this);
        mVideoView.setOnErrorListener(this);


        mVideoView.setDisplayAspectRatio(PLVideoView.ASPECT_RATIO_PAVED_PARENT);
        mVideoView.setMirror(true);
        //真实情况下使用注释的方式
//        mVideoView.setVideoPath(SystemConfig.ucloud_player_url + liveId);
        //临时固定地址使用
        mVideoView.setVideoPath("http://23340.live-vod.cdn.aodianyun.com/m3u8/0x0/merge/02235e76590a08db99d2296f8632c5cf.m3u8");
        mVideoView.start();

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (fragment != null) {
            fragment.onResume();
        }
        if (mVideoView != null) {
            mVideoView.start();
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
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }

    @Override
    public boolean onError(PLMediaPlayer plMediaPlayer, int errorCode) {
        switch (errorCode) {
            case PLMediaPlayer.ERROR_CODE_INVALID_URI:
                UIUtils.showToastCenterShort("Invalid URL !");
                break;
            case PLMediaPlayer.ERROR_CODE_404_NOT_FOUND:
                UIUtils.showToastCenterShort("404 resource not found !");
                break;
            case PLMediaPlayer.ERROR_CODE_CONNECTION_REFUSED:
                UIUtils.showToastCenterShort("Connection refused !");
                break;
            case PLMediaPlayer.ERROR_CODE_CONNECTION_TIMEOUT:
                UIUtils.showToastCenterShort("Connection timeout !");
                break;
            case PLMediaPlayer.ERROR_CODE_EMPTY_PLAYLIST:
                UIUtils.showToastCenterShort("Empty playlist !");
                break;
            case PLMediaPlayer.ERROR_CODE_STREAM_DISCONNECTED:
                UIUtils.showToastCenterShort("Stream disconnected !");
                break;
            case PLMediaPlayer.ERROR_CODE_IO_ERROR:
                UIUtils.showToastCenterShort("主播尚未开播!");
                loadingText.setText("主播尚未开播");
                progressBar.setVisibility(View.INVISIBLE);
                break;
            case PLMediaPlayer.ERROR_CODE_UNAUTHORIZED:
                UIUtils.showToastCenterShort("Unauthorized Error !");
                break;
            case PLMediaPlayer.ERROR_CODE_PREPARE_TIMEOUT:
                UIUtils.showToastCenterShort("Prepare timeout !");
                break;
            case PLMediaPlayer.ERROR_CODE_READ_FRAME_TIMEOUT:
                UIUtils.showToastCenterShort("Read frame timeout !");
                break;
            case PLMediaPlayer.MEDIA_ERROR_UNKNOWN:
            default:
                UIUtils.showToastCenterShort("unknown error !");
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
        UIUtils.showToastCenterShort("Play Completed !");
        finish();
    }

    @Override
    public void onVideoSizeChanged(PLMediaPlayer plMediaPlayer, int i, int i1) {

    }

    @OnClick({R.id.btn_close})
    public void onClick(View v) {
        if (v.getId() == R.id.btn_close) {
            showAlertDialog();
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
                    onBackPressed();
                }
            }
        }).setCancelable(true)
                .show();
    }


}
