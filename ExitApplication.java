package com.example.schoolonline.util;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class ExitApplication extends Application {
	private List<Activity> activityList = new LinkedList<Activity>();
	private static ExitApplication instance;
	
	private ExitApplication(){}
	
	public static ExitApplication getInstance(){
		if(null == instance)	instance = new ExitApplication();
		return instance;
	}
	
	public void addActivity(Activity activity){
		activityList.add(activity);
	}
	
	public void exit(){
		int size = activityList.size();
		for(int i = size - 1; i > 0; i--){
			activityList.get(i).finish();
			activityList.remove(i);
		}
	}
	
	public void pop(int num){
		int size = activityList.size();
		for(int i = size - num; i < size ; i++)
			activityList.get(i).finish();
		for(int i = size - 1; i >= size - num; i--)
			activityList.remove(i);
	}
}
