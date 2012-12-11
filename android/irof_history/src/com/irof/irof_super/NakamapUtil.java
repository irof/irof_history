package com.irof.irof_super;

import android.app.Activity;

import com.irof.irof_history.R;
import com.kayac.nakamap.sdk.Nakamap;

public class NakamapUtil {
	public void nakamapSetup(Activity activity) {
		String NAKAMAP_CLIENT_ID = activity.getResources().getString(R.string.nakamap_clientid);
		String newAccountBaseName = activity.getResources().getString(R.string.newAccountBaseName);
		Nakamap.setup(activity.getApplicationContext(),
		        NAKAMAP_CLIENT_ID,
		        newAccountBaseName);
	}
	

}
