package com.irof.irof_history;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class IrofImageView extends ImageView {

	private boolean draw_f = false;
	private Animation anime;
	private IrofDrawUtil instance = null;
	public IrofImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		instance = IrofDrawUtil.getInstance();
		anime = AnimationUtils.loadAnimation(context, R.anim.move);
		setClickable(true);
		
//		View.OnTouchListener listner = new View.OnTouchListener() {
//			//@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				return instance.onTouch(v,event);
//			}
//		};
//		setOnTouchListener(listner);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
	    super.onDraw(canvas);
	    if(draw_f)instance.onDraw(canvas);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(draw_f){
			//instance.onTouchEvent(event);
			if(instance.onTouch(this,event)){
			    invalidate();
			    return true;
			}
			return false;
		}
		return super.onTouchEvent(event);
	}
	

	public void on_groovy(View v) {
		switch(v.getId()){
			case R.id.icon_twitter05:
				{
					ImageView iv = _findViewById(R.id.icon_twitter05);
					iv.setAnimation(anime);
					iv.startAnimation(anime);
					if(iv.getTag()==null || "normal".equals(iv.getTag())){
						iv.setImageResource(R.drawable.icon_twitter05g);
						iv.setTag("groovy");
					}
					else{
						iv.setImageResource(R.drawable.icon_twitter05);
						iv.setTag("normal");
					}
				}
				break;
			case R.id.icon_twitter07:
				{
					ImageView iv = _findViewById(R.id.icon_twitter07);
					iv.setAnimation(anime);
					iv.startAnimation(anime);
					if(iv.getTag()==null || "normal".equals(iv.getTag())){
						iv.setImageResource(R.drawable.icon_twitter07g);
						iv.setTag("groovy");
					}
					else{
						iv.setImageResource(R.drawable.icon_twitter07);
						iv.setTag("normal");
					}
				}
				break;
			default:
				break;
		}
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends View> T _findViewById(final int id){
	    return (T)findViewById(id);
	}
}
