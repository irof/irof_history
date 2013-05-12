package com.irof.util;

import android.util.Log;

import com.irof.irof_history.BuildConfig;

public class LogUtil {

	public static void debug(String tag, Object msg) {
		if (!Log.isLoggable(tag, Log.DEBUG)) return;
		Log.d(tag, String.valueOf(msg));
	}

	public static void info(String tag, Object msg) {
		if (!Log.isLoggable(tag, Log.INFO)) return;
		Log.i(tag, String.valueOf(msg));
	}

	public static void warn(String tag, Object msg) {
		if (!Log.isLoggable(tag, Log.WARN)) return;
		Log.w(tag, String.valueOf(msg));
	}

	public static void error(String tag, Object msg) {
		if (!Log.isLoggable(tag, Log.ERROR)) return;
		Log.e(tag, String.valueOf(msg));
	}

	public static void error(String tag, Object msg, Exception ex) {
		if (!Log.isLoggable(tag, Log.ERROR)) return;
		Log.e(tag, String.valueOf(msg), ex);
	}

	public static boolean isDebugEnabled(String tag) {
		return Log.isLoggable(tag, Log.DEBUG);
	}

	public static boolean isInfoEnabled(String tag) {
		return Log.isLoggable(tag, Log.INFO);
	}

	public static boolean isWarnEnabled(String tag) {
		return Log.isLoggable(tag, Log.WARN);
	}

	public static boolean isErrorEnabled(String tag) {
		return Log.isLoggable(tag, Log.ERROR);
	}

	public static boolean isTraceEnabled(String tag) {
		return Log.isLoggable(tag, Log.VERBOSE);
	}

	public static void debug(Object msg) {
		String tag = getClassName();
		if (!Log.isLoggable(tag, Log.DEBUG)) return;

		Log.d(tag, getFunctionName() + String.valueOf(msg));
	}

	public static void info(Object msg) {
		String tag = getClassName();
		if (!Log.isLoggable(tag, Log.INFO)) return;
		Log.i(tag, getFunctionName() + String.valueOf(msg));
	}

	public static void warn(Object msg) {
		String tag = getClassName();
		if (!Log.isLoggable(tag, Log.WARN)) return;
		Log.w(tag, getFunctionName() + String.valueOf(msg));
	}

	public static void error(Object msg) {
		String tag = getClassName();
		if (!Log.isLoggable(tag, Log.ERROR)) return;
		Log.e(tag, getFunctionName() + String.valueOf(msg));
	}

	public static void error(Object msg, Exception ex) {
		String tag = getClassName();
		if (!Log.isLoggable(tag, Log.ERROR)) return;
		Log.e(tag, getFunctionName() + String.valueOf(msg), ex);
	}

	public static void trace(String tag, Object msg) {
		if (!BuildConfig.DEBUG) {
			if (!Log.isLoggable(tag, Log.VERBOSE)) return;
		}
		Log.v(tag, String.valueOf(msg));
	}

	public static void trace(Object msg) {
		String tag = getClassName();
		if (!BuildConfig.DEBUG) {
			if (!Log.isLoggable(tag, Log.VERBOSE)) return;
		}
		Log.v(tag, getFunctionName() + String.valueOf(msg));
	}

	private static final int	TRACE_CALLER_CALLER	= 2;

	public static String getClassName() {
		String fn = "";
		try {
			fn = new Throwable().getStackTrace()[TRACE_CALLER_CALLER]
					.getClassName();
			fn = fn.substring(fn.lastIndexOf('.') + 1, fn.length());
		}
		catch (Exception e) {
		}

		return fn;
	}

	private static String getFunctionName() {
		String fn = "";
		try {
			fn = new Throwable().getStackTrace()[TRACE_CALLER_CALLER]
					.getMethodName();
			fn = fn.substring(fn.lastIndexOf('.') + 1, fn.length());
		}
		catch (Exception e) {
		}

		return "[" + fn + "]:";
	}
}
