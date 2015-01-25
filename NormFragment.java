package com.example.schoolonline.fragment;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.Editable;
import android.text.Layout;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.schoolonline.PostEditorActivity;
import com.example.schoolonline.PostListActivity;
import com.example.schoolonline.R;
import com.example.schoolonline.SinglePostActivity;
import com.example.schoolonline.util.ExitApplication;
import com.example.schoolonline.util.HttpTask;
import com.example.schoolonline.util.HttpTask.HttpTaskHandler;

public class NormFragment extends ListFragment{
	private String TAG = NormFragment.class.getName();
  
	
	private ListView mListView;
	private Button ListBottem;
	private TextView NormImg, EssenImg;
	public ListViewAdapter mListViewAdapter;
	private final Handler mHandler = new Handler();
	private String jsonresult;
	private String username;
	private String subjectid;
	private String authority;
	private String userimage, defaultImage;
	private Vector<Integer> postid_ = new Vector<Integer>();
	private class ItemData{
		String user_name;
		String post_date;
		String title;
		String content;
		String remark_num;
		String essence;
		String top;
		String headimage;
	}
	
	
	private ArrayList<ItemData> data = new ArrayList<ItemData>();
	private int mcount = 0;
    
    
    
    @Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState) {  
        View view = inflater.inflate(R.layout.normal_post_fragment, container, false);
        NormImg=(TextView) getActivity().findViewById(R.id.NormBtnText);
        EssenImg=(TextView)getActivity().findViewById(R.id.EssenBtnText);
        NormImg.setBackgroundResource(R.drawable.top_tab_active);
        EssenImg.setBackgroundResource(R.drawable.tab_not_activ);
        mListView = (ListView)view.findViewById(android.R.id.list);
        ListBottem = new Button(view.getContext());
		ListBottem.setText("点击加载更多.....");
		ListBottem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						int num = mListViewAdapter.count;
						if(num == mcount) {
							ListBottem.setVisibility(View.GONE);
							Toast toast = Toast.makeText(getActivity(), "没有帖子了", Toast.LENGTH_SHORT); 
							toast.show();
						}
						if(mcount == 0) return;
						if(num + 5 > mcount) mListViewAdapter.count = mcount;
						else mListViewAdapter.count += 5;
						mListViewAdapter.notifyDataSetChanged();
					}
				}, 1000);
			}
		});
		mListView.addFooterView(ListBottem, null, false);
        Log.i(TAG, "--------onCreateView");
        mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, final View view,
					final int position, long id) {
				//String array[] = {"删除该贴","设置为精品贴","置顶该贴","将此用户禁止发帖一段时间"};
				String array[] = null;
				if(!authority.equals("student")){
					array = new String[4];
					array[0] = "删除该贴";
					array[1] = "设置为精品贴";
					array[2] = "置顶该贴";
					array[3] = "将此用户禁止发帖一段时间";
				}
				else{
					if(username.equals(data.get(position).user_name)){
						array = new String[1];
						array[0] = "删除该贴";
					}
					else return false;
				}
				final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
				alertDialog.setTitle("manager");
				alertDialog.setItems(array,new DialogInterface.OnClickListener() {
					Integer id_post = postid_.get(position);
					String id_str = id_post.toString();
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Log.d("id_str",data.get(position).title+" "+id_str);
						// TODO Auto-generated method stub
						switch(which){
						case 2:
							HttpTask task = new HttpTask();
        			        task.setTaskHandler(new HttpTaskHandler(){
        			            public void taskSuccessful(String json) {
        			            	Log.d("task1","fjiowje");
        			            	ExitApplication.getInstance().pop(1);
        			            	Intent intent = new Intent();
        			            	intent.setClass(getActivity(), PostListActivity.class);
        			            	Bundle bundle = new Bundle();
        			            	bundle.putString("username", username); 
        			                bundle.putString("subjectid", subjectid);
        			                bundle.putString("authority",authority);
        			                intent.putExtras(bundle);
        			    			startActivity(intent);
        			            }
        			            public void taskFailed() {
        			            }
        			        });
        			        task.execute("http://182.92.98.27:8000/toppost", "5", id_str);
							break;
						case 1:
							HttpTask task2 = new HttpTask();
        			        task2.setTaskHandler(new HttpTaskHandler(){
        			            public void taskSuccessful(String json) {
        			            	 Log.d("task2","sifwoehf");
        			            	 view.findViewById(R.id.Item_Essence).setVisibility(View.VISIBLE);
        			            }
        			            public void taskFailed() {
        			            }
        			        });
        			        task2.execute("http://182.92.98.27:8000/essence", "5", id_str);
							break;
						case 0:
							HttpTask task3 = new HttpTask();
        			        task3.setTaskHandler(new HttpTaskHandler(){
        			            public void taskSuccessful(String json) {
        			            	Log.d("task3","gerer");
        			            	/*postid_.remove(position);
        			            	data.remove(position);
        			            	if(mcount == 0) return;
        							int num = mListViewAdapter.count;
        							mcount --;
        							if(num + 5 > mcount) mListViewAdapter.count = mcount;
        							else mListViewAdapter.count += 5;
        							mListViewAdapter.notifyDataSetChanged();*/
        			            	ExitApplication.getInstance().pop(1);
        			            	Intent intent = new Intent();
        			            	intent.setClass(getActivity(), PostListActivity.class);
        			            	Bundle bundle = new Bundle();
        			            	bundle.putString("username", username); 
        			                bundle.putString("subjectid", subjectid);
        			                bundle.putString("authority",authority);
        			                intent.putExtras(bundle);
        			    			startActivity(intent);
        			            }
        			            public void taskFailed() {
        			            }
        			        });
        			        task3.execute("http://182.92.98.27:8000/removepost", "5", id_str);
							break;
						case 3:
							final Dialog mdialog = new Dialog(getActivity());
							mdialog.show();
							mdialog.getWindow().setContentView(R.layout.dialog_time);
							mdialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));  
							mdialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
							mdialog.getWindow().findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
								
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									String year = ((EditText)mdialog.getWindow().findViewById(R.id.time_year)).getText().toString();
									String month = ((EditText)mdialog.getWindow().findViewById(R.id.time_month)).getText().toString();
									String day = ((EditText)mdialog.getWindow().findViewById(R.id.time_day)).getText().toString();
									Log.d("yearday",year + " " + month + " " + day);
									if(year.length() == 0) year = "0";
									if(month.length() == 0) month = "0";
									if(day.length() == 0) day = "0";
									int year1 = Integer.parseInt(year);
									int month1 = Integer.parseInt(month);
									int day1 = Integer.parseInt(day);
									int time_total = (year1 * 365 + month1 * 30 + day1) * 24 * 3600;
									Log.d("datauser",year1 + " " + month1 + " " + day + " " + time_total);
									String user = data.get(position).user_name;
									Log.d("userdata",user);
									mdialog.dismiss();
									HttpTask task4 = new HttpTask();
		        			        task4.setTaskHandler(new HttpTaskHandler(){
		        			            public void taskSuccessful(String json) {
		        			            	Log.d("task4","vdsdsd");
		        			            }
		        			            public void taskFailed() {
		        			            }
		        			        });
		        			        task4.execute("http://182.92.98.27:8000/forbidden","12",user,String.valueOf(time_total));
								}
							});
							mdialog.getWindow().findViewById(R.id.cancle).setOnClickListener(new View.OnClickListener() {
								
								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									mdialog.dismiss();
								}
							});
							break;
						default:
							break;
						}
					}
				});
				alertDialog.show();
				// TODO Auto-generated method stub
				return true;
			}
		});
        return view;  
    }  
  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);
        mListViewAdapter = new ListViewAdapter(getActivity());
        setListAdapter(mListViewAdapter);
        defaultImage = new String("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");
        
		try {
			data =  getData();
			mcount = data.size();
			if(mcount != 0)
	    		mListViewAdapter.count = (mcount > 5 ? 5 : mcount);
	    	else mListViewAdapter.count = 0;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }  
      
    @Override  
    public void onListItemClick(ListView l, View v, int position, long id) {  
        super.onListItemClick(l, v, position, id);  
        Log.i("MyListViewBase", "你点击了ListView：" + position); 
        final int p = position;
		HttpTask task = new HttpTask();
        task.setTaskHandler(new HttpTaskHandler(){
            public void taskSuccessful(String json) {
               try {
            	Intent intent = new Intent();
   				intent.setClass(getActivity(), SinglePostActivity.class);
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
	
	public ArrayList<ItemData> getData() throws JSONException{  //get data from database;
		
		Bundle bdle = getArguments();
		username = bdle.getString("username");
		subjectid = bdle.getString("subjectid");
		jsonresult = bdle.getString("result");
		authority = bdle.getString("authority");
		Log.d("debug2", jsonresult + " " + username + " " + subjectid + " " + authority);
		
		ArrayList<ItemData> sarray = new ArrayList<ItemData>();

        JSONObject result = new JSONObject(jsonresult);

        JSONArray jsonArray = result.getJSONArray("result");
        JSONArray remarknum = result.getJSONArray("floorcount");
        for (int i=0; i<jsonArray.length(); i++) {

            JSONObject subject = jsonArray.getJSONObject(i);

            //every subject is a dictionary
            ItemData item = new ItemData();
            item.user_name = subject.getString("email");
            item.post_date = subject.getString("posttime");
            item.title = subject.getString("posttitle");
            item.remark_num = remarknum.get(i).toString();
            Log.d("remark_num",item.remark_num);
            item.content = subject.getString("posttext");
            item.essence = subject.getString("essence");
            item.headimage = subject.getString("imagetext");
            item.top = subject.getString("toppost");
            int a = subject.getInt("id");
            postid_.addElement(a);
            sarray.add(item);
        }
		return sarray;
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
    static class ViewHolder
    {
    	public TextView user;
    	public TextView post_date;
    	public TextView post_title;
    	public TextView post_content;
        public TextView remark_num;
        public ImageView essencepost;
        public ImageView toppost;
		public ImageView userHead;
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
				convertView = mInflater.inflate(R.layout.post_list_item, null);
				holder.user = (TextView)convertView.findViewById(R.id.Item_UserName);
				holder.post_date = (TextView)convertView.findViewById(R.id.Item_Postdate);
                holder.post_title = (TextView)convertView.findViewById(R.id.Item_MainTitle);
                holder.post_content = (TextView)convertView.findViewById(R.id.Item_MainText);
                holder.remark_num = (TextView)convertView.findViewById(R.id.Item_Remark_num);
                holder.essencepost = (ImageView)convertView.findViewById(R.id.Item_Essence);
                holder.toppost = (ImageView)convertView.findViewById(R.id.Item_top);
                holder.userHead = (ImageView)convertView.findViewById(R.id.Item_UserHead);
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
			byte [] baseByte;
			if(data.get(position).headimage.equals(""))
				baseByte = android.util.Base64.decode(defaultImage, Base64.DEFAULT);
			else
				baseByte = android.util.Base64.decode(data.get(position).headimage, Base64.DEFAULT);
			
	        Bitmap bitmap = BitmapFactory.decodeByteArray(baseByte, 0, baseByte.length);
	        holder.userHead.setImageBitmap(bitmap);
	        holder.userHead.setAdjustViewBounds(true);
	        holder.userHead.setMaxHeight(50);
	        holder.userHead.setMaxWidth(50);
			if(data.get(position).essence.equals("1")) holder.essencepost.setVisibility(View.VISIBLE);
			else holder.essencepost.setVisibility(View.INVISIBLE);
			if(data.get(position).top.equals("1")) holder.toppost.setVisibility(View.VISIBLE);
			else holder.toppost.setVisibility(View.INVISIBLE);
			return convertView;
		}
	}

}
