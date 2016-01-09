package com.wuxianxi.asynctaskloadtest.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import com.wuxianxi.asynctaskloadtest.adapter.MyAdapter;
import com.wuxianxi.asynctaskloadtest.untils.NewsBean;

public class ListActivity extends Activity {

	private ListView mListView;
	private final String URL = "http://www.imooc.com/api/teacher?type=4&num=30";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		
		mListView = (ListView) findViewById(R.id.lv_main);
		
		new NewsAsyncTask().execute(URL);
	}

	class NewsAsyncTask extends AsyncTask<String, Void, List<NewsBean>> {

		@Override
		protected List<NewsBean> doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			return getJSONData(arg0[0]);
		}

		@Override
		protected void onPostExecute(List<NewsBean> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			MyAdapter adapter = new MyAdapter(ListActivity.this, result, mListView);
			mListView.setAdapter(adapter);
		}
		
	}
	
	private List<NewsBean> getJSONData(String url) {
		List<NewsBean> newsBeanList = new ArrayList<NewsBean>();
		try {
			String jsonString = readStream(new URL(url).openStream());
			JSONObject jsonObject;
			NewsBean newsBean;
			jsonObject = new JSONObject(jsonString);
			JSONArray jsonArray = jsonObject.getJSONArray("data");
			for (int i=0; i<jsonArray.length(); i++) {
				jsonObject = jsonArray.getJSONObject(i);
				newsBean = new NewsBean();
				newsBean.newsIconUrl = jsonObject.getString("picSmall");
				newsBean.newsTitle = jsonObject.getString("name");
				newsBean.newsContent = jsonObject.getString("description");
				newsBeanList.add(newsBean);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newsBeanList;
	}

	private String readStream(InputStream is) {
		// TODO Auto-generated method stub
		String result = "";
		try {
			String line = "";
			InputStreamReader isr = new InputStreamReader(is, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			while ((line = br.readLine()) != null) {
				result += line;
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
}
