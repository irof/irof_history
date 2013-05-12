/*
 * Copyright (c) 2012 Soichiro Kashima
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.irof.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * View which has circle-formed page indicator. see
 * http://ksoichiro.blogspot.jp/2012/08/android-viewpager.html
 * 
 * @author ksoichiro
 */
public class ViewIndicator extends View {

	private static final float	RADIUS		= 5.0f;
	private static final float	DISTANCE	= 30.0f;

	private int					mNumOfViews;
	private int					mPosition;
	private ViewPager			mViewPager;

	public ViewIndicator(Context context) {
		super(context);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public ViewIndicator(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public ViewIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setPosition(final int position) {
		if (position < mNumOfViews) {
			mPosition = position;
			if (mViewPager != null) {
				mViewPager.setCurrentItem(mPosition);
			}
			invalidate();
		}
	}

	public void setViewPager(final ViewPager viewPager) {
		mViewPager = viewPager;
		updateNumOfViews();
		mViewPager
				.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
					// @Override
					public void onPageScrollStateChanged(int state) {
					}

					// @Override
					public void onPageScrolled(int position,
							float positionOffest, int positionOffestPixels) {
					}

					// @Override
					public void onPageSelected(int position) {
						updateNumOfViews();
						setPosition(position);
					}
				});
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Paint paint = new Paint();
		paint.setStrokeWidth(1);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setColor(Color.BLACK);
		paint.setAntiAlias(true);

		for (int i = 0; i < mNumOfViews; i++) {
			float cx = (getWidth() - (mNumOfViews - 1) * DISTANCE) / 2 + i
					* DISTANCE;
			float cy = getHeight() / 2.0f;
			if (mPosition == i) {
				paint.setStyle(Paint.Style.FILL_AND_STROKE);
			}
			else {
				paint.setStyle(Paint.Style.STROKE);
			}
			canvas.drawCircle(cx, cy, RADIUS, paint);
		}
	}

	private void updateNumOfViews() {
		if (mViewPager.getAdapter() == null) {
			mNumOfViews = 0;
		}
		else {
			mNumOfViews = mViewPager.getAdapter().getCount();
		}
	}

}
