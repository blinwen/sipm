package com.douziapp.exam.util;

import java.util.ArrayList;
import java.util.List;

import com.douziapp.exam.sipm.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class CommUI {

	static public void showExamGuide(Context context,View view){
		
		SharedPreferences exam_info = context.getSharedPreferences("exam", 0);
		boolean is_first = exam_info.getBoolean("is_first", true);
		
		if(is_first){
			
			exam_info.edit().putBoolean("is_first", false).commit();
		}else{
			return;
		}
		
		View contentView;
		final PopupWindow popupWindow;
		
		LayoutInflater li = LayoutInflater.from(context);
		contentView = li.inflate(R.layout.exam_rookie_guide, null);
		
		popupWindow = new PopupWindow(contentView,WindowManager.LayoutParams.MATCH_PARENT,
										WindowManager.LayoutParams.MATCH_PARENT);  
		
		contentView.setClickable(true);
		
		contentView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				popupWindow.dismiss();
				
			}
		});
		
		popupWindow.setBackgroundDrawable(null);
		popupWindow.setOutsideTouchable(true);
		
		popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
		
		
		popupWindow.update();
	}
	
	public void showExamBankManager(Context context,View view){
		
		View contentView;
		final PopupWindow popupWindow;
		ExamBankmAdapter adapter;
		List<String> data = new ArrayList<String>();

		LayoutInflater li = LayoutInflater.from(context);
		contentView = li.inflate(R.layout.main_exam_bank_manager, null);

		for (int ii = 0; ii < 10; ii++) {
			data.add("2012年上半年下午试题");
		}

		ExpandableListView exlistview = (ExpandableListView) contentView
				.findViewById(R.id.exam_bank_listview);

		adapter = new ExamBankmAdapter(context, data);

		exlistview.setAdapter(adapter);

		popupWindow = new PopupWindow(contentView,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);

		contentView.setClickable(true);

		popupWindow.setBackgroundDrawable(null);
		popupWindow.setOutsideTouchable(true);

		popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

		popupWindow.update();
	}
	
	class ExamBankmAdapter extends BaseExpandableListAdapter{

		List<String>	mData 		= null;
		Context			mContext 	= null;
		
		
		public ExamBankmAdapter(Context context,List<String> data){
			
			mContext	= context;
			mData 		= data;
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
			
			TextView text_view = new TextView(mContext);
			
			text_view.setText("管理");
			
			return text_view;
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
