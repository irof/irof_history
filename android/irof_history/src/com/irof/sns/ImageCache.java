package com.irof.sns;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.irof.util.LogUtil;

public final class ImageCache {
	private static final String TAG = "ImageCache";
    private static final int MEM_CACHE_SIZE = 1 * 1024 * 1024; // 1MB

    private static LruCache<String, Bitmap> sLruCache;

    static {
        sLruCache = new LruCache<String, Bitmap>(MEM_CACHE_SIZE) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
    }

    private ImageCache() {
    }

    public static void put(String key, Bitmap bitmap) {
        if (get(key) == null) {
            sLruCache.put(key, bitmap);
        }
    }

    public static Bitmap get(String key) {
        return sLruCache.get(key);
    }
    
    
	public static Activity activity;
	public static void init(Activity activity_){
		activity = activity_;
	}

    private final static String path = "SaveIconList.dat";
	@SuppressWarnings("unchecked")
	public static boolean loadIconList(){
		try {
			File iconFile = null;
		    FileInputStream fis = null;
		    iconFile = activity.getFileStreamPath(path);
		    if(iconFile.exists())fis = activity.openFileInput(path);
		    if(!iconFile.exists())return false;

		    long length = iconFile.length();
		    LogUtil.debug(TAG, "[loadIconList]length = " + length);

		    ObjectInputStream ois = new ObjectInputStream(fis);
		    Map<String,byte[]> iconList = (HashMap<String,byte[]>) ois.readObject();
		    ois.close();
		    
		    //byte => bitmap変換
		    for(String key:iconList.keySet()){
		    	byte[] bytSig = iconList.get(key);
    		    Bitmap value = BitmapFactory.decodeByteArray(bytSig, 0, bytSig.length);
    		    sLruCache.put(key, value);
		    }
		    LogUtil.trace(TAG, "[loadIconList]length = " + iconList.size());

		} catch (Exception e) {
		    Log.e(TAG, "loadIconList ",e);
		    return false;
		}
		return true;
	}

	public static boolean saveIconList(){
	    if(sLruCache.size()<=0)return false;
		FileOutputStream fos = null;
		try {
			File iconFile = null;
		    iconFile = activity.getFileStreamPath(path);
		    fos = activity.openFileOutput(path, Context.MODE_PRIVATE);
		    
		    Map<String,byte[]> iconList = new HashMap<String,byte[]>();
		    
            //bmp => byte変換
		    Map<String,Bitmap> snap = sLruCache.snapshot();
		    for(String key:snap.keySet()){
		    	Bitmap bmp  = snap.get(key);
				int size = bmp.getWidth() * bmp.getHeight();
				ByteArrayOutputStream out = new ByteArrayOutputStream(size);
				bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);   
		    	iconList.put(key, out.toByteArray());
		    }
		    
		    ObjectOutputStream oos = new ObjectOutputStream(fos);
		    oos.writeObject(iconList);
		    oos.close();

		    long length = iconFile.length();
		    LogUtil.trace(TAG, "[saveIconList]length = " + length);
		    return true;
		} catch (Exception e) {
		    Log.e(TAG, "saveIconList ",e);
		    return false;
		}
		finally{
			try {
				if(fos!=null)fos.close();
			} catch (IOException e) {}
		}
	}

}