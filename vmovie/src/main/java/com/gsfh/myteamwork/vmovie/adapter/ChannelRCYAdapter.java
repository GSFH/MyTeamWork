package com.gsfh.myteamwork.vmovie.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gsfh.myteamwork.vmovie.R;
import com.gsfh.myteamwork.vmovie.activity.ChannelSort_HotActivity;
import com.gsfh.myteamwork.vmovie.bean.ChannalBean;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * @ 董传亮
 * 首页的频道页面的显示 RCY 适配器
 * Created by admin on 2016/7/8.
 */
public class ChannelRCYAdapter extends RecyclerView.Adapter<ChannelRCYAdapter.Holder> implements View.OnClickListener {
    private Context mContext;
    private List<ChannalBean.DataBean> mBeanlist = new ArrayList();

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private String tab;
    private String type;
    private String cate_id;

    private int mposition=-1;



    public ChannelRCYAdapter(Context context, List<ChannalBean.DataBean> mChannalList) {
        this.mContext = context;
        this.mBeanlist = mChannalList;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_channel_rcyv_item, parent, false);
        Holder holder = new Holder(view);
        view.setOnClickListener(this);

        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {
        if (holder != null) {
            String name = mBeanlist.get(position).getCatename();
            String mURL = mBeanlist.get(position).getIcon();
            Picasso.with(mContext).load(mURL).into(holder.imageView);
            holder.textView.setText("#" + name + "#");
        }
        holder.itemView.setTag(position);//监听事件

    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        //  return 10;
        return mBeanlist == null ? 0 : mBeanlist.size();
    }

    @Override
    public void onClick(View v) {
        mposition= (int) v.getTag();
        type = mBeanlist.get(mposition).getCatename();
        tab = mBeanlist.get(mposition).getAlias();
        cate_id = mBeanlist.get(mposition).getCateid();
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v, tab, type, cate_id);
        }
    }


    class Holder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView textView;

        public Holder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.item_channel_show_im);
            textView = (TextView) itemView.findViewById(R.id.item_channel_rcyv_item_tv);

        }
    }

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, String tab, String type, String cate_id);
    }


}
