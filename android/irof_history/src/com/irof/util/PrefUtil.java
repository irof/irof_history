package com.irof.util;import java.util.UUID;import android.app.Activity;import android.content.SharedPreferences;import android.content.SharedPreferences.Editor;import android.os.Build;import android.preference.PreferenceManager;public class PrefUtil {	private final static String	TAG			= "PrefUtil";	private static Activity		activity	= null;	public static void init(Activity activity_) {		activity = activity_;	}	/**	 * uuidを取得する	 */	public static String create_uuid() {		String s2 = get_string("uuid", null);		if (s2 != null) return s2;		UUID uuid = UUID.randomUUID();		s2 = uuid.toString();		LogUtil.trace(TAG, "uuid gen " + s2);		PrefUtil.put_string("uuid", s2);		return s2;	}	public static void put_string(String name, String value) {		LogUtil.trace(TAG, String.format("put_%s %s", name, value));		if (activity == null) {			LogUtil.error(TAG, "need PrefUtil.init(activity) prepare !");			return;		}		SharedPreferences pref = PreferenceManager				.getDefaultSharedPreferences(activity);		Editor e = pref.edit();		e.putString(activity.getPackageName() + "." + name, value); // put		if (Build.VERSION.SDK_INT >= 9) ActivityUtil.apply(e);		else e.commit();	}	public static String get_string(String name, String default_str) {		if (activity == null) {			LogUtil.error(TAG, "need PrefUtil.init(activity) prepare !");			return null;		}		SharedPreferences pref = PreferenceManager				.getDefaultSharedPreferences(activity);		String str = pref.getString(activity.getPackageName() + "." + name,				default_str); // get		if (str == null) {			LogUtil.trace(TAG, String.format("get_%s null", name));		}		else {			LogUtil.trace(TAG, String.format("get_%s %s", name, str));		}		return str;	}	public static void put_int(String name, int value) {		LogUtil.trace(TAG, String.format("put_%s %s", name, value));		if (activity == null) {			LogUtil.error(TAG, "need PrefUtil.init(activity) prepare !");			return;		}		SharedPreferences pref = PreferenceManager				.getDefaultSharedPreferences(activity);		Editor e = pref.edit();		e.putInt(activity.getPackageName() + "." + name, value); // put		if (Build.VERSION.SDK_INT >= 9) ActivityUtil.apply(e);		else e.commit();	}	public static int get_int(String name, int default_int) {		if (activity == null) {			LogUtil.error(TAG, "need PrefUtil.init(activity) prepare !");			return -1;		}		SharedPreferences pref = PreferenceManager				.getDefaultSharedPreferences(activity);		int value = pref.getInt(activity.getPackageName() + "." + name,				default_int); // get		LogUtil.trace(TAG, String.format("get_%s %s", name, value));		return value;	}	public static void put_long(String name, long value) {		if (activity == null) {			LogUtil.error(TAG, "need PrefUtil.init(activity) prepare !");			return;		}		LogUtil.trace(TAG, String.format("put_%s %s", name, value));		SharedPreferences pref = PreferenceManager				.getDefaultSharedPreferences(activity);		Editor e = pref.edit();		e.putLong(activity.getPackageName() + "." + name, value); // put		if (Build.VERSION.SDK_INT >= 9) ActivityUtil.apply(e);		else e.commit();	}	public static long get_long(String name, long default_int) {		if (activity == null) {			LogUtil.error(TAG, "need PrefUtil.init(activity) prepare !");			return -1;		}		SharedPreferences pref = PreferenceManager				.getDefaultSharedPreferences(activity);		long value = pref.getLong(activity.getPackageName() + "." + name,				default_int); // get		LogUtil.trace(TAG, String.format("get_%s %s", name, value));		return value;	}	public static void put_boolean(String name, boolean flag) {		LogUtil.trace(TAG, "put_" + name + " " + flag);		if (activity == null) {			LogUtil.error(TAG, "need PrefUtil.init(activity) prepare !");			return;		}		SharedPreferences pref = PreferenceManager				.getDefaultSharedPreferences(activity);		Editor e = pref.edit();		e.putBoolean(activity.getPackageName() + "." + name, flag); // put		if (Build.VERSION.SDK_INT >= 9) ActivityUtil.apply(e);		else e.commit();	}	public static boolean get_boolean(String name, boolean default_boolean) {		if (activity == null) {			LogUtil.error(TAG, "need PrefUtil.init(activity) prepare !");			return false;		}		SharedPreferences pref = PreferenceManager				.getDefaultSharedPreferences(activity);		boolean flag = pref.getBoolean(activity.getPackageName() + "." + name,				default_boolean);		LogUtil.trace(TAG, "get_" + name + " " + flag);		return flag;	}}