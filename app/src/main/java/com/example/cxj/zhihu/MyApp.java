package com.example.cxj.zhihu;

import android.app.Application;
import android.content.Intent;

import com.example.cxj.zhihu.common.skin.Skin;
import com.example.cxj.zhihu.config.Constant;
import com.example.cxj.zhihu.service.AutoChangeSkinService;

import java.util.List;

import xiaojinzi.android.net.AsyncHttp;
import xiaojinzi.android.net.NetTask;
import xiaojinzi.android.net.ResultInfo;
import xiaojinzi.android.net.db.JsonCache;
import xiaojinzi.android.net.filter.NetFilter;
import xiaojinzi.android.util.log.L;
import xiaojinzi.android.util.store.SPUtil;

/**
 * Created by cxj on 2016/1/19.
 */
public class MyApp extends Application {

    /**
     * 网络请求的框架
     */
    public static AsyncHttp<Void> ah = null;

    /**
     * 应用的皮肤
     */
    public static Skin skin = null;

    /**
     * 是否wifi有用包含两点:
     * 1.连上了wifi
     * 2.有网络连接
     */
    public static boolean isWifiEable = false;

    /**
     * 是否开启自动换肤
     */
    public static boolean isAutoChangeSkin = false;

    /**
     * 加载图片的模式
     */
    public static int loadImageModeData = Constant.loadImageWithAll;

    @Override
    public void onCreate() {
        super.onCreate();
        //加载轻量级存储里面的数据
        initSpData();
        //初始化对象
        ah = new AsyncHttp<Void>();
        //初始化json缓存器
        initNetCache();
        skin = Skin.getInstance();
        //根据存储的数值加载相应的样式
        int mode = SPUtil.get(this, Constant.SP.common.showMode, 0);
        if (mode == 0) {
            skin.loadDayStyle(this);
        } else {
            skin.loadNightStyle(this);
        }
    }

    /**
     * 加载轻量级存储里面的数据
     */
    private void initSpData() {
        int loadImageMode = SPUtil.get(this, Constant.SP.settingAct.loadImageMode, Constant.loadImageWithAll);
        if (loadImageMode == (Constant.loadImageWithWifi)) {
            loadImageModeData = Constant.loadImageWithWifi;
        } else {
            loadImageModeData = Constant.loadImageWithAll;
        }
        isWifiEable = SPUtil.get(this, Constant.SP.common.isWifiEable, false);
        isAutoChangeSkin = SPUtil.get(this, Constant.SP.settingAct.isAutoChangeSkin, false);
        if (isAutoChangeSkin) {
            startService(new Intent(this, AutoChangeSkinService.class));
        }
    }

    /**
     * 初始化网络缓存
     */
    private void initNetCache() {
        //添加网络访问过滤器
        AsyncHttp.initCacheJson(this);
        ah.addNetFilter(new NetFilter() {
            @Override
            public boolean netTaskPrepare(NetTask<?> netTask) {
                return false;
            }

            @Override
            public boolean netTaskBegin(NetTask<?> netTask, ResultInfo<?> resultInfo) {
                if (loadImageModeData == Constant.loadImageWithWifi && isWifiEable == false) {
                    String url = netTask.url;
                    if (url.toLowerCase().endsWith(".jpg") || url.toLowerCase().endsWith(".png")) {
                        resultInfo.loadDataType = AsyncHttp.BaseDataHandler.ERRORDATA;
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean resultInfoReturn(ResultInfo<?> resultInfo) {
                return false;
            }
        });
    }

}
