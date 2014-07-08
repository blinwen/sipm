package com.douziapp.exam.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

public class CommUtil {
	
	public static void 	downApkFromBrowser(Context context,String strUrl){
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		
		Uri uri = Uri.parse(strUrl);
		intent.setData(uri);
		
		context.startActivity(intent);
	}
	
	public	static String	checkApkUpdate(){
		
		String	strUrl = "";
		String	strTempFile = "";
		
		boolean exist = Environment.getExternalStorageState().equalsIgnoreCase(
									Environment.MEDIA_MOUNTED);
		
		if(!exist){
			return "";
		}
		
		File down_path = Environment.getExternalStoragePublicDirectory(
									Environment.DIRECTORY_DOWNLOADS);
		
		if(!down_path.exists()){
			down_path.mkdirs();
		}
		
		strTempFile = down_path.getPath() + "/";
		
		try {
			
			URL	url = new URL(strUrl);
			
			URLConnection 	conn 	= url.openConnection();
			
			InputStream 	in 		= conn.getInputStream();
			
			BufferedInputStream	bin = new BufferedInputStream(in);
			
		} catch (Exception e) {
			
			
			//e.printStackTrace();
		}
		
		return null;
	}
	
	public	static String	getDBZhCNName(String db_en_name){
		
		String[] split_arr = db_en_name.split("_");
		
		if(split_arr.length != 4){
			return null;
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append(split_arr[0]);
		sb.append("年");
		
		if("1".equalsIgnoreCase(split_arr[1] )){
			sb.append("上半年");
		}else if("2".equalsIgnoreCase(split_arr[1])){
			sb.append("下半年");
		}else{
			return null;
		}
		
		if("sw.db".equalsIgnoreCase(split_arr[3])){
			sb.append("上午");
		}else if("xw.db".equalsIgnoreCase(split_arr[3])){
			sb.append("下午");
		}else{
			return null;
		}
		
		sb.append("试题分析与解答");
		
		return sb.toString();
	}
}
