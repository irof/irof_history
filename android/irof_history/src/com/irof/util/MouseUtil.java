package com.irof.util;import android.view.MotionEvent;public class MouseUtil {	public static int getPointerCount(MotionEvent event){		return event.getPointerCount();	}		public static float getX(MotionEvent event,int p){		return event.getX(p);	}		public static float getY(MotionEvent event,int p){		return event.getY(p);	}}