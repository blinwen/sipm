package com.douziapp.exam.sipm;

import com.douziapp.exam.slidingmenu.LeftFragment;
import com.douziapp.exam.slidingmenu.RightFragment;
import com.douziapp.exam.slidingmenu.SlidingMenu;
import com.douziapp.exam.slidingmenu.ViewPageFragment;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {

	RightFragment		rightFragment;
	LeftFragment 		leftFragment;
	SlidingMenu 		mSlidingMenu;
	ViewPageFragment 	centerFragment;
	
	TextView			mImgExamBankManager;
	
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
}
