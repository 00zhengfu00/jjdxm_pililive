package com.dou361.live.ui.adapter;

import android.content.Context;

import com.dou361.baseutils.utils.UIUtils;
import com.dou361.customui.adapter.BaseNoMoreAdapter;
import com.dou361.customui.holder.BaseHolder;
import com.dou361.live.bean.MessageBean;
import com.dou361.live.ui.holder.MessageItemHolder;

import java.util.List;

public class MessageAdapter extends BaseNoMoreAdapter<MessageBean> {


    private List<MessageBean> mDatas;

    public MessageAdapter(Context mContext, List<MessageBean> list) {
        super(mContext, list);
        this.mDatas = list;
    }

    public int getSize() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public BaseHolder getItemHolder(Context context, int i) {
        return new MessageItemHolder(context, i);
    }

    public void refresh() {
        UIUtils.runInMainThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }
}