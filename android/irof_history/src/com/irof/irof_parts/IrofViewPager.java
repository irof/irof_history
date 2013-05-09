package com.irof.irof_parts;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.irof.irof_history.game_main;
import com.jfeinstein.jazzyviewpager.JazzyViewPager.TransitionEffect;

public class IrofViewPager extends ViewPager {
//public class IrofViewPager extends JazzyViewPager {
	
	public IrofViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		if(game_main.instance.pause_f)return false;
		return super.onTouchEvent(arg0);
	}

//not action
	//Jazzy効果
	public void setObjectForPosition(Object obj, int position) {
		//super.setObjectForPosition(obj,position);
	}
	
	public void setTransitionEffect(TransitionEffect effect) {
		//super.setTransitionEffect(effect);
	}

}
