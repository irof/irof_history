package com.irof.irof_parts;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.irof.irof_history.R;
import com.irof.irof_history.game_main;

public class IrofPageAdapter extends PagerAdapter {

	private SparseIntArray		pages;
	// private String[] title_arr;
	private ArrayList<String>	title_arr;

	// 循環ループ対応
	private boolean				mugen_f			= false;
	public final int			MAX_PAGE_NUM	= 1000;

	private LayoutInflater		inflater;
	private Activity			activity;
	private Resources			m_r;
	private ArrayList<View>		pages_fn;

	public IrofPageAdapter(Activity activity_) {
		activity = activity_;
		m_r = activity.getResources();
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		TypedArray arr = m_r.obtainTypedArray(R.array.layout_list);
		int len = arr.length();

		pages = new SparseIntArray(len);
		pages_fn = new ArrayList<View>(len);
		for (int i = 0; i < len; i++) {
			int res_id = arr.getResourceId(i, -1);
			pages.put(i, res_id);
			// FrameLayout fn = (FrameLayout)inflater.inflate(res_id, null);
			View fn = inflater.inflate(res_id, null);
			pages_fn.add(fn);
		}
		arr.recycle();

		String[] arr_t = m_r.getStringArray(R.array.layout_titile);
		int len_t = arr_t.length;
		title_arr = new ArrayList<String>(len_t);
		for (String s : arr_t) {
			title_arr.add(s);
		}
	}

	// あとから動的追加
	public void addItem(int res_id, String title) {
		int index = pages.size();
		pages.put(index, res_id);
		// FrameLayout fn = (FrameLayout)inflater.inflate(res_id, null);
		View fn = inflater.inflate(res_id, null);
		pages_fn.add(fn);
		title_arr.add(title);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// 無限ループ対応
		int index = position;
		if (mugen_f) {
			int OBJECT_NUM = pages.size();
			int diff = (position - (MAX_PAGE_NUM / 2)) % OBJECT_NUM;
			index = (0 > diff) ? (OBJECT_NUM + diff) : diff;
		}
		View fn = pages_fn.get(index);

		try {
			ViewPager parent = (ViewPager) fn.getParent();
			int len_child = parent == null ? 0 : parent.getChildCount();
			for (int i = 0; i < len_child; i++) {
				View vx = parent.getChildAt(i);
				if (vx.equals(fn)) {
					parent.removeView(vx);
					break;
				}
			}
			container.addView(fn);
		}
		catch (Exception e) {
		}
		// notifyDataSetChanged();
		/*
		 * TextView tx = _findViewById(R.id.irof_title);
		 * tx.setText(String.format
		 * ("%s %02d",m_r.getString(R.string.irof_world),index));
		 */

		// Jazzy効果
		game_main.instance.mViewPager.setObjectForPosition(fn, position);
		return fn;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// 下手に書くとエラーで落ちるっぽいよ？？
	}

	@Override
	public int getCount() {
		if (mugen_f) return MAX_PAGE_NUM;
		return pages.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	// see http://neta-abc.blogspot.jp/2012/07/pagertabstrip.html
	// http://miquniqu.blogspot.jp/2012/07/pagertabstrip.html
	@Override
	public CharSequence getPageTitle(int position) {
		// 無限ループ対応
		int index = position;
		if (mugen_f) {
			int OBJECT_NUM = pages.size();
			int diff = (position - (MAX_PAGE_NUM / 2)) % OBJECT_NUM;
			index = (0 > diff) ? (OBJECT_NUM + diff) : diff;
		}

		return title_arr.get(index);
		// return super.getPageTitle(position);
	}

	// see http://d.hatena.ne.jp/mikkabo/20120622/1340375461
	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@SuppressWarnings("unchecked")
	protected <T extends View> T _findViewById(final int id) {
		return (T) activity.findViewById(id);
	}

}
