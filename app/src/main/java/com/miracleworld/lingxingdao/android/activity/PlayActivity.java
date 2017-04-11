package com.miracleworld.lingxingdao.android.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.base.BaseActivity;
import com.miracleworld.lingxingdao.android.bean.MusicLesson;
import com.miracleworld.lingxingdao.android.options.ImageLoaderOptions;
import com.miracleworld.lingxingdao.android.view.DefinedSingleToast;
import com.miracleworld.lingxingdao.android.view.myroundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by donghaifeng on 2015/12/21.
 */
public class PlayActivity extends BaseActivity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {

    private MediaPlayer player;
    private boolean ispause=false;
    private int progress;
    private Timer timer;
    //需要控件
    private TextView tv_progress;
    private TextView tv_max;
    private SeekBar seek;
    private ImageView music_paly_orpause;
    private TextView tv_marquee;
    private ImageView iv_play_center_photo;
    private RoundedImageView iv_teacher_head;
    private TextView tv_teacher_name;
    private TextView tv_class_category;
    private TextView tv_class_title;
    private ImageView play_loading_iv;
    private RotateAnimation rotateAnimation;
    //上页传参
    private String title;
    private String categoryName;
    private String pictureUrlSmall;
    private int initCurrentposition;
    private String url;

    private int itemposition;
    private String portraitUrlSmall;
    private String nickname;
    private ArrayList<MusicLesson> lessons;
    private TelephonyManager manager;
    private MyListener listener;

