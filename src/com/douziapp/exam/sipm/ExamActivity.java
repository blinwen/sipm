package com.douziapp.exam.sipm;


import com.douziapp.exam.slidingmenu.LeftFragment;
import com.douziapp.exam.slidingmenu.RightFragment;
import com.douziapp.exam.slidingmenu.SlidingMenu;
import com.douziapp.exam.slidingmenu.ViewPageFragment;
import com.douziapp.exam.slidingmenu.ViewPageFragment.MyPageChangeListener;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

public class ExamActivity extends FragmentActivity {

	@Override
	protected void onStart() {
		
		super.onStart();
		
		mTopLeftBtn = (ImageButton)findViewById(R.id.ivTitleBtnLeft);
		mTopRightBtn = (ImageButton)findViewById(R.id.ivTitleBtnRigh);
		
		mTopLeftBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				showLeft();
			}
		});
		
		mTopRightBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				showRight();
			}
		});
	}

	SlidingMenu mSlidingMenu;
	LeftFragment leftFragment;
	RightFragment rightFragment;
	ViewPageFragment viewPageFragment;
	
	ImageButton		mTopLeftBtn;
	ImageButton		mTopRightBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		
		init();
		
		initListener();
	}

	private void init() {
		
		mSlidingMenu = (SlidingMenu) findViewById(R.id.slidingMenu);
		mSlidingMenu.setLeftView(getLayoutInflater().inflate(
				R.layout.left_frame, null));
		mSlidingMenu.setRightView(getLayoutInflater().inflate(
				R.layout.right_frame, null));
		mSlidingMenu.setCenterView(getLayoutInflater().inflate(
				R.layout.center_frame, null));

		FragmentTransaction t = this.getSupportFragmentManager()
				.beginTransaction();
		leftFragment = new LeftFragment();
		t.replace(R.id.left_frame, leftFragment);

		rightFragment = new RightFragment();
		t.replace(R.id.right_frame, rightFragment);

		viewPageFragment = new ViewPageFragment();
		t.replace(R.id.center_frame, viewPageFragment);
		t.commit();
		

	}
	
	private void initListener() {
		/*
		viewPageFragment.setMyPageChangeListener(new MyPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				if(viewPageFragment.isFirst()){
					mSlidingMenu.setCanSliding(true,false);
				}else if(viewPageFragment.isEnd()){
					mSlidingMenu.setCanSliding(false,true);
				}else{
					mSlidingMenu.setCanSliding(false,false);
				}
				
				mSlidingMenu.setCanSliding(true,true);
			}
		});
		
		*/
	}
	
	public void showLeft() {
		mSlidingMenu.showLeftView();
	}

	public void showRight() {
		mSlidingMenu.showRightView();
	}
}
