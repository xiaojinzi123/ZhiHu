package com.example.cxj.zhihu.modular.main.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cxj.zhihu.R;
import com.example.cxj.zhihu.config.Constant;

import xiaojinzi.android.activity.BaseActivity;
import xiaojinzi.android.activity.annotation.Injection;
import xiaojinzi.android.adapter.animation.AnimationListenerAdapter;
import xiaojinzi.android.animation.AlphaAnimationUtil;
import xiaojinzi.android.animation.TranslateAnimationUtil;
import xiaojinzi.android.util.store.SPUtil;

/**
 * 主界面的提示页面
 */
public class MainTipActivity extends BaseActivity {

    @Injection(value = R.id.rl_act_main_tip_container, click = "clickView")
    private RelativeLayout rl_container = null;

    @Injection(R.id.iv_act_main_tip_point)
    private ImageView iv = null;

    @Injection(R.id.tv_act_main_tip_text)
    private TextView tv_tip = null;

    @Override
    public int getLayoutId() {
        return R.layout.act_main_tip;
    }

    @Override
    public void initView() {
        TranslateAnimation animation = TranslateAnimationUtil.translateSelf(0f, 0.5f, 0, 0,3000);
        animation.setRepeatMode(TranslateAnimation.RESTART);
        animation.setRepeatCount(Animation.INFINITE);
        iv.startAnimation(animation);
        tv_tip.startAnimation(animation);
    }

    /**
     * 点击时间的集中处理
     *
     * @param view
     */
    public void clickView(View view) {
        AlphaAnimationUtil.fillAfter = true;
        AlphaAnimation alphaAnimation = AlphaAnimationUtil.alphaToHide();
        alphaAnimation.setAnimationListener(new AnimationListenerAdapter() {
            @Override
            public void onAnimationEnd(Animation animation) {
//                SPUtil.put(context, Constant.SP.mainAct.isShowMainTip, true);
                rl_container.setAlpha(0f);
                finish();
                iv.clearAnimation();
                tv_tip.clearAnimation();
                rl_container.clearAnimation();
            }
        });
        rl_container.startAnimation(alphaAnimation);
    }

}