    //标记是否是空闲状态
    private boolean isCallIdle=true;
    //广播
    private BroadcastReceiver myNetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
                ConnectivityManager mConnectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo info = mConnectivity.getActiveNetworkInfo();
                if (info == null || !mConnectivity.getBackgroundDataSetting()) {
                    Log.e("jxf", "没网");
                    DefinedSingleToast.showToast(PlayActivity.this,getResources().getString(R.string.network_no_force));
                }
            }
        }
    };

    Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            Bundle bundle = msg.getData();
            int duration = bundle.getInt("duration");
            int currentPosition = bundle.getInt("currentPosition");
            progress=currentPosition;
            seek.setMax(duration);
            tv_max.setText(trans(duration));
            seek.setProgress(progress);
            tv_progress.setText(trans(progress));
        }
    };

    private String trans(int haomiao){
        int miaoZong=haomiao/1000;
        int miao=miaoZong%60;
        String miaoString = null;
        if (miao<10){
            miaoString="0"+miao;
        }
        else{
            miaoString=""+miao;
        }
        int fen=(miaoZong-miao)/60;
        String fenString = null;
        if (fen<10){
            fenString="0"+fen;
        }
        else {
            fenString=""+fen;
        }
        return fenString+":"+miaoString;
    }

    @Override
    protected void initView() {
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(myNetReceiver, mFilter);
        Bundle bundle = getIntent().getExtras();
        portraitUrlSmall=bundle.getString("portraitUrlSmall");
        nickname=bundle.getString("nickname");
        itemposition=bundle.getInt("itemposition");
        lessons= (ArrayList<MusicLesson>) getIntent().getSerializableExtra("list");
        title=lessons.get(itemposition).title;
        categoryName=lessons.get(itemposition).categoryName;
        pictureUrlSmall=lessons.get(itemposition).pictureUrlSmall;
        url=lessons.get(itemposition).url;
        initCurrentposition=lessons.get(itemposition).currentposition;
        progress=initCurrentposition;
        RelativeLayout pre= (RelativeLayout) findViewById(R.id.pre);
        pre.setOnClickListener(this);
        RelativeLayout next= (RelativeLayout) findViewById(R.id.next);
        next.setOnClickListener(this);
        ImageView iv_goback= (ImageView) findViewById(R.id.iv_goback);
        iv_goback.setOnClickListener(this);
        play_loading_iv= (ImageView) findViewById(R.id.play_loading_iv);
        rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.rotate_refresh_drawable_default);
        play_loading_iv.setAnimation(rotateAnimation);
        play_loading_iv.setVisibility(View.VISIBLE);
        tv_marquee= (TextView) findViewById(R.id.tv_marquee);
        iv_play_center_photo= (ImageView) findViewById(R.id.iv_play_center_photo);
        music_paly_orpause= (ImageView) findViewById(R.id.music_paly_orpause);
        tv_progress= (TextView) findViewById(R.id.tv_progress);
        tv_max= (TextView) findViewById(R.id.tv_max);
        seek= (SeekBar) findViewById(R.id.seek);
        iv_teacher_head= (RoundedImageView) findViewById(R.id.iv_teacher_head);
        ImageLoader.getInstance().displayImage(portraitUrlSmall, iv_teacher_head, ImageLoaderOptions.headOptions);
        tv_teacher_name= (TextView) findViewById(R.id.tv_teacher_name);
        tv_teacher_name.setText(nickname);
        tv_class_category= (TextView) findViewById(R.id.tv_class_category);
        tv_class_title= (TextView) findViewById(R.id.tv_class_title);
        tv_marquee.setText(title);
        ImageLoader.getInstance().displayImage(pictureUrlSmall, iv_play_center_photo, ImageLoaderOptions.playRoundOption);
        tv_class_category.setText(categoryName);
        tv_class_title.setText(getResources().getString(R.string.play_front_content) + title);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    progress = seekBar.getProgress();
                    Log.e("jxf", "seekbar进度改变：拖拽播放:进度到" + progress);
                    seekToplay(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        music_paly_orpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ispause) {
                    continuePlay();
                } else {
                    pause();
                }
            }

        });
        player=new MediaPlayer();
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //获取电话管理器
        manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener=new MyListener();
        //设置侦听监控电话状态
        manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        play();
    }


    private void play() {
        player.reset();
        try {
            player.setDataSource(url);
            player.prepareAsync();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                //异步准备完毕，此方法调用
                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.e("jxf", "准备完毕");
                    player.start();
                    addTimer();
                    if(progress!=0){
                        Log.e("jxf","progress不为0开始播放"+progress);
                        seekToplay(progress);
                    }
                    play_loading_iv.clearAnimation();
                    play_loading_iv.setVisibility(View.GONE);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("jxf","play异常扑捉"+e.toString());
        }
    }

    private void pause() {
        //此位置是为电话监听准备的判断
        if (player != null && player.isPlaying()) {
            Log.e("jxf","pause：对象不为空；并且正在播放");
            player.pause();
            ispause = true;
            music_paly_orpause.setImageResource(R.drawable.content_video_suspend);
        }else{
            Log.e("jxf","暂停走else");
        }
    }

    private void continuePlay() {
        if (player != null && (!player.isPlaying())) {
            Log.e("jxf","continuePlay：对象不为空；并且已经暂停");
            player.start();
            ispause = false;
            music_paly_orpause.setImageResource(R.drawable.content_video_suspend_display);
        }else{
            Log.e("jxf","continue走else");
        }
    }


    private void callPause(){
        if (player==null){
            Log.e("jxf", "pause：对象为空");
        }else{
            Log.e("jxf","pause：对象不为空");
            player.pause();
        }
    }

    private void callContinue(){
        //此位置是为电话监听准备的判断
        if (player==null){
            Log.e("jxf","continuePlay：对象为空");
        }else{
            Log.e("jxf","continuePlay：对象不为空");
            player.start();
        }
    }

    private void seekToplay(int progress) {
        Log.e("jxf","改变进度");
        player.seekTo(progress);
    }

    private void addTimer() {
        if(timer == null){
            timer = new Timer();
            timer.schedule(new TimerTask() {
                //此方法在子线程执行
                @Override
                public void run() {
                    int duration = player.getDuration();
                    int currentPosition = player.getCurrentPosition();
                    Message msg = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    Log.e("jxf","time验证走没走");
                    bundle.putInt("duration", duration);
                    bundle.putInt("currentPosition", currentPosition);
                    msg.setData(bundle);
                    handler.sendMessage(msg);

                }
            }, 5, 500);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(timer != null){
            timer.cancel();
            timer = null;
        }
        player.stop();
        player.release();
        player = null;
        if(myNetReceiver!=null){
            unregisterReceiver(myNetReceiver);
        }
        manager.listen(listener, PhoneStateListener.LISTEN_NONE);
        if (manager!=null){
            manager=null;
        }
        if (listener!=null){
            listener=null;
        }
        handler.removeCallbacksAndMessages(null);
        DefinedSingleToast.cancleToast();
        System.gc();
        Log.e("jxf", "play释放");
    }

    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_paly);
    }

    @Override
    protected void onClickEvent(View view) {
        switch (view.getId()){
            case R.id.iv_goback:
                Intent intent=new Intent();
                intent.putExtra("progress",progress);
                intent.putExtra("itemposition",itemposition);
                this.setResult(510,intent);
                PlayActivity.this.finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                break;
            case R.id.pre:
                if (itemposition==0){
                    DefinedSingleToast.showToast(this,"到头啦~");
                }else{
                    play_loading_iv.setAnimation(rotateAnimation);
                    play_loading_iv.setVisibility(View.VISIBLE);
                    itemposition=itemposition-1;
                    progress=0;
                    ispause=false;
                    music_paly_orpause.setImageResource(R.drawable.content_video_suspend_display);
                    refresh();
                }
                break;
            case R.id.next:
                if (itemposition==(lessons.size()-1)){
                    DefinedSingleToast.showToast(this,"到底啦~");
                }else{
                    play_loading_iv.setAnimation(rotateAnimation);
                    play_loading_iv.setVisibility(View.VISIBLE);
                    itemposition=itemposition+1;
                    progress=0;
                    ispause=false;
                    music_paly_orpause.setImageResource(R.drawable.content_video_suspend_display);
                    refresh();
                }
        }
    }

    private void refresh(){
        player.stop();
        title=lessons.get(itemposition).title;
        categoryName=lessons.get(itemposition).categoryName;
        pictureUrlSmall=lessons.get(itemposition).pictureUrlSmall;
        url=lessons.get(itemposition).url;
        tv_marquee.setText(title);
        ImageLoader.getInstance().displayImage(pictureUrlSmall, iv_play_center_photo, ImageLoaderOptions.playOption);
        tv_class_category.setText(categoryName);
        tv_class_title.setText(getResources().getString(R.string.play_front_content) + title);
        tv_max.setText("");
        tv_progress.setText("");
        if(timer != null){
            Log.e("jxf","计时器移除");
            timer.cancel();
            timer = null;
        }
        Log.e("jxf","调用play");
        play();
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent=new Intent();
        intent.putExtra("progress",progress);
        intent.putExtra("itemposition",itemposition);
        this.setResult(510, intent);
        PlayActivity.this.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    //添加boolean值做为判断:完成状态的进入：只有播放着进入才能出发监听：如果是暂停 拖拽进入 不会进入完成状态
    @Override
    public void onCompletion(MediaPlayer mp) {
        //一首播放完毕 自动下一首
        if (itemposition==(lessons.size()-1)){
            player.pause();
            ispause=true;
            music_paly_orpause.setImageResource(R.drawable.content_video_suspend);

        }else{
            play_loading_iv.setAnimation(rotateAnimation);
            play_loading_iv.setVisibility(View.VISIBLE);
            itemposition=itemposition+1;
            progress=0;
            refresh();
        }

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e("jxf","onErrorListener扑捉到:::what="+what+"...extra="+extra);
        switch (what)
        {
            case MediaPlayer.MEDIA_INFO_UNKNOWN:
                DefinedSingleToast.showToast(this,"MEDIA_INFO_UNKNOWN");
                break;
            case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                DefinedSingleToast.showToast(this,"MEDIA_INFO_BAD_INTERLEAVING");
                break;
            case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                DefinedSingleToast.showToast(this,"MEDIA_INFO_METADATA_UPDATE");
                break;
            case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                DefinedSingleToast.showToast(this,"MEDIA_INFO_NOT_SEEKABLE");
                break;
            case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                DefinedSingleToast.showToast(this,"MEDIA_INFO_VIDEO_TRACK_LAGGING");
                break;
        }
        return false;
    }

    //监控电话状态  空闲：平时 。 响铃 。摘机：接电话;挂了不接电话：进入空闲：使用服务：进入后台：启动服务

    class MyListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.e("jxf","空闲");
                    //如果已进入就是空闲：响铃和摘机都不是空闲状态
                    //接到电话后改变了
                    if (!isCallIdle){
                        if (ispause){
                            Log.e("jxf","来电返回：：：：之前就是暂停状态，不做任何处理");
                        }
                        else {
                            Log.e("jxf","来电返回：：：：之前就是播放状态，做处理");
                            callContinue();
                        }
                    }
                    isCallIdle=true;
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.e("jxf", "响铃");
                    isCallIdle=false;
//                    ispause = true;
                    if (ispause){

                    }else {
                        callPause();
                    }

//                    music_paly_orpause.setImageResource(R.drawable.content_video_suspend);
                    break;
//                case TelephonyManager.CALL_STATE_OFFHOOK:
//                    Log.e("jxf", "摘机");
//                    isCallIdle=false;
//                    ispause = true;
//                    pause();
//                    music_paly_orpause.setImageResource(R.drawable.content_video_suspend);
//                    break;

            }
        }
    }


}
