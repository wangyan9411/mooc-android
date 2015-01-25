package com.example.schoolonline;
//Download by htp://www.codefans.net
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.schoolonline.util.HttpTask;
import com.example.schoolonline.util.HttpTask.HttpTaskHandler;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class InfoActivity extends Activity {
	private String username;
	@Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info); 
        Intent intent = getIntent();
		Bundle bdle = intent .getExtras();
		
	    String json = bdle.getString("result");
	    username = bdle.getString("username");
        Log.d("infomationla", username);

	    Log.d("rec", json);
	    try {
            JSONObject res = new JSONObject(json);
            JSONObject subject = res.getJSONObject("result");

            //JSONArray jsonArray = (JSONArray) result.get("subjects");
            
                //every subject is a dictionary
                String name = subject.getString("realname");
                final String email = subject.getString("email");
                String signature = subject.getString("signature");
                String birthday = subject.getString("birthday");
                String university = subject.getString("university");
                TextView tv=(TextView)findViewById(R.id.tv_name);      
                tv.setText(name);
                TextView tv_e=(TextView)findViewById(R.id.tv_email);      
                tv_e.setText(email);
                TextView tv_sig=(TextView)findViewById(R.id.tv_signature);      
                tv_sig.setText(signature);
                TextView tv_bir=(TextView)findViewById(R.id.tv_birthday);      
                tv_bir.setText(birthday);
                TextView tv_u=(TextView)findViewById(R.id.tv_university);      
                tv_u.setText(university);
        	    Button addButton = (Button)findViewById(R.id.add);

                addButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Log.d("infomationlala", username);
						Log.d("infomationlala", email);

						HttpTask task = new HttpTask();
				        task.setTaskHandler(new HttpTaskHandler(){
				            public void taskSuccessful(String json) {
				            }
				            public void taskFailed() {
				            }
				        });
				        task.execute("http://182.92.98.27:8000/addcontacts", "8", username, email, "");
						
					}
				});
                
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

   public void btn_back(View v) {     //±ÍÃ‚¿∏ ∑µªÿ∞¥≈•
      	this.finish();
      } 
   public void btn_back_send(View v) {     //±ÍÃ‚¿∏ ∑µªÿ∞¥≈•
     	this.finish();
     } 
   public void head_xiaohei(View v) {     //Õ∑œÒ∞¥≈•
	   Intent intent = new Intent();
		intent.setClass(InfoActivity.this,InfoHead.class);
		startActivity(intent);
    } 
    
}
