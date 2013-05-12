package com.irof.irof_parts;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class IrofDraw extends RelativeLayout {

	private IrofDrawUtil	instance	= null;

	public IrofDrawUtil getInstance() {
		if (instance == null) instance = IrofDrawUtil.getInstance();
		return instance;
	}

	public IrofDraw(Context context, AttributeSet attrs) {
		super(context, attrs);
		getInstance();
		setClickable(true);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		instance.drawAction(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		instance.onTouchEvent(event);
		invalidate();
		return true;
	}

	public void clear() {
		instance.clear();
		invalidate();
	}

	public void undo() {
		instance.undo();
		invalidate();
	}

	// @Override
	// public boolean onInterceptTouchEvent (MotionEvent ev){
	// // タッチされたらまずonInterceptTouchEventが呼ばれる
	// // ここでtrueを返せば親ViewのonTouchEvent
	// // ここでfalseを返せば子ViewのonClickやらonLongClickやら
	// if(game_main.instance.pause_f)return true;
	// return false;
	// //return super.onInterceptTouchEvent(ev);
	// }

}
