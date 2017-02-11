package com.example.cxj.zhihu.modular.detail.ui;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.example.cxj.zhihu.R;
import com.example.cxj.zhihu.config.Constant;

import xiaojinzi.android.activity.BaseActivity;
import xiaojinzi.android.activity.annotation.Injection;
import xiaojinzi.android.adapter.animation.AnimationListenerAdapter;
import xiaojinzi.android.animation.AlphaAnimationUtil;
import xiaojinzi.android.util.store.SPUtil;

public class DetailTipActivity extends BaseActivity {

    @Injection(value = R.id.rl_act_detail_tip_container,click = "clickView")
    private RelativeLayout rl_container = null;

    @Override
    public int getLayoutId() {
        return R.layout.act_detail_tip;
    }

    public void clickView(View view) {
        AlphaAnimationUtil.fillAfter = true;
        AlphaAnimation alphaAnimation = AlphaAnimationUtil.alphaToHide();
        alphaAnimation.setAnimationListener(new AnimationListenerAdapter(){
            @Override
            public void onAnimationEnd(Animation animation) {
                SPUtil.put(context, Constant.SP.detailAct.isShowDetailTip,true);
                rl_container.setAlpha(0f);
                rl_container.clearAnimation();
                finish();
            }
        });
        rl_container.startAnimation(alphaAnimation);
    }
}
