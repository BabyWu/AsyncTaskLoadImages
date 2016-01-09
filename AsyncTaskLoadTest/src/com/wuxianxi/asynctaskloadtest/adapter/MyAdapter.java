package com.wuxianxi.asynctaskloadtest.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wuxianxi.asynctaskloadtest.untils.ImageLoad;
import com.wuxianxi.asynctaskloadtest.untils.NewsBean;
import com.wuxianxi.asynctaskloadtest.view.R;

public class MyAdapter extends BaseAdapter implements AbsListView.OnScrollListener {
	private List<NewsBean> mListNews;
	private LayoutInflater mInflater;
	private ImageLoad mImageLoad;
	private int mStartItem;
	private int mEndItem;	
	public static String[] mURLs;
	private ListView mListView;
	private boolean mFirst;

	public MyAdapter(Context context, List<NewsBean> listNews, ListView listView) {
		// TODO Auto-generated constructor stub
		mInflater = LayoutInflater.from(context);
		mListNews = listNews;
		mImageLoad = new ImageLoad();
		mListView = listView;
		mFirst = true;
		
		mURLs = new String[listNews.size()];		
		for (int i = 0; i < listNews.size(); i++) {
			mURLs[i] = listNews.get(i).newsIconUrl;
		}
		
		mListView.setOnScrollListener(this);
	}

	@Override
	public int getCount() {
		return mListNews.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mListNews.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.list_item, null);
			viewHolder.imgIcon = (ImageView) convertView
					.findViewById(R.id.img_icon);
			viewHolder.tvTitle = (TextView) convertView
					.findViewById(R.id.tv_title);
			viewHolder.tvContent = (TextView) convertView
					.findViewById(R.id.tv_content);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.imgIcon.setImageResource(R.drawable.ic_launcher);
		String url = mListNews.get(position).newsIconUrl;
		viewHolder.imgIcon.setTag(url);
		mImageLoad.showImageByAsyncTask(viewHolder.imgIcon, url);
		viewHolder.tvTitle.setText(mListNews.get(position).newsTitle);
		viewHolder.tvContent.setText(mListNews.get(position).newsContent);
		return convertView;
	}

	class ViewHolder {
		public ImageView imgIcon;
		public TextView tvTitle;
		public TextView tvContent;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		mStartItem = firstVisibleItem;
		mEndItem = firstVisibleItem + visibleItemCount;
		
		if (mFirst && visibleItemCount>0) {
			mImageLoad.LoadSeenImages(mStartItem, mEndItem, mListView);
			mFirst = false;
		}
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE) {
			mImageLoad.LoadSeenImages(mStartItem, mEndItem, mListView);
		} else {
			mImageLoad.CancelAllTasks();
		}
		
	}

}
