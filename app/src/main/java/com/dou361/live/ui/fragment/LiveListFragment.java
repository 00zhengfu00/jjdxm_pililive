package com.dou361.live.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.dou361.baseutils.utils.UIUtils;
import com.dou361.customui.ui.PullToRefreshGridView;
import com.dou361.customui.ui.PullToRefreshView;
import com.dou361.live.R;
import com.dou361.live.bean.LiveRoom;
import com.dou361.live.module.ApiServiceUtils;
import com.dou361.live.ui.adapter.AnchorAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class LiveListFragment extends Fragment implements PullToRefreshView.OnHeaderRefreshListener, PullToRefreshView.OnFooterRefreshListener {

    @Bind(R.id.plv_content)
    PullToRefreshGridView mPullToRefreshGridView;
    @Bind(R.id.ll_empty)
    LinearLayout ll_empty;
    GridView mGridView;
    private List<LiveRoom> list = new ArrayList<LiveRoom>();
    private AnchorAdapter adapter;
    private int loadIndex = 1;
    private Context mContext;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.mContext = getActivity();
        View view = UIUtils.inflate(R.layout.fragment_live_list);
        ButterKnife.bind(this, view);
        mPullToRefreshGridView.setPullDownDamp(true);
        mPullToRefreshGridView.setPullUpDamp(true);
        mPullToRefreshGridView.setOnHeaderRefreshListener(this);
        mPullToRefreshGridView.setOnFooterRefreshListener(this);
        adapter = new AnchorAdapter(mContext, list);
        mGridView = mPullToRefreshGridView.getContentView();
        mGridView.setNumColumns(2);
        mGridView.setAdapter(adapter);
        mPullToRefreshGridView.headerRefreshing();
        return view;
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        mPullToRefreshGridView.postDelayed(new Runnable() {

            @Override
            public void run() {
                Observable
                        .create(new Observable.OnSubscribe<List<LiveRoom>>() {
                            @Override
                            public void call(Subscriber<? super List<LiveRoom>> subscriber) {
                                loadIndex = 1;
                                List<LiveRoom> temp = ApiServiceUtils.getLiveList(loadIndex);
                                subscriber.onNext(temp);
                            }
                        })
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<List<LiveRoom>>() {
                            @Override
                            public void call(final List<LiveRoom> listTemp) {
                                adapter.setData(listTemp);
                                adapter.notifyDataSetChanged();
                                if (adapter.getSize() > 0) {
                                    ll_empty.setVisibility(View.GONE);
                                } else {
                                    ll_empty.setVisibility(View.VISIBLE);
                                }
                                mPullToRefreshGridView.onHeaderRefreshCompleteAndTime();
                            }
                        });
            }
        }, mPullToRefreshGridView.delay_DURATION);
    }

    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        mPullToRefreshGridView.postDelayed(new Runnable() {

            @Override
            public void run() {
                Observable
                        .create(new Observable.OnSubscribe<List<LiveRoom>>() {
                            @Override
                            public void call(Subscriber<? super List<LiveRoom>> subscriber) {
                                loadIndex = list.size() + 1;
                                List<LiveRoom> temp = ApiServiceUtils.getLiveList(loadIndex);
                                subscriber.onNext(temp);
                            }
                        })
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<List<LiveRoom>>() {
                            @Override
                            public void call(final List<LiveRoom> listTemp) {
                                adapter.addDatas(listTemp);
                                adapter.notifyDataSetChanged();
                                if (adapter.getSize() > 0) {
                                    ll_empty.setVisibility(View.GONE);
                                } else {
                                    ll_empty.setVisibility(View.VISIBLE);
                                }
                                mPullToRefreshGridView.onFooterRefreshComplete();
                            }
                        });
            }
        }, mPullToRefreshGridView.delay_DURATION);
    }

}
