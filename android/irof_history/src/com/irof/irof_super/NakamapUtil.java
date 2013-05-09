package com.irof.irof_super;

import android.app.Activity;
import android.app.Application;
import android.content.res.Resources;
import android.os.Handler;

import com.irof.irof_history.R;
import com.kayac.nakamap.sdk.Nakamap;

public class NakamapUtil {
	
	long delay_time = 1000; 
	public void nakamapSetup(final Activity activity) {
		Runnable command = new Runnable(){
			@Override
			public void run() {
				Resources m_r = activity.getResources();
				String NAKAMAP_CLIENT_ID = m_r.getString(R.string.nakamap_clientid);
				String newAccountBaseName = m_r.getString(R.string.newAccountBaseName);
				Nakamap.setup(activity.getApplicationContext(),
				        NAKAMAP_CLIENT_ID,
				        newAccountBaseName);
			}
		};
		new Handler(activity.getMainLooper()).postDelayed(command, delay_time);
	}
	
	public void nakamapSetup(final Application app) {
		Runnable command = new Runnable(){
			@Override
			public void run() {
				Resources m_r = app.getResources();
				String NAKAMAP_CLIENT_ID = m_r.getString(R.string.nakamap_clientid);
				String newAccountBaseName = m_r.getString(R.string.newAccountBaseName);
				Nakamap.setup(app.getApplicationContext(),
				        NAKAMAP_CLIENT_ID,
				        newAccountBaseName);
			}
		};
		new Handler(app.getMainLooper()).postDelayed(command, delay_time);
	}

}
