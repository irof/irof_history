package com.irof.irof_history;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class IrofPageAdapter extends PagerAdapter{
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
	
	//循環ループ対応
	public final int MAX_PAGE_NUM = 1000;
	private final int OBJECT_NUM = pages.length;
	
	private Activity activity;
	private Resources m_r;
	public IrofPageAdapter(Activity activity_){
		activity = activity_;
		m_r = activity.getResources();
	}
	
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		//int diff = (position - (MAX_PAGE_NUM / 2)) % OBJECT_NUM;
    	//int index = (0 > diff) ? (OBJECT_NUM + diff) : diff;
		int index = position;
    	
		LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		FrameLayout fn = (FrameLayout)inflater.inflate(pages[index], null);
		container.addView(fn);
		TextView tx = _findViewById(R.id.irof_title);
		tx.setText(String.format("%s %02d",m_r.getString(R.string.irof_world),index));
		return fn;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
	}

	@Override
	public int getCount() {
		//return MAX_PAGE_NUM;
		return OBJECT_NUM;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}
	
	//see http://d.hatena.ne.jp/mikkabo/20120622/1340375461
	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}
	
	
	
	@SuppressWarnings("unchecked")
	protected <T extends View> T _findViewById(final int id){
	    return (T)activity.findViewById(id);
	}

}
