package com.example.schoolonline.util;

import java.util.HashMap;
import java.util.Map;

import android.app.Application;

public class StaticData extends Application{
	public Map<String, String> chatlistphotos;
	public int initialed;
	public int touchnotify;
	public StaticData() {
		initialed = 0;
		touchnotify = 0;
		chatlistphotos = new HashMap<String, String>();
	}
}
