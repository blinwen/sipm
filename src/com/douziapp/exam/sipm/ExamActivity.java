package com.douziapp.exam.sipm;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.douziapp.exam.data.CommDBUtil;
import com.douziapp.exam.data.SingleChoice;
import com.douziapp.exam.slidingmenu.LeftFragment;
import com.douziapp.exam.slidingmenu.RightFragment;
import com.douziapp.exam.slidingmenu.SlidingMenu;
import com.douziapp.exam.slidingmenu.ViewPageFragment;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ExamActivity extends FragmentActivity {

	int type_img_wrong_right = 1;
	int type_rd_select_state = 2;
	int type_item_root_view = 3;

	
	
	List<SingleChoice> mListSc;
	
	SlidingMenu 		mSlidingMenu;
	LeftFragment 		leftFragment;
	RightFragment 		rightFragment;
	ViewPageFragment 	viewPageFragment;
	
	ImageButton			mTopLeftBtn;
	ImageButton			mTopRightBtn;
	
	GridView			mGridSelExamItem;
	
	SelExamItemAdapter	mGridAdapter;
	
	LinearLayout		mContentRoot 	= null;
	
	Button				mBtnNexItem		= null;
	Button				mBtnAnswer		= null;
	
	TextView			mAnswerDetail	= null;
	View				mRightViewRoot	= null;
	
	long				mCurItemIndex 	= 1;
	
	@SuppressLint("UseSparseArrays")
	Map<Long,List<Long>>	mVCIndex		= new HashMap<Long,List<Long>>();
	
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
		
		pre_trans_data();
		
		initView();
		
		initContent();
	}
	
	private void pre_trans_data(){
		
		if(null == mListSc){
			return;
		}
		
		long vid = 1;
		
		if(mListSc.size() > 1){
			
			vid = mListSc.get(0).getvID();
		}
		
		List<Long>	v = new ArrayList<Long>();
		
		for(int ii = 0; ii < mListSc.size(); ii++){
			
			SingleChoice sc = mListSc.get(ii);
			
			
			if(vid == sc.getvID()){
				v.add(sc.getId());
				continue;
			}
			
			{
				
				mVCIndex.put(vid, v);
				vid = sc.getvID();
				v = new ArrayList<Long>();
				v.add(sc.getId());
			}
		}
		
		mVCIndex.put(vid, v);
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
		
		if(null == strContetn || strContetn.length() < 1){
			return "";
		}
		
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

	              //int sh = (int) (dm.heightPixels * 0.9);
	              
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
		
		//添加题干
		mContentRoot.addView(tv,params);

		//通知网格,改变选中项的背景
		mGridAdapter.notifyDataSetChanged();
		
		//添加选择项
		long vid = mListSc.get((int)mCurItemIndex - 1).getvID();
		
		List<Long> lv = mVCIndex.get(vid);
		
		for (Long id : lv) {

			SingleChoice s = mListSc.get(id.intValue() - 1);
			
			LinearLayout	subroot = new LinearLayout(this);

			View			title 		= createTitle("题("+ id +")");
			
			subroot.setPadding(0, 4, 0, 6);
			subroot.setOrientation(LinearLayout.VERTICAL);
			subroot.addView(title,params);

			View choiceItem = createChoiceItem(s.getSelItemA(),id.intValue(),1);
			subroot.addView(choiceItem, params);
			
			choiceItem = createChoiceItem(s.getSelItemB(),id.intValue(),2);
			subroot.addView(choiceItem, params);
			
			choiceItem = createChoiceItem(s.getSelItemC(),id.intValue(),3);
			subroot.addView(choiceItem, params);
			
			choiceItem = createChoiceItem(s.getSelItemD(),id.intValue(),4);
			subroot.addView(choiceItem, params);
			
			mContentRoot.addView(subroot, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		}
		
		//答案详解
		String	strAnswerDetail = sc.getaContent();
		if(null == strAnswerDetail || strAnswerDetail.length() < 1){
			strAnswerDetail = getString(R.string.no_answer);
		}
		mAnswerDetail.setText(transContent(strAnswerDetail));
	}
	
	private View createTitle(String str){
		
		TextView v = new TextView(this);
		
		v.setText(str);
		v.setTextColor(Color.BLACK);
		//v.setTextSize(15);
		v.setPadding(0, 30, 0, 7);
		v.setGravity(Gravity.CENTER_VERTICAL);
		v.setBackgroundColor(Color.TRANSPARENT);
		
		return v;
	}
	
	private View createChoiceItem(String str,int id,int index){
		
//		Button btn = new Button(this);
//		
//		btn.setText(str);
//		btn.setTextColor(Color.BLACK);
//		//btn.setTextSize(18);
//		btn.setPadding(10, 1, 0, 1);
//		btn.setGravity(Gravity.CENTER_VERTICAL);
//		btn.setBackgroundColor(Color.TRANSPARENT);
//		btn.setId(id);
		

		String strIndex = "";

		switch (index) {
		case 1:
			strIndex = "A";
			break;
		case 2:
			strIndex = "B";
			break;
		case 3:
			strIndex = "C";
			break;
		case 4:
			strIndex = "D";
			break;
		default:
			break;
		}
		
		View item_view = View.inflate(this, R.layout.exam_choice_item, null);
		Button btn = (Button) item_view.findViewById(R.id.choice_btn);
		btn.setText(str);

		
		TextView index_view = (TextView)item_view.findViewById(R.id.choice_index_title);
		index_view.setText(strIndex);
		
		ImageButton view = (ImageButton)item_view.findViewById(R.id.item_right_wrong);
		view.setId(gen_item_id(id, type_img_wrong_right, index));
		
		ImageButton state = (ImageButton)item_view.findViewById(R.id.item_select_state);
		state.setId(gen_item_id(id, type_rd_select_state, index));
		
		LinearLayout ll = (LinearLayout)item_view.findViewById(R.id.item_root);
		
	
		ll.setId(gen_item_id(id,type_item_root_view,index));
		ll.setBackgroundResource(R.drawable.foot_button_bg_selector);

		OnClickListener oc = new OnExamItemClick(id,index);
		
		state.setOnClickListener(oc);
		btn.setOnClickListener(oc);
		
		return item_view;
	}
	
	private class OnExamItemClick implements View.OnClickListener{

		int mItemId;
		int mIndex;
		
		public OnExamItemClick(int item_id,int index){
			mItemId = item_id;
			mIndex = index;
		}
		
		@Override
		public void onClick(View v) {
			
			for(int ii = 1; ii < 5; ii++){
				
				int id = gen_item_id(mItemId, type_rd_select_state, ii);
				ImageButton ibtn= (ImageButton)findViewById(id);
				
				if(null == ibtn){
					return;
				}
				
				if(ii == mIndex)
					ibtn.setBackgroundResource(R.drawable.btn_radio_on_selected);
				else
					ibtn.setBackgroundResource(android.R.drawable.btn_radio);
				
				mListSc.get(mItemId - 1).setSelItem(mIndex);
			}

		}
		
	}
	
	private int gen_item_id(long id,int type,int index){
		
		int out = 6 * 10000;
		
		out += id * 1000;
		
		out += type * 100;
		
		out += index * 10;
		
		return out;
		
		
	}
	
	private void initView(){
		
		mTopLeftBtn = (ImageButton)findViewById(R.id.ivTitleBtnLeft);
		mTopRightBtn = (ImageButton)findViewById(R.id.ivTitleBtnRigh);
		
		mGridSelExamItem = (GridView)findViewById(R.id.grid_sel_exam_item);
		
		mContentRoot	= (LinearLayout)findViewById(R.id.content_root);
		
		mBtnNexItem		= (Button)findViewById(R.id.btn_next_exam_item);
		mBtnAnswer		= (Button)findViewById(R.id.btn_item_answer);
		
		mAnswerDetail	= (TextView)findViewById(R.id.answer_detail);
		mRightViewRoot	= findViewById(R.id.right_view_root);
		
		mGridAdapter 	= new SelExamItemAdapter(mListSc,this);
		mGridSelExamItem.setAdapter(mGridAdapter);
		
		ViewGroup.LayoutParams lp = mRightViewRoot.getLayoutParams();
		DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
		lp.width = (int) (dm.widthPixels * 0.83);
		mRightViewRoot.setLayoutParams(lp);
		
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
				
				long vid = mListSc.get((int)mCurItemIndex - 1).getvID();
				
				for(long ii = mCurItemIndex; 
						mCurItemIndex < mListSc.size();ii++){
					
					SingleChoice sc = mListSc.get((int)ii - 1);
					if(vid == sc.getvID()){
						continue;
					}
					
					mCurItemIndex = ii;
					break;
				}
						
				initContent();
			}
		});
		
		mBtnAnswer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				long vid = mListSc.get((int)mCurItemIndex - 1).getvID();
				
				List<Long> list_id = mVCIndex.get(vid);
				
				for(Long id : list_id){
					SingleChoice sc = mListSc.get(id.intValue() - 1);
					int right_id = gen_item_id(id, type_img_wrong_right, (int)sc.getRightItem());
					int sel_id = gen_item_id(id, type_rd_select_state, (int)sc.getRightItem());
					
					ImageButton iv = (ImageButton)findViewById(right_id);
					iv.setVisibility(View.VISIBLE);
					iv.setBackgroundResource(R.drawable.icon_state_right);
					
					findViewById(sel_id).setVisibility(View.GONE);
					
					if(sc.getRightItem() != sc.getSelItem() &&
							sc.getSelItem() != 0){
						
						int wrong_id = gen_item_id(id, type_img_wrong_right, sc.getSelItem());
						sel_id = gen_item_id(id, type_rd_select_state, sc.getSelItem());
						iv = (ImageButton)findViewById(wrong_id);
						iv.setVisibility(View.VISIBLE);
						iv.setBackgroundResource(R.drawable.icon_state_wrong);
						findViewById(sel_id).setVisibility(View.GONE);
					}
					
					
				}
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
	
	private static class ViewHolderItem{
		TextView 	title_view;
	}
	
	public class SelExamItemAdapter extends BaseAdapter{
		
		List<SingleChoice>	mData;
		Context	mContext;
		
		public SelExamItemAdapter(List<SingleChoice> data,Context context){
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
			
			ViewHolderItem viewHolder = null;
			
			if(null == view){
				
				LayoutInflater inflater =(LayoutInflater) mContext.getSystemService(
											Context.LAYOUT_INFLATER_SERVICE);
				
				view = inflater.inflate(R.layout.grid_sel_item, null);
				
				viewHolder = new ViewHolderItem();
				viewHolder.title_view = (TextView)view.findViewById(R.id.exam_item_index);
				
				view.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolderItem)view.getTag();
			}
			
			viewHolder.title_view.setText(i+1 + "题");
			
			SingleChoice sc = mListSc.get(i);
			
			viewHolder.title_view.setBackgroundColor(Color.TRANSPARENT);
			
			if(sc.getSelItem() == sc.getRightItem()){
				viewHolder.title_view.setBackgroundColor(Color.GREEN);
			}else if(sc.getSelItem() != 0){
				viewHolder.title_view.setBackgroundColor(Color.GRAY);
			}
			
			if(i + 1 == mCurItemIndex ){
				viewHolder.title_view.setBackgroundColor(Color.rgb(0xff, 0xA5, 0x00));
			}
			
			return view;
		}
		
	}
}
