package com.dou361.live.ui.adapter;

import android.content.Context;

import com.dou361.customui.adapter.BaseNoMoreAdapter;
import com.dou361.customui.holder.BaseHolder;
import com.dou361.live.bean.LiveRoom;
import com.dou361.live.ui.holder.AnchorItemHolder;

import java.util.List;

public class AnchorAdapter extends BaseNoMoreAdapter<LiveRoom> {


    private List<LiveRoom> mDatas;

    public AnchorAdapter(Context mContext, List<LiveRoom> list) {
        super(mContext, list);
        this.mDatas = list;
    }

    public int getSize() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public BaseHolder getItemHolder(Context context, int i) {
        return new AnchorItemHolder(context, i);
    }

    public void setData(List<LiveRoom> datas) {
        mDatas.clear();
        if (datas != null && datas.size() > 0) {
            mDatas.addAll(datas);
        }
    }

    public void addDatas(List<LiveRoom> datas) {
        if (datas != null && datas.size() > 0) {
            mDatas.addAll(datas);
        }
    }
}