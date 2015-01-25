package com.example.schoolonline.fragment;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.schoolonline.PostListActivity;
import com.example.schoolonline.R;
import com.example.schoolonline.util.HttpTask;
import com.example.schoolonline.util.HttpTask.HttpTaskHandler;

public class DiscussionFragment extends ListFragment{
	private List<Map<String ,Object>> subjectdata, showdata;
	private Map<String,String> userAuthority;
	private String TAG = DiscussionFragment.class.getName();  
    private ListView list;
    private MyAdapter mAdapter;
    private String mEmail, mSubjectid;
    int background = 0;
  
    /** 
     * @描述 在onCreateView中加载布局 
     * */  
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState) {  
        View view = inflater.inflate(R.layout.forum_fragment, container, false);  
        list = (ListView) view.findViewById(android.R.id.list);  
        Log.i(TAG, "--------onCreateView");  
        return view;
    }  
  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        Bundle b = savedInstanceState;  
        
        mAdapter = new MyAdapter(getActivity());
        setListAdapter(mAdapter);  
        Log.i(TAG, "--------onCreate");
        
        subjectdata = new ArrayList<Map<String,Object>>();
        Bundle bundle = getArguments();
        String json = bundle.getString("result");
        mEmail = bundle.getString("email");
        Log.d("infomation", "success");
        Log.d("infomation", json);
        try {
            JSONObject result = new JSONObject(json);
            //JSONArray jsonArray = (JSONArray) result.get("subjects");
            JSONArray jsonArray =result.getJSONArray("subjects");
            
            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject subject = jsonArray.getJSONObject(i);
                //every subject is a dictionary
                String title = subject.getString("subject");
                String teacher = subject.getString("teacher");
                String belongto = String.valueOf(subject.getInt("belongto"));
                String subjectid = String.valueOf(subject.getInt("subjectid"));
                Log.d("infomation", title);
                Log.d("infomation", belongto);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("course", title);
                map.put("belongto", belongto);
                map.put("teacher", teacher);
                map.put("subjectid", subjectid);
                subjectdata.add(map);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        showdata = new ArrayList<Map<String,Object>>();
        Map<String, Object> map1 = new HashMap<String, Object>();  
        map1.put("course", "人文课程");  
        map1.put("teacher", "");
        map1.put("genre", "1");
        showdata.add(map1);  
        Map<String, Object> map2 = new HashMap<String, Object>();  
        map2.put("course", "理工课程");  
        map2.put("teacher", "");
        map2.put("genre", "2");
        showdata.add(map2);   
        getAuthority();
    }  
     
    public void getAuthority(){
    	userAuthority = new HashMap<String,String>();
    	HttpTask task = new HttpTask();
        task.setTaskHandler(new HttpTaskHandler(){
            public void taskSuccessful(String json) {
            	try {
                    JSONObject result = new JSONObject(json);
                    //JSONArray jsonArray = (JSONArray) result.get("subjects");
                    JSONArray jsonArray =result.getJSONArray("subjects");
                    Log.d("jsonlength",jsonArray.length() + "");
                    for (int i = 0; i<jsonArray.length(); i++) {
                        JSONObject subject = jsonArray.getJSONObject(i);
                        //every subject is a dictionary
                        Log.d("jsontext",subject.toString());
                        String authority = subject.getString("permission");
                        Log.d("permission",authority);
                        String subjectid = String.valueOf(subject.getInt("subjectid"));
                        Log.d("sub_autho",subjectid + " " + authority);
                        userAuthority.put(subjectid, authority);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            public void taskFailed() {
            }
        });
        task.execute("http://182.92.98.27:8000/getpermissions","1",mEmail);
        
    }
    @Override  
    public void onListItemClick(ListView l, View v, int position, long id) {  
        super.onListItemClick(l, v, position, id);  
        
        if(showdata.get(position).get("teacher").toString().equals("")){
        	if(showdata.size() == position+1 || showdata.get(position+1).get("teacher").toString().equals("")){
        		String genre = showdata.get(position).get("genre").toString();
        		
        		List<Map<String ,Object>> datalist = new ArrayList<Map<String,Object>>();
        		
                for (int i = 0; i < subjectdata.size(); i++) {  
                	if(subjectdata.get(i).get("belongto").toString().equals(genre)){
                		
                		Map<String, Object> map = new HashMap<String, Object>();  
                        map.put("course", subjectdata.get(i).get("course").toString());  
                        map.put("teacher", subjectdata.get(i).get("teacher").toString());
                        map.put("subjectid", subjectdata.get(i).get("subjectid").toString());
                        
                        Log.d("clickgenre", subjectdata.get(i).get("course").toString()+"   "+subjectdata.get(i).get("teacher").toString());
                		
                        datalist.add(map);
                	}
                      
                      
                }
        		showdata.addAll(position+1, datalist);
        		
        		
        	}else{
        		while(!(showdata.size() == position+1 || showdata.get(position+1).get("teacher").toString().equals(""))){
        			showdata.remove(position+1);
        		}
        	}
        	mAdapter.notifyDataSetChanged();
        }else{
        	
    		Log.i("MyListViewBase", "你点击了ListView条目" + position);
            
            mSubjectid = showdata.get(position).get("subjectid").toString();
            Log.d("msubject",mSubjectid + " " + userAuthority.get("1"));
            Log.d("userauthority",userAuthority.get(mSubjectid));
            String mAuthority = userAuthority.get(mSubjectid);
            Log.d("authority",mAuthority);
            Intent intent = new Intent();
        	Bundle bundle = new Bundle();
     		intent.setClass(getActivity(), PostListActivity.class);
            bundle.putString("username", mEmail); 
            bundle.putString("subjectid", mSubjectid);
            bundle.putString("authority",mAuthority);
            intent.putExtras(bundle);
			startActivity(intent);
        }
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
                     convertView = mInflater.inflate(R.layout.course_item_list, null);
                     holder = new ViewHolder();
                    /*得到各个控件的对象*/
                    holder.course = (TextView) convertView.findViewById(R.id.item_course);
                    holder.teacher = (TextView) convertView.findViewById(R.id.item_teacher);
                    holder.btn = (Button) convertView.findViewById(R.id.item_button);
                    convertView.setTag(holder);//绑定ViewHolder对象
            }else{
                    holder = (ViewHolder)convertView.getTag();//取出ViewHolder对象
            }
            
            if(showdata.get(position).get("teacher").toString().equals("")){
            	holder.btn.setVisibility(View.GONE);
                holder.course.setText(showdata.get(position).get("course").toString());
                holder.teacher.setVisibility(View.GONE);
                convertView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                
            }else{
            /*设置TextView显示的内容，即我们存放在动态数组中的数据*/
	            if(background == 0){
	            	convertView.setBackgroundColor(Color.parseColor("#F0F8FF"));
	            	background = 1;
	            }
	            else{
	            	convertView.setBackgroundColor(Color.parseColor("#F5F5F5"));
	            	background = 0;
	            }
	
	            holder.btn.setVisibility(View.GONE);
	            holder.course.setVisibility(View.VISIBLE);
	            holder.teacher.setVisibility(View.VISIBLE);
	            holder.course.setText(showdata.get(position).get("course").toString());
	            holder.teacher.setText(showdata.get(position).get("teacher").toString());
	            
	            /*为Button添加点击事件*/
	             holder.btn.setOnClickListener(new OnClickListener() {
	                
	                @Override
	                public void onClick(View v) {
	                Log.v("MyListViewBase", "你点击了按钮" + position);                                //打印Button的点击信息
	                    
	                }
	            });
            }
            return convertView;
        }

		
    }
/*存放控件*/
	public final class ViewHolder{
	    public TextView course;
	    public TextView teacher;
	    public Button   btn;
	    }
}
