package com.irof.sns;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationContext;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;

import com.irof.irof_history.R;
import com.irof.util.ActivityUtil;
import com.irof.util.LogUtil;

public class AuthTwActivity extends Activity implements AuthTwWebView.Callback{

	private static final String TAG = "AuthTwActivity";
	
	/**
	 * AccessTokenがないときは、OAuth認証
	 */
	private boolean bXOOM_flag = false;
	private String CALLBACK_URL = null;
	private Class<?> finishClass = null;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    LogUtil.trace(TAG, "=> onCreate  start");
	    // アプリのタイトルバー表示なし
	    requestWindowFeature(Window.FEATURE_NO_TITLE);

		if(Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH){
		    ActivityUtil.IgnoreStrictMode();
	    }
		
    	if( "MOTO".equals(Build.BRAND) && "MZ604".equals(Build.MODEL) ){
    		bXOOM_flag = true;
    	}
	    
		//実機傾けた時
		if (savedInstanceState != null){
			mWebView = new AuthTwWebView(this);
			mWebView.restoreState(savedInstanceState);
	    	setContentView(mWebView);
	    	return;
		}
	    
	    Intent intent = getIntent();
		Uri uri = intent.getData();
/*		
		CALLBACK_URL = intent.getStringExtra("CALLBACK_URL");
		if(CALLBACK_URL==null)	CALLBACK_URL= twitter_main.CALLBACK_URL;
		else					twitter_main.CALLBACK_URL = CALLBACK_URL;
		String FINISH＿CLASS_NAME = intent.getStringExtra("finishClass");
		if(FINISH＿CLASS_NAME==null)	FINISH＿CLASS_NAME= twitter_main.FINISH＿CLASS_NAME;
		else						twitter_main.FINISH＿CLASS_NAME = FINISH＿CLASS_NAME;
		try {
			if(FINISH＿CLASS_NAME!=null){
				finishClass = Class.forName(FINISH＿CLASS_NAME);
			}
		} catch (ClassNotFoundException e) {
			finishClass = null;
		}
*/		
		Resources m_r = getResources();
		CALLBACK_URL = String.format("%s://%s", 
				m_r.getString(R.string.callback_scheme),
				m_r.getString(R.string.callback_host)
			);
		if(CALLBACK_URL==null)	CALLBACK_URL= twitter_main.CALLBACK_URL;
		else					twitter_main.CALLBACK_URL = CALLBACK_URL;
		
		ComponentName infoA =   getCallingActivity();
		String packageName = infoA==null ? null: infoA.getPackageName();
		String className = infoA==null ? null: infoA.getClassName();
		LogUtil.trace(TAG, packageName);
		LogUtil.trace(TAG, className);

		String FINISH＿CLASS_NAME = className;//intent.getStringExtra("finishClass");
		if(FINISH＿CLASS_NAME==null)	FINISH＿CLASS_NAME= twitter_main.FINISH＿CLASS_NAME;
		else						twitter_main.FINISH＿CLASS_NAME = FINISH＿CLASS_NAME;
		try {
			if(FINISH＿CLASS_NAME!=null){
				finishClass = Class.forName(FINISH＿CLASS_NAME);
			}
		} catch (ClassNotFoundException e) {
			finishClass = null;
		}

		//コールバック形式
		if (uri != null && CALLBACK_URL !=null && uri.toString().startsWith(CALLBACK_URL)) {
			String verifier = uri.getQueryParameter("oauth_verifier");
			//getTwInstance(verifier);
			twitter_main.twitterVeriffer(verifier);
			onSuccess(null);
			return;
		}
		
