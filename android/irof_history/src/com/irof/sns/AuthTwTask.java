package com.irof.sns;

import java.util.concurrent.CountDownLatch;

import android.net.Uri;
import android.os.AsyncTask;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.irof.util.LogUtil;

public class AuthTwTask extends AsyncTask<Object, Void, AuthTwWebView> {

	private static final String	TAG		= "AuthTwTask";

	private AuthTwWebView		mOAuthWebView;
	private String				mCallbackURL;
	private String				mCode;
	private CountDownLatch		mLatch	= new CountDownLatch(1);

	

	@Override
	protected AuthTwWebView doInBackground(Object... params) {

		mOAuthWebView = (AuthTwWebView) params[0];
		mCallbackURL = (String) params[1];
		mOAuthWebView.setWebChromeClient(new InternalWebChromeClient());
		mOAuthWebView.setWebViewClient(new InternalWebViewClient());
		mOAuthWebView.loadUrl(mCallbackURL);
		// publishProgress(); //onProgressUpdateを呼ぶ
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
		}
		catch (InterruptedException e) {
		}
	}

	
	private class InternalWebViewClient extends WebViewClient {
		// 特定のページをフック
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			LogUtil.trace(TAG, "shouldOverrideUrlLoading::url=" + url);
			if (check(view, url)) return true;
			return super.shouldOverrideUrlLoading(view, url);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			LogUtil.trace(TAG, "onPageFinished::url=" + url);
			check(view, url);
			super.onPageFinished(view, url);
		}

		private boolean check(WebView view, String url) {
			do {
				if (url == null || "".equals(url.trim())) break;
				if (mCallbackURL != null && url.startsWith(mCallbackURL)) {
					// Twitterの認証画面から発行されるIntentからUriを取得
					// oauth_verifierを取得する
					Uri uri = Uri.parse(url);
					String verifier = uri.getQueryParameter("oauth_verifier");
					// getTwInstance(verifier);
					if (verifier != null) {
						twitter_main.twitterVeriffer(verifier);
						mOAuthWebView.end();
						return true;
					}
				}
				if (url.endsWith("authorize")) {
					authorize(view);
					return true;
				}
			}
			while (false);
			return false;
		}
	};
	

    private void authorize(WebView view){
        if(twitter_main.mOAuth !=null)return;
        //view.loadUrl("javascript:window.activity.viewSource(document.documentElement.outerHTML);");
        String script = "javascript:var elem = document.getElementsByTagName('code')[0]; if(elem) alert(elem.childNodes[0].nodeValue);";
	view.loadUrl(script);
    }
    
	private class InternalWebChromeClient extends WebChromeClient{
		@Override
		public boolean onJsAlert(WebView view, String url, String verifier, JsResult result) {
			LogUtil.trace(TAG,"onJsAlert:"+ verifier);
			if (verifier!=null && !"".equals(verifier.trim())) {
				// getTwInstance(verifier);
				twitter_main.twitterVeriffer(verifier);
				mOAuthWebView.end();
				return true;//falseだとDialogが出てしまうらしい
			}
			return super.onJsAlert(view, url, verifier, result);
		}
    };


	// private boolean skip_f = false;
	public void viewSource(final String src) {
		LogUtil.trace(TAG, "viewSource=" + src);
		String oAuthVerifier = "";
		try {
			int start = src.indexOf("<code>");
			int end = src.indexOf("</code>");
			oAuthVerifier = src.substring(start + 6, end);
		}
		catch (Exception e) {
			LogUtil.error(TAG, "Exception", e);
		}

		if (oAuthVerifier == null || "".equals(oAuthVerifier)) {
			LogUtil.trace(TAG, "oAuthVerifier not getting");
			// skip_f = true;
			mOAuthWebView.loadUrl(src);
			return;
		}
		// getTwInstance(oAuthVerifier);
		twitter_main.twitterVeriffer(oAuthVerifier);
		mOAuthWebView.end();
		mLatch.countDown();
	}

}
