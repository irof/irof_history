package com.irof.adapter;

import java.io.File;

import android.content.Intent;
import android.net.Uri;

import com.irof.irof_history.IrofActivity;
import com.irof.irof_history.R;
import com.irof.sns.twitter_main;
import com.irof.util.LogUtil;

public class LinePostTask extends commonPostTask{

	String TAG =LinePostTask.this.getClass().getSimpleName();
	public LinePostTask(IrofActivity activity_) {
		super(activity_);
	}
	
    private File newfile;
    @Override
	protected void onPreExecute() {
		super.onPreExecute();
		newfile = saveImageGalleryFile();
		//ギャラリー保存できないときはエラー
		if(newfile==null){
			activity.showMessageBox(m_r.getString(R.string.infomation), m_r.getString(R.string.d_savedno));
		}
	}

	//see http://media.line.naver.jp/howto/ja/
	//
	@Override
	protected String doInBackground(String... params) {
		
    	String ret ="";
        String sNo  =params[0];
        
		if(newfile==null)return null;

        String str = createPostMessage(sNo);
    	try {
    		//Uri uri = android.net.Uri.parse("line://msg/text/" + str);
    		Uri uri = android.net.Uri.parse("line://msg/image/" + newfile.getAbsolutePath());
    		Intent browse = new Intent( Intent.ACTION_VIEW ,uri);
    		activity.startActivity( browse );
//    		activity.startActivityForCallback(browse, new OnActivityResultCallback() {
//	            // ここで値を受け取れる
//	            public void onResult(int resultCode, Intent data) {
//	            	//投稿終わったら削除しておく
//	            	newfile.delete();
//	            }
//	    	});
    		
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
