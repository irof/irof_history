package com.irof.irof_history;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

import java.util.Random;

import yanzm.products.quickaction.lib.ActionItem;
import yanzm.products.quickaction.lib.QuickAction;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerTabStrip;
import android.util.DisplayMetrics;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.capricorn.ArcMenu;
import com.irof.adapter.FacebookPostTask;
import com.irof.adapter.LinePostTask;
import com.irof.adapter.TwitterPostTask;
import com.irof.irof_parts.IrofDraw;
import com.irof.irof_parts.IrofImageView;
import com.irof.irof_parts.IrofPageAdapter;
import com.irof.irof_parts.IrofViewPager;
import com.irof.irof_super.IrofSuperActivity;
import com.irof.irof_super.OnActivityResultCallback;
import com.irof.sns.facebook_main;
import com.irof.sns.twitter_main;
import com.irof.util.LogUtil;
import com.irof.util.PrefUtil;
import com.irof.util.ViewIndicator;
import com.jfeinstein.jazzyviewpager.JazzyViewPager.TransitionEffect;
import com.navdrawer.SimpleSideDrawer;

public class IrofActivity extends IrofSuperActivity {
	
	private SparseIntArray balls_list;
	private int len_ball = 0;
	
	
	private SparseIntArray agents_list;
	private int len_agents = 0;
	
	private SparseIntArray sns_list;
	private int len_sns = 0;
	
//carry setting	add start
	private SparseIntArray balls_carry_list;
	private int len_ball_carry = 0;

	private String carry_msg_str;
	private String carry_msg_voice;

//carry setting	add end
	
    private final int MENU_CAPTURE = 200;
    private final int MENU_TWITTER = 201;
    private final int MENU_FACEBOOK = 202;
    private final int MENU_LINE = 203;
	private int[] sns_id ={
			MENU_CAPTURE,
			MENU_TWITTER,
			MENU_FACEBOOK,
			MENU_LINE,
	};
	
	private final int DEF_INFOMATION = 301;
	private final int DEF_OPERATE    = 302;
	private final int DEF_TWITPIC    = 303;
	
	
	private String[] judge_msg = null;
	private String[] judge_voice = null;
	private String[] judge_voice_jp = null;

	private IrofActivity activity;
	private String TAG ="";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TAG = LogUtil.getClassName();
        game_main.instance = this;
    	activity = this;
    	PrefUtil.init(this);//初期化
    	
        //起動ロゴ画面を表示する
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        
    	try {
        	setContentView(R.layout.opening);
        	new Handler().post(new Runnable(){
        		public void run() {
        			ViewStub stub = _findViewById(R.id.opening);
        			stub.setLayoutResource(R.layout.opening_img);
        			View ball = stub.inflate();
        			animate(ball).rotationYBy(360).setDuration(2000);        			
        		}
        	});
		} catch (Exception e2) {
			LogUtil.error(TAG,"onCreate",e2);
		}



