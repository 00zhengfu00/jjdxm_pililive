package com.dou361.live.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dou361.customui.ui.PullToRefreshListView;
import com.dou361.live.R;
import com.dou361.live.bean.MessageBean;
import com.dou361.live.ui.adapter.MessageAdapter;
import com.dou361.live.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
 * 创建日期：2016/7/31 8:58
 * <p/>
 * 描 述：房间聊天布局
 * <p/>
 * <p/>
 * 修订历史：
 * <p/>
 * ========================================
 */
public class RoomMessagesView extends RelativeLayout implements AdapterView.OnItemClickListener {


    MessageAdapter adapter;

    /**
     * 聊天列表
     */
    PullToRefreshListView mPullToRefreshListView;
    ListView mListView;
    /**
     * 输入面板
     */
    View sendContainer;
    /**
     * 关闭
     */
    ImageView closeView;
    /**
     * 弹幕
     */
    ImageView danmuImage;
    /**
     * 编辑框
     */
    EditText editText;
    /**
     * 发送
     */
    Button sendBtn;
    /**
     * 房间监听
     */
    MessageViewListener messageViewListener;
    /**
     * 消息列表
     */
    List<MessageBean> lists = new ArrayList<MessageBean>();
    /**
     * 是否是弹幕
     */
    public boolean isDanmuShow = false;
    /**
     * 艾特的id
     */
    private String atId = "";
    /**
     * 艾特的文本
     */
    private String atText = "";

    public RoomMessagesView(Context context) {
        super(context);
        init(context, null);
    }

    public RoomMessagesView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoomMessagesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater.from(context).inflate(R.layout.widget_room_messages, this);
        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.listview);
        sendContainer = findViewById(R.id.container_send);
        closeView = (ImageView) findViewById(R.id.close_image);
        danmuImage = (ImageView) findViewById(R.id.danmu_image);
        editText = (EditText) findViewById(R.id.edit_text);
        sendBtn = (Button) findViewById(R.id.btn_send);
    }

    public void init(String roomId) {
        adapter = new MessageAdapter(getContext(), lists);
        mListView = mPullToRefreshListView.getContentView();
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
        /**消息列表滚动监听*/
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        if (view.getFirstVisiblePosition() == 0) {
                            if (messageViewListener != null) {
                                messageViewListener.onLoadMore();
                            }
                        }
                        break;
                    case SCROLL_STATE_TOUCH_SCROLL:
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

            }

        });
        /**消息发送监听*/
        sendBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (messageViewListener != null) {
                    if (TextUtils.isEmpty(editText.getText())) {
                        Toast.makeText(getContext(), "文字内容不能为空！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String temp = editText.getText().toString();
                    temp = temp.replace("@" + atText + " ", "");
                    temp = temp.replace("@全体成员 ", "");
                    messageViewListener.onMessageSend(temp);
                    editText.setText("");
                    clearAT();
                }
            }
        });
        /**关闭输入框监听*/
        closeView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText != null) {
                    Utils.hideKeyboard(editText);
                }
                if (messageViewListener != null) {
                    messageViewListener.onHiderBottomBar();
                }
                editText.setText("");
                clearAT();
            }
        });
        /**弹幕开关*/
        danmuImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (danmuImage.isSelected()) {
                    danmuImage.setSelected(false);
                    isDanmuShow = false;
                } else {
                    danmuImage.setSelected(true);
                    isDanmuShow = true;
                }
            }
        });
        /**输入框文本输入监听*/
        editText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (!"".equals(atText)) {
                        if ((("@" + atText).equals(editText.getText().toString())) || (("@全体成员").equals(editText.getText().toString()))) {
                            editText.setText("");
                            clearAT();
                            editText.requestFocus();
                            return true;
                        }
                    }
                }
                return false;
            }
        });
        /**自动补全*/
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if ("@".equals(editText.getText().toString())) {
                    editText.setText("@全体成员 ");
                    atText = "全体成员";
                    atId = "all";
                    editText.setSelection(editText.getText().length());
                }
            }
        });

    }

    /**
     * 刷新消息列表
     */
    public synchronized void refreshMessageList(List<MessageBean> temp) {
        if (temp != null && temp.size() > 0) {
            lists.clear();
            lists.addAll(temp);
            /** 反转 */
            Collections.reverse(lists);
        }
    }

    /**
     * 加载更多消息
     */
    public synchronized void loadMoreMessageList(List<MessageBean> temp) {
        if (temp != null && temp.size() > 0) {
            /** 反转 */
            Collections.reverse(temp);
            lists.addAll(0, temp);
        }
    }

    /**
     * 发送消息
     */
    public void sendMsg(MessageBean temp) {
        if (temp != null) {
            lists.add(temp);
        }
    }

    /**
     * 显示或者隐藏输入框
     */
    public void setShowInputView(boolean showInputView) {
        if (showInputView) {
            sendContainer.setVisibility(View.VISIBLE);
        } else {
            sendContainer.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (messageViewListener != null && lists != null && lists.size() > position) {
            atId = lists.get(position).getUsername();
            atText = lists.get(position).getNickname();
            messageViewListener.onMessageOnclick(0, atId);
        }
    }

    /**
     * 设置房间监听
     */
    public void setMessageViewListener(MessageViewListener messageViewListener) {
        this.messageViewListener = messageViewListener;
    }

    /**
     * 获取消息数量
     */
    public int getMessageCount() {
        return lists.size();
    }

    /**
     * 刷新消息列表
     */
    public void refresh() {
        if (adapter != null) {
            adapter.refresh();
        }
    }

    /**
     * 定位到最新一条
     */
    public void refreshSelectLast() {
        if (adapter != null) {
            adapter.refresh();
            mListView.smoothScrollToPosition(adapter.getSize() - 1);
        }
    }

    /**
     * 获取艾特的文本
     */
    public String getATText() {
        return atText;
    }

    /**
     * 获取艾特的ID
     */
    public String getATId() {
        return atId;
    }

    /**
     * 清空内容
     */
    public void clearAT() {
        atId = "";
        atText = "";
    }

    /**
     * 艾特功能中设置艾特的人
     */
    public void setReplyer() {
        String txt = "@" + atText + " ";
//        SpannableString ss = new SpannableString(txt);
//        ss.setSpan(new ImageSpan(TextToDrawable(txt)), 0, txt.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE );
        editText.setText(txt);
        editText.setSelection(editText.getText().length());
    }

    public Drawable TextToDrawable(String s) {
        Bitmap bitmap = Bitmap.createBitmap(400, 90, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setTextSize(65);
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(Color.WHITE);
        Paint.FontMetrics fm = paint.getFontMetrics();
        canvas.drawText(s, 0, 145 + fm.top - fm.ascent, paint);
        canvas.save();
        Drawable drawableright = new BitmapDrawable(bitmap);
        drawableright.setBounds(0, 0, drawableright.getMinimumWidth(),
                drawableright.getMinimumHeight());
        return drawableright;
    }

    /**
     * 房间中监听事件
     */
    public interface MessageViewListener {

        /**
         * 消息发送监听
         */
        void onMessageSend(String content);

        /**
         * 隐藏输入面板
         */
        void onHiderBottomBar();

        /**
         * 消息点击监听
         */
        void onMessageOnclick(int id, String sid);

        /**
         * 消息加载更多
         */
        void onLoadMore();
    }

}
