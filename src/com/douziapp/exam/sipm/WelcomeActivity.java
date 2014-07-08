package com.douziapp.exam.sipm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class WelcomeActivity extends Activity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_welcome);
		
		new Handler().postDelayed(new StartMain(), 	1500);
	}

	
	private class StartMain extends Thread{

		@Override
		public void run() {

			super.run();
			
			Intent intent = new Intent(WelcomeActivity.this,MainActivity.class);
			
			startActivity(intent);
			
			WelcomeActivity.this.finish();
		}
		
	}
}
