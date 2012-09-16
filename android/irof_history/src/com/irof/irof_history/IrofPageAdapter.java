package com.irof.irof_history;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class IrofPageAdapter extends PagerAdapter{


	private SparseIntArray pages;
	private String[] title_arr;
	
	//循環ループ対応
	public final int MAX_PAGE_NUM = 1000;

	private Activity activity;
	private Resources m_r;
	private LayoutInflater inflater;
	public IrofPageAdapter(Activity activity_){
		activity = activity_;
		m_r = activity.getResources();
		pages = new SparseIntArray();
		
       title_arr = m_r.getStringArray(R.array.layout_titile);
       TypedArray arr = m_r.obtainTypedArray(R.array.layout_list);
       int len = arr.length();
       for(int i=0;i<len;i++){
    	   pages.put(i, arr.getResourceId(i, -1));
       }

	   inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}
	
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		//無限ループ対応
		/*
		int OBJECT_NUM = pages.size();
		int diff = (position - (MAX_PAGE_NUM / 2)) % OBJECT_NUM;
    	int index = (0 > diff) ? (OBJECT_NUM + diff) : diff;
		*/
		int index = position;
    	
		FrameLayout fn = (FrameLayout)inflater.inflate(pages.get(index), null);
		container.addView(fn);
		//notifyDataSetChanged();
		/*
		TextView tx = _findViewById(R.id.irof_title);
		tx.setText(String.format("%s %02d",m_r.getString(R.string.irof_world),index));
		*/
		return fn;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
	}

	@Override
	public int getCount() {
		//return MAX_PAGE_NUM;
		return pages.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	//see 	http://neta-abc.blogspot.jp/2012/07/pagertabstrip.html
	//		http://miquniqu.blogspot.jp/2012/07/pagertabstrip.html
	@Override
	public CharSequence getPageTitle(int position) {
		//無限ループ対応
		/*
		int OBJECT_NUM = pages.size();
		int diff = (position - (MAX_PAGE_NUM / 2)) % OBJECT_NUM;
    	int index = (0 > diff) ? (OBJECT_NUM + diff) : diff;
		*/
		int index = position;

    	return title_arr[index];
		//return super.getPageTitle(position);
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
