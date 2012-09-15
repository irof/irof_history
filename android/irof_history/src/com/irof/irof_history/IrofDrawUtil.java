package com.irof.irof_history;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;

import com.irof.util.MouseUtil;

/*
 * note)
 *  bitmap使わずに出来るか試作。ダメならbitmap作る形で
 * 　　see http://dev.classmethod.jp/smartphone/android-tips-1-view/
 * 
 */

public class IrofDrawUtil {

	private static IrofDrawUtil instance = null;
	
	public static IrofDrawUtil getInstance(){
		if(instance==null)instance = new IrofDrawUtil();
		return instance;
	}

	private Paint paint;
    private ArrayList<Path> pathList = new ArrayList<Path>();
    private Path path;

	private IrofDrawUtil(){
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(15);
	}

	public void onDraw(Canvas canvas) {
        for (Path path : pathList) {
            canvas.drawPath(path, paint);
        }
	}

	public void onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            // タッチしたとき
            path = new Path();
            path.moveTo(event.getX(), event.getY());
            pathList.add(path);
            break;
        case MotionEvent.ACTION_MOVE:
            // タッチしたまま動かしたとき
            path.lineTo(event.getX(), event.getY());
            break;
        case MotionEvent.ACTION_UP:
            // 指を離したとき
            path.lineTo(event.getX(), event.getY());
            break;
        default:
            break;
        }
	}

	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();

		int[] l = new int[2];
	    v.getLocationOnScreen(l);
	    int h_x = l[0];
	    int h_y = l[1];

		switch (action) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN:
			case MotionEvent.ACTION_POINTER_2_DOWN:
			case MotionEvent.ACTION_POINTER_3_DOWN:
			case 0x306:
			case 0x406:
				if(Build.VERSION.SDK_INT>=5){
					int pointCount = MouseUtil.getPointerCount(event);
					for (int p = 0; p < pointCount; p++) {
						int px=(int)MouseUtil.getX(event,p) + h_x;
						int py=(int)MouseUtil.getY(event,p) + h_y;
			            path = new Path();
			            path.moveTo(px, py);
			            pathList.add(path);
					}
				}
				else{
		            path = new Path();
		            path.moveTo(event.getX(), event.getY());
		            pathList.add(path);
				}
				break;

	        case MotionEvent.ACTION_MOVE:
	            // タッチしたまま動かしたとき
				if(Build.VERSION.SDK_INT>=5){
					int pointCount = MouseUtil.getPointerCount(event);
					for (int p = 0; p < pointCount; p++) {
						int px=(int)MouseUtil.getX(event,p) + h_x;
						int py=(int)MouseUtil.getY(event,p) + h_y;
			            path.lineTo(px, py);
					}
				}
				else{
		            path.lineTo(event.getX(), event.getY());
				}
	            break;
				
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
			case MotionEvent.ACTION_POINTER_2_UP:
			case MotionEvent.ACTION_POINTER_3_UP:
			case 0x305:
			case 0x405:
				if(Build.VERSION.SDK_INT>=5){
					int pointCount = MouseUtil.getPointerCount(event);
					for (int p = 0; p < pointCount; p++) {
						int px=(int)MouseUtil.getX(event,p) + h_x;
						int py=(int)MouseUtil.getY(event,p) + h_y;
			            path.lineTo(px, py);
					}
				}
				else{
		            path.lineTo(event.getX(), event.getY());
				}
				break;
		}
		return true;
	}

	public void clear() {
		pathList.clear();
	}

	public void undo() {
		pathList.remove(pathList.size()-1);
	}
	
}
