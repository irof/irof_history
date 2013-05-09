package com.irof.adapter;

import java.io.File;
import java.net.URL;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;

import com.irof.irof_history.IrofActivity;
import com.irof.irof_history.R;
import com.irof.sns.facebook_main;
import com.irof.sns.twitter_main;
import com.irof.util.LogUtil;
import com.irof.util.ShortURL;

import facebook4j.Media;
import facebook4j.Photo;
import facebook4j.PostUpdate;

/**
 * Task
 * バックグラウンド処理用のクラス
 */
public class FacebookPostTask extends commonPostTask
{
	String TAG =FacebookPostTask.this.getClass().getSimpleName();
    private URL exURL = null;
	
	// コンストラクタ  
    public FacebookPostTask(IrofActivity activity_) {
    	super(activity_);
    }  

    private File newfile;
    @Override
	protected void onPreExecute() {
		super.onPreExecute();
		newfile = saveImageFile();
	}

	@Override
    protected String doInBackground(String... params)
    {
    	String ret ="";
        String sNo  =params[0];
        
		if(newfile==null)return null;
		
        String str = createPostMessage(sNo);
    	try {
	    	Media source = new Media(newfile);
	    	String photoId = facebook_main.m_facebook.postPhoto(source,
	    		str,
	    		"",
	    		true);//trueだとtimeline上は非表示
	    	//
			Photo photo = facebook_main.m_facebook.getPhoto(photoId);
			if(exURL==null)exURL =  ShortURL.expand(new URL(m_r.getString(R.string.fb_login_url)));
			PostUpdate post =  new PostUpdate(exURL)
							   .picture(photo.getPicture())
							   .message(str);
			ret = facebook_main.m_facebook.postFeed(post);
			
			//twitpic念のためpostしておく
			if(twitter_main.isTwitterLogin()){
				upload_main(str,newfile);
			}
		} catch (Exception e) {
			LogUtil.error(TAG,"",e);
		}
    	LogUtil.trace(TAG,"[postAction]ret="+ret);

		
    	return ret;
    }
}


