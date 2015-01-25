package com.example.schoolonline;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.example.schoolonline.fragment.ChatFragment;
import com.example.schoolonline.fragment.CourseFragment;
import com.example.schoolonline.fragment.DiscussionFragment;
import com.example.schoolonline.fragment.MeFragment;
import com.example.schoolonline.util.ExitApplication;
import com.example.schoolonline.util.HttpTask;
import com.example.schoolonline.util.HttpTask.HttpTaskHandler;
import com.example.schoolonline.util.StaticData;

public class HomePageActivity extends ActionBarActivity {

    private static FragmentManager fMgr;
    private String mEmail;
    private Handler handler1, handler2;
	private Runnable runnable1, runnable2;

	private int total, test, refreshtime;
    @Override
    protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		handler1.removeCallbacks(runnable1); 
		handler2.removeCallbacks(runnable2);     
	}
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();  
		mEmail = intent.getStringExtra("email");
        setContentView(R.layout.activity_home_page);
        fMgr = getSupportFragmentManager();
        
        initFragment();
        dealBottomButtonsClickEvent();
        ExitApplication.getInstance().addActivity(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
            .add(R.id.container, new PlaceholderFragment()).commit();
        }
        StaticData sd = (StaticData)getApplicationContext();
        if(sd.touchnotify == 1 && fMgr.findFragmentByTag("ChatFragment")!=null && fMgr.findFragmentByTag("ChatFragment").isVisible()){
        	sd.touchnotify = 0;
        	ExitApplication.getInstance().exit();
        	ExitApplication.getInstance().pop(1);
        }
        total= 0;
        refreshtime = 2000;
        
        
        handler2 = new Handler();
        runnable2 = new Runnable() {
            @Override
            public void run() {
            	HttpTask task = new HttpTask();
    	        task.setTaskHandler(new HttpTaskHandler(){
    	            public void taskSuccessful(String json) {
    	            	Log.d("inittime", "successful");
    	            	try {
    	            		total = 0;
    	            		JSONObject result = new JSONObject(json);
		                    total = Integer.parseInt(result.getString("result"));
		                    Log.d("new messages", String.valueOf(total));
		                    if(total > 0){
		                        StaticData sd = (StaticData)getApplicationContext();
		                    	sd.touchnotify = 1;
		                    	String ns = Context.NOTIFICATION_SERVICE;
		        				NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		        				int icon = R.drawable.icon;
		        				CharSequence tickerText = "我的通知栏标题";
		        				long when = System.currentTimeMillis();
		        				Notification notification = new Notification(icon, tickerText, when);
		        				Context context = getApplicationContext();
		        				
		        				CharSequence contentTitle = "您有好友发送的未读信息";
		        				
		        				CharSequence contentText = String.valueOf(total)+"条";
		        				
		        				Intent notificationIntent = new Intent(getApplicationContext(), HomePageActivity.class);
		        				Bundle bundle=new Bundle();
		        				bundle.putString("email", mEmail);  
		        				notificationIntent.putExtras(bundle);
		        				PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		        				notification.flags=Notification.FLAG_AUTO_CANCEL;
		        				notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent); 
		        				mNotificationManager.notify(1, notification);
		                    }else{
		                    	String ns = Context.NOTIFICATION_SERVICE;
		        				NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		        				mNotificationManager.cancel(1);
		                    }
    	                }
    	                catch (Exception e) {
    	                    e.printStackTrace();
    	                }
    	            }

    	            public void taskFailed() {
    	            	Log.d("inittime", "failed");
    	            }
    	        });
    	        task.execute("http://182.92.98.27:8000/getunread", "getunread", mEmail, "2");
            	handler2.postDelayed(this, refreshtime);
            }
        };
        handler2.postDelayed(runnable2, refreshtime);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_log_out){
            ExitApplication.getInstance().exit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_home_page,
                                             container, false);
            return rootView;
        }
    }

    private void initFragment() {

       
        FragmentTransaction ft = fMgr.beginTransaction();
        ChatFragment sf = new ChatFragment();
        Bundle bundle =new Bundle();
        bundle.putString("email", mEmail);
        sf.setArguments(bundle);
        ft.add(R.id.fragmentRoot, sf, "ChatFragment");
        ft.addToBackStack("ChatFragment");
        ft.commit();
    }

    private void dealBottomButtonsClickEvent() {
        findViewById(R.id.rbCourse).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            	HttpTask task = new HttpTask();
                task.setTaskHandler(new HttpTaskHandler(){
                    public void taskSuccessful(String json) {
                        try {
                        	 popAllFragmentsExceptTheBottomOne();
                             FragmentTransaction ft = fMgr.beginTransaction();
                             ft.hide(fMgr.findFragmentByTag("ChatFragment"));
                            CourseFragment CourseFragment = new CourseFragment();
                            Bundle bundle_b =new Bundle();
                            bundle_b.putString("result", json);
                            bundle_b.putString("email", mEmail);
               	            CourseFragment.setArguments(bundle_b);
                            ft.add(R.id.fragmentRoot, CourseFragment, "CourseFragment");
                            ft.addToBackStack("CourseFragment");
                            ft.commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    public void taskFailed() {
                    }
                });
                task.execute("http://182.92.98.27:8000/allsubject", "4");
                

            }
        });
            
        findViewById(R.id.rbChat).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(fMgr.findFragmentByTag("ChatFragment")!=null && fMgr.findFragmentByTag("ChatFragment").isVisible()) {
                    return;
                }
                popAllFragmentsExceptTheBottomOne();

            }
            
        });
        findViewById(R.id.rbDiscussion).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                HttpTask task = new HttpTask();
                task.setTaskHandler(new HttpTaskHandler(){
                    public void taskSuccessful(String json) {
                        try {

                            popAllFragmentsExceptTheBottomOne();
                            FragmentTransaction ft = fMgr.beginTransaction();
                            ft.hide(fMgr.findFragmentByTag("ChatFragment"));
                            DiscussionFragment sf = new DiscussionFragment();
                            Bundle bundle_b =new Bundle();
                            bundle_b.putString("email", mEmail);
                            bundle_b.putString("result", json);
                            sf.setArguments(bundle_b);
                            ft.add(R.id.fragmentRoot, sf, "DiscussionFragment");
                            ft.addToBackStack("DiscussionFragment");
                            ft.commit();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    
                    public void taskFailed() {
                    }
                });
                task.execute("http://182.92.98.27:8000/userallsubject", "1", mEmail);
            }
        });
        findViewById(R.id.rbMe).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popAllFragmentsExceptTheBottomOne();
                FragmentTransaction ft = fMgr.beginTransaction();
                ft.hide(fMgr.findFragmentByTag("ChatFragment"));
                MeFragment sf = new MeFragment();
                Bundle bundle_b =new Bundle();
                bundle_b.putString("email", mEmail);
                sf.setArguments(bundle_b);
                ft.add(R.id.fragmentRoot, sf, "MeFragment");
                ft.addToBackStack("MeFragment");
                ft.commit();
            }
        });
    }
    
    
    public static void popAllFragmentsExceptTheBottomOne() {
        for (int i = 0, count = fMgr.getBackStackEntryCount() - 1; i < count; i++) {
            fMgr.popBackStack();
        }
    }
    
    @Override
    public void onBackPressed() {
        if(fMgr.findFragmentByTag("ChatFragment")!=null && fMgr.findFragmentByTag("ChatFragment").isVisible()) {
        	ExitApplication.getInstance().exit();
        	ExitApplication.getInstance().pop(1);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            super.onBackPressed();
        }
    }
}