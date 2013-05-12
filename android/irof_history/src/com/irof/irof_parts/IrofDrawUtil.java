package com.irof.irof_parts;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

import com.irof.irof_history.game_main;
import com.irof.util.LogUtil;

/*
 * note)
 *  bitmap使わずに出来るか試作。ダメならbitmap作る形で
 * 　　see http://dev.classmethod.jp/smartphone/android-tips-1-view/
 * 
 */

public class IrofDrawUtil {

	private static IrofDrawUtil	instance	= null;
	private static String		TAG			= IrofDrawUtil.class
													.getSimpleName();

	public static IrofDrawUtil getInstance() {
		if (instance == null) instance = new IrofDrawUtil();
		return instance;
	}

	private ArrayList<Path>		pathList	= new ArrayList<Path>();
	private ArrayList<Paint>	paintList	= new ArrayList<Paint>();
	private Path				path;

	private IrofDrawUtil() {
	}

	private Bitmap	mBitmap;
	private Canvas	mCanvas;
	public boolean	low_bmp_flag	= false;

	public void createBitmap(int width, int height) {
		try {
			if (low_bmp_flag) {
				mBitmap = Bitmap.createBitmap(width, height,
						Bitmap.Config.ARGB_4444);
			}
			else {
				mBitmap = Bitmap.createBitmap(width, height,
						Bitmap.Config.ARGB_8888);
			}
		}
		catch (Exception e) {
			LogUtil.error(TAG, "createBitmap", e);
		}
		mCanvas = new Canvas(mBitmap);
		mCanvas.drawColor(Color.WHITE);
	}

	public void finalize() {
		try {
			if (mBitmap != null) {
				mBitmap.recycle();
				mBitmap = null;
			}
		}
		catch (Exception e) {
		}
	}

	public void setLow_bmp_flag(boolean low_bmp_flag_) {
		low_bmp_flag = low_bmp_flag_;
	}

	private double	colorCnt	= 0;

	private Paint createPaint() {
		Paint paint = new Paint(Paint.DITHER_FLAG);
		paint.setColor(Color.GREEN);
		// paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStyle(Paint.Style.STROKE);
		paint.setAntiAlias(true);

		paint.setXfermode(null);
		paint.setMaskFilter(null);
		paint.setAlpha(0xFF);

		if (game_main.instance == null) {
			paint.setStrokeWidth(15);
		}
		else {
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

	private Paint	mBitmapPaint;

	public void drawAction(Canvas canvas) {
		if (mBitmap == null || mBitmapPaint == null) return;
		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
		/*
		 * int len_path = pathList.size(); for(int i=0;i<len_path;i++){ Path
		 * path =pathList.get(i); Paint paint =paintList.get(i);
		 * canvas.drawPath(path, paint); }
		 */
	}

	public void onTouchEvent(MotionEvent event) {
		onTouchEvent(event, 0, 0);
	}

	private int	step	= 0;

	public void onTouchEvent(MotionEvent event, int h_x, int h_y) {

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mBitmapPaint = createPaint();
				// タッチしたとき
				path = new Path();
				path.moveTo(event.getX() + h_x, event.getY() + h_y);
				pathList.add(path);
				paintList.add(mBitmapPaint);
				break;
			case MotionEvent.ACTION_MOVE:
				// タッチしたまま動かしたとき
				path.lineTo(event.getX() + h_x, event.getY() + h_y);
				break;
			case MotionEvent.ACTION_UP:
				// 指を離したとき
				path.lineTo(event.getX() + h_x, event.getY() + h_y);
				break;
			default:
				break;
		}
		canvasDraw(false);
	}

	public void canvasDraw(boolean refresh) {
		if (mCanvas == null) return;
		if (refresh) mCanvas.drawColor(Color.WHITE);
		int len_path = pathList == null ? 0 : pathList.size();
		for (int i = step; i < len_path; i++) {
			Path path = pathList.get(i);
			Paint paint = paintList.get(i);
			mCanvas.drawPath(path, paint);
		}
	}

	public void clear() {
		pathList.clear();
		paintList.clear();
		step = 0;
		canvasDraw(true);
	}

	public void undo() {
		if (pathList.size() >= 1) {
			pathList.remove(pathList.size() - 1);
		}
		if (paintList.size() >= 1) {
			paintList.remove(paintList.size() - 1);
		}
		step = 0;
		canvasDraw(true);
		int len_path = pathList == null ? 0 : pathList.size();
		if (len_path > 0) step = len_path - 1;
	}

}
