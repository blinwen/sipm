package com.douziapp.exam.sipm;

import java.util.ArrayList;
import java.util.List;

import com.douziapp.exam.slidingmenu.LeftFragment;
import com.douziapp.exam.slidingmenu.RightFragment;
import com.douziapp.exam.slidingmenu.SlidingMenu;
import com.douziapp.exam.slidingmenu.ViewPageFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.exam_main);
		
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

		mExamIndexAdapter = new ExamIndexAdapter(this);
		mExamIndex.setAdapter(mExamIndexAdapter);
		
		mExamIndex.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				Intent intent = new Intent(MainActivity.this,ExamActivity.class);
				
				
				startActivity(intent);
				
				//overridePendingTransition(android.R.anim.slide_in_left,
				//		android.R.anim.slide_out_right);
			}
		});
	}

	private void init() {
		
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
	
	private static class ViewHolderItem{
		TextView 	name_view;
	}
	
	private class ExamIndexAdapter extends BaseAdapter{

		private Context	mContext;
		
		private List<String>	mData;
		
		
		
		public ExamIndexAdapter(Context context){
			this.mContext = context;
			
			mData = new ArrayList<String>();
			
			mData.add("2009年上半年上午试题分析与解答");
			mData.add("2009年上半年上午试题分析与解答");
			mData.add("2009年上半年上午试题分析与解答");
			mData.add("2009年上半年上午试题分析与解答");
			
			mData.add("2010年上半年上午试题分析与解答");
			mData.add("2010年上半年上午试题分析与解答");
			mData.add("2010年上半年上午试题分析与解答");
			mData.add("2010年上半年上午试题分析与解答");
			
			mData.add("2011年上半年上午试题分析与解答");
			mData.add("2011年上半年上午试题分析与解答");
			mData.add("2011年上半年上午试题分析与解答");
			mData.add("2011年上半年上午试题分析与解答");
			
			mData.add("2012年上半年上午试题分析与解答");
			mData.add("2012年上半年上午试题分析与解答");
			mData.add("2012年上半年上午试题分析与解答");
			mData.add("2012年上半年上午试题分析与解答");
			
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