		//PIN認証形式
    	OAuth();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		Uri uri = intent.getData();
		if (uri != null && CALLBACK_URL !=null && uri.toString().startsWith(CALLBACK_URL)) {
			String verifier = uri.getQueryParameter("oauth_verifier");
			//getTwInstance(verifier);
			twitter_main.twitterVeriffer(verifier);
			onSuccess(null);
		}
	}
	
	private AuthTwWebView mWebView = null;
	boolean web_f = true;
	
	@SuppressLint("SetJavaScriptEnabled")
	private void OAuth()
	{
        //アプリの認証オブジェクト作成
        try {
        	//[TODO]ネットが繋がっていない OR CALLBACKが設定されていないときはこの形になる
        	if(CALLBACK_URL !=null){
        		//Twitetr4jの設定を読み込む
                Configuration conf = ConfigurationContext.getInstance();
                //Oauth認証オブジェクト作成
                twitter_main.mOAuth = new OAuthAuthorization(conf);
                //Oauth認証オブジェクトにconsumerKeyとconsumerSecretを設定
                twitter_main.mOAuth.setOAuthConsumer(twitter_main.CONSUMER_KEY, twitter_main.CONSUMER_SECRET);
                
            	try {
    				twitter_main.m_requestToken = twitter_main.mOAuth.getOAuthRequestToken(CALLBACK_URL);//"callbackttnlive://CallbackActivity");
    			} catch (Exception e) {
    				twitter_main.mOAuth = null;
    			}
        	}
        	
        	//コールバックが指定されていないときはとりあえずつｋる
        	if(twitter_main.m_requestToken==null){
				//twitterの作成
        		twitter_main.m_twitter = twitter_main.createInstance();
        		twitter_main.m_requestToken = twitter_main.m_twitter.getOAuthRequestToken();
        	}
        	
        	String call_uri = twitter_main.m_requestToken.getAuthorizationURL();
        	LogUtil.trace(TAG, "mRequestToken -  "+call_uri);

        	if(bXOOM_flag && twitter_main.mOAuth !=null)web_f=false;
        		
        	if(web_f){
    			mWebView = new AuthTwWebView(this);
//    			mWebView.getSettings().setJavaScriptEnabled(true);
//    			mWebView.getSettings().setBuiltInZoomControls(true);
//    			mWebView.setWebViewClient(mWebViewClient);
//    			mWebView.addJavascriptInterface(this, "activity");
    			setContentView(mWebView);
                //mWebView.loadUrl(call_uri);
    			mWebView.start(this,call_uri);
        	}
        	else{
      		    Intent i = new Intent(Intent.ACTION_VIEW , Uri.parse(call_uri));
      		    startActivity(i);
        	}
        } catch (TwitterException e) {
        	LogUtil.error(TAG, "=>OAuth::TwitterException ",e);
			Intent data = new Intent();
        	data.putExtra("State",0);
        	setResult(RESULT_OK, data);
        	finish();
        }
	}
	
    public void viewSource(final String src) {
    	if(mWebView!=null){
    		mWebView.viewSource(src);
    	}
    }

    private Bundle savedInstanceState = null;
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	if(mWebView==null)return;
    	mWebView.saveState(outState);
    	savedInstanceState = outState;
    }
    
    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfiguration) {
    	//キーボードがある端末の開閉イベントをスキップ
    	if (savedInstanceState != null){
    		mWebView = new AuthTwWebView(this);
    		mWebView.restoreState(savedInstanceState);
	    	setContentView(mWebView);
	    	return;
    	}
    	super.onConfigurationChanged(newConfiguration);
    }
	
	
    
//    public void getTwInstance(String verifier) {
//    	Intent result = new Intent();
//        if(twitter_main.twitterVeriffer(verifier)){
//    		result.putExtra("State", 1);
//        }
//        else{
//    		result.putExtra("State", 0);
//        }
//    	
//        if(twitter_main.mOAuth !=null && bXOOM_flag && finishClass!=null){
//        	Intent intent = new Intent(this, finishClass);
//    	    startActivity(intent);
//    	    return;
//    	}
//        
//    	setResult(RESULT_OK, result);
//    	finish();
//    }
    
    @Override
    public void onSuccess(Twitter twitter) {
        Intent data = new Intent();
        //data.putExtra(facebook_main.DATA_KEY_FACEBOOK, facebook);
        data.putExtra("State",twitter_main.m_accessToken ==null ? 0:1);//成功した時
        
        if(twitter_main.mOAuth !=null && bXOOM_flag && finishClass!=null){
        	Intent intent = new Intent(this, finishClass);
    	    startActivity(intent);
    	    return;
    	}

        setResult(RESULT_OK, data);
        finish();
    }

}
