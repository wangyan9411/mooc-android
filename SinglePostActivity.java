package com.example.schoolonline;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.schoolonline.util.HttpTask;
import com.example.schoolonline.util.HttpTask.HttpTaskHandler;
import com.example.schoolonline.util.MyEditText;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout.LayoutParams;
import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
public class SinglePostActivity extends Activity {
	private ImageView add;
	private Button ListBottem;
	private ListView mListView;
	private ListViewAdapter mListViewAdapter;
	private final Handler mHandler = new Handler();
	private int mcount = 0;
	private PopupWindow popupWindow = null;
	private LayoutInflater replyInflater = null;
	private MyEditText replyEditor;
	private String replytext;
	private String username;
	private String subjectid;
	private String postid;
	private String authority;
	private String userimage;
	private TextView postusername;
	private TextView postdate;
	private TextView posttitle;
	private TextView posttext;
	private TextView remarknum;
	private ImageView headimage;
	private class ItemData{
		String user_name;
		String remark_date;
		String userheadimage;
		String remark_content;
	}
	private ArrayList<ItemData> data = new ArrayList<ItemData>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		setContentView(R.layout.activity_single_post);
		postusername = (TextView)findViewById(R.id.Item_UserName);
		postdate = (TextView)findViewById(R.id.Item_Postdate);
		posttitle = (TextView)findViewById(R.id.Item_MainTitle);
		posttext = (TextView)findViewById(R.id.Item_MainText);
		remarknum = (TextView)findViewById(R.id.Item_Remark_num);
		headimage = (ImageView)findViewById(R.id.Item_UserHead);
		mListViewAdapter = new ListViewAdapter(this);
		mListView = (ListView)findViewById(R.id.commentview);
		replyInflater = LayoutInflater.from(this);
		Log.d("dawef","faewegreg");
		try {
			data = getData();
			mcount = data.size();
			if(mcount != 0)
	    		mListViewAdapter.count = (mcount > 5 ? 5 : mcount);
	    	else mListViewAdapter.count = 0;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("mcount3",mListViewAdapter.count+"");
		ListBottem = new Button(this);
		ListBottem.setText("点击加载更多");
		ListBottem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if(mcount == 0) return;
						int num = mListViewAdapter.count;
						if(num == mcount) {
							ListBottem.setVisibility(View.GONE);
							Toast toast = Toast.makeText(SinglePostActivity.this, "没有帖子了", Toast.LENGTH_SHORT); 
							toast.show();
						}
						if(num + 5 > mcount) mListViewAdapter.count = mcount;
						else mListViewAdapter.count += 5;
						mListViewAdapter.notifyDataSetChanged();
					}
				}, 1000);
			}
		});
		HttpTask task = new HttpTask();
        task.setTaskHandler(new HttpTaskHandler(){
            public void taskSuccessful(String json) {
                try {
                	 JSONObject result = new JSONObject(json);
                	 userimage = result.getString("result").toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            public void taskFailed() {
            }
        });
        task.execute("http://182.92.98.27:8000/getphotobyemail", "1", username);
		mListView.addFooterView(ListBottem, null, false);
		mListView.setAdapter(mListViewAdapter);
		add = (ImageView)findViewById(R.id.add);
	    add.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
					View layoutView = replyInflater.inflate(R.layout.activity_single_post_editor, null);
					//layout.setBackgroundColor(Color.GRAY);
					LinearLayout layout = (LinearLayout)layoutView.findViewById(R.id.linearlayout);
					layout.setFocusable(true);
					layout.setFocusableInTouchMode(true);
					replyEditor = (MyEditText)layoutView.findViewById(R.id.replyText);
					Button sendButton = (Button)layoutView.findViewById(R.id.sendButton);
					popupWindow = new PopupWindow(layoutView,LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
					popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
					popupWindow.setFocusable(true);
					popupWindow.setOutsideTouchable(true);
					replyEditor.setCurrentDialog(popupWindow);
					//popupWindow.setBackgroundDrawable(new PaintDrawable());
					layout.setOnKeyListener(new OnKeyListener() {
						@Override
						public boolean onKey(View v, int keyCode, KeyEvent event) {
							// TODO Auto-generated method stub
							if (keyCode == KeyEvent.KEYCODE_BACK) {  
					            popupWindow.dismiss();  
					            popupWindow = null;  
					            return true;  
					        }  
					        return false;
						}  
					});
					popupWindow.showAtLocation(findViewById(R.id.commentview), Gravity.BOTTOM, 0, 0);
					popupWindow.update();
					sendButton.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							if(popupWindow.isShowing()) {
								replytext = replyEditor.getText().toString();
		                    	boolean flag = checkAdd();
		        				if(flag == false){
		        					popupWindow.dismiss();
		        					Date date = new Date();
		        					final String nowtime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		        					HttpTask task = new HttpTask();
		        			        task.setTaskHandler(new HttpTaskHandler(){
		        			            public void taskSuccessful(String json) {
		        			            	Log.d("chen1", json + " " + username + " " + replytext + " " + nowtime);
		        			            	ItemData item = new ItemData();
		        			            	item.user_name = username;
		        			            	item.remark_content = replytext;
		        			            	item.remark_date = nowtime;
		        			            	item.userheadimage = userimage;
		        			            	data.add(item);
		        							int num = mListViewAdapter.count;
		        							mcount ++;
		        							remarknum.setText(String.valueOf(mcount));
		        							if(num + 5 > mcount) mListViewAdapter.count = mcount;
		        							else mListViewAdapter.count += 5;
		        							mListViewAdapter.notifyDataSetChanged();
		        			            }
		        			            public void taskFailed() {
		        			            }
		        			        });
		        			        task.execute("http://182.92.98.27:8000/replypost", "6", username, postid, replytext, "1");
		        			        
		        				}
		        				else popupWindow.showAsDropDown(v);
							}
						}
					});
			}
		});
	    Log.d("add","add marks");
	}
	
	private boolean checkAdd(){
		
		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(replytext)) {
			//replyEditor.setError(getString(R.string.error_field_required));
			focusView = replyEditor;
			cancel = true;
		}
		
		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		}
		
		return cancel;
	}
	public ArrayList<ItemData> getData() throws JSONException{  //get data from database;
		
		ArrayList<ItemData> sarray = new ArrayList<ItemData>();
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		username = bundle.getString("useremail");
		subjectid = bundle.getString("subjectid");
		authority = bundle.getString("authority");
		postid = bundle.getString("postid");
		Log.d("singlepost",username + " " + subjectid + " " + postid + " " + authority);
        String jsonresult = bundle.getString("result");
		
        JSONObject result = new JSONObject(jsonresult);

        JSONObject post = result.getJSONObject("post");
		postusername.setText(post.getString("email"));
		postdate.setText(post.getString("posttime"));
		posttitle.setText(post.getString("posttitle"));
		posttext.setText(post.getString("posttext"));
		String image = post.getString("imagetext");
		byte [] baseByte = android.util.Base64.decode(image, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(baseByte, 0, baseByte.length);
        headimage.setImageBitmap(bitmap);
        headimage.setAdjustViewBounds(true);
        headimage.setMaxHeight(50);
        headimage.setMaxWidth(50);
		Log.d("debug3", jsonresult);
		


      //JSONArray jsonArray = (JSONArray) result.get("subjects");

        JSONArray jsonArray = result.getJSONArray("result");
        Log.d("infomationsingle", jsonArray.length()+"");
        remarknum.setText(String.valueOf(jsonArray.length()));
        for (int i = 0; i < jsonArray.length(); i++) {

	         JSONObject subject = jsonArray.getJSONObject(i);
	
	         //every subject is a dictionary
	         ItemData item = new ItemData();
	         item.user_name = subject.getString("email");
	         item.remark_date = subject.getString("floortime");
	         item.remark_content = subject.getString("floortext");
	         item.userheadimage = subject.getString("imagetext");
	         sarray.add(item);
        }
		return sarray;
	}
	
    static class ViewHolder
    {
    	public TextView user;
    	public TextView remark_date;
        public TextView remark_content;
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
			return null;
		}
		public long getItemId(int position) {
			return position;
		}
		public View getView(int position, View convertView, ViewGroup parent) { 
			
			Log.d("count",count + "");
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.remark_list_item, null);
                holder.remark_content = (TextView)convertView.findViewById(R.id.Item_MainText);
                holder.remark_date = (TextView)convertView.findViewById(R.id.Item_Remarkdate);
                holder.user = (TextView)convertView.findViewById(R.id.Item_UserName);
                holder.userHead=(ImageView)convertView.findViewById(R.id.Item_UserHead);
                convertView.setTag(holder);
			}
			else{
                holder = (ViewHolder)convertView.getTag();  
            }
			Log.d("singledatasize",position  + " " + count);
			holder.user.setText(data.get(position).user_name);
			holder.remark_date.setText(data.get(position).remark_date);
			holder.remark_content.setText(data.get(position).remark_content);
			byte [] baseByte = android.util.Base64.decode(data.get(position).userheadimage, Base64.DEFAULT);
	        Bitmap bitmap = BitmapFactory.decodeByteArray(baseByte, 0, baseByte.length);
	        holder.userHead.setImageBitmap(bitmap);
	        holder.userHead.setAdjustViewBounds(true);
	        holder.userHead.setMaxHeight(40);
	        holder.userHead.setMaxWidth(35);
			return convertView;
		}
	}
}
