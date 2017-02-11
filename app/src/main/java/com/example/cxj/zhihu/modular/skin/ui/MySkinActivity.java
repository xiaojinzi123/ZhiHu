package com.example.cxj.zhihu.modular.skin.ui;

import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.cxj.zhihu.MyApp;
import com.example.cxj.zhihu.R;
import com.example.cxj.zhihu.db.skin.SkinDb;
import com.example.cxj.zhihu.modular.skin.adapter.MySkinActivityAdapter;
import com.example.cxj.zhihu.modular.skin.entity.JsonSkin;

import org.json.JSONException;

import java.util.List;

import xiaojinzi.android.activity.BaseActivity;
import xiaojinzi.android.activity.annotation.Injection;
import xiaojinzi.android.json.JsonUtil;
import xiaojinzi.android.util.os.T;

/**
 * 我的皮肤的界面
 */
public class MySkinActivity extends BaseActivity {

    @Injection(R.id.rl_act_myskin_titlebar)
    private RelativeLayout rl_titleBar = null;

    @Injection(R.id.rl_act_myskin_container)
    private RelativeLayout rl_container = null;

    @Injection(value = R.id.iv_act_myskin_back, click = "clickView")
    private ImageView iv_back = null;

    @Injection(value = R.id.iv_act_skin_confirm, click = "clickView")
    private ImageView iv_confirm = null;

    @Injection(value = R.id.iv_act_skin_delete, click = "clickView")
    private ImageView iv_delete = null;

    @Injection(R.id.lv_act_my_skin)
    private ListView lv = null;

    /**
     * 要显示的数据
     */
    private List<JsonSkin> jsonSkins;

    /**
     * 显示数据的适配器
     */
    private BaseAdapter adapter = null;

    /**
     * 记录哪一个条目被点击了,之所以要用数组的形式
     * 是因为adapter那边也要用,如果基本数据类型的话两边数据不相通的
     */
    private Integer[] selectIndex = {-1};

    @Override
    public int getLayoutId() {
        return R.layout.act_my_skin;
    }

    @Override
    public void initView() {
        loadStyle();
    }

    @Override
    public void initData() {
        //获取所有本地的皮肤
        jsonSkins = SkinDb.getInstance(this).queryAll();
        //创建适配器
        adapter = new MySkinActivityAdapter(this, jsonSkins, R.layout.act_myskin_item, selectIndex);
        //挂在适配器到listview上面
        lv.setAdapter(adapter);
    }

    @Override
    public void setOnlistener() {
        //设置listview的条目点击监听
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectIndex[0] == position) {
                    selectIndex[0] = -1;
                    adapter.notifyDataSetChanged();
                    return;
                }
                selectIndex[0] = position;
                adapter.notifyDataSetChanged();
            }
        });
    }


    /**
     * view控件的点击事件集中处理
     *
     * @param view
     */
    public void clickView(View view) throws JSONException {
        int id = view.getId();
        if (id == R.id.iv_act_myskin_back) {
            finish();
        } else if (id == R.id.iv_act_skin_confirm) {
            if (selectIndex[0] == -1) {
                T.showShort(this, "请选择要使用的皮肤");
            } else {
                JsonSkin jsonSkin = jsonSkins.get(selectIndex[0]);
                JsonUtil.parseObjectFromJson(MyApp.skin, jsonSkin.getSkinJsonData());
                T.showShort(this, "更换成功");
                selectIndex[0] = -1;
                adapter.notifyDataSetChanged();
            }
        } else if (id == R.id.iv_act_skin_delete) {
            int index = selectIndex[0];
            if (index != -1) {
                JsonSkin jsonSkin = jsonSkins.get(index);
                SkinDb.getInstance(this).delete(jsonSkin);
                jsonSkins.remove(index);
                selectIndex[0] = -1;
                adapter.notifyDataSetChanged();
                T.showShort(this, "删除成功");
            } else {
                T.showShort(this, "请选中一项,再点击删除");
            }
        }
    }

    /**
     * 加载样式
     */
    private void loadStyle() {
        rl_container.setBackgroundColor(MyApp.skin.mySkinActSkin.getContainerBgColor());
        rl_titleBar.setBackgroundColor(MyApp.skin.mySkinActSkin.getTitleBarBgColor());
        lv.setBackgroundColor(MyApp.skin.mySkinActSkin.getListBgColor());
    }

}
