package com.irof.irof_history;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.irof.util.LogUtil;

public class IrofActivity extends Activity {

	private Resources m_r;
	private String TAG;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irof);
        
        TAG = LogUtil.getClassName();
        
        m_r = getResources();
        
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
		
		//循環ループ対応
		public final int MAX_PAGE_NUM = 1000;
		private final int OBJECT_NUM = pages.length;
    	
		@Override
    	public Object instantiateItem(ViewGroup container, int position) {
			int diff = (position - (MAX_PAGE_NUM / 2)) % OBJECT_NUM;
	    	int index = (0 > diff) ? (OBJECT_NUM + diff) : diff;
			//int index = position
	    	
    		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
    		return MAX_PAGE_NUM;
    		//return OBJECT_NUM;
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
					IrofImageView iv = _findViewById(R.id.icon_twitter05);
					iv.on_groovy(v);
				}
				break;
			case R.id.icon_twitter07:
				{
					IrofImageView iv = _findViewById(R.id.icon_twitter07);
					iv.on_groovy(v);
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
        case R.id.menu_settings:
            return true;
        case R.id.menu_capture:
        	showMessageBox(m_r.getString(R.string.menu_capture), m_r.getString(R.string.ask_capture),true);
            return true;
        default:
            return super.onOptionsItemSelected(item);
		}
	}
	
	private static AlertDialog alertDialog = null;
	public void showMessageBox(final String title, final String message) {
		showMessageBox(title,message,false);
	}
	public void showMessageBox(final String title, final String message,boolean cancel_f) {
		if(alertDialog!=null){
			alertDialog.dismiss();
			alertDialog=null;
		}
		LogUtil.trace(TAG, "showMessageBox ");
		LogUtil.trace(TAG, "title = " + title);
		LogUtil.trace(TAG, "message = " + message);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setIcon(R.drawable.ic_launcher);
		alertDialogBuilder.setMessage(message);
		alertDialogBuilder.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						if(m_r.getString(R.string.menu_capture).equals(title)){
							if(saveImage(findViewById(R.id.root))){
								showMessageBox(m_r.getString(R.string.infomation), m_r.getString(R.string.d_saved));
							}
							else{
								showMessageBox(m_r.getString(R.string.infomation), m_r.getString(R.string.d_savedno));
							}
						}
					}
				});
		if(cancel_f){
			alertDialogBuilder.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
						}
					});
		}
		alertDialogBuilder.setCancelable(false);
		alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	
	 public boolean saveImage(View view) {
	        // 該当のViewのサイズを取得し、Bitmapを生成する
	        int width = view.getWidth();
	        int height = view.getHeight();
	        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);// ①

	        // Viewのdrawメソッドを実行する
	        Canvas canvas = new Canvas(bitmap);
	        view.draw(canvas);// ②

	        // Bitmapを保存する
	        try {
	        	String url = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "", null);
	        	if(url!=null) {
	        		return true;
	        	}
//	            String file =
//	                    Environment.getExternalStorageDirectory().getPath()
//	                            + "/capture.png";
//	            FileOutputStream fos = new FileOutputStream(file);
//	            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos); // ③
//	            fos.close();
	        } catch (Exception e) {
	        }
	        finally{
		        // bitmapの破棄
		        bitmap.recycle();
	        }
	        return false;

	    }
	 
		@Override
	    public boolean onKeyDown(int keyCode, KeyEvent event) {
	        if(keyCode == KeyEvent.KEYCODE_BACK){
	        	onDestroy();
				android.os.Process.killProcess(android.os.Process.myPid());
				return false;
	        }
	        return super.onKeyDown(keyCode, event);
		}

}
