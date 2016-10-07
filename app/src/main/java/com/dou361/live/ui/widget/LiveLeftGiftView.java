package com.dou361.live.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dou361.live.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by wei on 2016/6/7.
 */
@RemoteViews.RemoteView
public class LiveLeftGiftView extends RelativeLayout {
    @Bind(R.id.avatar)
    ImageView avatar;
    @Bind(R.id.name)
    TextView name;
    @Bind(R.id.gift_image)
    ImageView giftImage;

    public LiveLeftGiftView(Context context) {
        super(context);
        init(context, null);
    }

    public LiveLeftGiftView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);

    }

    public LiveLeftGiftView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.widget_left_gift, this);
        ButterKnife.bind(this);
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    public void setAvatar(String avatar) {
        if (avatar == null) {
            return;
        }
        try {
            Glide.with(getContext()).load(avatar)
                    .transform(new GlideCircleTransform(getContext())).into(this.avatar);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ImageView getGiftImageView() {
        return giftImage;
    }
}
