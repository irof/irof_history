package com.irof.irof_history;

import java.util.Locale;

import com.irof.util.LogUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;

public class IrofSuperActivty extends Activity {

	private String TAG ="";
	protected TextToSpeech mTts;
	protected int initTtsMode= -1;
	protected Resources m_r =null;

	public float m_density = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //dip計算用のデータ
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float scale = (metrics.widthPixels /metrics.density) / 320 ;
        m_density = metrics.density * scale;

        TAG = LogUtil.getClassName();
        m_r = getResources();

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
    	});
    }
    
    @Override
	protected void onDestroy() {
 	   if (mTts != null) {
           mTts.stop();
           mTts.shutdown();
       }
 	   super.onDestroy();
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

	@SuppressWarnings("unchecked")
	protected <T extends View> T _findViewById(final int id){
	    return (T)findViewById(id);
	}

}
