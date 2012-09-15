package com.irof.irof_history;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class IrofDraw extends RelativeLayout{

	private IrofDrawUtil instance = null;
    
	public IrofDraw(Context context, AttributeSet attrs) {
		super(context, attrs);
		instance = IrofDrawUtil.getInstance();
		setClickable(true);
	}
 
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        instance.onDraw(canvas);
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

}
