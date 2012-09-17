package com.irof.irof_history;

import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.PagerTabStrip;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.irof.util.LogUtil;
import com.irof.util.ViewIndicator;

public class IrofActivity extends Activity {

	
	public boolean pause_f = false;
	public float m_density = 0;
	
	private TextToSpeech mTts;
	private int initTtsMode= -1;
	
	private Resources m_r =null;
	private String TAG ="";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irof);

        TAG = LogUtil.getClassName();
        m_r = getResources();
        game_main.instance = this;

        mTts = new TextToSpeech(this,new TextToSpeech.OnInitListener(){
        		public void onInit(int status) {
        			if (status != TextToSpeech.SUCCESS)return;
        			
        			int result = 0;
        			do{
            			Locale locale = Locale.getDefault();
            			if(locale.equals(Locale.JAPAN) && isCheckJpTts()){
                   			result = mTts.setLanguage(Locale.JAPAN);
                			if (result == TextToSpeech.LANG_MISSING_DATA ||
                    	        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                       			//result = mTts.setLanguage(Locale.US);
                			}
                			else{
                				initTtsMode= 1;
                				break;
                			}
            			}
            			
               			result = mTts.setLanguage(Locale.US);
            			
            			if (result == TextToSpeech.LANG_MISSING_DATA ||
            	            result == TextToSpeech.LANG_NOT_SUPPORTED) {
            	            LogUtil.error(TAG, "Language is not available.");
            	            return;
            	        }
            			initTtsMode= 0;
        			}while(false);
        		}
        	}
        );
        
        //dip計算用のデータ
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


    @Override
	protected void onDestroy() {
    	   if (mTts != null) {
               mTts.stop();
               mTts.shutdown();
           }
    	   super.onDestroy();
	}


	//二回目起動時に呼ばれる場所
    @Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

    //n2ttsが入っているか判定
	private boolean isCheckJpTts() {
		try{
		  //パッケージ名を指定してインストール状況をチェック
		  PackageManager packageManager = this.getPackageManager();
		  ApplicationInfo applicationInfo = packageManager.getApplicationInfo("jp.kddilabs.n2tts",PackageManager.GET_META_DATA);
		  if(applicationInfo==null)return false;
		}
		catch(NameNotFoundException exception){
			return false;
		}
		return true;
	}

    
    
	private Animation animeB = null;
	public void on_groovy(View v) {
		int id = v.getId();
		LogUtil.trace(TAG,"on_groovy:"+id);
		switch(id){
			case R.id.icon_twitter05:
			case R.id.icon_twitter07:
				{
					IrofImageView iv = _findViewById(v.getId());
					if(iv!=null)iv.on_groovy(v);
				}
				break;
			default:
				{
					final ImageView iv = _findViewById(R.id.ball);
					if(animeB==null){
						animeB = AnimationUtils.loadAnimation(this, R.anim.out_to_right);
						animeB.setAnimationListener( new AnimationListener() {
							public void onAnimationEnd(Animation animation) {
					        	iv.setVisibility(View.GONE);
							}

							public void onAnimationRepeat(Animation animation) {
							}

							public void onAnimationStart(Animation animation) {
					        	iv.setVisibility(View.VISIBLE);
							}
						});
					}
				iv.setAnimation(animeB);
				iv.startAnimation(animeB);
			}
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
    
	private Animation animeJ = null;
    public void showViewStub(View v){
		switch(v.getId()){
			case R.id.menu_judge:
				{
					final FrameLayout fn = _findViewById(R.id.irof_judge);
					final ImageView iv = _findViewById(R.id.judge_image);
					final TextView tx = _findViewById(R.id.judge_text);
					//if(animeJ==null){
						animeJ = AnimationUtils.loadAnimation(this, R.anim.out_to_left);
						animeJ.setAnimationListener( new AnimationListener() {
							public void onAnimationEnd(Animation animation) {
								fn.setVisibility(View.GONE);
							}

							public void onAnimationRepeat(Animation animation) {
							}

							public void onAnimationStart(Animation animation) {
								iv.setImageResource(R.drawable.duke_gradle);
								tx.setText("がおー");
								HashMap<String, String> params = new HashMap<String, String>();
								params.put(TextToSpeech.Engine.KEY_PARAM_VOLUME, String.valueOf(0.8));
								params.put(TextToSpeech.Engine.KEY_PARAM_PAN, String.valueOf(1.0));
								switch(initTtsMode){
									case 0://英語
										mTts.speak("GAOO",TextToSpeech.QUEUE_FLUSH,params);
										break;
									case 1://日本語
										mTts.speak("がおー",TextToSpeech.QUEUE_FLUSH,params);
										break;
								}
								fn.setVisibility(View.VISIBLE);
							}
						});
					//}
					fn.setAnimation(animeJ);
					fn.startAnimation(animeJ);
				}
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
	        //Homeのキーコード対応は、セキュリティのため動きません
//	        if(keyCode == KeyEvent.KEYCODE_HOME){
//				return false;
//	        }

	        return super.onKeyDown(keyCode, event);
		}

}
