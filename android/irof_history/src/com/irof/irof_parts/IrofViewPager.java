package com.irof.irof_parts;

import com.irof.irof_history.game_main;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class IrofViewPager extends ViewPager {

	public IrofViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		if(game_main.instance.pause_f)return false;
		return super.onTouchEvent(arg0);
	}

	
}
