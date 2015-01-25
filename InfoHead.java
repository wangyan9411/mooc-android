package com.example.schoolonline;
//Download by htp://www.codefans.net
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.WindowManager;

public class InfoHead extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.info_head);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);//»•µÙ±ÍÃ‚¿∏
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //        WindowManager.LayoutParams.FLAG_FULLSCREEN);   //»´∆¡œ‘ æ
		//Toast.makeText(getApplicationContext(), "∫¢◊”£°∫√∫√±≥À–£°", Toast.LENGTH_LONG).show();
		//overridePendingTransition(R.anim.hyperspace_in, R.anim.hyperspace_out);

   }
	@Override
	public boolean onTouchEvent(MotionEvent event){
		finish();
		return true;
	}
	
}