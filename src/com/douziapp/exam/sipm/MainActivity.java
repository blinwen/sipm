package com.douziapp.exam.sipm;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.douziapp.exam.slidingmenu.LeftFragment;
import com.douziapp.exam.slidingmenu.RightFragment;
import com.douziapp.exam.slidingmenu.SlidingMenu;
import com.douziapp.exam.slidingmenu.ViewPageFragment;
import com.douziapp.exam.util.CommDBUtil;
import com.douziapp.exam.util.CommUI;
import com.douziapp.exam.util.CommUtil;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends FragmentActivity {

	RightFragment		rightFragment;
	LeftFragment 		leftFragment;
	SlidingMenu 		mSlidingMenu;
	ViewPageFragment 	centerFragment;
	
	ImageButton			mTopBtnLeft;
	
	TextView			mImgExamBankManager;
	TextView			mImgCheckNewVersion;
	
	TextView			mTextCheckNewVersion;
	
	View				mViewMainLeftBankManager;
	View				mViewMainLeftCheckVersion;
	
	CheckApkUpdateThread	mCheckNewThread;
	
	ListView			mExamIndex;
	ExamIndexAdapter	mExamIndexAdapter;
	List<String>		mExamData			= new ArrayList<String>();
	
	Handler				mHandler			= null;
	
	static 	final	String		mVersionUrl				= "http://exam.douziapp.com/version/";
	static	final	String		mNewApkUrl				= "http://exam.douziapp.com/apk/";
	
	static	final	int 		MSG_CHECK_NEW_VERSION 		= 1;
	static	final	int			MSG_CHECK_TIME_OUT			= 2;
	static	final	int			MSG_UPDATE_VERSION_STATE	= 3;
	
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
		
		mTopBtnLeft			= (ImageButton)findViewById(R.id.ivTitleBtnLeft);
		
		mImgExamBankManager = (TextView)findViewById(R.id.ico_exam_bank_manager);
		mImgCheckNewVersion = (TextView)findViewById(R.id.ico_check_new_version);
		
		mViewMainLeftBankManager	= findViewById(R.id.main_left_bank_manager);
		mViewMainLeftCheckVersion	= findViewById(R.id.main_left_check_version);
		
		mTextCheckNewVersion		= (TextView)findViewById(R.id.text_check_new_version);
		
		Typeface font = Typeface.createFromAsset(getAssets(), "font/glyphicons.ttf");
		mImgExamBankManager.setTypeface(font);
		mImgCheckNewVersion.setTypeface(font);
		
		mImgExamBankManager.setText(getString(R.string.ico_exam_bank_manager));
		mImgCheckNewVersion.setText(getString(R.string.ico_check_new_version));
		
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
		
		mViewMainLeftBankManager.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				//new CommUI().showExamBankManager(MainActivity.this, mSlidingMenu);
				Intent intent = new Intent(MainActivity.this,ExamBankManagerActivity.class);
				
				startActivity(intent);
				
				//mSlidingMenu.showLeftView();
				
			}
		});
		
		mViewMainLeftCheckVersion.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				
			}
		});
		
		mTopBtnLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				mSlidingMenu.showLeftView();
			}
		});
		
		mViewMainLeftCheckVersion.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				CommUtil.showProgressDlg(MainActivity.this, getString(R.string.wait));
				
				mCheckNewThread = new CheckApkUpdateThread(true);
				
				mCheckNewThread.start();
							
				mHandler.sendEmptyMessageDelayed(MSG_CHECK_TIME_OUT, 1000 * 10);

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
		
		new CheckApkUpdateThread(false).start();
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
	
	private void update_check_new_state(boolean no_new){
		
		if (null == mTextCheckNewVersion) {
			return;
		}

		Spanned sp = null;
		String strTitle = getString(R.string.check_new_version);
		if (!no_new && CommUtil.hasNewVersion(this)) {
			strTitle += "<font color=\"red\">(new)</font>";
		}

		sp = Html.fromHtml(strTitle);

		mTextCheckNewVersion.setText(sp);
	}
	
	private class CheckApkUpdateThread extends Thread{

		boolean mIsClick;
		boolean mNoShowMsgBox = false;
		
		CheckApkUpdateThread(boolean is_click){
			
			mIsClick = is_click;
		}
		
		public void setNoShowMsgBox(){
			mNoShowMsgBox = true;
		}
		
		@Override
		public void run() {
			
			super.run();
			
			String rtn = CommUtil.checkApkUpdate(MainActivity.this,mVersionUrl);
			
			mHandler.removeMessages(MSG_CHECK_TIME_OUT);
			mHandler.sendEmptyMessage(MSG_UPDATE_VERSION_STATE);
			
			String strAskVersion = CommUtil.getSPValue(MainActivity.this, "ask_version");
			
			CommUtil.setSPValue(MainActivity.this, "ask_version", rtn);
			
			if(mNoShowMsgBox){
				
				return;
			}
			
			if(!mIsClick){
				if(rtn == null || strAskVersion.equalsIgnoreCase(rtn)){
					return;
				}
			}
			
			Message msg = Message.obtain();
			
			msg.what = MSG_CHECK_NEW_VERSION;
			msg.obj = rtn;
			
			mHandler.sendMessageDelayed(msg, 1000 * 1);
		}
		
	}
	

	private class MyHandler extends Handler{

		@Override
		public void handleMessage(Message msg) {
			
			super.handleMessage(msg);
			
			switch(msg.what){
			case MSG_CHECK_NEW_VERSION:
				
				CommUtil.hidenProgressDlg();
				
				if(null != msg.obj){
					showUpdateMessageBox();
				}else{
					showIsNewMessageBox();
				}
				
				break;
			case MSG_CHECK_TIME_OUT:
				CommUtil.hidenProgressDlg();
				
				update_check_new_state(true);
				
				if(null != mCheckNewThread){
					
					mCheckNewThread.setNoShowMsgBox();;
					
					showIsNewMessageBox();
				}
				break;
			case MSG_UPDATE_VERSION_STATE:
				update_check_new_state(false);
				break;
			}
		}
		
	}
	
	private void showIsNewMessageBox(){
		Dialog alertDialog = new AlertDialog.Builder(this). 
                setTitle("提示"). 
                setMessage("已经是最新版本!"). 
                setIcon(R.drawable.ic_launcher). 
                setPositiveButton("确定", new DialogInterface.OnClickListener() { 
                     
                    @Override 
                    public void onClick(DialogInterface dialog, int which) { 
                        
                    } 
                }).
                create(); 
		
		alertDialog.setCanceledOnTouchOutside(false);
		
        alertDialog.show(); 
	}
	
	private void showUpdateMessageBox(){
		Dialog alertDialog = new AlertDialog.Builder(this). 
                setTitle("新版本提示"). 
                setMessage("有新版本,请问您是否需要升级？"). 
                setIcon(R.drawable.ic_launcher). 
                setNegativeButton("升级", new DialogInterface.OnClickListener() { 
                	
                    @Override 
                    public void onClick(DialogInterface dialog, int which) { 
                    	String	strUrl = "http://exam.douziapp.com/sipm.apk";
                        CommUtil.downApkFromBrowser(MainActivity.this, strUrl);
                    } 
                }). 
                setPositiveButton("取消", new DialogInterface.OnClickListener() { 
                     
                    @Override 
                    public void onClick(DialogInterface dialog, int which) { 
                        
                    } 
                }).
                create(); 
		
		alertDialog.setCanceledOnTouchOutside(false);
		
        alertDialog.show(); 
   
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
