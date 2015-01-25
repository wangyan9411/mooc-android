package com.example.schoolonline.fragment;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.schoolonline.ChatActivity;
import com.example.schoolonline.ChatMsgEntity;
import com.example.schoolonline.ContactsActivity;
import com.example.schoolonline.PostListActivity;
import com.example.schoolonline.R;
import com.example.schoolonline.util.HttpTask;
import com.example.schoolonline.util.HttpTask.HttpTaskHandler;
import com.example.schoolonline.util.StaticData;

public class ChatFragment extends ListFragment{
	private List<Map<String ,String>> showdata = new ArrayList<Map<String,String>>();
	
	private String TAG = ChatFragment.class.getName();  
    private ListView list ;
    private MyAdapter mAdapter;
	private ImageView add;
    private String mEmail, mImage, defaultImage;
    int background = 0;
	private Runnable runnable, runnable1;
	private Handler handler, handler1;

	private int test, refreshtime;
	
	private void fetchPhotos(){
		StaticData sd = (StaticData)getActivity().getApplicationContext();
    	HttpTask task = new HttpTask();
        task.setTaskHandler(new HttpTaskHandler(){
            public void taskSuccessful(String json) {
            	StaticData sd = (StaticData)getActivity().getApplicationContext();
            	Log.d("inittime", "successful");
            	try {
            		JSONObject result = new JSONObject(json);
                    JSONArray jsonArray =result.getJSONArray("result");
                    
                    for (int i=0; i<jsonArray.length(); i++) {
                        JSONObject subject = jsonArray.getJSONObject(i); 
                        String name = subject.getString("emailto");
                        String image = subject.getString("imagetext");
                        sd.chatlistphotos.put(name, image);
                    }
                    sd.initialed = 1;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void taskFailed() {
            	Log.d("inittime", "failed");
            	fetchPhotos();
            }
        });
        task.execute("http://182.92.98.27:8000/getunread", "getunread", mEmail, "0");

	}

    /** 
     * @描述 在onCreateView中加载布局 
     * */  
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState) { 

            	
        fetchPhotos();
        handler = new Handler();
        refreshtime = 2000;
        runnable=new Runnable() {
            @Override
            public void run() {
            	HttpTask task = new HttpTask();
    	        task.setTaskHandler(new HttpTaskHandler(){
    	            public void taskSuccessful(String json) {
    	            	Log.d("inittime", "successful");
    	            	StaticData sd = (StaticData)getActivity().getApplicationContext();
	            		String debugname = new String();
    	            	try {
    	            		showdata.clear();
    	            		JSONObject result = new JSONObject(json);
		                    JSONArray jsonArray =result.getJSONArray("result");
		                    
		                    for (int i=0; i<jsonArray.length(); i++) {
		                        JSONObject subject = jsonArray.getJSONObject(i);
		                        Map<String, String> map11 = new HashMap<String, String>();  
		                        String name = subject.getString("emailto");
		                        map11.put("name", name); 
		                        debugname = new String(name);
		                        String message = subject.getString("message");
		                        map11.put("message", message); 
		                        String count = subject.getInt("count") + "";
		                        map11.put("count", count); 
		                        String time = subject.getString("time");
		                        map11.put("time", time); 
		                        String image = sd.chatlistphotos.get(name).toString();
		                        map11.put("image_emailto", image);
		                		showdata.add(map11);
		                    }
		                    Log.d("infomation1", showdata.size()+"");
		    				mAdapter.notifyDataSetChanged();
    	                }
    	                catch (Exception e) {
    	                	Log.d("nullpointer?", "****"+debugname+"****");
    	                    e.printStackTrace();
    	                    
    	                }
    	            }

    	            public void taskFailed() {
    	            	Log.d("inittime", "failed");
    	            }
    	        });
    	        task.execute("http://182.92.98.27:8000/getunread", "getunread", mEmail, "1");
            	handler.postDelayed(this, refreshtime);
            }
        };
        handler.postDelayed(runnable, refreshtime);
        View view = inflater.inflate(R.layout.chat_fragment, container, false);  
        list = (ListView) view.findViewById(android.R.id.list);  
        add = (ImageView)view.findViewById(R.id.add);
	    add.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
            	Bundle bundle=new Bundle();

         		intent.setClass(getActivity(), ContactsActivity.class);
                bundle.putString("username", mEmail); 
                intent.putExtras(bundle);
                startActivity(intent);
			}
			
		});
        Log.i(TAG, "--------onCreateView");  
        return view;  
    }  
  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        Bundle b = savedInstanceState;  
        Bundle bundle = getArguments();
        defaultImage = new String("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");
        
        mEmail = bundle.getString("email");
        mImage = new String("");
        mAdapter = new MyAdapter(getActivity());

        setListAdapter(mAdapter);  
        Log.i(TAG, "--------onCreate");
        search();
    	HttpTask task = new HttpTask();
        task.setTaskHandler(new HttpTaskHandler(){
            public void taskSuccessful(String json) {
            	Log.d("getphotobyemail", json.toString());
            	try {
            		
            		JSONObject result = new JSONObject(json);
                    mImage =result.getString("result");
                    
                    
                  
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void taskFailed() {
            	Log.d("getphotobyemail", "failed");
            }
        });
        task.execute("http://182.92.98.27:8000/getphotobyemail", "getphotobyemail", mEmail);
        
        
    }
    
    
    
	@Override
	public void onResume() {
		fetchPhotos();
		super.onResume();
		
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		handler.removeCallbacks(runnable);   
		handler1.removeCallbacks(runnable1);  
	}
      
    @Override  
    public void onListItemClick(ListView l, View v, final int position, long id) {  
        super.onListItemClick(l, v, position, id);  
        Log.i("MyListViewBase", "你点击了ListView条目" + position);
        String from = mEmail;
        String to = showdata.get(position).get("name");
        String timestamp = new String("0");
        HttpTask task = new HttpTask();
        task.setTaskHandler(new HttpTaskHandler(){
            public void taskSuccessful(String json) {
               try {   
                Log.d("history", json);

            	Intent intent = new Intent();
            	Bundle bundle=new Bundle();

         		intent.setClass(getActivity(), ChatActivity.class);
                bundle.putString("history", json); 
                bundle.putString("emailfrom", mEmail);
                bundle.putString("emailto", showdata.get(position).get("name"));
                Log.d("getphotobyemail", "mImage = "+mImage);
                if(mImage.equals(""))
                	bundle.putString("image_emailfrom", defaultImage);
                else
                	bundle.putString("image_emailfrom", mImage); 
                if(showdata.get(position).get("image_emailto") == null || showdata.get(position).get("image_emailto").equals(""))
                	bundle.putString("image_emailto", defaultImage);
                else
                	bundle.putString("image_emailto", showdata.get(position).get("image_emailto")); 
                intent.putExtras(bundle);
                startActivityForResult(intent, 0);
                } catch (Exception e) {
                    e.printStackTrace();
               }
            }

            public void taskFailed() {
            }
        });
        task.execute("http://182.92.98.27:8000/getchat", "getchat", from, to, timestamp);
    
        	
          
    }  
      
      
      
  
    
    
  
    @Override  
    public void onActivityCreated(Bundle savedInstanceState) {  
        super.onActivityCreated(savedInstanceState);  
        Log.i(TAG, "--------onActivityCreated");  
  
    }  
      
    @Override  
    public void onAttach(Activity activity) {  
        super.onAttach(activity);  
        Log.i(TAG, "----------onAttach");  
    }
    private class MyAdapter extends BaseAdapter {
        
    	private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局

		/*构造函数*/
		public MyAdapter(Context context) {
		    this.mInflater = LayoutInflater.from(context);
		        }

        @Override
        public int getCount() {
            
            return showdata.size();//返回数组的长度
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
        /*书中详细解释该方法*/
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
             ViewHolder holder;
            
            if (convertView == null) {
                     convertView = mInflater.inflate(R.layout.chat_item_list, null);
                     holder = new ViewHolder();
                    /*得到各个控件的对象*/
                    holder.name = (TextView) convertView.findViewById(R.id.name);
                    holder.time = (TextView) convertView.findViewById(R.id.time);
                    holder.image = (ImageView) convertView.findViewById(R.id.head);
                    holder.last_text = (TextView) convertView.findViewById(R.id.last_text);
                    convertView.setTag(holder);//绑定ViewHolder对象
            }else{
                    holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
            }
            if(showdata.size() != 0){
				//holder.user.setText("jichenhui2012");
            	holder.name.setText(showdata.get(position).get("name"));
            	holder.last_text.setText(showdata.get(position).get("message") + "           ［ " + showdata.get(position).get("count") + " 条未读消息］");
            	holder.time.setText(showdata.get(position).get("time"));
            	byte [] baseByte;
            	if(showdata.get(position).get("image_emailto") == null || showdata.get(position).get("image_emailto").equals(""))
                	baseByte = android.util.Base64.decode(defaultImage, Base64.DEFAULT);
                else
                	baseByte = android.util.Base64.decode(showdata.get(position).get("image_emailto"), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(baseByte, 0, baseByte.length);
                holder.image.setImageBitmap(bitmap);
                holder.image.setAdjustViewBounds(true);
                holder.image.setMaxHeight(200);
                holder.image.setMaxWidth(200);


				
			}
	            
            return convertView;
        }

		
    }
    
    public void search(){
	    Log.d("bug", "lala");

	    showdata.clear();  
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
                        showdata = new ArrayList<Map<String,String>>();
                        Map<String, String> map1 = new HashMap<String, String>();  
                        String name = subject.getString("emailfrom");

                        map1.put("name", name);  
                        
                        showdata.add(map1);  
                    }
                    //mAdapter.count = showdata.size();
                    Log.d("infomation1", showdata.size()+"");
    				mAdapter.notifyDataSetChanged();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            public void taskFailed() {
            }
        });
        task.execute("http://182.92.98.27:8000/getcontacts", "getcontacts", mEmail);
		
	}

/*存放控件*/
	public final class ViewHolder{
	    public TextView name;
	    public TextView time;
	    public TextView last_text;
	    public ImageView image;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		    Bundle b=data.getExtras(); //data为B中回传的Intent
		    String timestamp=b.getString("timestamp");//str即为回传的值
		    String emailto = b.getString("emailto");
		    Log.d("fighting", timestamp);
		    Log.d("fighting", emailto);

		    HttpTask task = new HttpTask();
	        task.setTaskHandler(new HttpTaskHandler(){
	            public void taskSuccessful(String json) {
	                try {
	                    
	                }
	                catch (Exception e) {
	                    e.printStackTrace();
	                }
	            }
	            public void taskFailed() {
	            }
	        });
	        task.execute("http://182.92.98.27:8000/updatetimestamp", "updatetimestamp",timestamp, mEmail, emailto);
		    
		}
}
