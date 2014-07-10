package com.douziapp.exam.sipm;

import java.util.ArrayList;
import java.util.List;








import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

public class ExamBankManagerActivity extends Activity {

	private	List<String>				mData 		= new ArrayList<String>();
	private	ExamBankmAdapter			mAdapter 	= null;
	private	ExpandableListView			mListview	= null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main_exam_bank_manager);
		
		init();
	}
	
	private void init(){
		
		for (int ii = 0; ii < 10; ii++) {
			mData.add("2012年上半年下午试题");
		}

		mListview = (ExpandableListView) findViewById(R.id.exam_bank_listview);
		
		TextView	title = (TextView)findViewById(R.id.ivTitleName);
		ImageButton btn_back	= (ImageButton)findViewById(R.id.ivTitleBtnLeft);
		
		title.setText(R.string.exam_manager);
		btn_back.setBackgroundResource(R.drawable.foot_button_bg_selector);
		btn_back.setImageResource(R.drawable.btn_back_selector);
		btn_back.setPadding(1, 0, 20, 0);
		btn_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				finish();
			}
		});

		mAdapter = new ExamBankmAdapter(this, mData);

		mListview.setAdapter(mAdapter);
	}

	class ExamBankmAdapter extends BaseExpandableListAdapter{

		List<String>	mData 		= null;
		Context			mContext 	= null;
		Typeface 		mFont		= null;
		
		public ExamBankmAdapter(Context context,List<String> data){
			
			mContext	= context;
			mData 		= data;
			
			mFont = Typeface.createFromAsset(getAssets(), "font/glyphicons.ttf");
		}
		
		@Override
		public Object getChild(int arg0, int arg1) {
			
			return arg1;
		}

		@Override
		public long getChildId(int arg0, int arg1) {
			
			return arg1;
		}

		@Override
		public View getChildView(int arg0, int arg1, boolean arg2, View arg3,
				ViewGroup arg4) {
			
			LayoutInflater li = LayoutInflater.from(mContext);
			
			View view = li.inflate(R.layout.main_exam_bank_manager_item_child, null);
			
			TextView del_view = (TextView)view.findViewById(R.id.icon_exam_del);
			TextView down_view	= (TextView)view.findViewById(R.id.icon_exam_download);
			
			del_view.setTypeface(mFont);
			del_view.setTextColor(Color.RED);

			down_view.setTypeface(mFont);
			down_view.setTextColor(Color.rgb(0x2f, 0x5f, 0x90));
			
			del_view.setText(getString(R.string.ico_del));
			down_view.setText(getString(R.string.ico_download));
			
			return view;
		}

		@Override
		public int getChildrenCount(int arg0) {
			// TODO Auto-generated method stub
			return 1;
		}

		@Override
		public Object getGroup(int arg0) {
			
			return mData.get(arg0);
		}

		@Override
		public int getGroupCount() {
			
			if(null == mData)
				return 0;
			else
				return mData.size();
		}

		@Override
		public long getGroupId(int arg0) {
			
			return arg0;
		}

		@Override
		public View getGroupView(int arg0, boolean arg1, View arg2,
				ViewGroup arg3) {
			
			View	view;
			LayoutInflater li = LayoutInflater.from(mContext);
			
			view = li.inflate(R.layout.main_exam_bank_manager_item, null);
			
			TextView title = (TextView)view.findViewById(R.id.exam_item_title);
			
			title.setText(mData.get(arg0));
			
			return view;
		}

		@Override
		public boolean hasStableIds() {
			
			return false;
		}

		@Override
		public boolean isChildSelectable(int arg0, int arg1) {
			
			return false;
		}
		
	}
	
}
