package com.irof.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.irof.sns.ImageCache;
import com.irof.util.LogUtil;

public class TwArrayAdapter extends ArrayAdapter<String> {
	private java.lang.String TAG = "TwArrayAdapter";
    //private final int SEP = 26;
    private int layoutId;
    private LayoutInflater inflater;
    //private String[] items =null;
	private List<String> items =new ArrayList<String>();

    
	private static Resources m_r;

	//public TwArrayAdapter(Context context, int listLocal_,String[] objects) {
	public TwArrayAdapter(Context context, int listLocal_) {
		super(context, 0);
		this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutId = listLocal_;
		items.clear();
		
		m_r = context.getResources();
	}

	//public TwArrayAdapter(Context context, int listLocal_,String[] objects) {
	public TwArrayAdapter(Context context, int listLocal_,List<String> objects) {
		super(context, 0, objects);
		this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutId = listLocal_;
		items.clear();
		items.addAll(objects);
		
		m_r = context.getResources();
	}

	@Override
	public void add(String object) {
		super.add(object);
		items.add(object);
	}

    @Override
	public void insert(String object, int index) {
		super.insert(object, index);
		items.add(index, object);
	}

	
	static class ViewHolder {  
	    ImageView blockView;
	    TextView twitterView;  
		TextView textView;
	}  
	
	@Override
	public View getView(final int position, final View convertView,final ViewGroup parent) {
		
		ViewHolder holder;
		View rowView = convertView;
		ImageView blockView = null;
		TextView twitterView = null;
		TextView textView =null;

        if (rowView == null) {
        	rowView = inflater.inflate(layoutId, parent, false);
            
            holder = new ViewHolder();
            
            LinearLayout ln = (LinearLayout)rowView;
            blockView= (ImageView)ln.getChildAt(0);
            LinearLayout lnx = (LinearLayout)ln.getChildAt(1);
            if(lnx!=null){
               twitterView= (TextView)lnx.getChildAt(0);
               textView= (TextView)lnx.getChildAt(1);
            }
            
//            blockView= (ImageView)ln.getChildAt(0);
//            twitterView= (TextView)ln.getChildAt(1);
//            textView= (TextView)ln.getChildAt(2);

    		//twitterView.setTextSize(17);
    		//twitterView.setHeight(17);
    		//twitterView.setMinimumHeight(17);
    		//twitterView.setLines(1);//�P�s�ɐ�������

    		//textView.setTextSize(17);
            //textView.setHeight(17*3);
            //textView.setMinimumHeight(17*3);
            //textView.setLines(3);//�P�s�ɐ�������


            holder.textView =textView;
            holder.twitterView =twitterView;
            holder.blockView =blockView;
            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
    		twitterView = holder.twitterView;
            textView =  holder.textView;
    		blockView = holder.blockView;
        }

		//長いユーザ名が表示されない対応
		String str = items.get(position);
		
		LogUtil.trace(TAG,"str = " + str);
		final String arr[] =str.split(",");
		String screen_name ="";
		String set_str=str;
		String set_disc="";
		if(arr.length==3){
			screen_name =arr[0];
			set_str = String.format("%s@%s",arr[1],arr[0]);
			set_disc = arr[2];
		}
		twitterView.setText(set_str);
		textView.setText(set_disc);
		
		Bitmap m_bmp_panel = ImageCache.get(screen_name);
		if(m_bmp_panel!=null){
			//Bitmap m_bmp_panel = BitmapFactory.decodeByteArray(bytSig, 0, bytSig.length);
			blockView.setImageBitmap(m_bmp_panel);
			blockView.setVisibility(View.VISIBLE);
		}
		else{
			TwIconDownloadTask task = new TwIconDownloadTask(blockView);
			task.execute(screen_name);

			blockView.setImageBitmap(null);
			blockView.setVisibility(View.INVISIBLE);
		}

		
		if(rowView instanceof LinearLayout)return rowView;
		return textView;
	}
}