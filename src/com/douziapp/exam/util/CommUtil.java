package com.douziapp.exam.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.douziapp.exam.data.BankItem;
import com.douziapp.exam.sipm.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;


public class CommUtil {
	
	public static final String STR_KEY_NEW_VERSION_CODE 	= "new_version_code";
	
	public static final String STR_KEY_BANK_CONTENT			= "bank_last_content";
	
	public static final String STR_KEY_BANK_LAST_MODIFY		= "bank_last_modify";
	
	public static final String STR_KEY_VERSION_PRE			= "db_ver_";
	
	public static 	final	String		STR_URL_VERSION				= "http://exam.douziapp.com/version/";
	public static	final	String		STR_URL_BANK				= "http://exam.douziapp.com/bank/";
	public static	final	String		STR_URL_APK					= "http://exam.douziapp.com/apk/";
	
	private static ProgressDialog progressDlg = null;
	
	public static void 	downApkFromBrowser(Context context,String strUrl){
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		
		Uri uri = Uri.parse(strUrl);
		intent.setData(uri);
		
		context.startActivity(intent);
	}
	
	public static void showMessage(Context context,String strMsg){
		Dialog alertDialog = new AlertDialog.Builder(context). 
                setTitle("提示"). 
                setMessage(strMsg). 
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
	
	public static boolean  saveNetFile(String strUrl,String strSaveFile){
		
		byte[] file = getNetFile(strUrl);
		
		if(null == file){
			return false;
		}
		
		File toFile = new File(strSaveFile);
		
		if(toFile.exists()){
			toFile.delete();
		}
		
		try {
			
			FileOutputStream outImgStream = new FileOutputStream(toFile);  
			outImgStream.write(file);  
			outImgStream.close();
			
			return true;
		} catch (IOException e) {

		}  
		
		return false;
	}
	
	public static byte[]	getNetFile(String strUrl){

		byte[] fileData = null;
		
		try {

			URL url = new URL(strUrl);
			Log.e("test", strUrl);
			URLConnection httpConn =  url.openConnection();
			
			httpConn.setConnectTimeout(6000);
			httpConn.setReadTimeout(6000);
			
			httpConn.connect();
			
			InputStream cin = httpConn.getInputStream();
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024 * 10];
			int len = 0;
			
			while ((len = cin.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			
			cin.close();
			fileData = outStream.toByteArray();
			outStream.close();

		} catch (Exception e) {
			
			e.printStackTrace();
		}

		return fileData;
	}
	
	public static String	getNetContent(String strUrl){
		
		String	strOut = "";
		
		try {
			URL url = new URL(strUrl);

			URLConnection conn = url.openConnection();

			conn.setUseCaches(false);
			conn.setConnectTimeout(3000);
			conn.setReadTimeout(3000);
			
			InputStream in = conn.getInputStream();

			// BufferedInputStream bin = new BufferedInputStream(in);
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
			
			byte[] buffer = new byte[1024];
			int len = 0;  
			while((len = in.read(buffer) )!= -1){
				
				outStream.write(buffer, 0, len);
			}
			
			strOut = outStream.toString("UTF-8");
			
			in.close();
			outStream.close();
			
		} catch (MalformedURLException e) {
			
			//e.printStackTrace();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return strOut;
	}
	
	public static String checkApkUpdate(Context context) {

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

			String	strUrl = STR_URL_VERSION;
			
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);

			cur_version_code = pi.versionCode;
			
			strUrl = strUrl + "?t=" + getAppSName(context);
			
			String strVersion = getNetContent(strUrl);

			JSONTokener jsonParser = new JSONTokener(strVersion);

			JSONObject version = (JSONObject) jsonParser.nextValue();
			new_version_code = version.getInt("versionCode");

			if(new_version_code > cur_version_code){
				setSPValue(context, STR_KEY_NEW_VERSION_CODE,Integer.toString(new_version_code));
				return version.getString("versionName");
			}
			
		} catch (Exception e) {

			//e.printStackTrace();
		}

		return null;
	}
	
	public	static String	getDBZhCNName(String db_en_name,boolean auto_suffix){
		
		db_en_name = db_en_name.replaceFirst("\\.db", "");
		
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
		
		if("sw".equalsIgnoreCase(split_arr[3])){
			sb.append("上午");
		}else if("xw".equalsIgnoreCase(split_arr[3])){
			sb.append("下午");
		}else{
			return null;
		}
		
		sb.append("试题");
		
		if(auto_suffix)
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
	
	public static String	getSPValue(Context context,String strKey){
		
		SharedPreferences exam_info = context.getSharedPreferences("exam", 0);
		
		return exam_info.getString(strKey, "");
	}
	
	public static void	setSPValue(Context context,String strKey,String strValue){
		SharedPreferences exam_info = context.getSharedPreferences("exam", 0);
		exam_info.edit().putString(strKey, strValue).commit();
	}
	
	public static void setLValue(Context context,String strKey,long value){
		
		SharedPreferences exam_info = context.getSharedPreferences("exam", 0);
		exam_info.edit().putLong(strKey, value).commit();
	}
	
	public static long getLValue(Context context,String strKey){
		
		SharedPreferences exam_info = context.getSharedPreferences("exam", 0);
		
		return exam_info.getLong(strKey, 0);
	}
	
	public static void setIValue(Context context,String strKey,int value){
		
		SharedPreferences exam_info = context.getSharedPreferences("exam", 0);
		exam_info.edit().putInt(strKey, value).commit();
	}
	
	public static int getIValue(Context context,String strKey){
		
		SharedPreferences exam_info = context.getSharedPreferences("exam", 0);
		
		return exam_info.getInt(strKey, 0);
	}
	
	public static boolean	isFirstStart(Context context){
		
		SharedPreferences exam_info = context.getSharedPreferences("exam", 0);
		return exam_info.getBoolean("is_first", true);
		
	}
	
	public static void setFirstStart(Context context,boolean isFirstStart){
		
		SharedPreferences exam_info = context.getSharedPreferences("exam", 0);
		exam_info.edit().putBoolean("is_first", isFirstStart).commit();
	}
	
	public static PackageInfo getPackageInfo(Context context){
		
		PackageManager		pm;
		PackageInfo 		pi		= null;
		
		try {
			pm = context.getPackageManager();
			pi = pm.getPackageInfo(context.getPackageName(), 0);
			
			return pi;
		} catch (NameNotFoundException e) {
			
			
		}
		
		return pi;
	}
	
	public static String	getAppSName(Context context){
		
		String strOut = "null";

		PackageInfo pi;

		pi = getPackageInfo(context);
		
		if(null != pi){
			
			String[] temp_arr = pi.packageName.split("\\.");

			strOut = temp_arr[temp_arr.length - 1];
		}



		return strOut;
	}
	
	public static boolean	hasNewVersion(Context context){
		
		String strValue = getSPValue(context, STR_KEY_NEW_VERSION_CODE);
		
		if(null == strValue || strValue.length() < 1){
			return false;
		}
		
		PackageInfo pi;

		pi = getPackageInfo(context);
		
		if(null == pi){
			return false;
		}
		
		int new_version = Integer.parseInt(strValue);
		
		if(new_version > pi.versionCode){
			return true;
		}else{
			return false;
		}
	}
	
	public static void getBankLastFromServer(Context context,String strUrl){
		
		String	strSAppName = getAppSName(context);
		
		strUrl = strUrl + "?t=" + strSAppName;
		
		String	strContent = getNetContent(strUrl);
		
		if(null == strContent || strContent.length() < 1){
			return;
		}
		
		setSPValue(context, STR_KEY_BANK_CONTENT, strContent);
		setLValue(context, STR_KEY_BANK_LAST_MODIFY, System.currentTimeMillis());
	}
	
	public static void getBankLastFromServer(Context context){
		
		long last_modify_time = getLValue(context, STR_KEY_BANK_LAST_MODIFY);
		
		long cur_time = System.currentTimeMillis();
		
		if(cur_time - last_modify_time < 1000 * 60 * 60 * 12){
			return;
		}
		
		getBankLastFromServer(context,STR_URL_BANK);
	}
	
	public static List<BankItem>	getLocalBankItems(Context context,boolean localVersion){
		
		List<BankItem>	outData = new ArrayList<BankItem>();
		
		String strDataPath = CommDBUtil.getDBPath(context);
		String[] list_file = new File(strDataPath).list();
		
		if(null == list_file){
			return outData;
		}
		
		for (String string : list_file) {
			
			BankItem obj = new BankItem();

			obj.setName(string);
			
			outData.add(obj);
			//mExamData.add(string);
		}
		
		if(localVersion){
			getAllDBVersion(context,outData);
		}
		
		return outData;
	}
	
	public static void getAllDBVersion(Context context,List<BankItem> items){
		
		for(BankItem item : items){
			
			String strName = item.getName();
			
			String strKey = STR_KEY_VERSION_PRE + strName;
			int version = getIValue(context, strKey);
			
			if(version < 1){
				
				CommDBUtil db = new CommDBUtil(context, strName, CommDBUtil.OPEN_READONLY);
				
				version = db.getDBVersion(true);
				
				if(version < 1){
					continue;
				}
				
				setIValue(context, strKey, version);
			}
			
			item.setVersionLocal(version);
		}
	}
	
	public static List<BankItem>	getNetBankItems(Context context){
		
		List<BankItem>	outData = new ArrayList<BankItem>();
		
		String strContext = getSPValue(context, STR_KEY_BANK_CONTENT);
		
		JSONTokener jsonParser = new JSONTokener(strContext);

		try {
			
			JSONObject version = (JSONObject) jsonParser.nextValue();
			
			JSONArray items = version.getJSONArray("items");
			
			int len = items.length();
			for(int ii = 0; ii < len; ii++){
				
				JSONObject item = items.getJSONObject(ii);
							
				BankItem object = new BankItem();
				object.setName(item.getString("name"));
				object.setSizeNet(item.getInt("size"));
				object.setVersionNet(item.getInt("version"));
				
				outData.add(object);
			}
			
		} catch (JSONException e) {
			
			//e.printStackTrace();
		}
		
		return outData;
	}
	
}
