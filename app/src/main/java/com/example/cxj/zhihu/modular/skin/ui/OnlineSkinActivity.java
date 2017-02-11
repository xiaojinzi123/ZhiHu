package com.example.cxj.zhihu.modular.skin.ui;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.cxj.zhihu.MyApp;
import com.example.cxj.zhihu.R;
import com.example.cxj.zhihu.common.tool.Msg;
import com.example.cxj.zhihu.config.Constant;
import com.example.cxj.zhihu.db.skin.SkinDb;
import com.example.cxj.zhihu.modular.skin.fragment.OnlineSkinFragment;
import com.example.cxj.zhihu.modular.skin.entity.JsonSkin;
import com.example.cxj.zhihu.modular.skin.entity.SkinPaging;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import xiaojinzi.android.activity.BaseFragmentActivity;
import xiaojinzi.android.activity.annotation.Injection;
import xiaojinzi.android.json.JsonUtil;
import xiaojinzi.android.net.adapter.BaseDataHandlerAdapter;
import xiaojinzi.android.util.os.ProgressDialogUtil;
import xiaojinzi.android.util.os.T;

/**
 * 在线皮肤的界面,可以选择自己喜欢的皮肤
 */
public class OnlineSkinActivity extends BaseFragmentActivity {

    private Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == 666) {
                dialog.dismiss();
            }
        }
    };

    /**
     * pageSize的标识
     */
    public static final String PAGESIZEFLAG = "pageSize";

    @Injection(value = R.id.iv_act_online_skin_back, click = "clickView")
    private ImageView iv_back = null;

    @Injection(value = R.id.iv_act_online_skin_download, click = "clickView")
    private ImageView iv_download = null;

    @Injection(R.id.rl_act_online_skin_container)
    private RelativeLayout rl_container = null;

    @Injection(R.id.rl_act_online_titlebar)
    private RelativeLayout rl_titleBar = null;

    @Injection(R.id.vp_act_online_skin)
    private ViewPager vp = null;

    /**
     * viewpager用的fragment集合
     */
    private List<Fragment> fragments = new ArrayList<Fragment>();

    /**
     * 返回的数据
     */
    private SkinPaging p = null;

    /**
     * 进度条对话框
     */
    private ProgressDialog dialog;

    @Override
    public int getLayoutId() {
        return R.layout.act_online_skin;
    }


    @Override
    public void initView() {
        loadStyle();
    }

    @Override
    public void initData() {

        popupDialog();

        //加载在线皮肤信息
        MyApp.ah.get(Constant.Url.skinAct.skinListUrl, new BaseDataHandlerAdapter() {
            @Override
            public void handler(String data) throws Exception {
                Msg m = new Msg();
                m.setData(new SkinPaging(0, 0));
                JsonUtil.parseObjectFromJson(m, data);
                if (Msg.OK.equals(m.getMsg())) {
                    handData(m);
                } else {
                    T.showShort(OnlineSkinActivity.this, m.getMsgText());
                }
                closeDialog();
            }

            @Override
            public void error(Exception e) {
                closeDialog();
                T.showShort(OnlineSkinActivity.this, "服务器正在打盹~~~~~");
            }
        });
    }

    @Override
    public void setOnlistener() {
    }

    /**
     * 处理数据
     *
     * @param m
     */
    private void handData(Msg m) throws JSONException {
        fragments.clear();
        p = (SkinPaging) m.getData();
        List<JsonSkin> rows = p.getRows();
        for (int i = 0; rows != null && i < rows.size(); i++) {
            JsonSkin jsonSkin = rows.get(i);
            fragments.add(new OnlineSkinFragment(jsonSkin));
        }
        vp.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        });
    }


    /**
     * 弹出对话框
     */
    private void popupDialog() {
        if (dialog == null) {
            dialog = ProgressDialogUtil.show(this, ProgressDialog.STYLE_SPINNER, false);
        } else {
            dialog.show();
        }
    }

    /**
     * 关闭对话框,延迟关闭,效果更佳
     */
    private void closeDialog() {
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                }
                h.sendEmptyMessage(666);
            }
        }.start();
    }

    /**
     * View的点击事件集中处理
     *
     * @param v
     */
    public void clickView(View v) {
        int id = v.getId();
        if (id == R.id.iv_act_online_skin_back) {
            finish();
        } else if (id == R.id.iv_act_online_skin_download) {
            int currIndex = vp.getCurrentItem();
            if (currIndex < 0 || p == null || p.getRows() == null || p.getRows().size() == 0) {
                T.showShort(this, "请选择要下载的主题");
            } else {
                JsonSkin jsonSkin = p.getRows().get(currIndex);
                JsonSkin dbJsonSkin = SkinDb.getInstance(this).queryBySkinId(jsonSkin.getId());
                if (dbJsonSkin == null) {
                    SkinDb.getInstance(this).insert(jsonSkin);
                    T.showShort(this, "下载成功");
                } else {
                    T.showShort(this, "已经下载");
                }
            }
        }
    }

    /**
     * 加载样式
     */
    private void loadStyle() {
        rl_container.setBackgroundColor(MyApp.skin.onLineSkinActSkin.getContainerBgColor());
        rl_titleBar.setBackgroundColor(MyApp.skin.onLineSkinActSkin.getTitleBarBgColor());
        vp.setBackgroundColor(MyApp.skin.onLineSkinActSkin.getViewPagerBgColor());
    }

}
