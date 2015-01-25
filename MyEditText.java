package com.example.schoolonline.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.PopupWindow;

public class MyEditText extends EditText{
	PopupWindow mywindow = null;
	public MyEditText(Context context){
		super(context);
	} 
	public MyEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public MyEditText(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}
	public void setCurrentDialog(PopupWindow dialog){
		mywindow = dialog;
	}
	@Override
	public boolean dispatchKeyEventPreIme(KeyEvent event){
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK)
			mywindow.dismiss();
		return super.dispatchKeyEventPreIme(event);
	}
}
