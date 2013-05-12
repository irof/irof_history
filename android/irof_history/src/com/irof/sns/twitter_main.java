package com.irof.sns;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import proguard.annotation.Keep;
import proguard.annotation.KeepName;
import twitter4j.AsyncTwitter;
import twitter4j.AsyncTwitterFactory;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterAdapter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterListener;
import twitter4j.TwitterMethod;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.OAuthAuthorization;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.ListView;

import com.irof.adapter.TwArrayAdapter;
import com.irof.irof_history.R;
import com.irof.util.LogUtil;
import com.irof.util.ShortURL;

@Keep
public class twitter_main {

	public static String				CONSUMER_KEY		= null;
	public static String				CONSUMER_SECRET		= null;
	public static String				TWITPIC_API_KEY		= null;

	public static Twitter				m_twitter;
	// public static AsyncTwitter m_twitter;
	public static AccessToken			m_accessToken;
	public static RequestToken			m_requestToken;
	public static String				m_screenName;

	// CALLBACK専用
	public static OAuthAuthorization	mOAuth				= null;
	public static String				CALLBACK_URL		= null;
	public static String				FINISH＿CLASS_NAME	= null;

	private static String				TAG					= "twitter_main";
	private static String				path				= "ttest.dat";

	public static Activity				activity;

	@KeepName
	public static void init(Activity activity_) {
		activity = activity_;
		ImageCache.init(activity_);
		CONSUMER_KEY = activity.getString(R.string.consumar_key);
		CONSUMER_SECRET = activity.getString(R.string.consumar_secret);
		TWITPIC_API_KEY = activity.getString(R.string.twitpic_api_key);
		ImageCache.init(activity_);
		ImageCache.loadIconList();
	}

	// twitter認証用
	@KeepName
	public static Twitter loginOAuth() throws TwitterException {
		LogUtil.trace(TAG, "loginOAuth");
		m_accessToken = loadAccessToken();
		if (m_accessToken == null) {
			if (m_twitter != null) {
				m_twitter.shutdown();
				m_twitter.setOAuthAccessToken(null);
			}
			m_twitter = null;
			LogUtil.trace(TAG, "m_accessToken = null");
			return null;
		}
		// twitterの作成
		m_twitter = createInstance();
		return m_twitter;
	}

	@KeepName
	public static Twitter createInstance() throws TwitterException {
		ConfigurationBuilder confbuilder = new ConfigurationBuilder();
		if (m_accessToken != null) {
			confbuilder.setOAuthAccessToken(m_accessToken.getToken())
					.setOAuthAccessTokenSecret(m_accessToken.getTokenSecret());
		}
		confbuilder.setOAuthConsumerKey(CONSUMER_KEY).setOAuthConsumerSecret(
				CONSUMER_SECRET);
		Twitter m_twitter = new TwitterFactory(confbuilder.build())
				.getInstance();
		// m_screenName = m_twitter ==null ? "please login"
		// :m_twitter.getScreenName(); //名前を反映
		return m_twitter;
	}

	@KeepName
	public static boolean twitterVeriffer(String verifier) {
		// AccessToken m_accessToken = null;
		try {
			// AccessTokenオブジェクトを取得
			if (mOAuth == null) {
				m_accessToken = m_twitter.getOAuthAccessToken(m_requestToken,
						verifier);
			}
			else {
				m_accessToken = mOAuth.getOAuthAccessToken(
						m_requestToken,
						verifier);
			}
			m_twitter = createInstance();

			// AccessTokenを保存する
			storeAccessToken();

		}
		catch (Exception e) {
			LogUtil.error(TAG, "twitterVeriffer", e);
			return false;
		}
		return true;
	}

	// ===========================================================================

	// twitter => async-twitter変換
	@KeepName
	public static AsyncTwitter createInstance_async() {
		if (m_twitter == null) return null;
		AsyncTwitter m_twitterAsync = new AsyncTwitterFactory(
				m_twitter.getConfiguration()).getInstance(m_twitter
				.getAuthorization());
		m_twitterAsync.addListener(mListener);
		return m_twitterAsync;
	}

	private static TwitterListener	mListener	= null;
	static {
		// Twitterリスナー
		mListener = new TwitterAdapter() {
			@Override
			public void updatedStatus(Status statuses) {
				// つぶやきが完了すると呼び出されます
			}

			@Override
			public void onException(TwitterException ex, TwitterMethod method) {
				// 何かエラーが発生する（TwitterExceptionがthrowされる）と呼び出されます
			}
		};
	}

	// ===========================================================================
	@KeepName
	public static void eraseAccessToken() {
		activity.deleteFile(path);
		if (m_twitter != null) {
			m_twitter.shutdown();
			m_twitter.setOAuthAccessToken(null);
		}
		m_twitter = null;
		m_accessToken = null;
		m_requestToken = null;
		m_screenName = "please login";
	}

	// ついったーにログインしているかどうか
	@KeepName
	public static boolean isTwitterLogin() {
		return m_accessToken == null ? false : true;
	}

	// AccesTokenの読み込み
	@KeepName
	public static AccessToken loadAccessToken() {
		LogUtil.trace(TAG, "loadAccessToken");
		FileInputStream fis = null;
		try {
			File file = activity.getFileStreamPath(path);
			if (!file.exists()) return null;
			fis = activity.openFileInput(path);
		}
		catch (FileNotFoundException e1) {
			Log.e(TAG, "openFileInput ", e1);
			return null; // ファイルが読めない（存在しない）場合はnullを返す
		}

		ObjectInputStream is = null;
		try {
			is = new ObjectInputStream(fis);
			AccessToken accessToken = (AccessToken) is.readObject();
			LogUtil.trace(TAG, "loadAccessToken ok");
			return accessToken;

		}
		catch (Exception e) {
			Log.e(TAG, "loadAccessToken ", e);
			return null; // ファイルが読めない（存在しない）場合はnullを返す
		}
		finally {
			try {
				if (is != null) is.close();
			}
			catch (IOException e) {
			}
		}
	}

