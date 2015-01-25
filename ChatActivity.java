package com.example.schoolonline;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.schoolonline.util.HttpTask;
import com.example.schoolonline.util.HttpTask.HttpTaskHandler;


public class ChatActivity extends Activity implements OnClickListener{
    /** Called when the activity is first created. */

	private Button mBtnSend;
	private Button mBtnBack;
	private EditText mEditTextContent;
	private TextView title;
	private ListView mListView;
	private ChatMsgViewAdapter mAdapter;
	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
	private String emailfrom, emailto, timestamp, servertime;
	private Handler handler;
	private Runnable runnable;
	private int test, refreshtime;
	
    


	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_xiaohei);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        test = 0;
        refreshtime = 2000;
        timestamp = new String("init");
        initView();
        
        initData();
        handler = new Handler();
        runnable=new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                //要做的事情
            	if(timestamp.equals("init")){
            		HttpTask task = new HttpTask();
        	        task.setTaskHandler(new HttpTaskHandler(){
        	            public void taskSuccessful(String json) {
        	            	Log.d("inittime", "successful");
        	            	try {
        	                    JSONObject result = new JSONObject(json);
        	                    String time =result.getString("result");
        	                    timestamp = new String(time);
        	                    Log.d("inittime", timestamp);
        	                }
        	                catch (Exception e) {
        	                    e.printStackTrace();
        	                }
        	            }

        	            public void taskFailed() {
        	            	Log.d("inittime", "failed");
        	            }
        	        });
        	        task.execute("http://182.92.98.27:8000/gettimestamp", "gettimestamp", emailfrom, emailto);
            	}else{
            		HttpTask task = new HttpTask();
        	        task.setTaskHandler(new HttpTaskHandler(){
        	            public void taskSuccessful(String json) {
        	            	Log.d("gettimestamp", "successful");
        	            	try {
        	                    JSONObject result = new JSONObject(json);
        	                    servertime = result.getString("result");
        	                    Log.d("servertime", servertime);
        	                    Log.d("mytime", timestamp);
        	                    if(!servertime.equals(timestamp)){
            	                    Log.d("gettimestamp", "Different");
            	                    HttpTask task = new HttpTask();
            	        	        task.setTaskHandler(new HttpTaskHandler(){
            	        	            public void taskSuccessful(String json) {
            	        	            	Log.d("newrecord", json);
            	        	            	
            	        	            	try {
            	        	            		JSONObject result = new JSONObject(json);
            	        	                    JSONArray jsonArray =result.getJSONArray("result");
            	        	                    
            	        	                    for (int i=0; i<jsonArray.length(); i++) {
            	        	                        JSONObject item = jsonArray.getJSONObject(i);
            	        	                        String time = item.getString("time");
            	        	                        String from = item.getString("emailfrom");
            	        	                        String text = item.getString("recordtext");
            	        	                        ChatMsgEntity entity = new ChatMsgEntity();
            	        	                		entity.setDate(time);
            	        	                		entity.setName(from);
            	        	                		if(from.equals(emailfrom)){
            	        	                			entity.setMsgType(false);
            	        	                		}else{
            	        	                			entity.setMsgType(true);
            	        	                		}
            	        	                		entity.setText(text);
            	        	                		mDataArrays.add(entity);
            	        	                    }
            	        	                    mAdapter.notifyDataSetChanged();
            	        	                    mListView.setSelection(mListView.getCount() - 1);
            	        	                }
            	        	                catch (Exception e) {
            	        	                    e.printStackTrace();
            	        	                }

                	                    	timestamp = new String(servertime);
            	        	            }

            	        	            public void taskFailed() {
            	        	            	Log.d("gettimestamp", "failed");
            	        	            }
            	        	        });
            	        	        task.execute("http://182.92.98.27:8000/getchat", "getchat", emailfrom, emailto, timestamp);
            	            	
        	                    }
        	                }
        	                catch (Exception e) {
        	                    e.printStackTrace();
        	                }
        	            }

        	            public void taskFailed() {
        	            	Log.d("gettimestamp", "failed");
        	            }
        	        });
        	        task.execute("http://182.92.98.27:8000/gettimestamp", "gettimestamp", emailfrom, emailto);
            	}
                Log.d("testhandler", String.valueOf(test));
            	test++;
                handler.postDelayed(this, refreshtime);
            }
        };
        handler.postDelayed(runnable, refreshtime);
    }
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		handler.removeCallbacks(runnable);   
	}
    
    public void initView()
    {
    	mListView = (ListView) findViewById(R.id.listview);
    	mBtnSend = (Button) findViewById(R.id.btn_send);
    	mBtnSend.setOnClickListener(this);
    	mBtnBack = (Button) findViewById(R.id.btn_back);
    	mBtnBack.setOnClickListener(this);
    	title = (TextView) findViewById(R.id.title);
    	
    	mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
    }

    public void initData()
    {
    	Intent intent=getIntent();
		//mEmail = intent.getStringExtra("emailfrom");
    	emailfrom = intent.getStringExtra("emailfrom");
    	emailto = intent.getStringExtra("emailto");
    	title.setText(emailto);
        String json = intent.getStringExtra("history");
        Log.d("emailfrom", emailfrom);
        Log.d("emailfrom", emailto);

        //json = "";
        Log.d("history_in_activity", json);
        try {
            JSONObject result = new JSONObject(json);
            //JSONArray jsonArray = (JSONArray) result.get("subjects");
            JSONArray jsonArray =result.getJSONArray("result");
            
            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                String time = item.getString("time");
                String from = item.getString("emailfrom");
                String text = item.getString("recordtext");
                ChatMsgEntity entity = new ChatMsgEntity();
        		entity.setDate(time);
        		entity.setName(from);
        		if(from.equals(emailfrom)){
        			entity.setMsgType(false);
        		}else{
        			entity.setMsgType(true);
        		}
        		entity.setText(text);
        		mDataArrays.add(entity);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    	mAdapter = new ChatMsgViewAdapter(this, mDataArrays, intent.getStringExtra("image_emailfrom"), intent.getStringExtra("image_emailto"));
		mListView.setAdapter(mAdapter);
		
    }


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId())
		{
		case R.id.btn_send:
			send();
			break;
		case R.id.btn_back:
			Intent intent = getIntent();
	        Bundle bundle=new Bundle();

	        bundle.putString("timestamp", timestamp);
	        bundle.putString("emailto", emailto); 

	        intent.putExtras(bundle);

	        setResult(100, intent);
            //关闭掉这个Activity  
            finish();
			break;
		}
	}
	
	private void send()
	{
		String contString = mEditTextContent.getText().toString();
		if (contString.length() > 0)
		{
			ChatMsgEntity entity = new ChatMsgEntity();
			entity.setDate(getDate());
			entity.setName(emailfrom);
			entity.setMsgType(false);
			entity.setText(contString);
			
			mDataArrays.add(entity);
			mAdapter.notifyDataSetChanged();
			
			mEditTextContent.setText("");
			
			mListView.setSelection(mListView.getCount() - 1);
	        HttpTask task = new HttpTask();
	        task.setTaskHandler(new HttpTaskHandler(){
	            public void taskSuccessful(String json) {
	               try {   
	                Log.d("postchat", "successful");

	               } catch (Exception e) {
	                    e.printStackTrace();
	               }
	            }

	            public void taskFailed() {
	            	Log.d("postchat", "failed");
	            }
	        });
	        task.execute("http://182.92.98.27:8000/postchat", "postchat", emailfrom, emailto, contString);
		}
	}
	
    private String getDate() {
        Calendar c = Calendar.getInstance();

        String year = String.valueOf(c.get(Calendar.YEAR));
        String month = String.valueOf(c.get(Calendar.MONTH));
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH) + 1);
        String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
        String mins = String.valueOf(c.get(Calendar.MINUTE));
        String secs = String.valueOf(c.get(Calendar.SECOND));
        
        
        StringBuffer sbBuffer = new StringBuffer();
        sbBuffer.append(year + "-" + month + "-" + day + " " + hour + ":" + mins + ":" + secs); 
        						
        						
        return sbBuffer.toString();
    }


@Override
    public void onBackPressed() {
        Log.i("tag", "onBackPressed");
        Intent intent = getIntent();
        Bundle bundle=new Bundle();

        bundle.putString("timestamp", timestamp);
        bundle.putString("emailto", emailto); 

        intent.putExtras(bundle);

        setResult(100, intent);
        super.onBackPressed();
    }

/*public final void setResult(int resultCode, Intent data) {
    synchronized (this) {
        mResultCode = resultCode;
        mResultData = data;
    }
}

public void finish() {
    if (mParent == null) {
        int resultCode;
        Intent resultData;
        synchronized (this) {
            resultCode = mResultCode;
            resultData = mResultData;
        }
        if (Config.LOGV) Log.v(TAG, "Finishing self: token=" + mToken);
        try {
            if (ActivityManagerNative.getDefault()
                .finishActivity(mToken, resultCode, resultData)) {
                mFinished = true;
            }
        } catch (RemoteException e) {
            // Empty
        }
    } else {
        mParent.finishFromChild(this);
    }
}
*/

    
    public void head_xiaohei(View v) {     //������ ���ذ�ť
    	
      } 
}