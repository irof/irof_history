package com.irof.sns;

import java.util.concurrent.CountDownLatch;

import android.net.Uri;
import android.os.AsyncTask;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.irof.util.LogUtil;

public class AuthTwTask extends AsyncTask<Object, Void, AuthTwWebView> {

	private static final String TAG = "AuthTwTask";

    private AuthTwWebView mOAuthWebView;
    private String mCallbackURL;
    private String mCode;
    private CountDownLatch mLatch = new CountDownLatch(1);

	@Override
	protected AuthTwWebView doInBackground(Object... params) {

        mOAuthWebView = (AuthTwWebView) params[0];
        mCallbackURL = (String) params[1];
        mOAuthWebView.setWebViewClient(new InternalWebViewClient());
        mOAuthWebView.loadUrl(mCallbackURL);
        //publishProgress(); //onProgressUpdateを呼ぶ
        waitForAuthorization();
		return mOAuthWebView;
	}
	
    @Override
    protected void onPostExecute(AuthTwWebView result) {
        mOAuthWebView.end();
    }

    private void waitForAuthorization() {
        try {
            mLatch.await();
        } catch (InterruptedException e) {}
    }

    private class InternalWebViewClient extends WebViewClient {
        // 特定のページをフック
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
			LogUtil.trace(TAG,"shouldOverrideUrlLoading::url=" + url);
			if(check(view, url))return true;
            return super.shouldOverrideUrlLoading(view, url);
        }
        
		@Override
		public void onPageFinished(WebView view, String url) {
			LogUtil.trace(TAG,"onPageFinished::url=" + url);
			check(view, url);
    		super.onPageFinished(view, url);
		}

		private boolean check(WebView view, String url) {
			do{
				if(url == null)break;
	            if(mCallbackURL!=null && url.startsWith(mCallbackURL)){
	                // Twitterの認証画面から発行されるIntentからUriを取得
	                // oauth_verifierを取得する
	            	Uri uri = Uri.parse(url);
	                String verifier = uri.getQueryParameter("oauth_verifier");
	            	//getTwInstance(verifier);
	                if(verifier!=null){
		                twitter_main.twitterVeriffer(verifier);
		                mOAuthWebView.end();
		                return true;
	                }
	            }
	            /*
	            String kurl = url;
	            int idx = url.indexOf("?");
	            if(idx !=-1){
	            	kurl = url.substring(0,idx);
	            }
	            if (kurl.endsWith("authorize")) {
	            */
	            if (url.endsWith("authorize")) {
//	            	if(skip_f){
//	            		skip_f= false;
//	            		break;
//	            	}
	                if(twitter_main.mOAuth ==null){
	                	view.loadUrl("javascript:window.activity.viewSource(document.documentElement.outerHTML);");
	                }
	                return true;
	            }
			}while(false);
    		return false;
		}
    };
    
	//private boolean skip_f = false;
    public void viewSource(final String src) {
    	LogUtil.trace(TAG,"viewSource="+src);
    	String oAuthVerifier = "";
    	try{
    		int start = src.indexOf("<code>");
    		int end = src.indexOf("</code>");
    		oAuthVerifier = src.substring(start + 6,end);
        }
        catch (Exception e){
        	LogUtil.error(TAG,"Exception",e);
        }
    	
    	if(oAuthVerifier==null || "".equals(oAuthVerifier)){
        	LogUtil.trace(TAG,"oAuthVerifier not getting");
        	//skip_f = true;
        	mOAuthWebView.loadUrl(src);
        	return;
    	}
		//getTwInstance(oAuthVerifier); 
        twitter_main.twitterVeriffer(oAuthVerifier);
        mOAuthWebView.end();
        mLatch.countDown();
    }


}
