package com.wuxianxi.asynctaskloadtest.untils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import com.wuxianxi.asynctaskloadtest.adapter.MyAdapter;
import com.wuxianxi.asynctaskloadtest.view.R;

public class ImageLoad {
	public LruCache<String, Bitmap> mCaches;
	private ListView mListView;
	private Set<ImageAsyncTask> mTasks;

	public ImageLoad() {
		mTasks = new HashSet<ImageLoad.ImageAsyncTask>();
		
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheMenory = maxMemory / 4;
		mCaches = new LruCache<String, Bitmap>(cacheMenory) {

			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getByteCount();
			}
		};
	}

	public void addBitmapToCache(String url, Bitmap bitmap) {
		if (getBitmapFromCache(url) == null) {
			mCaches.put(url, bitmap);
		}
	}

	public Bitmap getBitmapFromCache(String url) {
		return mCaches.get(url);
	}

	public void showImageByAsyncTask(ImageView imageView, String url) {
		Bitmap bitmap = getBitmapFromCache(url);
		if (bitmap == null) {
			imageView.setImageResource(R.drawable.ic_launcher);
		} else {
			imageView.setImageBitmap(bitmap);
		}
	}
	
	public void LoadSeenImages(int startItem, int endItem, ListView listview) {
		mListView =  listview;
		for (int i = startItem; i < endItem; i++) {
			String url = MyAdapter.mURLs[i];
			Bitmap bitmap = getBitmapFromCache(url);
			if (bitmap == null) {
				ImageAsyncTask task = new ImageAsyncTask(url);
				task.execute(url);
				mTasks.add(task);
			} else {
				ImageView imageView = (ImageView) mListView.findViewWithTag(url);
				imageView.setImageBitmap(bitmap);
			}
		}
	}
	
	public void CancelAllTasks() {
		if (mTasks != null) {
			for (ImageAsyncTask task:mTasks) {
				task.cancel(false);
			}
		}
	}

	class ImageAsyncTask extends AsyncTask<String, Void, Bitmap> {
//		private ImageView asImageView;
		private String asUrl;

		public ImageAsyncTask(String url) {
//			asImageView = imageView;
			asUrl = url;
		}

		@Override
		protected Bitmap doInBackground(String... arg0) {
			Bitmap bitmap = getBitmapFromURL(arg0[0]);
			if (bitmap != null) {
				addBitmapToCache(arg0[0], bitmap);
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			super.onPostExecute(result);
			ImageView imageView = (ImageView) mListView.findViewWithTag(asUrl);
			if (imageView != null && result != null) {
				imageView.setImageBitmap(result);
			}
			mTasks.remove(this);
		}

	}

	private Bitmap getBitmapFromURL(String urlString) {
		Bitmap bitmap;
		InputStream is = null;
		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			is = new BufferedInputStream(connection.getInputStream());
			bitmap = BitmapFactory.decodeStream(is);
			connection.disconnect();
			return bitmap;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

}
