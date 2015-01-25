package com.example.schoolonline;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.schoolonline.fragment.EssenFragment;
import com.example.schoolonline.fragment.NormFragment;
import com.example.schoolonline.util.ExitApplication;
import com.example.schoolonline.util.HttpTask;
import com.example.schoolonline.util.HttpTask.HttpTaskHandler;



public class PostListActivity extends FragmentActivity{
	
	private ImageView add, search;
	private FrameLayout NormBtn, EssenBtn;
   // private TextView NormImg, EssenImg;
	private String subjectid;
	private String username;
	private String authority;
	private static FragmentManager fMgr;
	private final Handler mHandler = new Handler();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_list);
		Log.d("postlistactivity", "--------onCreate");
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		ExitApplication.getInstance().addActivity(this);
		Intent itnt = getIntent();
		subjectid = itnt.getExtras().getString("subjectid");
		username = itnt.getExtras().getString("username");
		authority = itnt.getExtras().getString("authority");
		Log.d("chenhui",subjectid + " " + username + " " + authority);
		
        NormBtn=(FrameLayout) findViewById(R.id.NormBtn);
        EssenBtn=(FrameLayout) findViewById(R.id.EssenBtn);
        
        fMgr = getSupportFragmentManager();
        initFragment();
              
        BtnOnclick btn=new BtnOnclick();
        NormBtn.setOnClickListener(btn);
        EssenBtn.setOnClickListener(btn);
        
        add = (ImageView)findViewById(R.id.add);
	    add.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
   				intent.setClass(PostListActivity.this, PostEditorActivity.class);
   				Bundle bundle=new Bundle();  
   				bundle.putString("username",username);
   				bundle.putString("subjectid",subjectid);
   				bundle.putString("authority",authority);
   				intent.putExtras(bundle); 
   				startActivity(intent);
			}
		});
	    
	    search = (ImageView)findViewById(R.id.search);
	    search.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
   				intent.setClass(PostListActivity.this, SearchPostActivity.class);
   				Bundle bundle=new Bundle();  
   				bundle.putString("username",username);
   				bundle.putString("subjectid",subjectid);
   				bundle.putString("authority",authority);
   				intent.putExtras(bundle); 
   				startActivity(intent);
			}
		});
	    
	    if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
            .add(R.id.container, new PlaceholderFragment()).commit();
        }
	}
	public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_post_list,
                                             container, false);
            return rootView;
        }
    }
	private void initFragment() {
        HttpTask task = new HttpTask();
        task.setTaskHandler(new HttpTaskHandler(){
            public void taskSuccessful(String json) {
                try {
                	 FragmentTransaction ft = fMgr.beginTransaction();
                     NormFragment NormFgt = new NormFragment();
                     Bundle bundle_b = new Bundle();
                     bundle_b.putString("subjectid", subjectid);
                     bundle_b.putString("username", username);
                     bundle_b.putString("authority",authority);
                     bundle_b.putString("result",json);
                     NormFgt.setArguments(bundle_b);
                     ft.add(R.id.fragmentRoot, NormFgt, "NormFragment");
                     ft.addToBackStack("NormFragment");
                     ft.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            public void taskFailed() {
            }
        });
        task.execute("http://182.92.98.27:8000/subjectallpost", "2", subjectid);
    }
	public static void popAllFragmentsExceptTheBottomOne() {
        for (int i = 0, count = fMgr.getBackStackEntryCount(); i < count; i++) {
            fMgr.popBackStack();
        }
    }
	private class  BtnOnclick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.NormBtn:
                	HttpTask task = new HttpTask();
                    task.setTaskHandler(new HttpTaskHandler(){
                        public void taskSuccessful(String json) {
                            try {
                            	 popAllFragmentsExceptTheBottomOne();
                            	 FragmentTransaction ft = fMgr.beginTransaction();
                                 NormFragment NormFgt = new NormFragment();
                                 Bundle bundle_b = new Bundle();
                                 bundle_b.putString("subjectid", subjectid);
                                 bundle_b.putString("username", username);
                                 bundle_b.putString("authority",authority);
                                 bundle_b.putString("result",json);
                                 NormFgt.setArguments(bundle_b);
                                 ft.add(R.id.fragmentRoot, NormFgt, "NormFragment");
                                 ft.addToBackStack("NormFragment");
                                 ft.commit();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        public void taskFailed() {
                        }
                    });
                    task.execute("http://182.92.98.27:8000/subjectallpost", "2", subjectid);
                    break;
                case R.id.EssenBtn:
                	HttpTask task1 = new HttpTask();
                    task1.setTaskHandler(new HttpTaskHandler(){
                        public void taskSuccessful(String json) {
                        	try{
                        		 popAllFragmentsExceptTheBottomOne();
                            	 FragmentTransaction ft = fMgr.beginTransaction();
                            	 ft.hide(fMgr.findFragmentByTag("NormFragment"));
                            	 EssenFragment EssenFgt = new EssenFragment();
                                 Bundle bundle_b = new Bundle();
                                 bundle_b.putString("subjectid", subjectid);
                                 bundle_b.putString("username", username);
                                 bundle_b.putString("authority",authority);
                                 bundle_b.putString("result",json);
                                 Log.d("Essenfgt",subjectid + " " + username + " " + json);
                                 EssenFgt.setArguments(bundle_b);
                                 ft.add(R.id.fragmentRoot, EssenFgt, "EssenFragment");
                                 ft.addToBackStack("EssenFragment");
                                 ft.commit();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        public void taskFailed() {
                        }
                    });
                    task1.execute("http://182.92.98.27:8000/getessence", "2", subjectid);
                    break;
                default:
                	break;
            }
        }
	}
	@Override
    public void onBackPressed() {
		PostListActivity.this.finish();
    }
}