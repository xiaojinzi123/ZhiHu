package com.example.cxj.zhihu.common.skin;

import android.content.Context;

import com.example.cxj.zhihu.R;

import java.io.InputStream;

import xiaojinzi.android.json.JsonUtil;
import xiaojinzi.android.util.os.T;
import xiaojinzi.java.util.StringUtil;

/**
 * Created by cxj on 2016/3/14.
 * 应用的皮肤
 */
public class Skin {

    /**
     * 构造函数私有化
     */
    private Skin() {
    }

    private static Skin skin = null;

    /**
     * 白天使用的环境
     */
    public static final int DAY_ENVIRONMENT = 0;

    /**
     * 晚上使用的环境
     */
    public static final int NIGHT_ENVIRONMENT = 1;

    /**
     * 环境
     */
    private int environment = DAY_ENVIRONMENT;

    /**
     * 菜单的皮肤
     */
    public MemuSkin memuSkin = new MemuSkin();

    /**
     * 主页的fragment的皮肤
     */
    public HomeFragmentSkin homeFragmentSkin = new HomeFragmentSkin();

    /**
     * 主题日报的fragment的皮肤
     */
    public ThemeFragmentSkin themeFragmentSkin = new ThemeFragmentSkin();

    /**
     * 详情界面的act皮肤
     */
    public DetailActSkin detailActSkin = new DetailActSkin();

    /**
     * 详情界面的fragment皮肤
     */
    public DetailFragmentSkin detailFragmentSkin = new DetailFragmentSkin();

    /**
     * 评论界面的皮肤
     */
    public CommentActSkin commentActSkin = new CommentActSkin();

    /**
     * 设置界面的皮肤
     */
    public SettingActSkin settingActSkin = new SettingActSkin();

    /**
     * 我的皮肤的界面的皮肤
     */
    public MySkinActSkin mySkinActSkin = new MySkinActSkin();

    /**
     * 在线皮肤界面的皮肤
     */
    public OnLineSkinActSkin onLineSkinActSkin = new OnLineSkinActSkin();

    /**
     * 我的收藏的界面
     */
    public MyCollectionActSkin myCollectionActSkin = new MyCollectionActSkin();

    /**
     * 获取实例对象
     *
     * @return
     */
    public static Skin getInstance() {
        if (skin == null) {
            skin = new Skin();
        }
        return skin;
    }

    /**
     * 加载白天的样式
     *
     * @param context 上下文对象
     */
    public void loadDayStyle(Context context) {
        try {
            InputStream is = context.getResources().openRawResource(R.raw.day);
            String str = StringUtil.isToStr(is, "UTF-8");
            JsonUtil.parseObjectFromJson(skin, str);
            environment = DAY_ENVIRONMENT;
        } catch (Exception e) {
            T.showShort(context, "更换皮肤失败");
        }
    }

    /**
     * 加载晚上的样式
     *
     * @param context 上下文对象
     */
    public void loadNightStyle(Context context) {
        try {
            InputStream is = context.getResources().openRawResource(R.raw.night);
            String str = StringUtil.isToStr(is, "UTF-8");
            JsonUtil.parseObjectFromJson(skin, str);
            environment = NIGHT_ENVIRONMENT;
        } catch (Exception e) {
            T.showShort(context, "更换皮肤失败");
        }
    }

    /**
     * 设置皮肤的环境
     *
     * @param environment
     */
    public void setEnvironment(int environment) {
        this.environment = environment;
    }

    /**
     * 获取皮肤的环境
     *
     * @return
     */
    public int getEnvironment() {
        return environment;
    }
}
