package com.example.qqmusic;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Menu;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends Activity {
	private IService iservice;
	private MyConn conn;
	static Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			//System.out.println(msg);
			Bundle bundle = (Bundle) msg.obj;
			//System.out.println(bundle);
			int duration = (Integer) bundle.get("duration");
			
			int currentPosition = (Integer) bundle.get("current");
			sBar.setMax(duration); //设置seekbar 的总进度
			sBar.setProgress(currentPosition);  //设置当前进度
			//System.out.println("========="+duration);
			//System.out.println("============"+currentPosition);
		};
	};
	 
	private static SeekBar sBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
       
        
        //先开启服务保证服务后台长期运行
        Intent service = new Intent(this,ActService.class);
        //startService(service);
        //然后绑定service  同一个service  目的是为了调用其方法
        //猜想这句话调用了MyConn ---public void onServiceConnected(ComponentName name, IBinder service)？？
        //调用了这句话之后才能拿到中间人。。
        conn = new MyConn();
        bindService(service, conn, BIND_AUTO_CREATE);
        sBar = (SeekBar) findViewById(R.id.sb_bar);
        //监听seekbar
        sBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			//停滞
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				//实现 拖动到哪 播哪 
				iservice.callSeekTo(seekBar.getProgress());
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				
			}
		});
        
        
       /// getContentResolver().registerContentObserver(uri, notifyForDescendents, observer)
        
        
        
    }
    //开始播放
    public void click1(View v){
    	iservice.callPlay();
    }
    //暂停
    public void click2(View v){
    	iservice.callPause();
    }
    //重新播放
    public void click3(View v){
    	iservice.callResume();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    private class MyConn implements ServiceConnection{

		

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			System.out.println("连接成功=========================");
			iservice = (IService) service;
			
			
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
    	
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	//连接是否单例  能否这么写？？
    	 /**
         * Perform any final cleanup before an activity is destroyed.  This can
         * happen either because the activity is finishing (someone called
         * {@link #finish} on it, or because the system is temporarily destroying
         * this instance of the activity to save space.  You can distinguish
         * between these two scenarios with the {@link #isFinishing} method.
         * 
         * <p><em>Note: do not count on this method being called as a place for
         * saving data! For example, if an activity is editing data in a content
         * provider, those edits should be committed in either {@link #onPause} or
         * {@link #onSaveInstanceState}, not here.</em> This method is usually implemented to
         * free resources like threads that are associated with an activity, so
         * that a destroyed activity does not leave such things around while the
         * rest of its application is still running.  There are situations where
         * the system will simply kill the activity's hosting process without
         * calling this method (or any others) in it, so it should not be used to
         * do things that are intended to remain around after the process goes
         * away.
         * 
         * <p><em>Derived classes must call through to the super class's
         * implementation of this method.  If they do not, an exception will be
         * thrown.</em></p>
         * 
         * @see #onPause
         * @see #onStop
         * @see #finish
         * @see #isFinishing
         */
    	unbindService(conn);
    	
    }
    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	/**
         * Call this when your activity is done and should be closed.  The
         * ActivityResult is propagated back to whoever launched you via
         * onActivityResult().
         */
    	super.onBackPressed();
    	//startActivity(new Intent(this,MainActivity.class));
    }
    
}
