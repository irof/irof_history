package com.irof.irof_parts;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.jfeinstein.jazzyviewpager.JazzyViewPager.TransitionEffect;

public class IrofViewPager extends ViewPager {
	// public class IrofViewPager extends JazzyViewPager {

	public IrofViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private boolean	pause_f	= false;

	public void setPauseF(boolean pause_f_) {
		this.pause_f = pause_f_;
	}

	// 画面ロックは onTouchEvent、onInterceptTouchEvent 両方ロック必要
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		if (pause_f) return false;
		return super.onTouchEvent(arg0);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// タッチされたらまずonInterceptTouchEventが呼ばれる
		// ここでtrueを返せば親ViewのonTouchEvent
		// ここでfalseを返せば子ViewのonClickやらonLongClickやら
		if (pause_f) return false;// true;
		return super.onInterceptTouchEvent(ev);
	}

	// not action
	// Jazzy効果
	public void setObjectForPosition(Object obj, int position) {
		// super.setObjectForPosition(obj,position);
	}

	public void setTransitionEffect(TransitionEffect effect) {
		// super.setTransitionEffect(effect);
	}

}