        new Thread(new Runnable(){
            public void run() {
                // これは別スレッド上での処理
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ignore) {
                }

        		activity.runOnUiThread(new Runnable(){
                    public void run() {
                    	onCreateAction();
                    }
        		});
            }
        }).start();
    }
    
    public IrofViewPager mViewPager;
    private IrofPageAdapter mPagerAdapter;
	private SimpleSideDrawer mSlidingMenu;
	private void onCreateAction() {
	    new Handler().post(new Runnable(){
	        public void run() {
	            TypedArray arr = m_r.obtainTypedArray(R.array.ball_list);
	            len_ball = arr.length();

	            balls_list = new SparseIntArray(len_ball);
	            for(int i=0;i<len_ball;i++){
	         	   int res_id =arr.getResourceId(i, -1);
	         	   balls_list.put(i, res_id);
	            } 
	            arr.recycle();
	            
//carry setting	add start
	            TypedArray arr_c = m_r.obtainTypedArray(R.array.ball_list_carry);
	            len_ball_carry = arr_c.length();

	            balls_carry_list = new SparseIntArray(len_ball_carry);
	            for(int i=0;i<len_ball_carry;i++){
	         	   int res_id =arr_c.getResourceId(i, -1);
	         	   balls_carry_list.put(i, res_id);
	            } 
	            arr_c.recycle();

				carry_msg_str = m_r.getString(R.string.duke_carry_god_msg);
				carry_msg_voice = m_r.getString(R.string.duke_carry_god_msg_voice);
//carry setting	add end
	            
	            TypedArray arr2 = m_r.obtainTypedArray(R.array.agent_list);
	            len_agents = arr2.length();

	            agents_list = new SparseIntArray(len_agents);
	            for(int i=0;i<len_agents;i++){
	         	   int res_id =arr2.getResourceId(i, -1);
	         	  agents_list.put(i, res_id);
	            }
	            arr2.recycle();
	            
	            TypedArray arr3 = m_r.obtainTypedArray(R.array.sns_list);
	            len_sns = arr3.length();

	            sns_list = new SparseIntArray(len_sns);
	            for(int i=0;i<len_sns;i++){
	         	   int res_id =arr3.getResourceId(i, -1);
	         	  sns_list.put(i, res_id);
	            }
	            arr3.recycle();
	            		
	            judge_msg = m_r.getStringArray(R.array.judge_msg);
	            judge_voice = m_r.getStringArray(R.array.judge_voice);
	            judge_voice_jp = m_r.getStringArray(R.array.judge_voice_jp);
	        }
	    });
	    
	    
	       
	    new Handler().post(new Runnable(){
	        public void run() {
	            setContentView(R.layout.activity_irof);
	            
	            //IS01だとNakamap動かないようなので保留にしておく
	    	  	if( "KDDI".equals(Build.BRAND) && "IS01".equals(Build.MODEL) ){
	    	  		findViewById(R.id.menu_nakamap).setVisibility(View.GONE);
	    	  	}
	    	  	else{
	    	        //Nakamapの表示
	    			ViewStub stub = _findViewById(R.id.menu_nakamap);
	    			stub.setLayoutResource(R.layout.nakamap_view);
	    			View v = stub.inflate();
	    			v.bringToFront();
	    			v.requestFocus(View.FOCUS_FORWARD);
	    	  	}

	        	
	    	  	setupJazziness(TransitionEffect.ZoomIn);
	            
	            //viewPagerにタブを付ける
	            PagerTabStrip pagerTabStrip = _findViewById(R.id.pager_tab_strip);
	            pagerTabStrip.setDrawFullUnderline(true);
	            pagerTabStrip.setTabIndicatorColor(Color.DKGRAY);
	            
	            //円形インジケータ追加
	            ViewIndicator indicator = _findViewById(R.id.indicator);
	            indicator.setViewPager(mViewPager);
	            indicator.setPosition(0);
	            
	            //ArcMenu
	            ArcMenu arcMenu = _findViewById(R.id.arc_menu);
	            for (int i = 0; i < len_sns; i++) {
	            	int id = sns_id[i];
	            	//Lineがインストールされていないときは選択アイコンを表示しない
	            	if(id == MENU_LINE && !isCheckLine())continue;
	    			ImageView item = new ImageView(activity);
	    			item.setImageResource(sns_list.get(i));
	    			item.setId(id);
	    			arcMenu.addItem(item, new OnClickListener() {
	    				@Override
	    				public void onClick(View v) {
	    					on_sns(v.getId());
	    				}
	    			});
	    		}
	            
	            mSlidingMenu = new SimpleSideDrawer(activity);
				mSlidingMenu.setRightBehindContentView(R.layout.activity_behind_right_simple);
				mSlidingMenu.findViewById(R.id.menu_carry).setOnClickListener(new OnClickListener() {
			        public void onClick(View v) {
			        	onHide(v);
			        	showViewCarry(v);
			        	unlockCarry(v);
			        }
				});

	        }
	    });
    }
	
	private void setupJazziness(TransitionEffect effect) {
        mViewPager = _findViewById(R.id.viewpager);
        mPagerAdapter = new IrofPageAdapter(activity);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(0);

        mViewPager.setTransitionEffect(effect);
		mViewPager.setPageMargin(30);
	}

	
	private boolean carry_f = false;
	public void unlockCarry(View v){
		if(carry_f)return;
		carry_f = true;
	    String[] arr_t = m_r.getStringArray(R.array.layout_titile_carry);
	    TypedArray arr = m_r.obtainTypedArray(R.array.layout_list_carry);
	    int len = arr.length();
	    for(int i=0;i<len;i++){
	    	int res_id =arr.getResourceId(i, -1);
	    	mPagerAdapter.addItem(res_id, arr_t[i]);
	    }
	    arr.recycle();
	    //mViewPager.refreshDrawableState();
	    
        //タブ表示更新
        PagerTabStrip pagerTabStrip = _findViewById(R.id.pager_tab_strip);
        pagerTabStrip.bringToFront();
        //円形インジケータ表示更新
        ViewIndicator indicator = _findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);
        //indicator.refreshDrawableState();
	}
	
	public void onHide(View v){
    	if(mSlidingMenu==null)return;
		if(mSlidingMenu.isClosed())return;
		mSlidingMenu.toggleRightDrawer();
	}
	
	public void onSlide(View v){
		if(mSlidingMenu==null)return;
		mSlidingMenu.toggleRightDrawer();
	}
	
	public void on_sns(int id) {
		LogUtil.trace(TAG,"on_sns:"+id);
		switch(id){
			case MENU_CAPTURE:
				showMessageBox(m_r.getString(R.string.menu_capture), m_r.getString(R.string.ask_capture),true);
				break;
			case MENU_TWITTER:
				if(twitter_main.isTwitterLogin()){
					TwitterPostTask post = new TwitterPostTask(activity);
					post.execute(new String[]{""+mPagerAdapter.getPageTitle(mViewPager.getCurrentItem())});
					break;
				}
		    	Intent intentTw = new Intent(activity, com.irof.sns.AuthTwActivity.class);
		    	startActivityForCallback(intentTw, new OnActivityResultCallback() {
		            // ここで値を受け取れる
		            public void onResult(int resultCode, Intent data) {
	                	final int state = data==null ? 0:data.getIntExtra("State",0);
	                	LogUtil.trace(TAG,"state="+state);

	                	if(state==1){
	    					TwitterPostTask post = new TwitterPostTask(activity);
	    					post.execute(new String[]{""+mPagerAdapter.getPageTitle(mViewPager.getCurrentItem())});
	                	}
		            }
		    	});
				break;
			case MENU_FACEBOOK:
				if(facebook_main.isFacebookLogin()){
					FacebookPostTask post = new FacebookPostTask(activity);
					post.execute(new String[]{""+mPagerAdapter.getPageTitle(mViewPager.getCurrentItem())});
					break;
				}
		    	Intent intentFb = new Intent(activity, com.irof.sns.AuthFbActivity.class);
		    	startActivityForCallback(intentFb, new OnActivityResultCallback() {
		            // ここで値を受け取れる
		            public void onResult(int resultCode, Intent data) {
	                	final int state = data==null ? 0:data.getIntExtra("State",0);
	                	LogUtil.trace(TAG,"state="+state);

	                	if(state==1){
	                		FacebookPostTask post = new FacebookPostTask(activity);
	    					post.execute(new String[]{""+mPagerAdapter.getPageTitle(mViewPager.getCurrentItem())});
	                	}
		            }
		    	});
				break;
			case MENU_LINE:
				if(isCheckLine()){
					LinePostTask post = new LinePostTask(activity);
					post.execute(new String[]{""+mPagerAdapter.getPageTitle(mViewPager.getCurrentItem())});
				}
				break;
		}
	}

    
	
	private Animation animeC = null;
	private Animation animeS = null;
	public void on_carry(View v) {
		int id = v.getId();
		LogUtil.trace(TAG,"on_carry:"+id);
		switch(id){
			case R.id.icon_twitter_jenkins01:
				{
					final ImageView iv = _findViewById(R.id.ball);
					iv.setImageResource(balls_carry_list.get(RANDOM.nextInt(len_ball_carry)));
					if(animeS==null){
						animeS = AnimationUtils.loadAnimation(this, R.anim.out_to_left);
						animeS.setAnimationListener( new AnimationListener() {
							public void onAnimationEnd(Animation animation) {
					        	iv.setVisibility(View.GONE);
							}
		
							public void onAnimationRepeat(Animation animation) {
							}
		
							public void onAnimationStart(Animation animation) {
					        	iv.setVisibility(View.VISIBLE);
							}
						});
					}
					iv.setAnimation(animeS);
					iv.startAnimation(animeS);
				}
				break;
			default:
			{
				final ImageView iv = _findViewById(R.id.ball);
				iv.setImageResource(balls_carry_list.get(RANDOM.nextInt(len_ball_carry)));
				if(animeC==null){
					animeC = AnimationUtils.loadAnimation(this, R.anim.out_to_right);
					animeC.setAnimationListener( new AnimationListener() {
						public void onAnimationEnd(Animation animation) {
				        	iv.setVisibility(View.GONE);
						}
	
						public void onAnimationRepeat(Animation animation) {
						}
	
						public void onAnimationStart(Animation animation) {
				        	iv.setVisibility(View.VISIBLE);
						}
					});
				}
				iv.setAnimation(animeC);
				iv.startAnimation(animeC);
			}
		break;
}
		
	}
	
    private static final Random RANDOM = new Random();
	private Animation animeB = null;
	public void on_groovy(View v) {
		int id = v.getId();
		LogUtil.trace(TAG,"on_groovy:"+id);
		switch(id){
			case R.id.icon_twitter05:
			case R.id.icon_twitter07:
				{
					IrofImageView iv = _findViewById(v.getId());
					if(iv!=null)iv.on_groovy(v);
				}
				break;
			default:
				{
					final ImageView iv = _findViewById(R.id.ball);
					iv.setImageResource(balls_list.get(RANDOM.nextInt(len_ball)));
					if(animeB==null){
						animeB = AnimationUtils.loadAnimation(this, R.anim.out_to_right);
						animeB.setAnimationListener( new AnimationListener() {
							public void onAnimationEnd(Animation animation) {
					        	iv.setVisibility(View.GONE);
							}

							public void onAnimationRepeat(Animation animation) {
							}

							public void onAnimationStart(Animation animation) {
					        	iv.setVisibility(View.VISIBLE);
							}
						});
					}
					iv.setAnimation(animeB);
					iv.startAnimation(animeB);
				}
				break;
		}
	}
	
    public void showViewCarry(View v){
		switch(v.getId()){
			case R.id.menu_carry:
				{
					tts_play(carry_msg_voice);
					final FrameLayout fn = _findViewById(R.id.irof_judge);
					final ImageView iv = _findViewById(R.id.judge_image);
					final TextView tx = _findViewById(R.id.judge_text);
					//if(animeJ==null){
						if(animeJ==null)animeJ = AnimationUtils.loadAnimation(this, R.anim.out_to_left);
						animeJ.setAnimationListener( new AnimationListener() {
							public void onAnimationEnd(Animation animation) {
								fn.setVisibility(View.GONE);
							}

							public void onAnimationRepeat(Animation animation) {
							}

							public void onAnimationStart(Animation animation) {
								
								tx.setText(carry_msg_str);
								int txSize = 64 * 4/ carry_msg_str.length();
								if(txSize < 24)txSize=24;
								tx.setTextSize(txSize);
								
								iv.setImageResource(R.drawable.duke_carry_god);
								fn.setVisibility(View.VISIBLE);
							}
						});
					//}
					fn.setAnimation(animeJ);
					fn.startAnimation(animeJ);
				}
	    		break;
		}
    }
    
    private static final Random RANDOM_V = new Random();
	public boolean pause_f = false;
	private Animation animeJ = null;
    public void showViewStub(View v){
		switch(v.getId()){
			case R.id.menu_judge:
				{
					final FrameLayout fn = _findViewById(R.id.irof_judge);
					final ImageView iv = _findViewById(R.id.judge_image);
					final TextView tx = _findViewById(R.id.judge_text);
					//if(animeJ==null){
						if(animeJ==null)animeJ = AnimationUtils.loadAnimation(this, R.anim.out_to_left);
						final int pos = RANDOM.nextInt(len_agents);
						switch(initTtsMode){
						case 0://英語
							tts_play(judge_voice[pos]);
							break;
						case 1://日本語
							tts_play(judge_voice_jp[pos]);
							break;
						}

						animeJ.setAnimationListener( new AnimationListener() {
							public void onAnimationEnd(Animation animation) {
								fn.setVisibility(View.GONE);
							}

							public void onAnimationRepeat(Animation animation) {
							}

							public void onAnimationStart(Animation animation) {
								String vmsg = judge_msg[pos];
								
								tx.setText(vmsg);
								int txSize = 64 * 4/ vmsg.length();
								if(txSize < 24)txSize=24;
								tx.setTextSize(txSize);
								
								int pos_i = RANDOM_V.nextInt(len_agents);
								iv.setImageResource(agents_list.get(pos_i));
								fn.setVisibility(View.VISIBLE);
							}
						});
					//}
					fn.setAnimation(animeJ);
					fn.startAnimation(animeJ);
				}
	    		break;
			case R.id.menu_pause:
				pause_f = !pause_f;
				ImageButton btn = (ImageButton)v;
				if(!pause_f)btn.setImageResource(android.R.drawable.ic_menu_myplaces);
				else		btn.setImageResource(android.R.drawable.ic_menu_my_calendar);
	    		break;
	        case R.id.menu_clear:
	    		{
	    			IrofDraw root = _findViewById(R.id.root);
	    			root.clear();
	    		}
	    		break;
	        case R.id.menu_undo:
		    	{
		    		IrofDraw root = _findViewById(R.id.root);
		    		root.undo();
		    	}
	    		break;
	        default:
	        	break;
		}
    }
    
    
    android.view.View.OnClickListener qaAction = new android.view.View.OnClickListener(){
    	public void onClick(View v) {
    		switch(v.getId()){
/*    		
    			case MENU_CAPTURE:
    				on_sns(MENU_CAPTURE);
    				break;
    			case MENU_TWITTER:
    				on_sns(MENU_TWITTER);
    				break;
    			case MENU_FACEBOOK:
    				on_sns(MENU_FACEBOOK);
    				break;
*/    				
				case DEF_INFOMATION:
					infoDlg(m_r.getString(R.string.info_title),m_r.getString(R.string.info_desc));
					break;
				case DEF_OPERATE:
					infoDlg(m_r.getString(R.string.operate_title),m_r.getString(R.string.operate_desc));
					break;
       			case DEF_TWITPIC:
    				String inURL ="http://twitpic.com/tag/irofhistory";
    				Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( inURL ) );
    			    startActivity( browse );
    				break;
    			default:
    				break;
    		}
    	}
    };
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
        case R.id.menu_settings:{
        		Intent intent = new Intent(activity, setting_main.class);
        		startActivity(intent);
        	}
            return true;
        case R.id.menu_preferences:
        	{
        		View anchor = this.findViewById(R.id.pager_tab_strip);
        		QuickAction qa = new QuickAction(anchor);
        		String[] arr_title = m_r.getStringArray(R.array.list_data_qa);

        		ActionItem item_info = new ActionItem();  
        		item_info.setId(DEF_INFOMATION);
        		item_info.setTitle(arr_title[0]);  
        		item_info.setIcon(m_r.getDrawable(android.R.drawable.ic_menu_info_details));  
        		item_info.setOnClickListener(qaAction);
        		qa.addActionItem(item_info);
        		
        		ActionItem item_operate = new ActionItem();  
        		item_operate.setId(DEF_OPERATE);
        		item_operate.setTitle(arr_title[1]);  
        		item_operate.setIcon(m_r.getDrawable(android.R.drawable.ic_menu_help));  
        		item_operate.setOnClickListener(qaAction);
        		qa.addActionItem(item_operate);

        		ActionItem item_twitpic = new ActionItem();  
        		item_twitpic.setId(DEF_TWITPIC);
        		item_twitpic.setTitle(arr_title[2]);  
        		item_twitpic.setIcon(m_r.getDrawable(R.drawable.twitpic));  
        		item_twitpic.setOnClickListener(qaAction);
        		qa.addActionItem(item_twitpic);

/*        		
        		ActionItem capture = new ActionItem();  
        		capture.setId(MENU_CAPTURE);
        		capture.setTitle(m_r.getString(R.string.menu_capture));  
        		capture.setIcon(m_r.getDrawable(android.R.drawable.ic_menu_camera));  
        		capture.setOnClickListener(qaAction);
        		qa.addActionItem(capture);

        		ActionItem twitter = new ActionItem();  
        		twitter.setId(MENU_TWITTER);
        		twitter.setTitle(m_r.getString(R.string.menu_twitter));  
        		twitter.setIcon(m_r.getDrawable(R.drawable.twitter));  
        		twitter.setOnClickListener(qaAction);
        		qa.addActionItem(twitter);
        		
        		ActionItem facebook = new ActionItem();  
        		facebook.setId(MENU_FACEBOOK);
        		facebook.setTitle(m_r.getString(R.string.menu_facebook));  
        		facebook.setIcon(m_r.getDrawable(R.drawable.facebook));  
        		facebook.setOnClickListener(qaAction);
        		qa.addActionItem(facebook);
*/
        		
        		qa.setLayoutStyle(QuickAction.STYLE_LIST);
        		qa.setAnimStyle(QuickAction.ANIM_GROW_FROM_CENTER);  
        		qa.show();
        	}
        	//showMessageBox(m_r.getString(R.string.menu_capture), m_r.getString(R.string.ask_capture),true);
            return true;
        case R.id.menu_clear:
        	{
        		IrofDraw root = _findViewById(R.id.root);
        		root.clear();
        	}
            return true;
        case R.id.menu_undo:
        	{
        		IrofDraw root = _findViewById(R.id.root);
        		root.undo();
        	}
            return true;
        default:
            return super.onOptionsItemSelected(item);
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_irof, menu);
        return true;
    }

	//二回目起動時に呼ばれる場所
    @Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
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
