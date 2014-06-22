package com.douziapp.exam.sipm;


import java.util.ArrayList;

import com.douziapp.exam.slidingmenu.LeftFragment;
import com.douziapp.exam.slidingmenu.RightFragment;
import com.douziapp.exam.slidingmenu.SlidingMenu;
import com.douziapp.exam.slidingmenu.ViewPageFragment;
import com.douziapp.exam.slidingmenu.ViewPageFragment.MyPageChangeListener;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

public class ExamActivity extends FragmentActivity {

	@Override
	protected void onStart() {
		
		super.onStart();
		
		initView();
	}
	
	SlidingMenu mSlidingMenu;
	LeftFragment leftFragment;
	RightFragment rightFragment;
	ViewPageFragment viewPageFragment;
	
	ImageButton		mTopLeftBtn;
	ImageButton		mTopRightBtn;
	
	GridView		mGridSelExamItem;
	
	ArrayList<Integer>	mData = new ArrayList<Integer>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		
		init();
		
		initListener();
	}

	private void initView(){
		
		mData.clear();
		for(int ii = 1; ii <= 75; ii++){
			mData.add(ii);
		}
		
		mTopLeftBtn = (ImageButton)findViewById(R.id.ivTitleBtnLeft);
		mTopRightBtn = (ImageButton)findViewById(R.id.ivTitleBtnRigh);
		
		mGridSelExamItem = (GridView)findViewById(R.id.grid_sel_exam_item);
		
		mGridSelExamItem.setAdapter(new SelExamItemAdapter(mData,this));
		
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
	
	
	
	public class SelExamItemAdapter extends BaseAdapter{
		
		ArrayList<Integer>	mData;
		Context	mContext;
		
		public SelExamItemAdapter(ArrayList<Integer>data,Context context){
			super();
			mData = data;
			mContext = context;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mData.size();
		}

		@Override
		public Object getItem(int i) {
			// TODO Auto-generated method stub
			return mData.get(i);
		}

		@Override
		public long getItemId(int i) {
			// TODO Auto-generated method stub
			return i;
		}

		@Override
		public View getView(int i, View view, ViewGroup viewgroup) {
			
			LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(
										Context.LAYOUT_INFLATER_SERVICE);
			
			View root = inflater.inflate(R.layout.grid_sel_item, null);
			
			TextView title = (TextView)root.findViewById(R.id.exam_item_index);
			
			title.setText(mData.get(i) + "Ã‚");
			
			return root;
		}
		
	}
}
