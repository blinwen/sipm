package com.douziapp.exam.util;

import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.PopupWindow;

public class CommUI {

	static public void showExamGuide(){
		PopupWindow popupWindow;
		
		popupWindow = new PopupWindow(null,WindowManager.LayoutParams.MATCH_PARENT,
										WindowManager.LayoutParams.MATCH_PARENT);  
		
		popupWindow.setBackgroundDrawable(null);
		popupWindow.showAtLocation(null, Gravity.CENTER, 0, 0);
	}
}
