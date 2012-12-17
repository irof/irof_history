package com.irof.irof_super;

import java.util.HashMap;
import java.util.Locale;

import twitter4j.TwitterException;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;

import com.google.tts.TextToSpeechBeta;
import com.irof.irof_history.R;
import com.irof.sns.ImageCache;
import com.irof.sns.twitter_main;
import com.irof.util.HeadsetStateReceiver;
import com.irof.util.LogUtil;
import com.irof.util.PrefUtil;

public class IrofSuperActivity extends BaseActivity {

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
        PrefUtil.init(this);
        
        tts_init();
        
        //IS01だとNakamap動かないようなので保留にしておく
	  	if( "KDDI".equals(Build.BRAND) && "IS01".equals(Build.MODEL) ){
        
	  	}
	  	else{
	        //Nakamapの初期化
	        new NakamapUtil().nakamapSetup(this);
	  	}

        
        //音声初期化
        sound_init();
        
        //tw_iconの保存
    	twitter_main.init(this);
    	//twitter認証
    	try {
			twitter_main.loginOAuth();
		} catch (TwitterException e) {
			LogUtil.error(TAG,"",e);
		}
    }


	private void tts_init() {
		//2.1以下で、
		//	Text-To-Speech Extended
		//	 N2 TTS 
		//	が入っている状態のとき
		if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.ECLAIR_MR1 
			&& isCheck_N2Tts() && isCheck_GoogleTts()){
			TextToSpeechBeta.OnInitListener listner = 
					new TextToSpeechBeta.OnInitListener(){
						//TTS
						@Override
						public void onInit(int status, int version) {
							if (status != TextToSpeech.SUCCESS)return;
							
							int result = 0;
			    			do{
			        			Locale locale = Locale.getDefault();
			        			if(locale.equals(Locale.JAPAN) && isCheck_N2Tts()){
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
					};
			mTts = new TextToSpeechBeta( this,listner);
		}
		else{
			TextToSpeech.OnInitListener listner = 
				new TextToSpeech.OnInitListener(){
					//TTS
					@Override
					public void onInit(int status) {
						if (status != TextToSpeech.SUCCESS)return;
						
						int result = 0;
		    			do{
		        			Locale locale = Locale.getDefault();
		        			if(locale.equals(Locale.JAPAN) && isCheck_N2Tts()){
		               			result = mTts.setLanguage(Locale.JAPAN);
		            			if (result == TextToSpeech.LANG_MISSING_DATA ||
		                	        result == TextToSpeech.LANG_NOT_SUPPORTED) {
		                   			//result = mTts.setLanguage(Locale.US);
		            			}
		            			else{
		            				//女性の声の方が良い場合の指定方法
		            				//mTts.setLanguage(new Locale(Locale.JAPAN.getLanguage(), Locale.JAPAN.getCountry(), "F01")); 
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
				};
			mTts = new TextToSpeech( this,listner);
		}
	}
    
    private static int org_musicVol = 0;
    private static int set_musicVol = 0;
    private AudioManager audio = null;
    private HeadsetStateReceiver headsetStateReceiver = null;
    private void sound_init(){
    	audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    	org_musicVol = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
    	LogUtil.trace(TAG,"[se_load]Media Volume:" + org_musicVol);

    	set_musicVol = PrefUtil.get_int("set_musicVol",-1);
    	if(set_musicVol==-1){
        	int sep_musicVol = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)* 1/2;
        	set_musicVol =org_musicVol;
        	if(sep_musicVol > org_musicVol)set_musicVol = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC) * 2/3;
    	}
    	audio.setStreamVolume(AudioManager.STREAM_MUSIC,set_musicVol, 0);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);	//ボリューム調整をメディア
    }
    
	private String[] MODE_S = {"RINGER_MODE_SILENT","RINGER_MODE_VIBRATE"};
    private Handler tts_handler = null;
	protected void tts_play(final String voice) {
		if(tts_handler==null)tts_handler = new Handler();
		else				 tts_handler.removeMessages(0);
		
    	tts_handler.post(new Runnable() {
			public void run() {
		    	if(audio==null)audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		    	int mode = audio.getRingerMode();
		    	switch (mode) {
			    	case AudioManager.RINGER_MODE_SILENT:
			    	case AudioManager.RINGER_MODE_VIBRATE:
			    		// マナーモードなので何もしない
			    		boolean bPlugged = headsetStateReceiver.isPlugged();
			    		LogUtil.trace(TAG, "[se_play]mode="+MODE_S[mode]);
			    		LogUtil.trace(TAG, "[se_play]bPlugged="+bPlugged);
			    		if(bPlugged)break; //イヤフォンの場合は鳴らす
			    		return;
			    		//break;
			    	default:
			    		break;
		    	}
		    	
				mTts.setSpeechRate(1.0f);//しゃべる速さ（遅い＜＝＞速い）
				mTts.setPitch(2.0f);//声の音程(低い＜＝＞高い)
				HashMap<String, String> params = new HashMap<String, String>();
				params.put(TextToSpeech.Engine.KEY_PARAM_VOLUME, String.valueOf(0.8));
				params.put(TextToSpeech.Engine.KEY_PARAM_PAN, String.valueOf(1.0));
				mTts.speak(voice,TextToSpeech.QUEUE_FLUSH,params);
			}
    	});
	}
    
    @Override
	protected void onDestroy() {
 	   if (mTts != null) {
           mTts.stop();
           mTts.shutdown();
       }
 	   
 	   //音量をオリジナルに戻す
 	   if(audio==null)audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

 	   //音量保存対応
 	   set_musicVol = audio.getStreamVolume(AudioManager.STREAM_MUSIC);//最新の値取得
 	   PrefUtil.put_int("set_musicVol",set_musicVol);
 	   audio.setStreamVolume(AudioManager.STREAM_MUSIC,org_musicVol, 0);

 	  ImageCache.saveIconList();
 	  
 	   super.onDestroy();
	}

    
	@Override
    public void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
		try {
	    	if(headsetStateReceiver==null)headsetStateReceiver = new HeadsetStateReceiver();
	        registerReceiver(headsetStateReceiver, filter);
			enableReceiver(true,headsetStateReceiver);
		} catch (Exception e) {}

	}

    @Override
	protected void onPause() {
    	super.onPause();
		try {
			unregisterReceiver(headsetStateReceiver);
			enableReceiver(false,headsetStateReceiver);
		} catch (Exception e) {}
    }

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		LogUtil.trace(TAG, "===onLowMemory===");
		try {
			unregisterReceiver(headsetStateReceiver);
			enableReceiver(false,headsetStateReceiver);
		} catch (Exception e) {}
	}

	private void enableReceiver(boolean enabled,BroadcastReceiver receiver) {
		PackageManager pm = getPackageManager();
		ComponentName receiverName = new ComponentName(this, receiver.getClass());
		int newState;
		if (enabled) {
			newState = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
		} else {
			newState = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
		}
		pm.setComponentEnabledSetting(receiverName, newState, PackageManager.DONT_KILL_APP);
	}

    //google_ttsが入っているか判定
  	private boolean isCheck_GoogleTts() {
  		try{
  		  //パッケージ名を指定してインストール状況をチェック
  		  PackageManager packageManager = this.getPackageManager();
  		  ApplicationInfo applicationInfo = packageManager.getApplicationInfo("com.google.tts",PackageManager.GET_META_DATA);
  		  if(applicationInfo==null)return false;
  		}
  		catch(NameNotFoundException exception){
  			return false;
  		}
  		return true;
  	}

	//n2ttsが入っているか判定
	private boolean isCheck_N2Tts() {
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
	        Bitmap bitmap = getBitmap(view);

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

	public Bitmap getBitmap(View view) {
		int width = view.getWidth();
		int height = view.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);// ①

		// Viewのdrawメソッドを実行する
		Canvas canvas = new Canvas(bitmap);
		view.draw(canvas);// ②
		return bitmap;
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
