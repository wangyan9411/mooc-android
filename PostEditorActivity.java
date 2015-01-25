package com.example.schoolonline;

import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.schoolonline.util.ExitApplication;
import com.example.schoolonline.util.HttpTask;
import com.example.schoolonline.util.HttpTask.HttpTaskHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class PostEditorActivity extends Activity{
	private ImageView add;
	private String title;
	private String content;
	private EditText mTitleView;
	private EditText mContentView;
	private String userName;
	private String subjectid;
	private String authority;
	private Intent intent;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_post_editor);
	    ExitApplication.getInstance().addActivity(this);
	    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
	    intent = getIntent();
	    Bundle bdle = intent.getExtras();
	    userName = bdle.getString("username");
	    subjectid = bdle.getString("subjectid");
	    authority = bdle.getString("authority");
	    Log.d("postEditor",userName + " " + subjectid);
	    
	    mTitleView = (EditText)findViewById(R.id.posttitle);
	    mContentView = (EditText)findViewById(R.id.postcontent);
	    mTitleView.setText(title);
	    mContentView.setText(content);
	    
	    add = (ImageView)findViewById(R.id.add);
	    add.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
					title = mTitleView.getText().toString();
					content = mContentView.getText().toString();
					boolean flag = checkAdd();
					if(flag == false){
						HttpTask task = new HttpTask();
				        task.setTaskHandler(new HttpTaskHandler(){
				            public void taskSuccessful(String json) {
				            	Log.d("infomationposteditor", json);
				            	try{
					            	JSONObject myobject = new JSONObject(json);
					            	String yorn = myobject.getString("result");
					            	Log.d("yorn",yorn);
					            	if(!yorn.equals("yes")){
					            		String remainday = myobject.getString("remaindays");
					            		String remainseconds = myobject.getString("remainseconds");

					            		Toast.makeText(PostEditorActivity.this, "对不起，你被禁言了，离解禁还有"+remainday+"天 " +remainseconds + "秒", Toast.LENGTH_SHORT).show();
					            		PostEditorActivity.this.finish();
					            		return;
					            	}
					            	Intent intent = new Intent();
					   				intent.setClass(PostEditorActivity.this, PostListActivity.class);
					   				Bundle bundle=new Bundle();  
					   				bundle.putString("username",userName);
					   				bundle.putString("subjectid",subjectid);
					   				bundle.putString("authority", authority);
					   				intent.putExtras(bundle); 
					   				ExitApplication.getInstance().pop(2);
					   				startActivity(intent);
				            	}catch(JSONException e){
				            		e.printStackTrace();
				            	}
				            }
				            public void taskFailed() {
				            }
				        });
				        task.execute("http://182.92.98.27:8000/post", "3", userName, subjectid, title, content);
				        Log.d("infomation", "post");
					}
					else {
						Log.d("infomation","run not");
					}
			}
		});
	 }
		
	private boolean checkAdd(){
		
		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(title)) {
			mTitleView.setError(getString(R.string.error_field_required));
			focusView = mTitleView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(content)) {
			mContentView.setError(getString(R.string.error_field_required));
			focusView = mContentView;
			cancel = true;
		}
		
		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		}
		
		return cancel;
	}
	 
}