package com.irof.irof_super;

import android.app.Activity;
import android.content.Intent;

public abstract class BaseActivity extends Activity {
	/**
	 * Activity を呼び出す。第2引数で呼び出し先からの戻り値を受け取る。
	 */
	public void startActivityForCallback(Intent intent,
			OnActivityResultCallback callback) {
		MyApplication app = (MyApplication) this.getApplication();
		Integer id = app.getActivityCallbackNum();
		app.putActivityCallback(id, callback);
		startActivityForResult(intent, id);
	}

	/**
	 * Activity からの戻りを受け取り、callbacks 内のコールバックを呼び出す。
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// MyApplication._callbacks に requestCode があったら、そのコールバックを呼び出す。
		MyApplication app = (MyApplication) this.getApplication();
		OnActivityResultCallback callback = app
				.getActivityCallback(requestCode);
		if (callback != null) {
			callback.onResult(resultCode, data);
		}
		else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

}
