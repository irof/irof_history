package com.irof.adapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.media.ImageUploadFactory;
import twitter4j.media.MediaProvider;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.irof.irof_history.IrofActivity;
import com.irof.irof_history.R;
import com.irof.sns.twitter_main;
import com.irof.util.LogUtil;

public abstract class commonPostTask extends AsyncTask<String, String, String>{

	private String TAG = commonPostTask.this.getClass().getSimpleName();
	
	protected IrofActivity activity = null;
	protected String progressMsg = "";
	protected Resources m_r;
    public commonPostTask(IrofActivity activity_) {
    	activity = activity_;
    	m_r = activity.getResources();
		progressMsg = m_r.getString(R.string.msg_progress_reflesh);
    	progress(false,progressMsg);
    }

	private ProgressDialog progressDialog;
	public void progress(boolean enabled,String msg){
		if(!enabled){
        	progressDialog = new ProgressDialog(activity);
        	progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        	progressDialog.setMessage(msg);
        	progressDialog.setCancelable(true);
        	progressDialog.show();
		}
		else{
			if(progressDialog!=null)progressDialog.dismiss();
			progressDialog = null;
		}
	}
	
    /**
     * onPostExecute
     * @param String result 通信結果
     * 結果を受け取ったときに・・・メインスレッド
     */
    @Override
    protected void onPostExecute(String result)
    {
    	progress(true,progressMsg);
    }

    protected String createPostMessage(String sNo){
		PackageInfo m_sPackInfo =null;
    	try {
    		Context context = activity.getApplicationContext();
			m_sPackInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			LogUtil.error(TAG,"getPackageInfo ",e);
		}

        String verS = m_sPackInfo.versionName;
        verS = verS.replace(".", "");
        String appVer = String.format("%s_%s", verS,m_sPackInfo.versionCode);
        String str = m_r.getString(R.string.tw_msg,sNo,appVer);
		LogUtil.trace(TAG, "tweet :" + str);
		return str;
    }
	
    
    protected File saveImageGalleryFile(){
        File newfile = null;
        
        try {
            String ret= MediaStore.Images.Media.insertImage(activity.getContentResolver(), 
            		activity.getBitmap(activity.findViewById(R.id.root)), "", null);
            if(ret==null)return null;
            Cursor c = activity.getContentResolver().query(Uri.parse(ret), null, null, null, null);
            c.moveToFirst();
            String path = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
            newfile = new File(path);
        } catch (Exception e) {
        	LogUtil.error(TAG, "IOException ", e);
            return null;
        }
        finally{
        }
        return newfile;
    }


	protected File saveImageFile(){
		File newfile = null;
		Bitmap b = activity.getBitmap(activity.findViewById(R.id.root));	//キャンバスで使ってる描画用のBMPを持ってくる
		try {
			ByteArrayOutputStream jpg = new ByteArrayOutputStream();
			b.compress(CompressFormat.JPEG, 100, jpg);
//			newfile = new File(activity.getCacheDir(), "dst.txt");
//		    newfile.createNewFile();
//			FileOutputStream fo = new FileOutputStream(newfile);
			
			newfile = new File(activity.getFilesDir(), "dst.txt");
			FileOutputStream fo = activity.openFileOutput("dst.txt",Context.MODE_WORLD_READABLE);
		    fo.write(jpg.toByteArray());
		    fo.close();
		} catch (IOException e) {
			LogUtil.error(TAG, "IOException ",e); 
			return null;
		}
		finally{
			//if(b!=null) b.recycle();
			b = null;
		}
		return newfile;
	}
	
	int mode_twitter = 0;
	protected String upload_main(String str,File newfile){
		LogUtil.trace(TAG, "upload_main");
		mode_twitter = 0;
		if(twitter_main.m_twitter==null)return null;
		
		
		String url = null;
		try{
			url = upload(str, newfile, MediaProvider.TWITPIC);  
			mode_twitter = 0;
		} catch(twitter4j.TwitterException e){  
			//エラー処理  
			LogUtil.error(TAG, "twitter4j.TwitterException ", e); 
			try{
				url = upload(str, newfile, MediaProvider.TWITTER);  
				mode_twitter = 1;
			} catch(twitter4j.TwitterException ex){  
				//エラー処理  
				LogUtil.error(TAG, "twitter4j.TwitterException ", ex); 
				url = null;
			}
		}
		return url;
	}

	private String upload(String str, File newfile, MediaProvider mediaprovider) throws TwitterException {
		Twitter twitter = twitter_main.m_twitter;
		String url ="";
		//cb2
        ConfigurationBuilder cb2 = new ConfigurationBuilder()
        .setOAuthConsumerKey(twitter.getConfiguration().getOAuthConsumerKey())
        .setOAuthConsumerSecret(twitter.getConfiguration().getOAuthConsumerSecret())
        .setOAuthAccessToken(twitter.getOAuthAccessToken().getToken())
        .setOAuthAccessTokenSecret(twitter.getOAuthAccessToken().getTokenSecret());
		//
        if(mediaprovider==MediaProvider.TWITPIC){
            cb2.setMediaProviderAPIKey(twitter_main.TWITPIC_API_KEY);
        }
        //conf
		twitter4j.conf.Configuration conf = cb2.build();  
		
		//upload1
		twitter4j.media.ImageUpload upload1 = new ImageUploadFactory(conf).getInstance(mediaprovider);	//mediaproviderを指定する
	  	url = upload1.upload(newfile,str); 
	  	LogUtil.trace(TAG, "url = " + url);
		return url;
	}

}
