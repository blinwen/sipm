package com.douziapp.exam.sipm;

import java.util.ArrayList;
import java.util.List;













import com.douziapp.exam.data.BankItem;
import com.douziapp.exam.util.CommDBUtil;
import com.douziapp.exam.util.CommUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

public class ExamBankManagerActivity extends Activity {

	private	ExamBankmAdapter			mAdapter 	= null;
	private	ExpandableListView			mListview	= null;
	
	private List<BankItem>				mItems		= new ArrayList<BankItem>();
	
	private	Handler						mHandler	= null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main_exam_bank_manager);
		
		init();
		
		mHandler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				
				super.handleMessage(msg);
				
				switch(msg.what){
				
				case 1:
					CommUtil.hidenProgressDlg();
					CommUtil.showMessage(ExamBankManagerActivity.this, "下载成功!");
					init();
					break;
				case 2:
					CommUtil.hidenProgressDlg();
					CommUtil.showMessage(ExamBankManagerActivity.this, "下载失败,请稍后再试!");
					init();
					break;
				}
			}
			
		};
	}
	
	private void init(){
		
		mergeNetAndLocal();
		
		for (int ii = 0; ii < mItems.size(); ii++) {
			
			BankItem item = mItems.get(ii);
			String strCnName = (CommUtil.getDBZhCNName(item.getName(),false));
			
			item.setCnName(strCnName);
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

		mAdapter = new ExamBankmAdapter(this, mItems);

		mListview.setAdapter(mAdapter);
	}

	private void mergeNetAndLocal(){
		
		List<BankItem>		db_net		= CommUtil.getNetBankItems(this);
		mItems 	= CommUtil.getLocalBankItems(this, true);
		
		
		for (int jj = 0; jj < db_net.size(); jj++) {

			BankItem item_net = db_net.get(jj);
			boolean	match = false;
			
			for (int ii = 0; ii < mItems.size(); ii++) {

				BankItem item_local = mItems.get(ii);
				
				if (item_local.getName().equals(item_net.getName())) {

					item_local.setSizeNet(item_net.getSizeNet());
					item_local.setVersionNet(item_net.getVersionNet());

					match = true;
					
					break;
				}
			}

			if(!match){
				mItems.add(item_net);
			}
			
		}
		
	}
	
	class DownOnClick implements OnClickListener{
		
		String	mDBName;
		Context	mContext;
		
		DownOnClick(Context context,String strName){
			
			mDBName 	= strName;
			mContext	= context;
		}
		
		@Override
		public void onClick(View v) {
			
			CommUtil.showProgressDlg(mContext, getString(R.string.downloading_info));
			
			new Thread(){

				@Override
				public void run() {
					
					boolean rtn = CommDBUtil.downloadDb(mContext, mDBName);
					
					if(rtn){
						mHandler.sendEmptyMessageAtTime(1, 0);
					}else{
						mHandler.sendEmptyMessageAtTime(2, 0);
					}
					
					super.run();
				}
				

			}.start();

		}
		
	}
	
	class DelOnClick implements OnClickListener{

		String	mDBName;
		Context	mContext;
		
		DelOnClick(Context context,String strName){
			mDBName = strName;
			mContext	= context;
		}
		
		@Override
		public void onClick(View v) {
			CommDBUtil.deleteDb(mContext, mDBName);
			CommUtil.showMessage(mContext, "删除成功");
			init();
		}
		
	}
	
	class ExamBankmAdapter extends BaseExpandableListAdapter{

		List<BankItem>	mData 		= null;
		Context			mContext 	= null;
		Typeface 		mFont		= null;
		
		public ExamBankmAdapter(Context context,List<BankItem> data){
			
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
		public View getChildView(int groupPosition, int arg1, boolean arg2, View arg3,
				ViewGroup arg4) {
			
			LayoutInflater li = LayoutInflater.from(mContext);
			
			View view = li.inflate(R.layout.main_exam_bank_manager_item_child, null);
			
			View	root_del 	= view.findViewById(R.id.root_del);
			View	root_down	= view.findViewById(R.id.root_download);
			
			TextView del_view = (TextView)view.findViewById(R.id.icon_exam_del);
			TextView down_view	= (TextView)view.findViewById(R.id.icon_exam_download);
			
			del_view.setTypeface(mFont);
			del_view.setTextColor(Color.RED);

			down_view.setTypeface(mFont);
			down_view.setTextColor(Color.rgb(0x2f, 0x5f, 0x90));
			
			del_view.setText(getString(R.string.ico_del));
			down_view.setText(getString(R.string.ico_download));
			
			BankItem item = mData.get(groupPosition);
			
			if(item.getVersionNet() <= item.getVersionLocal()){
				down_view.setTextColor(getResources().getColor(R.color.disabled_color));
				root_down.setBackgroundResource(android.R.color.transparent);
			}else{
				
				root_down.setOnClickListener(new DownOnClick(mContext, item.getName()));
			}
			
			if(item.getVersionLocal() == 0){
				del_view.setTextColor(getResources().getColor(R.color.disabled_color));
				root_del.setBackgroundResource(android.R.color.transparent);
			}else{
				root_del.setOnClickListener(new DelOnClick(mContext, item.getName()));
			}
			
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
			
			TextView title 		= (TextView)view.findViewById(R.id.exam_item_title);
			TextView state		= (TextView)view.findViewById(R.id.exam_item_state);
			
			BankItem item = mData.get(arg0);
			title.setText(item.getCnName());
			
			if(item.getVersionLocal() == 0){
				title.setTextColor(Color.rgb(0xc0, 0xc0, 0xc0));
			}
			
			if(item.getVersionLocal() < item.getVersionNet()){
				state.setVisibility(View.VISIBLE);
				state.setText("(new)");
			}
			
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
