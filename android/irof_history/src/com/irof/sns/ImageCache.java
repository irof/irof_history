package com.irof.sns;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.support.v4.util.LruCache;

import com.irof.irof_history.R;
import com.irof.util.ActivityUtil;
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
    	day_erase = activity.getResources().getInteger(R.integer.day_erase);
    }

    public static int differenceDays(Date date1,Date date2) {
        long datetime1 = date1.getTime();
        long datetime2 = date2.getTime();
        long one_date_time = 1000 * 60 * 60 * 24;
        long diffDays = (datetime1 - datetime2) / one_date_time;
        return (int)diffDays; 
    }

    private static int day_erase = 1;//3;//消去期限をとりあえず３日に
    private final static String path = "SaveIconList.dat";
	public static boolean loadIconList(){
		return loadIconList(true);
	}
	@SuppressWarnings("unchecked")
	public static boolean loadIconList(boolean init_f){
		try {
			File iconFile = null;
		    FileInputStream fis = null;
			if(isSDCardWriteReady()){
				File dir = getSDCardDir();
				// 新規のファイルオブジェクトを作成
				iconFile = new File(dir, path);
				if(iconFile.exists())fis = new FileInputStream(iconFile);
			}
			else{
			    iconFile = activity.getFileStreamPath(path);
			    if(iconFile.exists())fis = activity.openFileInput(path);
			}
		    if(!iconFile.exists())return false;

		    if(init_f){
			    Date lastModDate = new Date(iconFile.lastModified());
			    if(differenceDays(new Date(),lastModDate)>=day_erase){
			    	iconFile.delete();
			    	return false;
			    }
		    }

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
		    LogUtil.debug(TAG, "[loadIconList]length = " + iconList.size());

		} catch (Exception e) {
		    LogUtil.error(TAG, "loadIconList ",e);
		    return false;
		}
		return true;
	}

	public static boolean saveIconList(){
	    if(sLruCache.size()<=0)return false;
		FileOutputStream fos = null;
		try {
			File iconFile = null;
			
			if(isSDCardWriteReady()){
				File dir = getSDCardDir();
				iconFile = new File(dir, path);
				fos = new FileOutputStream(iconFile);
			}
			else{
			    iconFile = activity.getFileStreamPath(path);
			    fos = activity.openFileOutput(path, Context.MODE_PRIVATE);
			}
		    
		    Map<String,byte[]> iconList = new HashMap<String,byte[]>();
		    
            //bmp => byte変換
		    Map<String,Bitmap> snap = sLruCache.snapshot();
		    for(String key:snap.keySet()){
		    	Bitmap bmp  = snap.get(key);
		    	int size = bmp.getWidth() * bmp.getHeight();
		    	ByteArrayOutputStream out = new ByteArrayOutputStream(size);
		    	bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);   
		    	iconList.put(key, out.toByteArray());
		    	//bmp.recycle();
		    }
		    
		    ObjectOutputStream oos = new ObjectOutputStream(fos);
		    oos.writeObject(iconList);
		    oos.close();

		    long length = iconFile.length();
		    LogUtil.debug(TAG, "[saveIconList]length = " + length);
		    return true;
		} catch (Exception e) {
		    LogUtil.error(TAG, "saveIconList ",e);
		    return false;
		}
		finally{
			try {
				if(fos!=null)fos.close();
			} catch (IOException e) {}
		}
	}
	
    private static boolean isSDCardWriteReady(){
    	if (activity.checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)return false;
    	 String state = Environment.getExternalStorageState();
    	 return (Environment.MEDIA_MOUNTED.equals(state) && !Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }
    private static File getSDCardDir(){
    	File dir = null;
    	if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO){
    		dir = ActivityUtil.getExternalFilesDir(activity,null);
    	}
    	else{
    		dir = new File(Environment.getExternalStorageDirectory(),"/Android/data/" + activity.getClass().getPackage().getName() +"/files");
    		dir.mkdirs();
    	}
    	return dir;
    }

}