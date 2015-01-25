package com.example.schoolonline.fragment;

import java.io.IOException;
import java.util.ArrayList;

import com.example.schoolonline.PostListActivity;
import com.example.schoolonline.R;
import com.example.schoolonline.util.ExitApplication;
import com.example.schoolonline.util.HttpTask;
import com.example.schoolonline.util.HttpTask.HttpTaskHandler;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class MeFragment extends Fragment {
	
	private EditText username;
	private EditText gender;
	private TextView email;
	private EditText region;
	private EditText birthday;
	private TextView usertype;
	private ImageView modifyusername;
	private ImageView modifybirthday;
	private ImageView modifyregion;
	private ImageView modifygendar;
	private Button saveButton;
	private ImageView backButton;
	private String useremail;
	private String username_str="11";
	private String gender_str="fwe";
	private String region_str="fwew";
	private String birthday_str="ewewe";
	private String usertype_str="fwege";
	private ArrayList<String> classdata = new ArrayList<String>();
	private SetPersonalInfoTask setTask = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.me_fragment, container, false);
		username = (EditText)view.findViewById(R.id.xianshi_nicheng);
		gender = (EditText)view.findViewById(R.id.xianshi_xingbie);
		email = (TextView)view.findViewById(R.id.xianshi_qianming);
		region = (EditText)view.findViewById(R.id.xianshi_like);
		birthday = (EditText)view.findViewById(R.id.xianshi_nianling);
		usertype = (TextView)view.findViewById(R.id.xianshi_chumodi);
		//classtakinglayout = (LinearLayout)view.findViewById(R.id.classlist);
		modifyusername = (ImageView)view.findViewById(R.id.img_edit_name1);
		modifybirthday = (ImageView)view.findViewById(R.id.img_edit_name2);
		modifyregion = (ImageView)view.findViewById(R.id.img_edit_name5);
		modifygendar = (ImageView)view.findViewById(R.id.img_edit_name);
		saveButton = (Button)view.findViewById(R.id.img_my_more);
		backButton = (ImageView)view.findViewById(R.id.img_back);
		modifyusername.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				username.setEnabled(true);
			}
		});
		modifybirthday.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						birthday.setEnabled(true);
					}
				});
		modifyregion.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				region.setEnabled(true);
			}
		});
		modifygendar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				gender.setEnabled(true);
			}
		});
		Bundle bundle = getArguments();
		useremail = bundle.getString("email");
		email.setText(useremail);
		show();
		
		
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setTask = new SetPersonalInfoTask();
				setTask.execute((Void) null);
				username.setEnabled(false);
				birthday.setEnabled(false);
				gender.setEnabled(false);
				region.setEnabled(false);
			}
		});
		
		backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MeFragment.this.onDestroy();
			}
		});
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	private void show(){
		HttpTask task = new HttpTask();
        task.setTaskHandler(new HttpTaskHandler(){
            public void taskSuccessful(String json) {
            	Log.d("task1","fjiowje");
            	try{
	            	JSONObject result = new JSONObject(json);
					String status = (String) result.get("status");
					Log.d("meFragmentresult", result+"");
					if (status.equals("ok")) {
						JSONObject jsonobject= (JSONObject)result.get("result");
						username_str = (String)jsonobject.get("realname");
						gender_str = (String)jsonobject.get("gender");
						region_str = (String)jsonobject.get("region");
						usertype_str = (String)jsonobject.get("usertype");
						birthday_str = (String)jsonobject.get("birthday");
						birthday.setEnabled(false);
						gender.setEnabled(false);
						region.setEnabled(false);
						username.setEnabled(false);
						usertype.setText(usertype_str);
						username.setText(username_str);
						region.setText(region_str);
						gender.setText(gender_str);
						birthday.setText(birthday_str);
					}
            	}catch(Exception e){
            		e.printStackTrace();
            	}
            }
            public void taskFailed() {
            }
        });
        task.execute("http://182.92.98.27:8000/getprofilebyemail", "showinfo",useremail);	
	}

	public class SetPersonalInfoTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				Log.d("meFragment", "update");

				String url = "http://182.92.98.27:8000/updateprofile";
				HttpPost request = new HttpPost(url);

				JSONObject param = new JSONObject();
				String str = "";
				str = username.getText().toString();
				param.put("realname", str);
				str = gender.getText().toString();
				param.put("gender", str);
				str = region.getText().toString();
				param.put("region", str);
				str = birthday.getText().toString();
				param.put("birthday", str);
				param.put("email",useremail);
				param.put("photoid", 1);

				StringEntity se = new StringEntity(param.toString());
				request.setEntity(se);

				Log.d("meFragment", "connect");
				HttpResponse httpResponse = new DefaultHttpClient()
						.execute(request);

				String retSrc = EntityUtils.toString(httpResponse
						.getEntity());
				Log.d("meFragment receive", retSrc);

				JSONObject result = new JSONObject(retSrc);
				String status = (String) result.get("result");

				if (status.equals("ok")) {
					Log.d("setPersonalInfo", "ok");
					return true;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				Log.d("meFragment", "json error");
			} catch (Exception e) {
				e.printStackTrace();
				Log.d("meFragment", "click error");
			}

			return false;
		}
	}
}
