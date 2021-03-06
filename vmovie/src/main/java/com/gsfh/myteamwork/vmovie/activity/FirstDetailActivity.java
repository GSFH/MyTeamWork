package com.gsfh.myteamwork.vmovie.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.google.gson.Gson;
import com.gsfh.myteamwork.vmovie.R;
import com.gsfh.myteamwork.vmovie.bean.FirstDetailBean;
import com.gsfh.myteamwork.vmovie.bean.MainDetailNewViewBean;
import com.gsfh.myteamwork.vmovie.bean.VideoListBean;
import com.gsfh.myteamwork.vmovie.util.IOKCallBack;
import com.gsfh.myteamwork.vmovie.util.OkHttpTool;
import com.gsfh.myteamwork.vmovie.util.URLConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

public class FirstDetailActivity extends AppCompatActivity {

    private FirstDetailBean.DataBean firstDetailData = new FirstDetailBean.DataBean();
    private List<FirstDetailBean.DataBean.ContentBean.VideoBean> videoAdressList = new ArrayList<>();
    private JCVideoPlayerStandard jcVideoPlayer;
    private BridgeWebView webView;
    private TextView like_counts;
    private TextView share_counts;
    private TextView comment_counts;
    private TextView cache_btn;
    private String from;
    private boolean showVideoPlayer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_detail);

        initView();
        initData();
        initBridge();
    }

    private void initView() {

        jcVideoPlayer = (JCVideoPlayerStandard) findViewById(R.id.first_detail_videoplayer);

        webView = (BridgeWebView) findViewById(R.id.first_detail_webview);

        like_counts = (TextView) findViewById(R.id.first_detail_bottom_like_counts);
        share_counts = (TextView) findViewById(R.id.first_detail_bottom_share_counts);
        comment_counts = (TextView) findViewById(R.id.first_detail_bottom_comment_counts);
        //cache_btn = (TextView) findViewById(R.id.first_detail_bottom_cache);
    }

    private void initData() {

        String postid = getIntent().getStringExtra("id");

        postRequest(postid);

    }

    private void postRequest(final String postid) {

        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("postid", postid);

        OkHttpTool.newInstance().post(paramMap).start(URLConstants.LATEST_DETAIL_URL).callback(new IOKCallBack() {
            @Override
            public void success(String result) {

                if (null == result) {
                    return;
                }

                Gson gson = new Gson();
                FirstDetailBean firstDetailBean = gson.fromJson(result, FirstDetailBean.class);

                firstDetailData = firstDetailBean.getData();

                initPageDetail(postid);
            }
        });
    }

    private void initPageDetail(String postid) {

        String webAdress = "http://app.vmoiver.com/"+postid+"?qingapp=app_new";
        videoAdressList = firstDetailData.getContent().getVideo();

        //判断页面类型
        if (videoAdressList.size() <= 2){

            showVideoPlayer = true;

            jcVideoPlayer.setVisibility(View.VISIBLE);
//            cache_btn.setVisibility(View.VISIBLE);
            String videoAdress = videoAdressList.get(0).getQiniu_url();
            jcVideoPlayer.setUp(videoAdress, "");

        }

        //加载网页
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(webAdress);

        String count_like = firstDetailData.getCount_like();
        String count_share = firstDetailData.getCount_share();
        String count_comment = firstDetailData.getCount_comment();

        like_counts.setText(count_like);
        share_counts.setText(count_share);
        comment_counts.setText(count_comment);

    }

    private void initBridge() {

        webView.registerHandler("handlerNewView", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                //data ---->  {"id":3958,"type":0}
                if (null == data) {
                    return;
                }

                Gson gson = new Gson();
                MainDetailNewViewBean newViewBean = gson.fromJson(data, MainDetailNewViewBean.class);

                String id = newViewBean.getId();
                Intent intent = new Intent(FirstDetailActivity.this, FirstDetailActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);

                function.onCallBack("submitFromWeb exe, response data from Java");
            }
        });

        webView.registerHandler("handlerVideo", new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                //data ---->  {"videoIdx":0,"type":0}
                if (null == data) {
                    return;
                }

                Gson gson = new Gson();
                VideoListBean videoListBean = gson.fromJson(data,VideoListBean.class);

                if(!showVideoPlayer){

                    jcVideoPlayer.setVisibility(View.VISIBLE);
                }

                int videoIdx = videoListBean.getVideoIdx();
                String videoAdress = videoAdressList.get(videoIdx).getQiniu_url();

                jcVideoPlayer.release();
                jcVideoPlayer.setUp(videoAdress,"");

                function.onCallBack("submitFromWeb exe, response data from Java");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        jcVideoPlayer.releaseAllVideos();
    }

    public void onClick(View view){

        Intent intent = new Intent(FirstDetailActivity.this,LoginActivity.class);
        startActivity(intent);
    }

}
