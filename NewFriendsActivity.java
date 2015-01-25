package com.example.schoolonline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.schoolonline.util.HttpTask;
import com.example.schoolonline.util.HttpTask.HttpTaskHandler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class NewFriendsActivity extends Activity{
	
	private ListView mListView;
	private ArrayList<String> data = new ArrayList<String>();
	private ListViewAdapter mAdapter;
	private String username = "wy";
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent=getIntent();  
		username = intent.getStringExtra("username");
        Log.d("infomationla", username);
	    setContentView(R.layout.activity_new_friends);
	    //Intent intent = getIntent();
	    //username = intent.getExtras().getString("email").toString();
	    mAdapter = new ListViewAdapter(this);
	    mListView = (ListView)findViewById(R.id.newfriendresult);
	    mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				//if the item was clicked, then the interface turn to the information of the user who was searched
			}
		});
	    mListView.setAdapter(mAdapter);
	   
	    
	    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    	 
	    	             @Override
	    	              public void onItemClick(AdapterView<?> arg0, View arg1, int position,
	    	                      long arg3) {
	    	            	 String email = data.get(position).toString();
	    	                 HttpTask task = new HttpTask();
	    	                 task.setTaskHandler(new HttpTaskHandler(){
	    	                     public void taskSuccessful(String json) {
	    	                         Log.d("errolar", json);
	    	                        try {   
	    	                            Log.d("finderror", json);

	    	                     	Intent intent = new Intent();
	    	                     	Bundle bundle=new Bundle();

	    	                  		intent.setClass(NewFriendsActivity.this, InfoActivity.class);
	    	                         bundle.putString("result", json);
	    	     					bundle.putString("username", username);  
	    	     					
	    	     	                intent.putExtras(bundle);
	    	                         Log.d("finderror", json);

	    	                         intent.putExtras(bundle);
	    	            				Log.d("haha","haha");

	    	            				startActivity(intent);
	    	            				Log.d("haha","haha");
	    	                        } catch (Exception e) {
	    	                             e.printStackTrace();
	    	                        }
	    	                     }

	    	                     public void taskFailed() {
	    	                     }
	    	                 });
	    	                 task.execute("http://182.92.98.27:8000/getprofilebyemail", "9", email);	    	  
	    	              }
	    	          });
	    search();
	}
	
	public void search(){
	    Log.d("bug", "lala");

	    data.clear();  
	    Log.d("bug", "lala");
		HttpTask task = new HttpTask();
	    Log.d("bug", "lala2");
        task.setTaskHandler(new HttpTaskHandler(){
            public void taskSuccessful(String json) {
                Log.d("infomation1", json);
                try {
                    JSONObject result = new JSONObject(json);
                    //JSONArray jsonArray = (JSONArray) result.get("subjects");
                    JSONArray jsonArray =result.getJSONArray("result");
                    
                    for (int i=0; i<jsonArray.length(); i++) {
                        JSONObject subject = jsonArray.getJSONObject(i);
                        //every subject is a dictionary
                        String name = subject.getString("emailfrom");
                		data.add(name);
                    }
                    mAdapter.count = data.size();
                    Log.d("infomation1", data.size()+"");
    				mAdapter.notifyDataSetChanged();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            public void taskFailed() {
            }
        });
        task.execute("http://182.92.98.27:8000/getunsolvedcontacts", "getunsolvedcontacts", username);
		
	}

	class ViewHolder{
		TextView user;
		Button accept;
		Button reject;
	}
	
	
	
        	
          

	class ListViewAdapter extends BaseAdapter{
		
		
		public int count = 0;
		private LayoutInflater mInflater = null;
		private ListViewAdapter(Context context){
			this.mInflater = LayoutInflater.from(context);
		}
		
		public int getCount() {
			return count;
		}
		
		public Object getItem(int position) {
			return position;
		}
		
		public long getItemId(int position) {
			return position;
		}
		
		public View getView(final int position, View convertView, ViewGroup parent) { 
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.new_friends, null);
				holder.user = (TextView)convertView.findViewById(R.id.newfriendresult);
				holder.accept = (Button)convertView.findViewById(R.id.acceptFriendButton);
				holder.reject = (Button)convertView.findViewById(R.id.rejectFriendButton);

                convertView.setTag(holder);
			}
			else{
                holder = (ViewHolder)convertView.getTag();  
            }
			Log.d("info4",count + " " + data.size());
			if(data.size() != 0){
				//holder.user.setText("jichenhui2012");
				holder.user.setText(data.get(position).toString());
				holder.accept.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						HttpTask task = new HttpTask();
				        task.setTaskHandler(new HttpTaskHandler(){
				            public void taskSuccessful(String json) {
				            	search();
				            }
				            public void taskFailed() {
				            }
				        });
				        task.execute("http://182.92.98.27:8000/updatecontacts", "10", data.get(position).toString(),username);
						
					}
				});
				holder.reject.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						HttpTask task = new HttpTask();
				        task.setTaskHandler(new HttpTaskHandler(){
				            public void taskSuccessful(String json) {
				            	search();
				            }
				            public void taskFailed() {
				            }
				        });
				        task.execute("http://182.92.98.27:8000/rejectcontacts", "10", data.get(position).toString(),username);
						
					}
				});
			}
			return convertView;
		}
	}
}
