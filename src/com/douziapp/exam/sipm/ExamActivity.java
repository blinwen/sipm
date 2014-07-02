package com.douziapp.exam.sipm;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.douziapp.exam.data.CommDBUtil;
import com.douziapp.exam.data.SingleChoice;
import com.douziapp.exam.slidingmenu.LeftFragment;
import com.douziapp.exam.slidingmenu.RightFragment;
import com.douziapp.exam.slidingmenu.SlidingMenu;
import com.douziapp.exam.slidingmenu.ViewPageFragment;






















import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ExamActivity extends FragmentActivity {

	List<SingleChoice> mListSc;
	
	SlidingMenu 		mSlidingMenu;
	LeftFragment 		leftFragment;
	RightFragment 		rightFragment;
	ViewPageFragment 	viewPageFragment;
	
	ImageButton			mTopLeftBtn;
	ImageButton			mTopRightBtn;
	
	GridView			mGridSelExamItem;
	
	LinearLayout		mContentRoot 	= null;
	
	Button				mBtnNexItem		= null;
	
	long				mCurItemIndex 	= 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.exam_main);
		
		initSlidingMenu();
		
		initListener();
	}

	@Override
	protected void onStart() {
		
		super.onStart();
		
		CommDBUtil db_tool = new CommDBUtil("", 1);
		mListSc = db_tool.getAllSingleChoice(true);
		
		initView();
		
		initContent();
	}
	
	private String saveImage(byte[] i,String s){
		
		if(null == i || null == s){
			return null;
		}
		
		String	out = null;
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		try{
			String dir = this.getFilesDir().getPath();
			File file = new File(dir + "/" + s + ".png");
			fos = new FileOutputStream(file);  
			bos = new BufferedOutputStream(fos);  
            bos.write(i);
            
            out = file.getPath();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(null != bos){
				try {
					bos.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
			
			if(null != fos){
				
				try {
					fos.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		}
		
		return out;
	}
	
	private String	transImage(String strContetn){
		
		String strExp = "#\\{\\w{8}-(\\w{4}-){3}\\w{12}\\}";

		Pattern pat = Pattern.compile(strExp);
		
		Matcher mat = pat.matcher(strContetn);
		
		if(!mat.find()){
			return strContetn;
		}
		
		for(int i = 0; i < mat.groupCount(); i++){
			
			String strMatch = mat.group(i);
			String strGuid = strMatch.substring(2, strMatch.length()-1);
			String	strFile;
			CommDBUtil db_tool = new CommDBUtil("", 1);
			
			byte[] b = db_tool.i(strGuid, true);
			if(null != b){
				strFile = saveImage(b, strGuid);
				
				if(null != strFile){
					
					String temp = "<img src='"+strFile+"'>";
					
					strContetn = strContetn.replace(strMatch, temp);
				}

			}
		}
		
		return strContetn;
	}
	private CharSequence  transContent(String strContetn){
		
		ImageGetter imageGetter = new Html.ImageGetter(){

			@Override
			public Drawable getDrawable(String source) {
				
				Drawable drawable = null;  
                
	              drawable = Drawable.createFromPath(source); //显示本地图片  
	              int width = drawable.getIntrinsicWidth();
	              int height = drawable.getIntrinsicHeight();
	              
	              //{{
	              DisplayMetrics dm = new DisplayMetrics();

	              getWindowManager().getDefaultDisplay().getMetrics(dm);

	              int sw = (int) (dm.widthPixels * 0.9);

	              int sh = (int) (dm.heightPixels * 0.9);
	              
	              if(sw < width){
	            	  
	            	  height = (int) (sw * (height * 1.0 / width));
	            	  
	            	  width = sw;
	              }
	              //}}
	              drawable.setBounds(0, 0, width, height);  
	              
	              return drawable;
			}
			
		};
		
		strContetn = transImage(strContetn);
		
		Spanned sp = Html.fromHtml(strContetn, imageGetter, null);
		
		return sp;
	}
	
	private void initContent(){
		
		if(null == mListSc || mListSc.size() == 0){
			return;
		}
		
		if(mCurItemIndex > mListSc.size()){
			return;
		}
		
		SingleChoice sc = mListSc.get((int)mCurItemIndex - 1);
		
		String	strContent = sc.getvContent();
		TextView tv = new TextView(this);
		
		tv.setText(transContent(strContent));
		tv.setTextColor(Color.BLACK);
		
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

		mContentRoot.removeAllViews();
		mContentRoot.addView(tv,params);
	}
	
	private void initView(){
		
		mTopLeftBtn = (ImageButton)findViewById(R.id.ivTitleBtnLeft);
		mTopRightBtn = (ImageButton)findViewById(R.id.ivTitleBtnRigh);
		
		mGridSelExamItem = (GridView)findViewById(R.id.grid_sel_exam_item);
		
		mContentRoot	= (LinearLayout)findViewById(R.id.content_root);
		
		mBtnNexItem		= (Button)findViewById(R.id.next_exam_item);
		
		mGridSelExamItem.setAdapter(new SelExamItemAdapter(mListSc,this));
		
		mGridSelExamItem.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id ) {
				
				
				if(position + 1 > mListSc.size()){
					return;
				}
				
				mCurItemIndex = position + 1;
				
				initContent();
				
				mSlidingMenu.showLeftView();
			}
		});
		
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
		
		mBtnNexItem.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(null == mListSc || mListSc.size() == 0){
					return;
				}
				
				if(mCurItemIndex >= mListSc.size() ){
					
					mCurItemIndex = mListSc.size();
					
					Toast.makeText(ExamActivity.this, "已经是最后一题了", Toast.LENGTH_LONG).show();
					
					return;
				}
				
				mCurItemIndex++;
						
				initContent();
			}
		});
	}
	
	private void initSlidingMenu() {
		
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
		
		List<?>	mData;
		Context	mContext;
		
		public SelExamItemAdapter(List<?> data,Context context){
			super();
			mData = data;
			mContext = context;
		}
		
		public boolean isEmpty(){
			
			return mData == null ? true: false;
		}
		
		@Override
		public int getCount() {
			
			if(isEmpty()){
				return 0;
			}
			return mData.size();
		}

		@Override
		public Object getItem(int i) {
			
			if(isEmpty()){
				return 0;
			}
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
			
			title.setText(i+1 + "题");
			
			return root;
		}
		
	}
}
