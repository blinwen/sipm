package com.douziapp.exam.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;

import org.json.JSONObject;
import org.json.JSONTokener;

import com.douziapp.exam.sipm.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;

public class CommUtil {
	
	private static ProgressDialog progressDlg = null;
	
	public static void 	downApkFromBrowser(Context context,String strUrl){
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		
		Uri uri = Uri.parse(strUrl);
		intent.setData(uri);
		
		context.startActivity(intent);
	}
	
	public static String checkApkUpdate(Context context, String strUrl) {

		PackageManager pm = context.getPackageManager();

		int new_version_code;
		int cur_version_code;

		// boolean exist =
		// Environment.getExternalStorageState().equalsIgnoreCase(
		// Environment.MEDIA_MOUNTED);
		//
		// if(!exist){
		// return "";
		// }

		// File down_path = Environment.getExternalStoragePublicDirectory(
		// Environment.DIRECTORY_DOWNLOADS);

		// if(!down_path.exists()){
		// down_path.mkdirs();
		// }
		//
		// strTempFile = down_path.getPath() + "/";

		try {

			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);

			cur_version_code = pi.versionCode;

			URL url = new URL(strUrl);

			URLConnection conn = url.openConnection();

			InputStream in = conn.getInputStream();

			// BufferedInputStream bin = new BufferedInputStream(in);
			byte[] buffer = new byte[1024];
			in.read(buffer, 0, 1024);

			String strVersion = new String(buffer, "UTF-8");

			JSONTokener jsonParser = new JSONTokener(strVersion);

			JSONObject version = (JSONObject) jsonParser.nextValue();
			new_version_code = version.getInt("versionCode");

			if(new_version_code > cur_version_code){
				return version.getString("versionName");
			}
			
		} catch (Exception e) {

			// e.printStackTrace();
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
	
	public static void showProgressDlg(Context context,String strMessage){
		if(null == progressDlg){
			progressDlg = new ProgressDialog(context);
			
			progressDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDlg.setTitle(context.getString(R.string.app_name));
			progressDlg.setMessage(strMessage);
			progressDlg.setIndeterminate(false);
			progressDlg.setCancelable(false);
			progressDlg.setIcon(R.drawable.ic_launcher);
			progressDlg.show();
		}
	}
	
	public static void hidenProgressDlg(){
		if(null != progressDlg){
			progressDlg.dismiss();
			progressDlg = null;
		}
	}
}
