package com.example.cxj.zhihu.modular.comment.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cxj.zhihu.MyApp;
import com.example.cxj.zhihu.R;
import com.example.cxj.zhihu.config.Constant;
import com.example.cxj.zhihu.modular.comment.adapter.CommentActAdapter;
import com.example.cxj.zhihu.modular.comment.entity.Comment;
import com.example.cxj.zhihu.modular.comment.entity.Comments;
import com.example.cxj.zhihu.modular.comment.entity.TmpInfo;

import java.util.ArrayList;
import java.util.List;

import xiaojinzi.android.activity.BaseActivity;
import xiaojinzi.android.activity.annotation.Injection;
import xiaojinzi.android.json.JsonUtil;
import xiaojinzi.android.message.EBus;
import xiaojinzi.android.net.adapter.BaseDataHandlerAdapter;

/**
 * 显示评论的activity
 */
public class CommentAct extends BaseActivity {

    /**
     * 类的标识
     */
    public static final String TAG = "CommentAct";

    @Injection(R.id.rl_act_comment_comtainer)
    private RelativeLayout rl_container = null;

    @Injection(R.id.rl_act_comment_titlebar)
    private RelativeLayout rl_titleBar = null;

    @Injection(value = R.id.iv_act_comment_back, click = "viewClick")
    private ImageView iv_back = null;

    @Injection(R.id.tv_act_comment_number)
    private TextView tv_comments = null;

    @Injection(R.id.lv_act_comment)
    private ListView lv = null;

    /**
     * listview使用的适配器
     */
    private BaseAdapter adapter = null;

    /**
     * 总的评论对象
     */
    private Comments comments = new Comments();

    /**
     * activity和adapter之间通用数据而用的对象
     */
    private TmpInfo tmpInfo = new TmpInfo();

    /**
     * 故事的id
     */
    private String storyId = null;

    @Override
    public int getLayoutId() {
        return R.layout.act_comment;
    }

    @Override
    public void initView() {
        //加载样式
        loadStyle();
    }

    @Override
    public void initData() {
        //获取之前页面传送过来的故事id
        storyId = getIntent().getStringExtra(Constant.STORY_FLAG);
        //获取总的评论数目
        String comments = getIntent().getStringExtra("comments");
        tv_comments.setText(comments + "条点评");

        //加载长评数据,长评数据加载成功自动加载短评数据,短评加载成功就开始显示在界面上
        loadLongCommentData();

    }

    /**
     * 加载长评数据
     */
    private void loadLongCommentData() {
        //根据故事的id获取这个故事的长评的信息
        MyApp.ah.get(Constant.Url.commentAct.commentUrlPrefix + storyId + Constant.Url.commentAct.longCommentUrlSubfix, new BaseDataHandlerAdapter() {
            @Override
            public void handler(String data) throws Exception {
                JsonUtil.parseObjectFromJson(comments, data);
                comments.setLongComments(comments.getComments());
                comments.setComments(null);
                loadShortCommentData();
            }

            @Override
            public void error(Exception e) {
            }
        });
    }

    /**
     * 加载短评数据
     */
    private void loadShortCommentData() {
        //根据故事的id获取这个故事的长评的信息
        MyApp.ah.get(Constant.Url.commentAct.commentUrlPrefix + storyId + Constant.Url.commentAct.shortCommentUrlSubfix, new BaseDataHandlerAdapter() {
            @Override
            public void handler(String data) throws Exception {
//                L.s(data);
                JsonUtil.parseObjectFromJson(comments, data);
                comments.setShortComments(comments.getComments());
                comments.setComments(null);
                showData();
            }

            @Override
            public void error(Exception e) {
            }
        });
    }

    /**
     * 要显示的数据
     */
    private List<Comment> data = new ArrayList<Comment>();

    /**
     * 显示数据
     */
    private void showData() {
        //创建一个评论头
        Comment header = new Comment();
        header.setIsHeader(true);
        header.setCommentNumber(comments.getLongComments().size());
        data.add(header);

        data.addAll(comments.getLongComments());

        tmpInfo.shortCommentTagPosition = data.size();

        header = new Comment();
        header.setIsHeader(true);
        header.setCommentNumber(comments.getShortComments().size());
        data.add(header);

        data.addAll(comments.getShortComments());

        rl_titleBar.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int titleBarHeight = rl_titleBar.getMeasuredHeight();

        adapter = new CommentActAdapter(this, data, R.layout.act_comment_item, titleBarHeight, tmpInfo);

        lv.setAdapter(adapter);

    }

    @Override
    public void setOnlistener() {
        //设置listview的点击事件
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && data.get(position).isHeader()) {
                    tmpInfo.isOpenShortComment = !tmpInfo.isOpenShortComment;
                    adapter.notifyDataSetChanged();
                    if (tmpInfo.isOpenShortComment) {
                        EBus.getInstance().postEvent(1200, CommentAct.TAG, "smoothScrollToPosition", tmpInfo.shortCommentTagPosition);
                    }
                }
            }
        });
    }

    /**
     * view控件的点击事件集中处理
     *
     * @param v
     */
    public void viewClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_act_comment_back) {
            finish();
        }
    }

    /**
     * 加载样式
     */
    private void loadStyle() {
        rl_container.setBackgroundColor(MyApp.skin.commentActSkin.getContainerBgColor());
        rl_titleBar.setBackgroundColor(MyApp.skin.commentActSkin.getTitleBarBgColor());
    }


    /**
     * 让listview滚动到指定的位置
     *
     * @param position
     */
    public void onEventSmoothScrollToPosition(Integer position) {
        lv.setSelection(position);
    }

}
