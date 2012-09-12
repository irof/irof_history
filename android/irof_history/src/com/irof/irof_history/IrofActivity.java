package com.irof.irof_history;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class IrofActivity extends Activity {

	
	private Animation anime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irof);
        
        anime = AnimationUtils.loadAnimation(this, R.anim.move);

        ViewPager mViewPager = _findViewById(R.id.viewpager);
        PagerAdapter mPagerAdapter = new MyPagerAdapter();
        mViewPager.setAdapter(mPagerAdapter);
    }

    private class MyPagerAdapter extends PagerAdapter {
		int[] pages = {
				R.layout.layout01, 
				R.layout.layout02, 
				R.layout.layout03,
				R.layout.layout04,
				R.layout.layout05,
				R.layout.layout06,
   				R.layout.layout07,
   				R.layout.layout08,
   				R.layout.layout09,
   				R.layout.layout10,
   				R.layout.layout11,
   				R.layout.layout12,
   				R.layout.layout13,
   		};
    	@Override
    	public Object instantiateItem(ViewGroup container, int position) {
    		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    		FrameLayout fn = (FrameLayout)inflater.inflate(pages[position], null);
    		container.addView(fn);
    		TextView tx = _findViewById(R.id.irof_title);
    		tx.setText(String.format("%s %02d",getResources().getString(R.string.irof_world),position));
    		return fn;
    	}

    	@Override
    	public void destroyItem(ViewGroup container, int position, Object object) {
    		((ViewPager)container).removeView((View)object);
    	}

    	@Override
    	public int getCount() {
    		return pages.length;
    	}

    	@Override
    	public boolean isViewFromObject(View view, Object object) {
    		return view.equals(object);
    	}
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
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_irof, menu);
        return true;
    }

}
