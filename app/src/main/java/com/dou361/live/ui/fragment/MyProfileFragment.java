package com.dou361.live.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.dou361.live.R;
import com.dou361.live.ui.activity.LoginActivity;
import com.dou361.live.utils.UserManager;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyProfileFragment extends Fragment {

    @Bind(R.id.btn_logout)
    Button btnLogout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btn_logout)
    public void onClick() {

        UserManager.getInstance().setLogin(false);
        getActivity().finish();
        startActivity(new Intent(getActivity(), LoginActivity.class));
    }
}
