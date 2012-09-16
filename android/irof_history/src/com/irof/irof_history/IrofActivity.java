package com.irof.irof_history;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.PagerTabStrip;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.irof.util.LogUtil;
import com.irof.util.ViewIndicator;

public class IrofActivity extends Activity {

	
	public boolean pause_f = false;
	public float m_density = 0;
	
	private Resources m_r;
	private String TAG;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irof);

        TAG = LogUtil.getClassName();
        m_r = getResources();
        game_main.instance = this;

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float scale = (metrics.widthPixels /metrics.density) / 320 ;
        m_density = metrics.density * scale;

        
        IrofViewPager mViewPager = _findViewById(R.id.viewpager);
        IrofPageAdapter mPagerAdapter = new IrofPageAdapter(this);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(0);
        
        //viewPagerにタブを付ける
        PagerTabStrip pagerTabStrip = _findViewById(R.id.pager_tab_strip);
        pagerTabStrip.setDrawFullUnderline(true);
        pagerTabStrip.setTabIndicatorColor(Color.DKGRAY);
        
        //円形インジケータ追加
        ViewIndicator indicator = _findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);
        indicator.setPosition(0);
        
    }


    //二回目起動時に呼ばれる場所
    @Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
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
    
    public void showViewStub(View v){
		switch(v.getId()){
			case R.id.menu_judge:
	    		break;
			case R.id.menu_pause:
				pause_f = !pause_f;
				ImageButton btn = (ImageButton)v;
				if(!pause_f)btn.setImageResource(android.R.drawable.ic_menu_myplaces);
				else		btn.setImageResource(android.R.drawable.ic_menu_my_calendar);
	    		break;
	        case R.id.menu_clear:
	    		{
	    			IrofDraw root = _findViewById(R.id.root);
	    			root.clear();
	    		}
	    		break;
	        case R.id.menu_undo:
		    	{
		    		IrofDraw root = _findViewById(R.id.root);
		    		root.undo();
		    	}
	    		break;
	        default:
	        	break;
		}
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
        case R.id.menu_settings:
            return true;
        case R.id.menu_capture:
        	showMessageBox(m_r.getString(R.string.menu_capture), m_r.getString(R.string.ask_capture),true);
            return true;
        case R.id.menu_clear:
        	{
        		IrofDraw root = _findViewById(R.id.root);
        		root.clear();
        	}
            return true;
        case R.id.menu_undo:
        	{
        		IrofDraw root = _findViewById(R.id.root);
        		root.undo();
        	}
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
