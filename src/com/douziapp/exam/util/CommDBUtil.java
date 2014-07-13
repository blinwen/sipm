package com.douziapp.exam.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import com.douziapp.exam.data.SingleChoice;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Base64;

public class CommDBUtil {

	private String	mStrDB;
	private SQLiteDatabase	mDB;
	
	public static final int  OPEN_READONLY  = SQLiteDatabase.OPEN_READONLY;
	public static final int OPEN_READWRITE 	= SQLiteDatabase.OPEN_READWRITE;
	
	private Context mContext				= null;

	public CommDBUtil(Context context,String db,int mode){

		mStrDB = db;
		mContext = context;
		
		int open_mode = mode;

		String strBDFile = getDBFullFile(mContext, mStrDB);

		try {
			
			mDB = SQLiteDatabase.openDatabase(strBDFile, null, open_mode);
			
		} catch (Exception e) {
			
			//e.printStackTrace();
		}
	}
	
	public String	genDBFile(String strYear,String strSXYear,String strSXDay){
		
		StringBuffer	sb = new StringBuffer();
		
		sb.append(strYear);
		sb.append("_");
		sb.append(strSXYear);
		sb.append("_");
		sb.append("xtjc");
		sb.append(strSXDay);
		sb.append(".db");
		
		return sb.toString();
	}
	
	public int	getDBVersion(boolean auto_close){
		
		int		outData = 0;
		
		if(null == mDB || !mDB.isOpen()){
			return outData;
		}
		
		String	strSQL = "select v.v from version v";
		
		Cursor cursor = mDB.rawQuery(strSQL, null);
	
		if(cursor.moveToNext()){
			
			outData = cursor.getInt(0);
		}
		
		cursor.close();
		
		if(auto_close){
			mDB.close();
		}
		
		return outData;
	}
	
	public List<SingleChoice>	getAllSingleChoice(boolean auto_close){
		
		List<SingleChoice> outData= new ArrayList<SingleChoice>();
		
		if(null == mDB || !mDB.isOpen()){
			return outData;
		}
		
		String	strSQL = "select c.id,c.citema,c.citemb,c.citemc,c.citemd,c.right,v.id,v.content,a.content"
					+ " from choice c,vignette v,answer a where a.cid=c.id and v.id=c.vid order by c.id";
		
		Cursor cursor = mDB.rawQuery(strSQL, null);

		while(cursor.moveToNext()){
			
			SingleChoice sc = new SingleChoice();
			
			sc.setId(cursor.getLong(0));
			sc.setSelItemA(cursor.getString(1));
			sc.setSelItemB(cursor.getString(2));
			sc.setSelItemC(cursor.getString(3));
			sc.setSelItemD(cursor.getString(4));
			
			sc.setRightItem(cursor.getLong(5));
			sc.setvID(cursor.getLong(6));
			sc.setvContent(cursor.getString(7));
			
			sc.setaContent(cursor.getString(8));
			
			sc.setSelItemA(t(sc.getSelItemA(),null));
			sc.setSelItemB(t(sc.getSelItemB(),null));
			sc.setSelItemC(t(sc.getSelItemC(),null));
			sc.setSelItemD(t(sc.getSelItemD(),null));
			sc.setvContent(t(sc.getvContent(),null));
			sc.setaContent(t(sc.getaContent(),null));
			
			outData.add(sc);
		}
		
		cursor.close();
		
		if(auto_close){
			closeDB();
		}
		
		return outData;
	}
	
	public List<SingleChoice>			getAllExamQA(boolean auto_close){
		
		List<SingleChoice> outData= new ArrayList<SingleChoice>();
		
		if(null == mDB || !mDB.isOpen()){
			return outData;
		}
		
		String	strSQL = "select v.id,v.content,a.content"
					+ " from vignette v,answer a where a.cid=v.id order by v.id";
		
		Cursor cursor = mDB.rawQuery(strSQL, null);

		while(cursor.moveToNext()){
			
			SingleChoice sc = new SingleChoice();
			
			sc.setId(cursor.getLong(0));

			sc.setvID(cursor.getLong(0));
			sc.setvContent(cursor.getString(1));
			
			sc.setaContent(cursor.getString(2));

			sc.setvContent(t(sc.getvContent(),null));
			sc.setaContent(t(sc.getaContent(),null));
			
			outData.add(sc);
		}
		
		cursor.close();
		
		if(auto_close){
			closeDB();
		}
		
		return outData;
	}
	
	public void closeDB(){
		
		if(null == mDB || !mDB.isOpen()){
			return ;
		}
		
		mDB.close();
	}
	
	public byte[] i(String s,boolean a){
		byte[] out = null;
		
		if(null == mDB || !mDB.isOpen()){
			return out;
		}
		
		String strSQL = "select image from images where unid like '"+s+"'";
		
		Cursor cursor = mDB.rawQuery(strSQL, null);
		
		if(cursor.moveToNext()){
			out = cursor.getBlob(0);
		}
		
		cursor.close();
		
		if(a){
			closeDB();
		}
		
		return out;
	}
	public String t(String s,String k){
		

		
		
		return s;
	}
	
	public static String	getDBPath(Context context){
		
		return context.getFilesDir().getParent() + "/data/";
	}
	
	public static String	getDBFullFile(Context context,String file){
		
		if(!file.endsWith(".db")){
			file = file + ".db";
		}
		return getDBPath(context) + file;
	}
	
	public static boolean checkDB(Context context){
		//判断assets中的文件是否和data中的一直,不一直,拷贝
		
		try {
			String[] datas = context.getAssets().list("data");
			
			String	strDataPath = context.getFilesDir().getParent() + "/data/";
			
			File path = new File(strDataPath);
			if(!path.exists()){
				path.mkdirs();
			}
			
			for(String strData : datas){
				String strTemp = strDataPath + strData;
				
				File file = new File(strTemp);
				if(file.exists()){
					continue;
				}
				
				copyAssetsToFilesystem(context,"data/" + strData,strTemp);
			}
		} catch (IOException e) {
			
			e.printStackTrace();
			
			return false;
		}
		
		return true;
	}
	
	public static boolean copyAssetsToFilesystem(Context context,String assetsSrc, String des){  
		
        InputStream istream = null;  
        OutputStream ostream = null;  
        try{  
            AssetManager am = context.getAssets();  
            istream = am.open(assetsSrc);  
            ostream = new FileOutputStream(des);  
            byte[] buffer = new byte[1024];  
            int length;  
            while ((length = istream.read(buffer))>0){  
                ostream.write(buffer, 0, length);
            }  
            istream.close();  
            ostream.close();  
        }  
        catch(Exception e){  
            e.printStackTrace();  
            try{  
                if(istream!=null)  
                    istream.close();  
                if(ostream!=null)  
                    ostream.close();  
            }  
            catch(Exception ee){  
                ee.printStackTrace();  
            }  
            return false;  
        }  
        return true;  
    }  
	
	public static void deleteDb(Context context,String strDbName){
		
		String strFile = getDBFullFile(context, strDbName);
		new File(strFile).delete();
	}
	
	public static boolean downloadDb(Context context,String strDbName){
		
		StringBuffer	sb = new StringBuffer();
		sb.append(CommUtil.STR_URL_BANK);
		sb.append(CommUtil.getAppSName(context));
		sb.append("/");
		sb.append(strDbName);
		sb.append(".db");
		
		String	strUrl 			= sb.toString();
		String strSaveFile 		= getDBFullFile(context, strDbName);
		
		return CommUtil.saveNetFile(strUrl, strSaveFile);
		
		
	}
}
