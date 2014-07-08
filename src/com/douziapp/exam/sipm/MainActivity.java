package com.douziapp.exam.sipm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.douziapp.exam.slidingmenu.LeftFragment;
import com.douziapp.exam.slidingmenu.RightFragment;
import com.douziapp.exam.slidingmenu.SlidingMenu;
import com.douziapp.exam.slidingmenu.ViewPageFragment;
import com.douziapp.exam.util.CommDBUtil;
import com.douziapp.exam.util.CommUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {

	RightFragment		rightFragment;
	LeftFragment 		leftFragment;
	SlidingMenu 		mSlidingMenu;
	ViewPageFragment 	centerFragment;
	
	TextView			mImgExamBankManager;
	
	ListView			mExamIndex;
	ExamIndexAdapter	mExamIndexAdapter;
	List<String>		mExamData			= new ArrayList<String>();
	
	Handler				mHandler			= null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.exam_main);
		
		initSlidingMenu();
		
		initData();
		
		init();
	}

	@Override
	protected void onStart() {
		
		super.onStart();
		
		mImgExamBankManager = (TextView)findViewById(R.id.ico_exam_bank_manager);
		
		Typeface font = Typeface.createFromAsset(getAssets(), "font/glyphicons.ttf");
		mImgExamBankManager.setTypeface(font);
		
		mImgExamBankManager.setText(getString(R.string.ico_exam_bank_manager));
		
		mExamIndex = (ListView)findViewById(R.id.main_exam_index_view);

		mExamIndexAdapter = new ExamIndexAdapter(this,mExamData);
		mExamIndex.setAdapter(mExamIndexAdapter);
		
		mExamIndex.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				Intent intent = new Intent(MainActivity.this,ExamActivity.class);
				
				String db_name = mExamData.get(arg2);
				
				intent.putExtra("db", db_name);
				
				startActivity(intent);
				
				//overridePendingTransition(android.R.anim.slide_in_left,
				//		android.R.anim.slide_out_right);
			}
		});
		
		
	}

	private void getExamDBData(){
		try {
			String strDataPath = CommDBUtil.getDBPath(this);
			String[] list_file = new File(strDataPath).list();
			
			if(null == list_file){
				return;
			}
			
			for (String string : list_file) {
				mExamData.add(string);
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
	private void init(){
		
		mHandler = new MyHandler();
	}
	
	private void initData(){
		
		CommDBUtil.checkDB(this);
		
		getExamDBData();
	}
	
	private void initSlidingMenu() {
		
		mSlidingMenu = (SlidingMenu) findViewById(R.id.slidingMenu);
		mSlidingMenu.setLeftView(getLayoutInflater().inflate(
				R.layout.left_frame, null));
		mSlidingMenu.setRightView(getLayoutInflater().inflate(
				R.layout.right_frame, null));
		mSlidingMenu.setCenterView(getLayoutInflater().inflate(
				R.layout.center_frame, null));

		mSlidingMenu.setAutoGesture(false);
		mSlidingMenu.setCanSliding(true, false);
		
		FragmentTransaction t = this.getSupportFragmentManager()
				.beginTransaction();
		
		leftFragment = new LeftFragment();
		leftFragment.setResource(R.layout.main_left);
		t.replace(R.id.left_frame, leftFragment);

		rightFragment = new RightFragment();
		t.replace(R.id.right_frame, rightFragment);
		
		centerFragment = new ViewPageFragment();
		centerFragment.setResource(R.layout.activity_main);
		t.replace(R.id.center_frame, centerFragment);
		
		t.commit();

		
	}
	
	
	
	private class CheckApkUpdateThread extends Thread{

		@Override
		public void run() {
			
			super.run();
			
			String rtn = CommUtil.checkApkUpdate();
		}
		
	}
	
	private static class MyHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			
			super.handleMessage(msg);
			
			switch(msg.what){
			
			}
		}
		
	}
	
	private static class ViewHolderItem{
		TextView 	name_view;
	}
	
	private class ExamIndexAdapter extends BaseAdapter{

		private Context	mContext;
		
		private List<String>	mData;
		
		
		public ExamIndexAdapter(Context context,List<String> data){
			
			this.mContext = context;
			mData = new ArrayList<String>();
			
			if(null != data ){
				
				for(int ii = 0; ii < data.size(); ii++){
					
					String db_name 			= data.get(ii);
					String	db_zhcn_name 	= CommUtil.getDBZhCNName(db_name);
					
					if(null == db_zhcn_name || db_zhcn_name.length() < 1){
						continue;
					}
					
					mData.add(db_zhcn_name);
				}
			}
			
			
			
		}
		
		@Override
		public int getCount() {
			
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolderItem viewHolder;
			
			if(null == convertView ){
				LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
				
				convertView = inflater.inflate(R.layout.main_exam_index_item, null);
				
				viewHolder = new ViewHolderItem();

				viewHolder.name_view = (TextView)convertView.findViewById(R.id.exam_name);

				convertView.setTag(viewHolder);
				
			}else{
				viewHolder = (ViewHolderItem)convertView.getTag();
			}

			String	strName = mData.get(position);
			viewHolder.name_view.setText(strName);
			
			return convertView;
		}
		
	}
}
