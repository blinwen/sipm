package com.douziapp.exam.data;

import java.util.ArrayList;
import java.util.List;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Base64;

public class CommDBUtil {

	private String	mStrDB;
	private SQLiteDatabase	mDB;
	
	public CommDBUtil(String db,int mode){
		mStrDB = db;
		
		 int open_mode;
		 if(1 == mode ){
			 open_mode = SQLiteDatabase.OPEN_READONLY;
		 }else{
			 open_mode = SQLiteDatabase.OPEN_READWRITE;
		 }
		 
		 String strTemp = Environment.getExternalStorageDirectory() + "/2009_1_xtjc_sw.db";
		 
		 mDB = SQLiteDatabase.openDatabase(strTemp, null, open_mode);
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
			
			outData.add(sc);
		}
		
		cursor.close();
		
		if(auto_close){
			closeDB();
		}
		
		return outData;
	}
	
	public List<ExamQA>			getAllExamQA(){
		return null;
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
		
	
		
		
		return null;
	}
}
