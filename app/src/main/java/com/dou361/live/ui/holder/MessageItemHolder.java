package com.dou361.live.ui.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.dou361.baseutils.utils.StringUtils;
import com.dou361.baseutils.utils.UIUtils;
import com.dou361.customui.holder.BaseHolder;
import com.dou361.live.R;
import com.dou361.live.bean.MessageBean;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * ========================================
 * <p/>
 * 版 权：dou361.com 版权所有 （C） 2015
 * <p/>
 * 作 者：陈冠明
 * <p/>
 * 个人网站：http://www.dou361.com
 * <p/>
 * 版 本：1.0
 * <p/>
 * 创建日期：2016/3/9
 * <p/>
 * 描 述：主播item
 * <p/>
 * <p/>
 * 修订历史：
 * <p/>
 * ========================================
 */
public class MessageItemHolder extends BaseHolder<MessageBean> {

    private View view;
    @Bind(R.id.name)
    TextView name;
    @Bind(R.id.content)
    TextView content;
    private MessageBean mData;

    public MessageItemHolder(Context mContext, int i) {
        super(mContext, i);
    }

    @Override
    public View initView() {
        view = UIUtils.inflate(R.layout.layout_room_msgs_item);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void refreshView() {
        mData = getData();
        String aa = StringUtils.getString(mData.getReplayer());
        name.setText(mData.getNickname() + ":");
        if ("0".equals(aa)) {
            content.setText(mData.getComments());
        } else if ("all".equalsIgnoreCase(aa)) {
            content.setText("@全体成员 " + mData.getComments());
        } else {
            if ("".equals(StringUtils.getString(mData.getRepname()))) {
                content.setText(mData.getComments());
            } else {
                content.setText("@" + mData.getRepname() + " " + mData.getComments());
            }
        }
    }
}
