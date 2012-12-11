package com.irof.irof_history;

import static com.nineoldandroids.view.ViewPropertyAnimator.animate;

import java.util.Random;

import yanzm.products.quickaction.lib.ActionItem;
import yanzm.products.quickaction.lib.QuickAction;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerTabStrip;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import com.irof.adapter.TwitterPostTask;
import com.irof.irof_parts.IrofDraw;
import com.irof.irof_parts.IrofImageView;
import com.irof.irof_parts.IrofPageAdapter;
import com.irof.irof_parts.IrofViewPager;
import com.irof.irof_super.IrofSuperActivity;
import com.irof.irof_super.OnActivityResultCallback;
import com.irof.sns.twitter_main;
import com.irof.util.LogUtil;
import com.irof.util.PrefUtil;
import com.irof.util.ViewIndicator;

public class IrofActivity extends IrofSuperActivity {
	
	private SparseIntArray balls;
	private int len_ball = 0;
	
	private SparseIntArray agents;
	private int len_agents = 0;
	
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
    
    private IrofViewPager mViewPager;
    private IrofPageAdapter mPagerAdapter;
	private void onCreateAction() {
	    new Handler().post(new Runnable(){
	        public void run() {
	            TypedArray arr = m_r.obtainTypedArray(R.array.ball_list);
	            len_ball = arr.length();

	            balls = new SparseIntArray(len_ball);
	            for(int i=0;i<len_ball;i++){
	         	   int res_id =arr.getResourceId(i, -1);
	         	   balls.put(i, res_id);
	            } 
	            
	            TypedArray arr2 = m_r.obtainTypedArray(R.array.agent_list);
	            len_agents = arr2.length();

	            agents = new SparseIntArray(len_agents);
	            for(int i=0;i<len_agents;i++){
	         	   int res_id =arr2.getResourceId(i, -1);
	         	  agents.put(i, res_id);
	            } 
	            
	            judge_msg = m_r.getStringArray(R.array.judge_msg);
	            judge_voice = m_r.getStringArray(R.array.judge_voice);
	            judge_voice_jp = m_r.getStringArray(R.array.judge_voice_jp);
	            
	            
	            setContentView(R.layout.activity_irof);
	            
	            //IS01だとNakamap動かないようなので保留にしておく
	    	  	if( "KDDI".equals(Build.BRAND) && "IS01".equals(Build.MODEL) ){
	    	  		findViewById(R.id.menu_nakamap).setVisibility(View.GONE);
	    	  	}
	    	  	else{
	    	        //Nakamapの表示
	    			ViewStub stub = _findViewById(R.id.menu_nakamap);
	    			stub.setLayoutResource(R.layout.nakamap_view);
	    			stub.inflate();
	    	  	}
	            
	            mViewPager = _findViewById(R.id.viewpager);
	            mPagerAdapter = new IrofPageAdapter(activity);
	            mViewPager.setAdapter(mPagerAdapter);
	            mViewPager.setCurrentItem(0);
	            
	            //viewPagerにタブを付ける
	            PagerTabStrip pagerTabStrip = _findViewById(R.id.pager_tab_strip);
	            pagerTabStrip.setDrawFullUnderline(true);
	            pagerTabStrip.setTabIndicatorColor(Color.DKGRAY);
	            
	            //円形インジケータ追加
	            ViewIndicator indicator = _findViewById(R.id.indicator);
	            indicator.setViewPager(mViewPager);
	            indicator.setPosition(0);
	            
	        }
	    });
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
					iv.setImageResource(balls.get(RANDOM.nextInt(len_ball)));
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
						final int pos_i = RANDOM_V.nextInt(len_agents);
						animeJ.setAnimationListener( new AnimationListener() {
							public void onAnimationEnd(Animation animation) {
								fn.setVisibility(View.GONE);
							}

							public void onAnimationRepeat(Animation animation) {
							}

							public void onAnimationStart(Animation animation) {
								iv.setImageResource(agents.get(pos_i));
								String vmsg = judge_msg[pos];
								tx.setText(vmsg);
								int txSize = 64 * 4/ vmsg.length();
								if(txSize < 24)txSize=24;
								tx.setTextSize(txSize);
								fn.setVisibility(View.VISIBLE);
								
								switch(initTtsMode){
								case 0://英語
									tts_play(judge_voice[pos]);
									break;
								case 1://日本語
									tts_play(judge_voice_jp[pos]);
									break;
								}
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
    
    private final int MENU_CAPTURE = 200;
    private final int MENU_TWITTER = 201;
    android.view.View.OnClickListener qaAction = new android.view.View.OnClickListener(){
    	public void onClick(View v) {
    		switch(v.getId()){
    			case MENU_CAPTURE:
    				showMessageBox(m_r.getString(R.string.menu_capture), m_r.getString(R.string.ask_capture),true);
    				break;
    			case MENU_TWITTER:
    				if(twitter_main.isTwitterLogin()){
    					TwitterPostTask post = new TwitterPostTask(activity);
    					post.execute(new String[]{""+mPagerAdapter.getPageTitle(mViewPager.getCurrentItem())});
    					break;
    				}
			    	Intent intent = new Intent(activity, com.irof.sns.AuthTwActivity.class);
			    	/*
			    	//コールバック形式の時
			    	intent.putExtra("CALLBACK_URL", 
			    			String.format("%s://%s", 
			    						game_main.instance.m_r.getString(R.string.callback_scheme),
			    						game_main.instance.m_r.getString(R.string.callback_host)
			    					));
			    	intent.putExtra("finishClass", this.getClass().getName()); 
			    	*/
			    	startActivityForCallback(intent, new OnActivityResultCallback() {
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

        		ActionItem capture = new ActionItem();  
        		capture.setId(MENU_CAPTURE);
        		capture.setTitle(m_r.getString(R.string.menu_capture));  
        		capture.setIcon(m_r.getDrawable(android.R.drawable.ic_menu_camera));  
        		capture.setOnClickListener(qaAction);
        		qa.addActionItem(capture);

        		ActionItem twitter = new ActionItem();  
        		twitter.setId(MENU_TWITTER);
        		twitter.setTitle(m_r.getString(R.string.menu_twitter));  
        		twitter.setIcon(m_r.getDrawable(R.drawable.twitter_bird_light_bgs));  
        		twitter.setOnClickListener(qaAction);
        		qa.addActionItem(twitter);
        		
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

}
