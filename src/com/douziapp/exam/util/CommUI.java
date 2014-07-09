package com.douziapp.exam.util;

import com.douziapp.exam.sipm.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.PopupWindow;

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
}
