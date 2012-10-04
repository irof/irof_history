package com.irof.irof_parts;

import java.util.ArrayList;

import com.irof.irof_history.game_main;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

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
	
    private ArrayList<Path> pathList = new ArrayList<Path>();
    private ArrayList<Paint> paintList = new ArrayList<Paint>();
    private Path path;

	private IrofDrawUtil(){
	}
	
	private double colorCnt =0;
	private Paint createPaint(){
		Paint paint = new Paint(Paint.DITHER_FLAG);
        paint.setColor(Color.GREEN);
        //paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

        paint.setXfermode(null);
        paint.setMaskFilter(null);
        paint.setAlpha(0xFF);

        if(game_main.instance==null){
        	paint.setStrokeWidth(15);
        }
        else{
    		paint.setStrokeWidth(15 * game_main.instance.m_density);
        }

		colorCnt += 0.1;
		if (colorCnt >= 1.0) {
			colorCnt -= 1.0;
		}
		float hsv[] = { (float) colorCnt * 360, 1.0f, 1.0f };
		int c = Color.HSVToColor(hsv);
		int r = Color.red(c);
		int g = Color.green(c);
		int b = Color.blue(c);
		paint.setColor(Color.argb(255, r, g, b));

        return paint;
	}

	public void onDraw(Canvas canvas) {
		int len_path = pathList.size();
		for(int i=0;i<len_path;i++){
			Path path =pathList.get(i);
			Paint paint =paintList.get(i);
            canvas.drawPath(path, paint);
        }
	}
	
	public void onTouchEvent(MotionEvent event) {
		onTouchEvent(event,0,0);
	}

	public void onTouchEvent(MotionEvent event,int h_x,int h_y) {
		
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            // タッチしたとき
            path = new Path();
            path.moveTo(event.getX() + h_x, event.getY() + h_y);
            pathList.add(path);
            paintList.add(createPaint());
            break;
        case MotionEvent.ACTION_MOVE:
            // タッチしたまま動かしたとき
            path.lineTo(event.getX()+ h_x, event.getY() + h_y);
            break;
        case MotionEvent.ACTION_UP:
            // 指を離したとき
            path.lineTo(event.getX()+ h_x, event.getY() + h_y);
            break;
        default:
            break;
        }
        
	}


	
	public void clear() {
		pathList.clear();
		paintList.clear();
	}

	public void undo() {
		if(pathList.size()>=1){
			pathList.remove(pathList.size()-1);
		}
		if(paintList.size()>=1){
			paintList.remove(paintList.size()-1);
		}
	}
	
}
