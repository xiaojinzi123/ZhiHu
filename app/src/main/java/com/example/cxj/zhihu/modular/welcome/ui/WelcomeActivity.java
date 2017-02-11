package com.example.cxj.zhihu.modular.welcome.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.example.cxj.zhihu.MyApp;
import com.example.cxj.zhihu.R;
import com.example.cxj.zhihu.config.Constant;
import com.example.cxj.zhihu.modular.main.ui.MainActivity;
import com.example.cxj.zhihu.modular.welcome.entity.StartImage;

import xiaojinzi.android.activity.BaseActivity;
import xiaojinzi.android.activity.annotation.Injection;
import xiaojinzi.android.adapter.animation.AnimationListenerAdapter;
import xiaojinzi.android.animation.AlphaAnimationUtil;
import xiaojinzi.android.animation.ScaleAnimationUtil;
import xiaojinzi.android.image.ImageLoad;
import xiaojinzi.android.json.JsonUtil;
import xiaojinzi.android.net.adapter.BaseDataHandlerAdapter;
import xiaojinzi.android.util.activity.ActivityUtil;
import xiaojinzi.android.util.image.ImageUtil;
import xiaojinzi.android.util.log.L;
import xiaojinzi.android.util.os.T;

/**
 * 欢迎的界面
 */
public class WelcomeActivity extends BaseActivity {

    /**
     * 类的标识
     */
    private String tag = "WelcomeActivity";

    @Injection(R.id.iv_act_welcome)
    private ImageView iv_startImage = null;

    @Override
    public int getLayoutId() {
        return R.layout.act_welcome;
    }

    @Override
    public void initView() {
        super.initView();
    }

    @Override
    public void initData() {

        //获取启动图片的信息
        MyApp.ah.getWithoutJsonCache(Constant.Url.WelcomeAct.startImage, new BaseDataHandlerAdapter() {
            @Override
            public void handler(String data) throws Exception {
                //完成json数据和实体对象的转化
                StartImage startImage = JsonUtil.createObjectFromJson(StartImage.class, data);
                //加载启动图片,并监听
                ImageLoad.getInstance().asyncLoadImage(iv_startImage, startImage.getImg(),null, true, true, new ImageLoad.OnResultListener() {
                    @Override
                    public void onResult(boolean isSuccess) {
                        scale();
                    }
                });
            }

            @Override
            public void error(Exception e) {
                scale();
            }
        });
    }

    /**
     * 实现图片的缩放,并完成跳转
     */
    private void scale() {
        ScaleAnimationUtil.fillAfter = true;
        ScaleAnimationUtil.defaultDuration = 3000;
        ScaleAnimation scaleAnimation = ScaleAnimationUtil.scaleSelf(1f, 1.2f, 1f, 1.2f);
        scaleAnimation.setAnimationListener(new AnimationListenerAdapter() {
            @Override
            public void onAnimationEnd(Animation animation) {
                iv_startImage.setScaleX(1.2f);
                iv_startImage.setScaleY(1.2f);
                //开始透明动画
                alpha();
            }
        });
        iv_startImage.startAnimation(scaleAnimation);
    }

    /**
     * 透明化
     */
    private void alpha() {
        AlphaAnimationUtil.fillAfter = true;
        AlphaAnimationUtil.defaultDuration = 1200;
        AlphaAnimation alphaAnimation = AlphaAnimationUtil.alphaToHide();
        alphaAnimation.setAnimationListener(new AnimationListenerAdapter() {
            @Override
            public void onAnimationEnd(Animation animation) {
                //跳转到主界面
                ActivityUtil.startActivity(context, MainActivity.class);
                //结束欢迎界面
                finish();
            }
        });
        iv_startImage.startAnimation(alphaAnimation);
    }


    @Override
    public void setOnlistener() {
        super.setOnlistener();
    }
}
