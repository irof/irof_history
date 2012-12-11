package com.irof.irof_history;

import java.util.Locale;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.irof.sns.twitter_main;
import com.irof.util.LogUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

public class setting_main extends ListActivity {
	private final String TAG = getClass().getSimpleName();
	private String[] mStrings = { "Infomation", "Version", "twitterOAuth"};

	private Resources m_r;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_view_list);
		m_r = 	getResources();
		mStrings = m_r.getStringArray(R.array.list_data);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, mStrings);
		setListAdapter(adapter);

	}
	final int DEF_INFOMATION = 0;
	final int DEF_OPERATE = 1;
	final int DEF_UPDATE = 2;
	final int DEF_VERSION = 3;
	final int DEF_TWITPIC = 4;
	final int DEF_TWITTER = 5;
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		switch(position){
			case DEF_INFOMATION:
				infoDlg(m_r.getString(R.string.info_title),m_r.getString(R.string.info_desc));
				break;
			case DEF_OPERATE:
				infoDlg(m_r.getString(R.string.operate_title),m_r.getString(R.string.operate_desc));
				break;
			case DEF_UPDATE:
				infoDlg(m_r.getString(R.string.update_title),m_r.getString(R.string.update_desc));
				break;
			case DEF_VERSION:
		  		String appVer = "";
				try {
	        		Context context = getApplicationContext();
	        		PackageInfo m_sPackInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		            String verS = m_sPackInfo.versionName;
		            appVer = String.format("%s/%s", verS,m_sPackInfo.versionCode);
				} catch (NameNotFoundException e) {
				}
				showMessageBoxWelcome( m_r.getString(R.string.app_name),"version: " + appVer);
				break;
			case DEF_TWITPIC:
				String inURL ="http://twitpic.com/tag/irofhistory";
				Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( inURL ) );
			    startActivity( browse );
				break;
			case DEF_TWITTER:
				if(twitter_main.isTwitterLogin()){
					twitter_main.eraseAccessToken();
				}
				Intent intent = new Intent(this, com.irof.sns.AuthTwActivity.class);
				startActivity(intent);
				break;
		}
	}
	
	private static AlertDialog wellcomeDialog = null;

	public void showMessageBoxWelcome(final String title, final String message) {
		if(wellcomeDialog!=null){
			wellcomeDialog.dismiss();
			wellcomeDialog=null;
		}
		LogUtil.trace(TAG, "title = " + title);
		LogUtil.trace(TAG, "message = " + message);

		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setIcon(R.drawable.ic_launcher);
		alertDialogBuilder.setMessage(message);
		alertDialogBuilder.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//[TODO]not good action
						/*
						MarketService ms = new MarketService(activity);
						ms.force(true);
						ms.level(MarketService.MINOR).checkVersion();
						*/
					}
				});
		

		alertDialogBuilder.setCancelable(false);
		
		if(isConnected()){//なんらかの接続チェック情報
			PackageInfo m_sPackInfo = null;
			try {
				Context context = getApplicationContext();
				m_sPackInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			} catch (NameNotFoundException ex) {
				LogUtil.error(TAG,"getPackageInfo",ex);
			}
			if(m_sPackInfo==null){
				wellcomeDialog.show();
				return;
			}
	    	final String verS = m_sPackInfo.versionName;
			final String packageName = m_sPackInfo.packageName;
			String url = "https://androidquery.appspot.com/api/market?app="+ packageName+"&locale="+Locale.getDefault();
			LogUtil.trace(TAG,"url:"+url);

			AsyncHttpClient client = new AsyncHttpClient();
			client.setTimeout(3000);//time out time
			//連打対応を考えるなら AsyncHttpClient clientをグローバル変数に持って
			//下記の関数でキャンセル
			//client.cancelRequests(this, true);

			client.get(url, new JsonHttpResponseHandler() {
				@Override
				public void onSuccess(JSONObject response) {
					try {
						String published = (String)response.get("published");
		            	String version = (String)response.get("version");
						LogUtil.trace(TAG,"<INFO>published:"+published);
						LogUtil.trace(TAG,"<INFO>version:"+version);

			            	if(verS.compareTo(version) < 0){
			            	//if(verS < version){
							//version different
							alertDialogBuilder.setMessage(message + m_r.getString(R.string.Message_Update,published,version));
							alertDialogBuilder.setNegativeButton("Update",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											String inURL ="market://details?id="+ packageName;
											Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( inURL ) );
											startActivity( browse );
										}
									});
						}
						else{
							//version same
						}
					} catch (Exception e) {
					}
					wellcomeDialog = alertDialogBuilder.create();
					wellcomeDialog.show();
				}

				@Override
		            public void onFailure(Throwable e, JSONObject errorResponse){
						wellcomeDialog = alertDialogBuilder.create();
						wellcomeDialog.show();
		            }
			});
		}
		else{
			wellcomeDialog = alertDialogBuilder.create();
			wellcomeDialog.show();
		}
	}

	public boolean isConnected(){
		return true;
	}
	
	Dialog mDialog = null;
    private void infoDlg(String title,String msg){
		if(mDialog!=null){
			try{
				mDialog.dismiss();
			}catch(Exception ex){mDialog.cancel();}
		}

		mDialog = new Dialog(this);
		mDialog.setTitle(title);
		//mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		mDialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
		mDialog.setContentView(R.layout.dialog_info);

		mDialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent e) {
		        if(e.getKeyCode() != KeyEvent.KEYCODE_BACK) return false;
	        	if(e.getAction() != KeyEvent.ACTION_DOWN)return false;
            	LogUtil.trace(TAG, "onKey ACTION_DOWN KEYCODE_BACK");
            	mDialog.cancel();
            	mDialog = null;
		        return false;
			}
		});

    	TextView tx2 = (TextView)mDialog.findViewById(R.id.InfoBody);
    	tx2.setText(msg);

    	mDialog.findViewById(R.id.InfoOK).setOnClickListener(
    			new OnClickListener() {
    				public void onClick(View v) {
    					mDialog.dismiss();
    					mDialog = null;
    				}
    			});
    	
		DisplayMetrics metrics = m_r.getDisplayMetrics();  
		int dialogWidth = (int) (metrics.widthPixels * 0.9);  
		//int dialogHeight = (int) (metrics.heightPixels * 0.6);  
		WindowManager.LayoutParams lp = mDialog.getWindow().getAttributes();  
	    lp.width = dialogWidth;  
	    //lp.height =dialogHeight;
	    mDialog.getWindow().setAttributes(lp);  
    	mDialog.show();
		mDialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ic_launcher);
    }

}
