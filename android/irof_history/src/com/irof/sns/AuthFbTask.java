/*
 * Copyright 2012 Ryuji Yamashita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.irof.sns;

import java.net.URL;
import java.util.concurrent.CountDownLatch;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.AsyncTask;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.auth.AccessToken;

/**
 * @author Ryuji Yamashita - roundrop at gmail.com
 */
@TargetApi(3)
public class AuthFbTask extends AsyncTask<Object, Void, AuthFbWebView> {

    private AuthFbWebView mOAuthWebView;
    private URL mCallbackURL;
    private String mCode;
    private CountDownLatch mLatch = new CountDownLatch(1);

    @Override
    protected AuthFbWebView doInBackground(Object... params) {
        mOAuthWebView = (AuthFbWebView) params[0];
        mCallbackURL = (URL) params[1];

        mOAuthWebView.setWebViewClient(new InternalWebViewClient());
        Facebook instance = new FacebookFactory().getInstance();
        mOAuthWebView.setFacebook(instance);
        publishProgress(); //onProgressUpdateを呼ぶ
        waitForAuthorization();
        if (mCode == null) {
            System.out.println("oauth code is null!!!!!!!!");
            return mOAuthWebView;
        }
        AccessToken accessToken = getAccessToken();
        if (accessToken == null) {
            System.out.println("Access Token is null!!!!!!!!");
            return mOAuthWebView;
        }
        
        //AccessTokenの保存
        facebook_main.m_facebook = instance;
        facebook_main.m_accessToken = accessToken;
        facebook_main.storeAccessToken();
        
        return mOAuthWebView;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        Facebook facebook = mOAuthWebView.getFacebook();
        //patch merge https://github.com/ryosms/facebook4j-android-example/commit/4b1692eaa0a5b3276c3ccfc987e354661a37e13d
        facebook.setOAuthAppId(facebook_main.FB_APPID, facebook_main.FB_APPSECRET);    // TODO: put your "App ID" and "App Secret"
        facebook.setOAuthPermissions(facebook_main.FB_PERMISSIONS);//"read_stream");
        String url = facebook.getOAuthAuthorizationURL(mCallbackURL.toString());
        mOAuthWebView.loadUrl(url);
    }


    @Override
    protected void onPostExecute(AuthFbWebView result) {
        mOAuthWebView.end();
    }

    private void waitForAuthorization() {
        try {
            mLatch.await();
        } catch (InterruptedException e) {}
    }

    private AccessToken getAccessToken() {
        try {
            Facebook facebook = mOAuthWebView.getFacebook();
            return facebook.getOAuthAccessToken(mCode);
        } catch (FacebookException e) {
            e.printStackTrace();
            return null;
        }
    }



    private class InternalWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!url.startsWith(mCallbackURL.toString())) {
                return false;
            }
            Uri uri = Uri.parse(url);
            mCode = uri.getQueryParameter("code");
            mLatch.countDown();
            return true;
        }
        
    }

}
