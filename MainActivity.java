package com.example.schoolonline;

import com.example.schoolonline.LoginActivity;
import com.example.schoolonline.MainActivity;
import com.example.schoolonline.R;
import com.example.schoolonline.RegisterActivity;
import com.example.schoolonline.util.ExitApplication;
import com.example.schoolonline.util.StaticData;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.os.Build;

public class MainActivity extends ActionBarActivity {
	Button loginbutton = null;
    Button registerbutton = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ExitApplication.getInstance().addActivity(this);
        StaticData sd = (StaticData)getApplicationContext();
        sd.initialed = 0;
        sd.touchnotify = 0;
        loginbutton = (Button)findViewById(R.id.button);
        loginbutton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				Bundle bundle=new Bundle();
				bundle.putString("username", "wy");  
                intent.putExtras(bundle);
				intent.setClass(MainActivity.this, LoginActivity.class);
				startActivity(intent);
			}
        	
        });
        
        registerbutton = (Button)findViewById(R.id.button1);
        registerbutton.setOnClickListener(new OnClickListener(){
        	
        	public void onClick(View arg0) {
        		Intent intent = new Intent();
        		intent.setClass(MainActivity.this, RegisterActivity.class);
        		startActivity(intent);
        	}
        });
        
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}