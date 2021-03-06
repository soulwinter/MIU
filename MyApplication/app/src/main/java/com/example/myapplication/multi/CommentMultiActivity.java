package com.example.myapplication.multi;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.example.myapplication.AddWifiRecord;
import com.example.myapplication.bean.CommentEntity;
import com.example.myapplication.bean.CommentMoreBean;
import com.example.myapplication.bean.FirstLevelBean;
import com.example.myapplication.bean.SecondLevelBean;
import com.example.myapplication.dialog.InputTextMsgDialog;
import com.example.myapplication.entity.Ap;
import com.example.myapplication.entity.CommentOfTag;
import com.example.myapplication.entity.Tag;
import com.example.myapplication.entity.User;
import com.example.myapplication.listener.SoftKeyBoardListener;
import com.example.myapplication.util.RecyclerViewUtil;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.example.myapplication.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CommentMultiActivity extends AppCompatActivity {

    private List<MultiItemEntity> data = new ArrayList<>();
    private List<FirstLevelBean> datas = new ArrayList<>();
    private BottomSheetDialog bottomSheetDialog;
    private InputTextMsgDialog inputTextMsgDialog;
    private float slideOffset = 0;
    private String content = "???????????????";
    private CommentDialogMutiAdapter bottomSheetAdapter;
    private RecyclerView rv_dialog_lists;
    private int totalCount = 5;
    private int offsetY;
    private int positionCount = 0;
    private RecyclerViewUtil mRecyclerViewUtil;
    private SoftKeyBoardListener mKeyBoardListener;

    private int userId, tagId, areaId;
    private User currentUser;
    private Integer recommentWho;  // ?????????????????????id
    private final Integer MAX_SHOW_COMMENTS = 5;
    private Integer totalComents = 1;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private OkHttpClient okHttpClient2 = new OkHttpClient();
    private OkHttpClient okHttpClient3 = new OkHttpClient();
    private OkHttpClient okHttpClient4 = new OkHttpClient();
    private List<CommentOfTag> commentOfTagList = new ArrayList<>();
    private List<CommentOfTag> secondComentList = new ArrayList<>();
    private List<CommentOfTag> moreCommentList = new ArrayList<>();
    private List<FirstLevelBean> moreFirstLevelBean = new ArrayList<>();
    private List<Tag> tagList = new ArrayList<>();

    private User user;

    ImageView tagImage;
    TextView text_tag_name, text_description;
    String tagImagePath, tagName, tagDescription;
    static Map TagIdMap = new HashMap();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // todo ????????????????????????????????????????????????????????????user
//        userId = 15;
//        tagId = 5;
//        areaId = 14;
//        tagName = "333_tag";
//        tagDescription = "333??????tag";
//        tagImagePath = "res/drawable/user_0.jpeg";
//        currentUser = new User();
//        currentUser.setId(15);
//        currentUser.setUsername("333");

        //?????????????????????
        Intent intent =  getIntent();
        tagName = intent.getStringExtra("tagName");
        tagId = generateTagId(tagName);
        tagDescription = intent.getStringExtra("tagDescribe");
        tagImagePath = (String) intent.getSerializableExtra("tagPhotoPath");
        userId = intent.getIntExtra("userID", userId);
        areaId = intent.getIntExtra("areaID", areaId);
        user = (User)intent.getSerializableExtra("user");
        currentUser = new User();
        currentUser.setId(userId);
        currentUser.setUsername(user.getUsername());

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("????????????");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_multi);

        // ?????????tag????????????????????????
        initTagView();

        mRecyclerViewUtil = new RecyclerViewUtil();
