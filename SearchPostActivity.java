package com.example.schoolonline;

import java.util.ArrayList;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.example.schoolonline.util.ExitApplication;
import com.example.schoolonline.util.HttpTask;
import com.example.schoolonline.util.ItemData;
import com.example.schoolonline.util.HttpTask.HttpTaskHandler;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.os.Build;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchPostActivity extends Activity{
	private Button searchButton;
	private String searchInfo;
	private ListView mListView;
	private ListViewAdapter mAdapter;
	private ImageView backIamge;
	private EditText searchinfo;
	private TextView mTopMenuOne,mTopMenuTwo;
	private boolean searchflag = true;
	private String username;
	private String subjectid;
	private String authority;
	private Vector<Integer> postid_ = new Vector<Integer>();
	private ArrayList<ItemData> data = new ArrayList<ItemData>();
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_search_post);
	    ExitApplication.getInstance().addActivity(this);
	    Intent intent = getIntent();
	    username = intent.getExtras().getString("username").toString();
	    subjectid = intent.getExtras().getString("subjectid").toString();
	    authority = intent.getExtras().getString("authority").toString();
	    mAdapter = new ListViewAdapter(SearchPostActivity.this);
	    mListView = (ListView)findViewById(R.id.searchpostresult);
	    mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				 final int p = position;
					HttpTask task = new HttpTask();
			        task.setTaskHandler(new HttpTaskHandler(){
			            public void taskSuccessful(String json) {
			               try {
			            	Intent intent = new Intent();
			   				intent.setClass(SearchPostActivity.this, SinglePostActivity.class);
			   				Bundle bundle = new Bundle();
			   				bundle.putString("useremail", username);
			   				bundle.putString("subjectid",subjectid);
			   				bundle.putString("postid",postid_.get(p).toString());
			   				bundle.putString("authority",authority);
			   				Log.d("chenhui2",data.get(p).content);
			                bundle.putString("result", json);  
			                Log.d("rs",json);
			                intent.putExtras(bundle);  
			   				startActivity(intent);
			               } catch (Exception e) {
			                    e.printStackTrace();
			               }
			            }

			            public void taskFailed() {
			            }
			        });
			        task.execute("http://182.92.98.27:8000/postallfloor", "11", subjectid, postid_.get(p).toString());
			}
		});
	    mListView.setAdapter(mAdapter);
	    searchinfo = (EditText)findViewById(R.id.searchpostInfo);
	    searchButton = (Button)findViewById(R.id.searchpostButton);
	    searchButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				searchInfo = searchinfo.getText().toString();
				if(searchInfo.length() == 0) return;
				Log.d("string",searchInfo);
				search(searchInfo);				
			}
		});
	    mTopMenuOne = (TextView)findViewById(R.id.TopMenuOne);
	    mTopMenuTwo = (TextView)findViewById(R.id.TopMenuTwo);

		mTopMenuOne.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				searchflag = true;
				mTopMenuOne.setTextColor(Color.WHITE);
				mTopMenuOne.setBackgroundResource(R.drawable.top_tab_active);
				mTopMenuTwo.setTextColor(Color.WHITE);
				//mTopMenuTwo.setBackgroundDrawable(new PaintDrawable());
				mTopMenuTwo.setBackgroundResource(R.drawable.tab_not_activ);
				searchinfo.setHint("按用户搜");
				mAdapter.count = 0;
				mAdapter.notifyDataSetChanged();
			}
		});
		mTopMenuTwo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				searchflag = false;
				mTopMenuTwo.setTextColor(Color.WHITE);
				mTopMenuTwo.setBackgroundResource(R.drawable.top_tab_active);

				mTopMenuOne.setTextColor(Color.WHITE);
				//mTopMenuOne.setBackgroundDrawable(new PaintDrawable());
				mTopMenuOne.setBackgroundResource(R.drawable.tab_not_activ);;
				searchinfo.setHint("按帖子搜");
				mAdapter.count = 0;
				mAdapter.notifyDataSetChanged();
			}
		});
		
		backIamge = (ImageView)findViewById(R.id.Back);
		backIamge.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SearchPostActivity.this.finish();
			}
		});
		
	}
	public void search(String searchInfo){
		if(searchflag == true){
			data.clear();
			HttpTask task1 = new HttpTask();
	        task1.setTaskHandler(new HttpTaskHandler(){
	            public void taskSuccessful(String json) {
	            	try{
		            	JSONObject result = new JSONObject(json);
		            	JSONArray jsonArray = result.getJSONArray("result");
		                JSONArray remarknum = result.getJSONArray("floorcount");
		                Log.d("searchpostbuname",json);
		                for (int i=0; i<jsonArray.length(); i++) {
	
		                    JSONObject subject = jsonArray.getJSONObject(i);
		                    ItemData item = new ItemData();
		                    item.user_name = subject.getString("email");
		                    item.post_date = subject.getString("posttime");
		                    item.title = subject.getString("posttitle");
		                    item.remark_num = remarknum.get(i).toString();
		                    Log.d("remark_num",item.remark_num);
		                    item.content = subject.getString("posttext");
		                    item.essence = subject.getString("essence");
		                    int a = subject.getInt("id");
		                    postid_.addElement(a);
		                    data.add(item);
		                }
	            	}catch(Exception e){
	            		e.printStackTrace();
	            	}
	            	if(data.size() == 0)
						Toast.makeText(SearchPostActivity.this, "无结果", Toast.LENGTH_SHORT).show();
					else{
						mAdapter.count = data.size();
						mAdapter.notifyDataSetChanged();
					}
	            }

	            public void taskFailed() {
	            }
	        });
	        task1.execute("http://182.92.98.27:8000/searchpostbyname","searchbyname",searchInfo);
		}
		else{
			data.clear();
			HttpTask task2 = new HttpTask();
	        task2.setTaskHandler(new HttpTaskHandler(){
	            public void taskSuccessful(String json) {
	            	try{
		            	JSONObject result = new JSONObject(json);
		            	JSONArray jsonArray = result.getJSONArray("result");
		                JSONArray remarknum = result.getJSONArray("floorcount"); 
		                Log.d("searchpost",json);
		                for (int i=0; i<jsonArray.length(); i++) {
	
		                    JSONObject subject = jsonArray.getJSONObject(i);
		                    ItemData item = new ItemData();
		                    item.user_name = subject.getString("email");
		                    item.post_date = subject.getString("posttime");
		                    item.title = subject.getString("posttitle");
		                    item.remark_num = remarknum.get(i).toString();
		                    Log.d("remark_num",item.remark_num);
		                    item.content = subject.getString("posttext");
		                    item.essence = subject.getString("essence");
		                    int a = subject.getInt("id");
		                    postid_.addElement(a);
		                    data.add(item);
		                }
	            	}catch(Exception e){
	            		e.printStackTrace();
	            	}
	            	if(data.size() == 0)
						Toast.makeText(SearchPostActivity.this, "无结果", Toast.LENGTH_SHORT).show();
					else{
						mAdapter.count = data.size();
						mAdapter.notifyDataSetChanged();
					}
	            }

	            public void taskFailed() {
	            }
	        });
	        task2.execute("http://182.92.98.27:8000/searchpost","searchbypost",searchInfo);
		}
	}
	static class ViewHolder
    {
    	public TextView user;
    	public TextView post_date;
    	public TextView post_title;
    	public TextView post_content;
        public TextView remark_num;
        public ImageView essencepost;
    }
    
	class ListViewAdapter extends BaseAdapter {
		int count = 0;
		private LayoutInflater mInflater = null;  
		private ListViewAdapter(Context context)  
        {  
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
		public View getView(int position, View convertView, ViewGroup parent) { 
			
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.post_list_item,null);
				holder.user = (TextView)convertView.findViewById(R.id.Item_UserName);
				holder.post_date = (TextView)convertView.findViewById(R.id.Item_Postdate);
                holder.post_title = (TextView)convertView.findViewById(R.id.Item_MainTitle);
                holder.post_content = (TextView)convertView.findViewById(R.id.Item_MainText);
                holder.remark_num = (TextView)convertView.findViewById(R.id.Item_Remark_num);
                holder.essencepost = (ImageView)convertView.findViewById(R.id.Item_Essence);
                convertView.setTag(holder);
			}
			else{
                holder = (ViewHolder)convertView.getTag();  
            }
			Log.d("datasize",position + " " + " " + count);
			holder.user.setText(data.get(position).user_name);
			holder.post_date.setText(data.get(position).post_date);
			holder.post_title.setText(data.get(position).title);
			holder.post_content.setText(data.get(position).content);
			holder.remark_num.setText(data.get(position).remark_num);
			if(data.get(position).essence.equals("1")) holder.essencepost.setVisibility(View.VISIBLE);
			else holder.essencepost.setVisibility(View.INVISIBLE);
			return convertView;
		}
	}
}
