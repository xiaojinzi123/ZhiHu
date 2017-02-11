package com.example.cxj.zhihu.modular.detail.fragment;

import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.cxj.zhihu.MyApp;
import com.example.cxj.zhihu.R;
import com.example.cxj.zhihu.config.Constant;
import com.example.cxj.zhihu.modular.collection.db.MyCollectionDb;
import com.example.cxj.zhihu.modular.collection.entity.DbStory;
import com.example.cxj.zhihu.modular.detail.entity.DetailStory;
import com.example.cxj.zhihu.modular.detail.entity.StoryExtraInfo;
import com.example.cxj.zhihu.modular.detail.ui.DetailActivity;
import com.example.cxj.zhihu.modular.detail.view.MyScrollView;

import xiaojinzi.android.activity.annotation.Injection;
import xiaojinzi.android.fragment.BaseFragment;
import xiaojinzi.android.image.ImageLoad;
import xiaojinzi.android.json.JsonUtil;
import xiaojinzi.android.message.EBus;
import xiaojinzi.android.net.adapter.BaseDataHandlerAdapter;
import xiaojinzi.android.util.log.L;

/**
 * Created by cxj on 2016/1/22.
 * 显示一个故事详情的fragment
 */
public class DetailFragment extends BaseFragment {

    /**
     * 类的标识
     */
    private String tag = "DetailFragment";

    /**
     * 离线加载的网页内容要加上的一些网页源码
     */
    private String codePrefixOne = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">" +
            "<html>" +
            "<head>" +
            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=\">" +
            "<style type=\"text/css\">";
    private String codePrefixTwo = "</style>" + "</head>" + "<body></body>" + "</html>";
    private String codeSubfix = "</body></html>";

    /**
     * 故事的id
     */
    private String storyId = null;

    @Injection(R.id.sv_frag_detail_container)
    private MyScrollView sv = null;

    @Injection(R.id.iv_frag_detail_image)
    private ImageView iv_image = null;

    @Injection(R.id.wv_frag_detail)
    private WebView myWebView = null;

    /**
     * 故事的额外的信息
     */
    private StoryExtraInfo storyExtraInfo;

    public DetailFragment() {
    }

    /**
     * 构造函数
     *
     * @param storyId 传进来一个故事的id
     */
    public DetailFragment(String storyId) {
        this.storyId = storyId;
    }

    @Override
    public int getLayoutId() {
        return R.layout.frag_detail;
    }

    @Override
    public void initView() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) iv_image.getLayoutParams();
        lp.topMargin = DetailActivity.getTitleBarHeight();
    }

    /**
     * 加载数据
     */
    @Override
    public void initData() {

        loadStyle();

        //根据故事id加载故事的详情
        MyApp.ah.get(Constant.Url.detailAct.detailStoryUrl + storyId, new BaseDataHandlerAdapter() {
            @Override
            public void handler(String data) throws Exception {
                //转化json数据为故事详情的实体对象
                DetailStory detailStory = JsonUtil.createObjectFromJson(DetailStory.class, data);
                if (detailStory.getImage() != null && !"".equals(detailStory.getImage())) {
                    iv_image.setVisibility(View.VISIBLE);
                    //异步加载上面的大图
                    ImageLoad.getInstance().asyncLoadImage(iv_image, detailStory.getImage());
                } else {
                    iv_image.setVisibility(View.GONE);
                }
                WebSettings settings = myWebView.getSettings();
                settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//                settings.setUseWideViewPort(true);// 这个很关键
//                settings.setSupportZoom(true);
//                settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//                settings.setLoadWithOverviewMode(true);
                String webData = codePrefixOne + "*{color:" + MyApp.skin.detailFragmentSkin.getWebViewTextColor() + ";}body{word-wrap:break-word;font-family:Arial}" + codePrefixTwo + detailStory.getBody() + codeSubfix;
                myWebView.loadDataWithBaseURL(null, webData, "text/html", "UTF-8", null);
//                myWebView.setInitialScale(80);

                //初始化额外的信息
                initExtraInfo();
            }

            @Override
            public void error(Exception e) {
                closeDialog();
                L.s(tag, "加载故事详情失败");
            }
        });


    }

    private int count = 0;

    @Override
    public void setOnlistener() {
        sv.setOnScrollChange(new MyScrollView.OnScrollChange() {
            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                L.s(tag, "top = " + t + ",oldTop = " + oldt);
                EBus.postEvent(DetailActivity.TAG, "changeTitleBarOpacity", t);
                int height = iv_image.getHeight();
                if (t == height + DetailActivity.getTitleBarHeight() && oldt == 0) {
                    sv.scrollTo(0, 0);
                }
            }
        });
    }

    /**
     * 加载样式
     */
    private void loadStyle() {
        myWebView.setBackgroundColor(MyApp.skin.detailFragmentSkin.getWebViewBgColor());
    }

    /**
     * 获取额外的信息
     */
    private void initExtraInfo() {
        MyApp.ah.get(Constant.Url.detailAct.extraStoryInfoUrl + storyId, new BaseDataHandlerAdapter() {
            @Override
            public void handler(String data) throws Exception {
                storyExtraInfo = JsonUtil.createObjectFromJson(StoryExtraInfo.class, data);
                //通知数据加载完毕
                notifyLoadDataComplete();
            }

            @Override
            public void error(Exception e) {
                closeDialog();
                L.s(tag, "加载故事的额外的信息失败");
            }
        });
    }


    @Override
    public void freshUI() {
        //让试图滚到最上面
        sv.smoothScrollTo(0, 0);
        //如果故事的额外信息已经获取
        if (storyExtraInfo != null) {
            //发送一个事件
            EBus.postEvent(storyExtraInfo);
        }
        DbStory dbStory = MyCollectionDb.getInstance(context).queryByStoryId(Integer.parseInt(storyId));
        if (dbStory == null) {
            EBus.postEvent(DetailActivity.TAG, Constant.CHANGECOLLECTICON_FLAG, false);
        } else {
            EBus.postEvent(DetailActivity.TAG, Constant.CHANGECOLLECTICON_FLAG, true);
        }
    }

    @Override
    public boolean isPopupDialog() {
        return false;
    }

}
