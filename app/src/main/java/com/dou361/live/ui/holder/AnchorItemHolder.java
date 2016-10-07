package com.dou361.live.ui.holder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dou361.baseutils.utils.UIUtils;
import com.dou361.customui.holder.BaseHolder;
import com.dou361.live.R;
import com.dou361.live.bean.LiveRoom;
import com.dou361.live.ui.activity.PlayLiveActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
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
 * 创建日期：2016/3/9
 * <p>
 * 描 述：主播item
 * <p>
 * <p>
 * 修订历史：
 * <p>
 * ========================================
 */
public class AnchorItemHolder extends BaseHolder<LiveRoom> {
    private View view;
    @Bind(R.id.photo)
    ImageView imageView;
    @Bind(R.id.author)
    TextView anchor;
    @Bind(R.id.audience_num)
    TextView audienceNum;
    private LiveRoom mData;

    public AnchorItemHolder(Context mContext, int i) {
        super(mContext, i);
    }

    @Override
    public View initView() {
        view = UIUtils.inflate(R.layout.layout_livelist_item);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void refreshView() {
        mData = getData();
        anchor.setText(mData.getNickname());
        audienceNum.setText(mData.getAudienceNum() + "人");
        Glide.with(mContext)
                .load(mData.getPicture())
                .placeholder(R.color.placeholder)
                .into(imageView);
    }


    @OnClick(R.id.ll_item)
    public void onClick() {
        mContext.startActivity(new Intent(mContext, PlayLiveActivity.class)
                .putExtra("liveId", mData.getDirid())
                .putExtra("liveUrl", mData.getPlayurl())
                .putExtra("roomId", mData.getId()));
    }
}
