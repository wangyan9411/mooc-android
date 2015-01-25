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
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schoolonline.CoursePageActivity;
import com.example.schoolonline.R;
import com.example.schoolonline.util.HttpTask;
import com.example.schoolonline.util.HttpTask.HttpTaskHandler;

public class CourseFragment extends ListFragment{
	private List<Map<String ,Object>> subjectdata, showdata;
	private String TAG = CourseFragment.class.getName();  
	private List<String> usersubject;
    private ListView list ;
    private MyAdapter mAdapter;
    private String mEmail;
    int background = 0;
	private Map<String,String> userAuthority;

  
    /** 
     * @描述 在onCreateView中加载布局 
     * */  
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState) {  
        View view = inflater.inflate(R.layout.course_fragment, container, false);  
        list = (ListView) view.findViewById(android.R.id.list);  
        Log.i(TAG, "--------onCreateView");  
        return view;  
    }  
  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        
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
            JSONArray jsonArray =result.getJSONArray("result");
            
            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject subject = jsonArray.getJSONObject(i);
                //every subject is a dictionary
                String title = subject.getString("subject");
                String teacher = subject.getString("teacher");
                String assistant = subject.getString("assistant");
                String teacheremail = subject.getString("teacheremail");
                String teacherphone = subject.getString("teacherphone");
                String subjectid = String.valueOf(subject.getInt("subjectid"));
                String belongto = String.valueOf(subject.getInt("belongto"));
                String subjectinfo = subject.getString("subjectinfo");
                String image = subject.getString("imagetext");
                Log.d("infomation", title);
                Log.d("infomation", belongto);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("subject", title);
                map.put("belongto", belongto);
                map.put("teacher", teacher);
                map.put("assistant", assistant);
                map.put("teacheremail", teacheremail);
                map.put("teacherphone", teacherphone);
                map.put("image", image);
                map.put("subjectid", subjectid);
                map.put("subjectinfo", subjectinfo);
                subjectdata.add(map);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        usersubject = new ArrayList<String>();
        HttpTask task = new HttpTask();
        task.setTaskHandler(new HttpTaskHandler(){
            public void taskSuccessful(String json) {
            	try {
	                	JSONObject result = new JSONObject(json);
	                    JSONArray jsonArray = result.getJSONArray("subjects");
	                    
	                    for (int i=0; i<jsonArray.length(); i++) {
	                        JSONObject subject = jsonArray.getJSONObject(i);
	                        String subjectid = String.valueOf(subject.getInt("subjectid"));
	                        //改变所有课程中已选课程的belongto属性，效率低！
	                        for(Map<String ,Object> map: subjectdata){
	                        	if(map.get("subjectid").toString().equals(subjectid)){
	                        		map.put("belongto_origin", map.get("belongto").toString());
	                        		map.put("belongto", "0");
	                        		break;
	                        	}
	                        }
	                        Log.d("usersubjectid", subjectid);
	                        usersubject.add(subjectid);
	                    }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                public void taskFailed() {
                }
            });
        task.execute("http://182.92.98.27:8000/userallsubject", "1", mEmail);
        showdata = new ArrayList<Map<String,Object>>();
        Map<String, Object> map0 = new HashMap<String, Object>();  
        map0.put("subject", "已选课程");  
        map0.put("teacher", "");
        map0.put("genre", "0");
        map0.put("subjectid", "");
        showdata.add(map0); 
        Map<String, Object> map1 = new HashMap<String, Object>();  
        map1.put("subject", "人文课程");  
        map1.put("teacher", "");
        map1.put("genre", "1");
        map1.put("subjectid", "");
        showdata.add(map1);  
        Map<String, Object> map2 = new HashMap<String, Object>();  
        map2.put("subject", "理工课程");  
        map2.put("teacher", "");
        map2.put("genre", "2");
        map2.put("subjectid", "");
        showdata.add(map2);  
        
        getAuthority();
        
          
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
                		datalist.add(subjectdata.get(i));
                	}
                }
                if(datalist.size() == 0){
                	Toast toast = Toast.makeText(getActivity().getApplicationContext(),
                		     "此分类当前没有课程", Toast.LENGTH_LONG);
                		   toast.setGravity(Gravity.CENTER, 0, 0);
                		   toast.show();
                }else
                	showdata.addAll(position+1, datalist);
        		
        		
        	}else{
        		while(!(showdata.size() == position+1 || showdata.get(position+1).get("teacher").toString().equals(""))){
        			showdata.remove(position+1);
        		}
        	}
        	mAdapter.notifyDataSetChanged();
        }else{
        	Intent intent = new Intent();
			Bundle bundle=new Bundle();
			if(showdata.get(position).get("belongto").toString().equals("0")){
				if(showdata.get(position).get("belongto_origin").toString().equals("1")){
					bundle.putString("belongto", "人文课程");  
				}else{
					bundle.putString("belongto", "理工课程");
				}
			}else{
				if(showdata.get(position).get("belongto").toString().equals("1")){
					bundle.putString("belongto", "人文课程");  
				}else{
					bundle.putString("belongto", "理工课程");
				}
			}
			bundle.putString("subject", showdata.get(position).get("subject").toString());  
			bundle.putString("subjectid", showdata.get(position).get("subjectid").toString()); 
			bundle.putString("subjectinfo", showdata.get(position).get("subjectinfo").toString()); 
			bundle.putString("teacher", showdata.get(position).get("teacher").toString()); 
			bundle.putString("assistant", showdata.get(position).get("assistant").toString()); 
			bundle.putString("teacheremail", showdata.get(position).get("teacheremail").toString()); 
			bundle.putString("teacherphone", showdata.get(position).get("teacherphone").toString()); 
			bundle.putString("image", showdata.get(position).get("image").toString());
            intent.putExtras(bundle);
    		intent.setClass(getActivity(), CoursePageActivity.class);
    		startActivity(intent);
        }
        	
          
    }  
      
      
      
    public void getAuthority(){
    	userAuthority = new HashMap<String,String>();
    	for ( int i=1; i<= 5; i++ ){
    		userAuthority.put(i+"", "student");
    	}
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
                holder.course.setText(showdata.get(position).get("subject").toString());
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
	
	            holder.btn.setVisibility(View.VISIBLE);
	            holder.course.setVisibility(View.VISIBLE);
	            holder.teacher.setVisibility(View.VISIBLE);
	            holder.course.setText(showdata.get(position).get("subject").toString());
	            holder.teacher.setText(showdata.get(position).get("teacher").toString());
	            if(showdata.get(position).get("belongto").toString().equals("0")){
	            	//
	            	String mSubjectid = showdata.get(position).get("subjectid").toString();
	            	//if ()
	                Log.d("msubject",mSubjectid + " " + userAuthority.get("1"));
	                Log.d("userauthority",userAuthority.get(mSubjectid));
	                String mAuthority = userAuthority.get(mSubjectid);
	            	if(mAuthority.equals("student"))
	            		holder.btn.setText("退课");
	            	else{
	            		holder.btn.setVisibility(View.INVISIBLE) ;
	            		if (mAuthority.equals("teacher")){
	        	            holder.teacher.setText(showdata.get(position).get("teacher").toString()+
	        	            		"                         您是本课程的教师");

	            		}else{
	            			holder.teacher.setText(showdata.get(position).get("teacher").toString()+
	        	            		"                         您是本课程的助教");
	            		}
	            	}
	            }
	            else
	            	holder.btn.setText("选课");
	            /*为Button添加点击事件*/
	             holder.btn.setOnClickListener(new OnClickListener() {
	                
	                @Override
	                public void onClick(View v) {
	                	if(showdata.get(position).get("belongto").toString().equals("0")){
	                		Log.v("MyListViewBase", "你点击了退课按钮" + position);                                //打印Button的点击信息
	                		String subjectid = showdata.get(position).get("subjectid").toString();
	                		HttpTask task = new HttpTask();
			                task.setTaskHandler(new HttpTaskHandler(){
			                    public void taskSuccessful(String json) {
				                    }
			
				                    public void taskFailed() {
				                    }
				                });
				            task.execute("http://182.92.98.27:8000/deletesubject", "deletesubject", mEmail, subjectid);
	                		String belongto = new String("");
	                		for(Map<String ,Object> map: subjectdata){
	                        	if(map.get("subjectid").toString().equals(subjectid)){
	                        		showdata.remove(map);
	                        		map.put("belongto", map.get("belongto_origin").toString());
	                        		belongto = map.get("belongto_origin").toString();
	                        		
	                        		break;
	                        	}
	                        }
	                		for(int i = 0; i < showdata.size(); i++){
	                			Map<String, Object> map = showdata.get(i);
	                        	if(map.get("teacher").toString().equals("") && map.get("genre").toString().equals(belongto)){
	                        		int position = i;
	                        		if(showdata.size() == position+1 || showdata.get(position+1).get("teacher").toString().equals("")){
	                        			break;
	                        		}else{
		                        		while(!(showdata.size() == position+1 || showdata.get(position+1).get("teacher").toString().equals(""))){
		                        			showdata.remove(position+1);
		                        		}
		                        		
		                        		String genre = showdata.get(position).get("genre").toString();
		                        		
		                        		List<Map<String ,Object>> datalist = new ArrayList<Map<String,Object>>();
		                        		
		                                for (int j = 0; j < subjectdata.size(); j++) {  
		                                	if(subjectdata.get(j).get("belongto").toString().equals(genre)){
		                                		datalist.add(subjectdata.get(j));
		                                	}
		                                }
		                                if(datalist.size() == 0){
		                                	Toast toast = Toast.makeText(getActivity().getApplicationContext(),
		                                		     "此分类当前没有课程", Toast.LENGTH_LONG);
		                                		   toast.setGravity(Gravity.CENTER, 0, 0);
		                                		   toast.show();
		                                }else
		                                	showdata.addAll(position+1, datalist);
		                        		break;
	                        		}
	                        	}
	                        }
	                		mAdapter.notifyDataSetChanged();
	                		
	                	}else{
			                Log.v("MyListViewBase", "你点击了选课按钮" + position);   
			                //打印Button的点击信息
			                String subjectid = showdata.get(position).get("subjectid").toString();
	                		
			                HttpTask task = new HttpTask();
			                task.setTaskHandler(new HttpTaskHandler(){
			                    public void taskSuccessful(String json) {
			                    	getAuthority();
				                    }
			
				                    public void taskFailed() {
				                    }
				                });
				            task.execute("http://182.92.98.27:8000/selectsubject", "selectsubject", mEmail, showdata.get(position).get("subjectid").toString());
				            String belongto = new String("0");
	                		for(Map<String ,Object> map: subjectdata){
	                        	if(map.get("subjectid").toString().equals(subjectid)){
	                        		showdata.remove(map);
	                        		map.put("belongto_origin", map.get("belongto").toString());
	                        		map.put("belongto", "0");
	                        		
	                        		break;
	                        	}
	                        }
	                		for(int i = 0; i < showdata.size(); i++){
	                			Map<String, Object> map = showdata.get(i);
	                        	if(map.get("teacher").toString().equals("") && map.get("genre").toString().equals(belongto)){
	                        		int position = i;
	                        		if(showdata.size() == position+1 || showdata.get(position+1).get("teacher").toString().equals("")){
	                        			break;
	                        		}else{
		                        		while(!(showdata.size() == position+1 || showdata.get(position+1).get("teacher").toString().equals(""))){
		                        			showdata.remove(position+1);
		                        		}
		                        		
		                        		String genre = showdata.get(position).get("genre").toString();
		                        		
		                        		List<Map<String ,Object>> datalist = new ArrayList<Map<String,Object>>();
		                        		
		                                for (int j = 0; j < subjectdata.size(); j++) {  
		                                	if(subjectdata.get(j).get("belongto").toString().equals(genre)){
		                                		datalist.add(subjectdata.get(j));
		                                	}
		                                }
		                                if(datalist.size() == 0){
		                                	Toast toast = Toast.makeText(getActivity().getApplicationContext(),
		                                		     "此分类当前没有课程", Toast.LENGTH_LONG);
		                                		   toast.setGravity(Gravity.CENTER, 0, 0);
		                                		   toast.show();
		                                }else
		                                	showdata.addAll(position+1, datalist);
		                        		break;
	                        		}
	                        	}
	                        }
	                		mAdapter.notifyDataSetChanged();
	                	}
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