//        getInitialData(); // ?????????????????????????????????
        WorkThread workThread = new WorkThread();
        workThread.start();
        try {
            workThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        tempgetInitialData();
        initData(); // ???commentOfTagList??????datas
        dataSort(0);
        showSheetDialog();

    }

    private void tempgetInitialData(){

        CommentOfTag c2 = new CommentOfTag();
        c2.setId(1);
        c2.setUserId(1);
        c2.setTagId(0);
        c2.setComment("c2???comment");
        c2.setRecommentWho(0);
        c2.setCreateTime(new Date(1000));
        c2.setLikes(66);
        c2.setUserName("sxj1");

        CommentOfTag c3 = new CommentOfTag();
        c3.setId(2);
        c3.setUserId(2);
        c3.setTagId(0);
        c3.setComment("c3???comment");
        c3.setRecommentWho(0);
        c3.setCreateTime(new Date(1000));
        c3.setLikes(99);
        c3.setUserName("sxj2");

        List<CommentOfTag> list = new ArrayList<>();
        list.add(c2);
        list.add(c3);

        CommentOfTag c1 = new CommentOfTag();
        c1.setId(0);
        c1.setUserId(0);
        c1.setTagId(0);
        c1.setComment("c1???comment");
        c1.setRecommentWho(-1);
        c1.setCreateTime(new Date(1000));
        c1.setLikes(33);
        c1.setUserName("333");
        c1.setChildList(list);

        commentOfTagList.add(c1);
    }

    private class WorkThread extends Thread{
        public void run() {
            try {
                //2???????????????????????????
                Request request = new Request.Builder().url("http://114.116.234.63:8080/comment/listCommentByTagId?tagId="+tagId).get().build();
                //3???????????????????????????
                Call call = okHttpClient2.newCall(request);
                //4?????????????????????,?????????????????????
                Response response = call.execute();
                //??????json?????????
                String json = response.body().string();
                Log.i("josn",json);
                JSONObject jsonObject = JSONObject.parseObject(json);
                String initialCommentString = jsonObject.getString("data");

                commentOfTagList = JSONObject.parseArray(initialCommentString, CommentOfTag.class);  //???area?????????ap
//                    printCommentList(commentOfTagList);
//                    copyToMoreCommentList(commentOfTagList);
//                Log.i("commentOfTagList-size",String.valueOf(commentOfTagList.size()));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // ?????????????????????????????????(?????????commentOfTagList???
    private void getInitialData(){

        Thread t =  new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    //2???????????????????????????
                    Request request = new Request.Builder().url("http://114.116.234.63:8080/comment/listCommentByTagId?tagId="+tagId).get().build();
                    //3???????????????????????????
                    Call call = okHttpClient2.newCall(request);
                    //4?????????????????????,?????????????????????
                    Response response = call.execute();
                    //??????json?????????
                    String json = response.body().string();
                    Log.i("josn",json);
                    JSONObject jsonObject = JSONObject.parseObject(json);
                    String initialCommentString = jsonObject.getString("data");

                    commentOfTagList = JSONObject.parseArray(initialCommentString, CommentOfTag.class);  //???area?????????ap
//                    printCommentList(commentOfTagList);
//                    copyToMoreCommentList(commentOfTagList);
                    Log.i("commentOfTagList-size",String.valueOf(commentOfTagList.size()));

//                    initData();
//                    dataSort(0);
//                    showSheetDialog();


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();


    }

    private Integer generateTagId(String tagName){
        if(!TagIdMap.containsKey(tagName)) {
            int id = TagIdMap.size();
            TagIdMap.put(tagName, id);
            return id;
        }else{
            return (Integer) TagIdMap.get(tagName);
        }
    }

    private void copyToMoreCommentList(List<CommentOfTag> commentOfTagList){
        if (commentOfTagList.size()>3){

            CommentOfTag commentOfTag = new CommentOfTag();

            for(int i = 3; i<commentOfTagList.size(); i++){

                commentOfTag = commentOfTagList.get(i);
                moreCommentList.add(commentOfTag);

                FirstLevelBean firstLevelBean = new FirstLevelBean();
                firstLevelBean.setUserName(commentOfTag.getUserName());
                firstLevelBean.setId(commentOfTag.getId());
//                firstLevelBean.setHeadImg("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1918451189,3095768332&fm=26&gp=0.jpg");
                firstLevelBean.setCreateTime(commentOfTag.getCreateTime().getTime());
                firstLevelBean.setContent(commentOfTag.getComment());
                firstLevelBean.setLikeCount(commentOfTag.getLikes());
                firstLevelBean.setSecondLevelBeans(new ArrayList<SecondLevelBean>());

                moreFirstLevelBean.add(firstLevelBean);

            }
        }


    }

    private void printCommentList(List<CommentOfTag> commentOfTagList){
        for(CommentOfTag comment: commentOfTagList ){
            Log.i("here-comment_"+comment.getId(), String.valueOf(comment.getId()));
        }
    }

    private void initRefresh() {
        datas.clear();
        initData();
        dataSort(0);
        bottomSheetAdapter.setNewData(data);
    }

    // ????????????commentOfTagList?????????datas
    // todo image
    private void initData() {

        int size = commentOfTagList.size();
//        Log.i("first:commentoftag", String.valueOf(size));
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        for (int i = 0; i < size; i++) {
            CommentOfTag commentOfTag = commentOfTagList.get(i);
            FirstLevelBean firstLevelBean = new FirstLevelBean();
            firstLevelBean.setId(commentOfTag.getId());
//            firstLevelBean.setHeadImg("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3370302115,85956606&fm=26&gp=0.jpg");
            firstLevelBean.setUserName(commentOfTag.getUserName());
            firstLevelBean.setUserId(commentOfTag.getUserId());
            firstLevelBean.setContent(commentOfTag.getComment());
            firstLevelBean.setCreateTime(commentOfTag.getCreateTime().getTime());
            firstLevelBean.setLikeCount(commentOfTag.getLikes());
            firstLevelBean.setIsLike(0); // ???????????????
            firstLevelBean.setTotalCount(i + size);

            Log.i("first:get-comment:", commentOfTag.getComment());
            Log.i("first:get-comentId:", String.valueOf(commentOfTag.getId()));


            secondComentList = commentOfTag.getChildList();
            int secondSize = secondComentList.size();
            List<SecondLevelBean> beans = new ArrayList<>();
            if (secondSize!=0) {
                for (int j = 0; j < secondSize; j++) {
                    CommentOfTag secondComent = secondComentList.get(j);
                    SecondLevelBean secondLevelBean = new SecondLevelBean();
                    secondLevelBean.setContent(secondComent.getComment());
                    secondLevelBean.setCreateTime(secondComent.getCreateTime().getTime());
                    //                secondLevelBean.setHeadImg("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1918451189,3095768332&fm=26&gp=0.jpg");
                    secondLevelBean.setId(secondComent.getId());
                    secondLevelBean.setIsLike(0);
                    secondLevelBean.setLikeCount(secondComent.getLikes());
                    secondLevelBean.setUserName(secondComent.getUserName());
                    secondLevelBean.setUserId(secondLevelBean.getUserId());
                    secondLevelBean.setIsReply(1);
                    secondLevelBean.setReplyUserName(secondComent.getReplyUsername());
                    //                secondLevelBean.setTotalCount(firstLevelBean.getTotalCount());
                    beans.add(secondLevelBean);

                    Log.i("second:get-comment:", secondComent.getComment());
                    Log.i("second:get-comentId:", String.valueOf(secondComent.getId()));
                }
                firstLevelBean.setSecondLevelBeans(beans);
            }else {
                firstLevelBean.setSecondLevelBeans(beans);
            }
            datas.add(firstLevelBean);
//            Log.i("first0:datas.size=", String.valueOf(datas.size()));

        }

//        Log.i("first:datas.size=", String.valueOf(datas.size()));
    }

    /**
     * ???????????????????????????
     * ???????????????????????????????????????????????????item
     * ????????????????????????
     *
     * @param position
     */
    private void dataSort(int position) {
        if (datas.isEmpty()) {
//            Log.i("datas.size=", String.valueOf(datas.size()));
            data.add(new MultiItemEntity() {
                @Override
                public int getItemType() {
                    return CommentEntity.TYPE_COMMENT_EMPTY;
                }
            });
            return;
        }

        if (position <= 0) data.clear();
        int posCount = data.size();
        int count = datas.size();
//        int count = MAX_SHOW_COMMENTS;
        for (int i = 0; i < count; i++) {
            if (i < position) continue;

            //????????????
            FirstLevelBean firstLevelBean = datas.get(i);
            if (firstLevelBean == null) continue;
            firstLevelBean.setPosition(i);
            posCount += 2;
            List<SecondLevelBean> secondLevelBeans = firstLevelBean.getSecondLevelBeans();
            if (secondLevelBeans == null || secondLevelBeans.isEmpty()) {
                firstLevelBean.setPositionCount(posCount);
                data.add(firstLevelBean);
                continue;
            }
            int beanSize = secondLevelBeans.size();
//            int beanSize = MAX_SHOW_COMMENTS;
            posCount += beanSize;
            firstLevelBean.setPositionCount(posCount);
            data.add(firstLevelBean);

            //????????????
            for (int j = 0; j < beanSize; j++) {
                SecondLevelBean secondLevelBean = secondLevelBeans.get(j);
                secondLevelBean.setChildPosition(j);
                secondLevelBean.setPosition(i);
                secondLevelBean.setPositionCount(posCount);
                data.add(secondLevelBean);
            }

//            //???????????????item
//            if (beanSize <= 8) {
//                CommentMoreBean moreBean = new CommentMoreBean();
//                moreBean.setPosition(i);
//                moreBean.setPositionCount(posCount);
//                moreBean.setTotalCount(firstLevelBean.getTotalCount());
//                data.add(moreBean);
//            }

        }
//        Log.i("here-datasize:", String.valueOf(data.size()));
    }

    public void show(View view) {
        bottomSheetAdapter.notifyDataSetChanged();
        slideOffset = 0;
        bottomSheetDialog.show();
    }


    private void showSheetDialog() {
        if (bottomSheetDialog != null) {
            return;
        }

        Log.e("intoShow:", "intoshow");
        //view
        View view = View.inflate(this, R.layout.dialog_bottomsheet, null);
        ImageView iv_dialog_close = (ImageView) view.findViewById(R.id.dialog_bottomsheet_iv_close);
        rv_dialog_lists = (RecyclerView) view.findViewById(R.id.dialog_bottomsheet_rv_lists);
        RelativeLayout rl_comment = view.findViewById(R.id.rl_comment);
        iv_dialog_close.setOnClickListener(v -> bottomSheetDialog.dismiss());
        rl_comment.setOnClickListener(v -> {
            //todo ??????????????????
            initInputTextMsgDialog(null, false, null, -1);
        });

        //adapter
        bottomSheetAdapter = new CommentDialogMutiAdapter(data);
        rv_dialog_lists.setHasFixedSize(true);
        rv_dialog_lists.setLayoutManager(new LinearLayoutManager(this));
        closeDefaultAnimator(rv_dialog_lists);
//        bottomSheetAdapter.setOnLoadMoreListener(this, rv_dialog_lists);
        rv_dialog_lists.setAdapter(bottomSheetAdapter);

        //dialog
        bottomSheetDialog = new BottomSheetDialog(this, R.style.dialog);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        BottomSheetBehavior mDialogBehavior = BottomSheetBehavior.from((View) view.getParent());
        mDialogBehavior.setPeekHeight(getWindowHeight());
        //dialog????????????
        mDialogBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    mDialogBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else if (newState == BottomSheetBehavior.STATE_SETTLING) {
                    if (slideOffset <= -0.28) {
                        //?????????????????? ????????????
                        bottomSheetDialog.dismiss();
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                CommentMultiActivity.this.slideOffset = slideOffset;//???????????????
            }
        });

        initListener();
    }

    private void modifyLikeCount(int id, int likes){
        boolean ok = false;
        // ???????????????
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    //1????????????????????????
                    FormBody formBody = new FormBody.Builder().add("id", String.valueOf(id)).add("likes", String.valueOf(likes)).build();
                    //2???????????????????????????
                    Request request = new Request.Builder().url("http://114.116.234.63:8080/comment/updateComment").post(formBody).build();
                    //3???????????????????????????
                    Call call = okHttpClient3.newCall(request);
                    //4?????????????????????,?????????????????????
                    Response response = call.execute();

                    //??????json?????????
                    String json = response.body().string();
                    JSONObject jsonObject = JSONObject.parseObject(json);
                    Integer code = jsonObject.getInteger("code");
//                    if (code == 200){
//                        Looper.prepare();
//                        Toast.makeText(CommentMultiActivity.this, "??????????????????????????????", Toast.LENGTH_SHORT).show();
//                        Looper.loop();
//                    }else{
//                        Looper.prepare();
//                        Toast.makeText(CommentMultiActivity.this, "??????????????????????????????", Toast.LENGTH_SHORT).show();
//                        Looper.loop();
//                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void initListener() {
        // ????????????
        bottomSheetAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view1, int position) {
                switch ((int) view1.getTag()) {
                    case CommentEntity.TYPE_COMMENT_PARENT:
                        if (view1.getId() == R.id.rl_group) {
                            //todo ??????????????????
                            CommentMultiActivity.this.initInputTextMsgDialog((View) view1.getParent(), false, bottomSheetAdapter.getData().get(position), position);
                        } else if (view1.getId() == R.id.ll_like) {
                            //todo ?????????????????? ?????????????????????????????? ?????????????????????
                            FirstLevelBean bean = (FirstLevelBean) bottomSheetAdapter.getData().get(position);
                            bean.setLikeCount(bean.getLikeCount() + (bean.getIsLike() == 0 ? 1 : -1));
                            // ??????????????????
                            modifyLikeCount(bean.getId(), bean.getLikeCount() + (bean.getIsLike() == 0 ? 0 : 1));
                            bean.setIsLike(bean.getIsLike() == 0 ? 1 : 0);
                            datas.set(bean.getPosition(), bean);
                            CommentMultiActivity.this.dataSort(0);
                            bottomSheetAdapter.notifyDataSetChanged();
                        }
                        break;
                    case CommentEntity.TYPE_COMMENT_CHILD:

                        if (view1.getId() == R.id.rl_group) {
                            //todo ??????????????????????????????
                            CommentMultiActivity.this.initInputTextMsgDialog(view1, true, bottomSheetAdapter.getData().get(position), position);
                        } else if (view1.getId() == R.id.ll_like) {
                            //todo ?????????????????? ?????????????????????????????? ?????????????????????
                            SecondLevelBean bean = (SecondLevelBean) bottomSheetAdapter.getData().get(position);
                            bean.setLikeCount(bean.getLikeCount() + (bean.getIsLike() == 0 ? 0 : 1));
                            // ??????????????????
                            modifyLikeCount(bean.getId(), bean.getLikeCount() + (bean.getIsLike() == 0 ? 1 : -1));

                            bean.setIsLike(bean.getIsLike() == 0 ? 1 : 0);

                            List<SecondLevelBean> secondLevelBeans = datas.get((int) bean.getPosition()).getSecondLevelBeans();
                            secondLevelBeans.set(bean.getChildPosition(), bean);
//                            CommentMultiActivity.this.dataSort(0);
                            bottomSheetAdapter.notifyDataSetChanged();
                        }

                        break;
                    case CommentEntity.TYPE_COMMENT_MORE:
                        // ?????????????????????????????????????????????????????????????????????????????????????????????setId???
                        CommentMoreBean moreBean = (CommentMoreBean) bottomSheetAdapter.getData().get(position);
                        SecondLevelBean secondLevelBean = new SecondLevelBean();

                        secondLevelBean.setUserId(0);
                        secondLevelBean.setRecommentWho(1);
                        secondLevelBean.setContent("xxxxxxxxx");

                        datas.get((int) moreBean.getPosition()).getSecondLevelBeans().add(secondLevelBean);
                        CommentMultiActivity.this.dataSort(0);
                        bottomSheetAdapter.notifyDataSetChanged();

                        break;
                    case CommentEntity.TYPE_COMMENT_EMPTY:
                        CommentMultiActivity.this.initRefresh();
                        break;

                }

            }
        });
        //????????????
        if (mRecyclerViewUtil != null) mRecyclerViewUtil.initScrollListener(rv_dialog_lists);

        mKeyBoardListener = new SoftKeyBoardListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
            }

            @Override
            public void keyBoardHide(int height) {
                dismissInputDialog();
            }
        });
    }

    private void initInputTextMsgDialog(View view, final boolean isReply, final MultiItemEntity item, final int position) {
        dismissInputDialog();
        if (view != null) {
            offsetY = view.getTop();
            scrollLocation(offsetY);
        }
        if (inputTextMsgDialog == null) {
            inputTextMsgDialog = new InputTextMsgDialog(this, R.style.dialog);
            inputTextMsgDialog.setmOnTextSendListener(new InputTextMsgDialog.OnTextSendListener() {
                @Override
                public void onTextSend(String msg) {
                    addComment(isReply, item, position, msg);
                }

                @Override
                public void dismiss() {
                    //item???????????????
                    scrollLocation(-offsetY);
                }
            });
        }
        showInputTextMsgDialog();
    }

    //todo ????????????
    private void addComment(boolean isReply, MultiItemEntity item, final int position, String msg) {
        String userName = currentUser.getUsername();
        if (position >= 0) {
            int pos = 0;
            String replyUserName = "??????";
            if (item instanceof FirstLevelBean) {
                FirstLevelBean firstLevelBean = (FirstLevelBean) item;
                positionCount = (int) (firstLevelBean.getPositionCount() + 1);
                pos = (int) firstLevelBean.getPosition();
                replyUserName = firstLevelBean.getUserName();
                recommentWho = firstLevelBean.getId();

                System.out.println("--------recommendWho-----------");
                System.out.println("firstLevelBean="+firstLevelBean.getContent());
                System.out.println("firstLevelBean="+firstLevelBean.getId());


            } else if (item instanceof SecondLevelBean) {
                SecondLevelBean secondLevelBean = (SecondLevelBean) item;
                positionCount = (int) (secondLevelBean.getPositionCount() + 1);
                pos = (int) secondLevelBean.getPosition();
                replyUserName = secondLevelBean.getUserName();
                recommentWho = secondLevelBean.getId();

                System.out.println("--------recommendWho-----------");
                System.out.println("secondLevelBean="+secondLevelBean.getContent());
                System.out.println("secondLevelBean="+secondLevelBean.getId());
            }

            //todo ??????????????????
            SecondLevelBean secondLevelBean = new SecondLevelBean();
            secondLevelBean.setReplyUserName(replyUserName);
            secondLevelBean.setIsReply(isReply ? 1 : 0);
            secondLevelBean.setContent(msg);
            secondLevelBean.setHeadImg("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3370302115,85956606&fm=26&gp=0.jpg");
            secondLevelBean.setCreateTime(System.currentTimeMillis());
            secondLevelBean.setIsLike(0);
            secondLevelBean.setUserName(userName);
            secondLevelBean.setId(currentUser.getId());
            secondLevelBean.setPosition(positionCount);

            // todo ???2???????????????????????????
            new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        //1????????????????????????
                        FormBody formBody = new FormBody.Builder().add("comment",msg)
                                .add("recommentWho", String.valueOf(recommentWho))
                                .add("userId",String.valueOf(currentUser.getId()))
                                .add("tagId",String.valueOf(tagId)).build();
                        //2???????????????????????????
                        Request request = new Request.Builder().url("http://114.116.234.63:8080/comment/addComment").post(formBody).build();
                        //3???????????????????????????
                        Call call = okHttpClient.newCall(request);
                        //4?????????????????????,?????????????????????
                        Response response = call.execute();

                        //??????json?????????
                        String json = response.body().string();
                        JSONObject jsonObject = JSONObject.parseObject(json);
                        Integer code = jsonObject.getInteger("code");
//                        if (code == 200){
//                            Looper.prepare();
//                            Toast.makeText(CommentMultiActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
//                            Looper.loop();
//                        }else{
//                            Looper.prepare();
//                            Toast.makeText(CommentMultiActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
//                            Looper.loop();
//                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            datas.get(pos).getSecondLevelBeans().add(secondLevelBean);
            CommentMultiActivity.this.dataSort(0);
            bottomSheetAdapter.notifyDataSetChanged();
            rv_dialog_lists.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((LinearLayoutManager) rv_dialog_lists.getLayoutManager())
                            .scrollToPositionWithOffset(positionCount >= data.size() - 1 ? data.size() - 1
                                    : positionCount, positionCount >= data.size() - 1 ? Integer.MIN_VALUE : rv_dialog_lists.getHeight());
                }
            }, 100);

        } else {
            //todo ??????????????????
            FirstLevelBean firstLevelBean = new FirstLevelBean();
            firstLevelBean.setUserName(currentUser.getUsername());
//            firstLevelBean.setId(bottomSheetAdapter.getItemCount() + 1);
            firstLevelBean.setId(totalComents++);
            firstLevelBean.setHeadImg("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=1918451189,3095768332&fm=26&gp=0.jpg");
            firstLevelBean.setCreateTime(System.currentTimeMillis());
            firstLevelBean.setContent(msg);
            firstLevelBean.setLikeCount(0);
            firstLevelBean.setSecondLevelBeans(new ArrayList<SecondLevelBean>());
            datas.add(0, firstLevelBean);
            CommentMultiActivity.this.dataSort(0);
            bottomSheetAdapter.notifyDataSetChanged();
            rv_dialog_lists.scrollToPosition(0);

            // todo ???1???????????????????????????
            new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        //1????????????????????????
                        FormBody formBody = new FormBody.Builder().add("comment",msg)
                                .add("recommentWho", String.valueOf(-1))
                                .add("userId",String.valueOf(currentUser.getId()))
                                .add("tagId",String.valueOf(tagId)).build();
                        //2???????????????????????????
                        Request request = new Request.Builder().url("http://114.116.234.63:8080/comment/addComment").post(formBody).build();
                        //3???????????????????????????
                        Call call = okHttpClient.newCall(request);
                        //4?????????????????????,?????????????????????
                        Response response = call.execute();

                        //??????json?????????
                        String json = response.body().string();
                        JSONObject jsonObject = JSONObject.parseObject(json);
                        Integer code = jsonObject.getInteger("code");
//                        if (code == 200){
//                            Looper.prepare();
//                            Toast.makeText(CommentMultiActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
//                            Looper.loop();
//                        }else{
//                            Looper.prepare();
//                            Toast.makeText(CommentMultiActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
//                            Looper.loop();
//                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    private void dismissInputDialog() {
        if (inputTextMsgDialog != null) {
            if (inputTextMsgDialog.isShowing()) inputTextMsgDialog.dismiss();
            inputTextMsgDialog.cancel();
            inputTextMsgDialog = null;
        }
    }

    private void showInputTextMsgDialog() {
        inputTextMsgDialog.show();
    }

    private int getWindowHeight() {
        Resources res = getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

//    @Override
//    public void onLoadMoreRequested() {
//        if (datas.size() >= totalCount) {
//            bottomSheetAdapter.loadMoreEnd(false);
//            return;
//        }
//        FirstLevelBean firstLevelBean = new FirstLevelBean();
//
//
//        firstLevelBean = moreFirstLevelBean.get(0);
//        datas.add(firstLevelBean);
//        dataSort(datas.size() - 1);
//        bottomSheetAdapter.notifyDataSetChanged();
//        bottomSheetAdapter.loadMoreComplete();
//        return;
//    }

    // item??????
    public void scrollLocation(int offsetY) {
        try {
            rv_dialog_lists.smoothScrollBy(0, offsetY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ??????????????????????????????
     */
    public void closeDefaultAnimator(RecyclerView mRvCustomer) {
        if (null == mRvCustomer) return;
        mRvCustomer.getItemAnimator().setAddDuration(0);
        mRvCustomer.getItemAnimator().setChangeDuration(0);
        mRvCustomer.getItemAnimator().setMoveDuration(0);
        mRvCustomer.getItemAnimator().setRemoveDuration(0);
        ((SimpleItemAnimator) mRvCustomer.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    @Override
    protected void onDestroy() {
        if (mRecyclerViewUtil != null){
            mRecyclerViewUtil.destroy();
            mRecyclerViewUtil = null;
        }
        if (mKeyBoardListener != null) {
            mKeyBoardListener.setOnSoftKeyBoardChangeListener(null);
            mKeyBoardListener = null;
        }
        bottomSheetAdapter = null;
        super.onDestroy();
    }

    private void initTagView(){
        tagImage = (ImageView) findViewById(R.id.imageview_tag_image);
        text_tag_name = (TextView) findViewById(R.id.text_tag_name);
        text_description = (TextView) findViewById(R.id.text_description);

        text_tag_name.setText(tagName);
        text_description.setText(tagDescription);

        setImage();

    }

    private void setImage(){

        //?????????????????????
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String path = "http://114.116.234.63:8080/image" + tagImagePath;
                    Log.i("PATH", path);
                    //2:????????????????????????URL??????
                    URL url = new URL(path);
                    //3:????????????????????????????????????????????????????????????????????????
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //4:?????????????????????
                    conn.setRequestMethod("GET");
                    //??????????????????
                    conn.setConnectTimeout(8000);
                    //??????????????????
                    conn.setReadTimeout(8000);
                    //5:???????????????????????????????????????
                    conn.connect();
                    //??????????????????200?????????????????????
                    if (conn.getResponseCode() == 200) {
                        //?????????????????????????????????
                        InputStream is = conn.getInputStream();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        try {
            thread.join();
//            ??????tag??????
            boolean flag = true;
            while (flag){
                try{
                    Glide.with(this).load("http://114.116.234.63:8080/image/" + tagImagePath).into(tagImage);                    flag = false;
                }catch (Exception e){
                    flag = true;
                }
            }


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