	// AccesTokenの保存
	@KeepName
	public static void storeAccessToken() {
		LogUtil.trace(TAG, "storeAccessToken");
		// String s = createAccessTokenFileName(m_name);
		FileOutputStream fos = null;
		try {
			fos = activity.openFileOutput(path, Context.MODE_PRIVATE);
		}
		catch (FileNotFoundException e1) {
			Log.e(TAG, "FileNotFoundException", e1);
		}

		ObjectOutputStream os = null;
		try {
			os = new ObjectOutputStream(fos);
			os.writeObject(m_accessToken);
			LogUtil.trace(TAG, "storeAccessToken ok");
			// tnum4.m_twitter = m_twitter;
		}
		catch (IOException e) {
			Log.e(TAG, "IOException", e);
		}
		finally {
			try {
				if (os != null) os.close();
			}
			catch (IOException e) {
			}
		}
	}

	// =============================================================
	// TimeLine対応

	public static Map<String, User>	userList	= new HashMap<String, User>();
	/*
	 * public static Map<String,byte[]> iconList = new HashMap<String,byte[]>();
	 * public static boolean loadIconList(){ LogUtil.trace(TAG, "loadIconList");
	 * String path = "SaveIconList.dat"; try { File iconFile = null;
	 * FileInputStream fis = null; iconFile = activity.getFileStreamPath(path);
	 * if(iconFile.exists())fis = activity.openFileInput(path);
	 * if(!iconFile.exists())return false; long length = iconFile.length();
	 * LogUtil.debug(TAG, "[load_RePlay]length = " + length); ObjectInputStream
	 * ois = new ObjectInputStream(fis); iconList = (HashMap<String,byte[]>)
	 * ois.readObject(); //リプレイデータ ois.close(); } catch (Exception e) {
	 * Log.e(TAG, "loadIconList ",e); return false; } return true; } public
	 * static boolean saveIconList(){ LogUtil.debug(TAG, "saveIconList");
	 * if(iconList == null || iconList.size()<=0)return false; String path =
	 * "SaveIconList.dat"; FileOutputStream fos = null; try { File iconFile =
	 * null; iconFile = activity.getFileStreamPath(path); fos =
	 * activity.openFileOutput(path, Context.MODE_PRIVATE); ObjectOutputStream
	 * oos = new ObjectOutputStream(fos); oos.writeObject(iconList);
	 * oos.close(); long length = iconFile.length(); LogUtil.debug(TAG,
	 * "[saveIconList]length = " + length); return true; } catch (Exception e) {
	 * Log.e(TAG, "saveIconList ",e); return false; } finally{ try {
	 * if(fos!=null)fos.close(); } catch (IOException e) {} } }
	 */

	private static TwArrayAdapter	adapterTw;
	private static String[]			tw_arr		= null;
	public static List<String>		tw_arr_list	= new ArrayList<String>();

	public static void timeLineRefresh(ListView listTw) {
		List<String> tw_arr_list_tmp = new ArrayList<String>(tw_arr_list.size());
		for (String twMsg : tw_arr_list) {
			int st = twMsg.indexOf("http://t.co/");
			if (st == -1) {
				tw_arr_list_tmp.add(twMsg);
				continue;
			}

			int ed = twMsg.indexOf(" ", st);
			String urlStr = "";
			if (ed == -1) {
				urlStr = twMsg.substring(st);
			}
			else {
				urlStr = twMsg.substring(st, ed);
			}
			try {
				URL exurl = ShortURL.expand(new URL(urlStr));
				if (ed == -1) {
					twMsg = twMsg.substring(0, st) + exurl.toString();
				}
				else {
					twMsg = twMsg.substring(0, st) + exurl.toString()
							+ twMsg.substring(ed);
				}
			}
			catch (Exception e) {
			}
			tw_arr_list_tmp.add(twMsg);
		}

		adapterTw = new TwArrayAdapter(activity, R.layout.list_twitter,
				tw_arr_list_tmp);
		listTw.setAdapter(adapterTw);
	}

	public static void timeLineCreate(ListView listTw, String[] tw_arr_) {
		if (tw_arr_ == null) return;
		tw_arr = tw_arr_;
		// tw_arr_list = new ArrayList<String>(tw_arr.length);
		tw_arr_list.clear();
		for (String s : tw_arr)
			tw_arr_list.add(s);
		adapterTw = new TwArrayAdapter(activity, R.layout.list_twitter,
				tw_arr_list);
		listTw.setAdapter(adapterTw);
	}

	public static TwArrayAdapter timeLineAdd(ListView listTw, String tw) {
		return timeLineAdd(listTw, tw, false);
	}

	public static TwArrayAdapter timeLineAdd(ListView listTw, String tw,
			boolean init_f) {
		if (init_f) {
			adapterTw = new TwArrayAdapter(activity, R.layout.list_twitter);
			listTw.setAdapter(adapterTw);
			tw_arr_list.clear();
		}
		adapterTw.add(tw);
		tw_arr_list.add(tw);
		adapterTw.notifyDataSetChanged();
		return adapterTw;
	}
}
