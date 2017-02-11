package com.example.cxj.zhihu.modular.skin.fragment;

import android.annotation.SuppressLint;

import com.example.cxj.zhihu.R;
import com.example.cxj.zhihu.modular.skin.entity.JsonSkin;

import xiaojinzi.android.activity.annotation.Injection;
import xiaojinzi.android.fragment.BaseFragment;
import xiaojinzi.android.view.nineImage.NineImageView;

/**
 * Created by cxj on 2016/3/26.
 */
@SuppressLint("ValidFragment")
public class OnlineSkinFragment extends BaseFragment {

    /**
     * 要显示的数据
     */
    private JsonSkin jsonSkin = null;

    public OnlineSkinFragment(JsonSkin jsonSkin) {
        this.jsonSkin = jsonSkin;
    }

    @Injection(R.id.nv_frag_online_skin)
    private NineImageView nineImageView = null;

    @Override
    public int getLayoutId() {
        return R.layout.frag_online_skin;
    }


    @Override
    public void initView() {
    }

    @Override
    public void initData() {
        nineImageView.addImageUrl(jsonSkin.getImageUrl(),R.mipmap.loading);
    }

    @Override
    public void setOnlistener() {
    }

    @Override
    public void freshUI() {
    }
}
