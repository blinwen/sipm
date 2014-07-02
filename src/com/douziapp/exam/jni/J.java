package com.douziapp.exam.jni;

public class J {

	public static native String a();
	
	public static native String b();
	
	static{
		System.loadLibrary(".com.douziapp.exam");
	}
}
