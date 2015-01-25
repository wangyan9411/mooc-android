
package com.example.schoolonline.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;

public class HttpTask extends AsyncTask<String, Integer, String> {
    private static final String TAG = "HTTP_TASK";

    @Override
    protected String doInBackground(String... params) {
        // Performed on Background Thread
        //String url = params[0];
        try {
			JSONObject param = new JSONObject();

			String url = params[0];
			String type = params[1];
			if ( type.equals("1")){               // 鑾峰彇鐢ㄦ埛鎵�閫夎绋�
				String memail = params[2];
				param.put("email", memail);
			} else if (type.equals("2")) {       // 鑾峰彇甯栧瓙
				String subjectid = params[2];
				Integer id = new Integer(subjectid);
				int i = id.intValue();
				param.put("subjectid", i);
			} else if (type.equals("3")) {    // 鍙戝笘
				String memail = params[2];
				param.put("email", memail);
				String subjectid = params[3];
				Integer id = new Integer(subjectid);
				int i = id.intValue();
				param.put("subjectid", i);
				String posttitle = params[4];
				param.put("posttitle", posttitle);
				String posttext = params[5];
				param.put("posttext", posttext);
			} else if (type.equals("4")) {           //鑾峰彇鎵�鏈夎绋嬪垪琛�
			} else if (type.equals("5")) {           
				String postid = params[2];
				Integer id = new Integer(postid);
				int i = id.intValue();
				param.put("postid", i);
			}else if (type.equals("6")) {
				String email = params[2];
				param.put("email", email);
				String postid = params[3];
				Integer index = new Integer(postid);
				int in = index.intValue();
				param.put("postid", in);
				String floortext = params[4];
				param.put("floortext", floortext);
				String floorresponse = params[5];
				index = new Integer(floorresponse);
				in = index.intValue();
				param.put("floorresponse", in);
			}else if (type.equals("11")) {
				String subjectid = params[2];
				param.put("subjectid", subjectid);
				String postid = params[3];
				Integer index = new Integer(postid);
				int in = index.intValue();
				param.put("postid", in);
			}else if (type.equals("12")) {
				String email = params[2];
				param.put("email", email);
				String time = params[3];
				Integer index = new Integer(time);
				int in = index.intValue();
				param.put("lasttime", in);
			}else if(type.equals("searchbypost")){
				String searchpost = params[2];
				param.put("posttitle", searchpost);
			}else if(type.equals("searchbyname")){
				String searchuser = params[2];
				param.put("email", searchuser);
			}else if (type.equals("selectsubject")) {
				String email = params[2];
				param.put("email", email);
				String subjectid = params[3];
				int id = Integer.valueOf(subjectid).intValue();
				param.put("subjectid", id);
			}else if (type.equals("deletesubject")) {
				String email = params[2];
				param.put("email", email);
				String subjectid = params[3];
				int id = Integer.valueOf(subjectid).intValue();
				param.put("subjectid", id);
			} else if (type.equals("7")) {
				String email = params[2];
				param.put("email", email);
			}else if (type.equals("8")) {
				String email = params[2];
				param.put("emailfrom", email);
				email = params[3];
				param.put("emailto", email);
				String extra = params[4];
				param.put("extratext", extra);
			}else if (type.equals("getunsolvedcontacts")) {
				String email = params[2];
				param.put("email", email);
			}else if (type.equals("getcontacts")) {
				String email = params[2];
				param.put("email", email);
			}else if (type.equals("10")) {
				String email = params[2];
				param.put("emailfrom", email);
				email = params[3];
				param.put("emailto", email);
			}else if (type.equals("getchat")) {
				String from = params[2];
				param.put("emailfrom", from);
				String to = params[3];
				param.put("emailto", to);
				String timestamp = params[4];
				param.put("timestamp", timestamp);
			}else if (type.equals("updatetimestamp")) {
				String time = params[2];
				param.put("timestamp", time);
				String from = params[3];
				param.put("emailfrom", from);
				String to = params[4];
				param.put("emailto", to);
			} else if (type.equals("postchat")) {
				String from = params[2];
				param.put("emailfrom", from);
				String to = params[3];
				param.put("emailto", to);
				String text = params[4];
				param.put("recordtext", text);
			} else if (type.equals("gettimestamp")) {
				String from = params[2];
				param.put("emailfrom", from);
				String to = params[3];
				param.put("emailto", to);
			} else if (type.equals("getunread")) {
				String from = params[2];
				param.put("email", from);
				String restype = params[3];
				param.put("type", restype);//0 give me photos; 1 give me new messages; 2 give me total number of new messages
				
			} else if ( type.equals("showinfo")){               // 鑾峰彇鐢ㄦ埛鎵�閫夎绋�
				String memail = params[2];
				param.put("email", memail);
			} else if ( type.equals("getphotobyemail")){ 
				String memail = params[2];
				param.put("email", memail);
			}

			
			HttpPost request = new HttpPost(url);
			StringEntity se = new StringEntity(param.toString()); 
			request.setEntity(se);
			Log.d("connect", "start sending");
			HttpResponse httpResponse = new DefaultHttpClient().execute(request);
			Log.d("connect", "send a request");
			String retSrc = EntityUtils.toString(httpResponse.getEntity());
			Log.d("connect", "recieve");
			return retSrc;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.d("connect", "json");
			Log.d("connect", e.getMessage());
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			Log.d("connect", "encoding");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			Log.d("connect", "client");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			Log.d("connect", "IO");
			Log.d("connect", e.getMessage());
			Log.d("connect", e.toString());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "null";
    }

    @Override
    protected void onPostExecute(String json) {
        // Done on UI Thread
        if (json != null && json != "") {
            Log.d(TAG, "taskSuccessful");
            int i1 = json.indexOf("["), i2 = json.indexOf("{"), i = i1 > -1
                    && i1 < i2 ? i1 : i2;
            if (i > -1) {
                json = json.substring(i);
                taskHandler.taskSuccessful(json);
            } else {
                Log.d(TAG, "taskFailed");
                taskHandler.taskFailed();
            }
        } else {
            Log.d(TAG, "taskFailed");
            taskHandler.taskFailed();
        }
    }

    public static interface HttpTaskHandler {
        void taskSuccessful(String json);

        void taskFailed();
    }

    HttpTaskHandler taskHandler;

    public void setTaskHandler(HttpTaskHandler httpTaskHandler) {
        this.taskHandler = httpTaskHandler;
    }
}