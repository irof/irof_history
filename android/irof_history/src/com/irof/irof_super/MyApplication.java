package com.irof.irof_super;

import android.app.Application;
import android.util.SparseArray;

public final class MyApplication extends Application {
	private static SparseArray<OnActivityResultCallback>	_activityCallbacks	= new SparseArray<OnActivityResultCallback>();

	public void onCreate() {
		super.onCreate();
		// Nakamap2.0から 起動時にやるとめちゃ重いのでLazy Loadに変更
		/*
		 * //IS01だとNakamap動かないようなので保留にしておく if( "KDDI".equals(Build.BRAND) &&
		 * "IS01".equals(Build.MODEL) ){ } else{ //Nakamapの初期化 new
		 * NakamapUtil().nakamapSetup(this); }
		 */
	}

	public OnActivityResultCallback getActivityCallback(int requestCode) {
		if (_activityCallbacks.indexOfKey(requestCode) >= 0) {
			OnActivityResultCallback found = _activityCallbacks
					.get(requestCode);
			_activityCallbacks.remove(requestCode);
			return found;
		}
		else {
			return null;
		}
	}

	public void putActivityCallback(int requestCode,
			OnActivityResultCallback callback) {
		_activityCallbacks.put(requestCode, callback);
	}

	public int getActivityCallbackNum() {
		return _activityCallbacks.size();
	}
}