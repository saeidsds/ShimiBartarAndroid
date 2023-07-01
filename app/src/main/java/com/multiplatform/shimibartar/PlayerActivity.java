package com.multiplatform.shimibartar;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.multiplatform.helper.Constant;
import com.multiplatform.helper.EncryptedFileDataSourceFactory;
import com.multiplatform.helper.MultiplatformHelper;
import com.multiplatform.model.PostObject;

import java.io.File;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.google.android.exoplayer2.ui.PlayerView.SHOW_BUFFERING_NEVER;

public class PlayerActivity extends AppCompatActivity {


    SharedPreferences sp ;
    SharedPreferences.Editor spe;
    Context ctx;
    String webservice="";
    PostObject data=new PostObject();
    PlayerView video_view ;
    SimpleExoPlayer video_player;


    Cipher cipher ;
    SecretKeySpec keySpec;
    IvParameterSpec ivSpec;
    DefaultBandwidthMeter bandwidthMeter;
    DataSource.Factory dfs;
    float current_speed = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_layout);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        //toolbar.setNavigationIcon(R.drawable.ic_back_icon);
        ctx=this;
        sp= getSharedPreferences("init", Activity.MODE_PRIVATE);
        spe = sp.edit();
        webservice= Constant.get_webservice();
        data=new Gson().fromJson(getIntent().getStringExtra("data"), PostObject.class);


        init();





    }




    public void init(){

        video_view = findViewById(R.id.video_view);
        DefaultTrackSelector trackSelector = new DefaultTrackSelector(ctx);
        DefaultLoadControl loadControl = new DefaultLoadControl();
        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(ctx);

        SimpleExoPlayer.Builder player_builder = new SimpleExoPlayer.Builder(ctx);
        player_builder.setTrackSelector(trackSelector);
        player_builder.setLoadControl(loadControl);
        video_player = player_builder.build();
        video_view.setPlayer(video_player);
        //video_view.setFastForwardIncrementMs(10000);

        if(isDestroyed()==false)
        {
            Glide.with(ctx)
                    .asBitmap()
                    .load(data.cover_image)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            if(resource!=null)
                            {
                                video_view.setDefaultArtwork(new BitmapDrawable(getResources(), resource));
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {

                        }
                    });
        }

        try {
            if(cipher==null || keySpec==null || ivSpec==null || bandwidthMeter==null || dfs==null)
            {
                cipher = Cipher.getInstance("AES/CTR/NoPadding");
                keySpec = new SecretKeySpec("0123456789012345".getBytes(), "AES");
                ivSpec = new IvParameterSpec("0123459876543210".getBytes());
                cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
                bandwidthMeter = new DefaultBandwidthMeter.Builder(ctx).build();
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, ctx.getPackageName()));
                dfs = new EncryptedFileDataSourceFactory(dataSourceFactory.createDataSource(),cipher, keySpec, ivSpec, bandwidthMeter);
            }
            ProgressiveMediaSource item = null ;

            final String src_path = MultiplatformHelper.get_directory(ctx)+"/"+data.post_id+"_"+data.file_name;
            if(new File(src_path).exists()==false){ // show online
                video_view.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS);
                Uri uri = Uri.parse(data.file_link);
                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, ctx.getPackageName());
                item = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            }else{
                video_view.setShowBuffering(SHOW_BUFFERING_NEVER);
                item = new ProgressiveMediaSource.Factory(dfs).createMediaSource(Uri.parse(src_path));
            }



            video_player.addListener(new Player.EventListener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (playbackState == ExoPlayer.STATE_ENDED) {
                        if(video_player!=null)
                        {}
                    }
                }


            });


            video_player.setPlayWhenReady(true);
            video_player.prepare(item, true, true);
        }catch (Exception e){

        }

        video_view.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                if(visibility == View.VISIBLE){
                    findViewById(R.id.speed_label).setVisibility(View.VISIBLE);
                    findViewById(R.id.speed_tv).setVisibility(View.VISIBLE);
                }else{
                    findViewById(R.id.speed_label).setVisibility(View.GONE);
                    findViewById(R.id.speed_tv).setVisibility(View.GONE);
                }
            }
        });
        findViewById(R.id.speed_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current_speed = current_speed + 0.20f;
                if(current_speed>2.1f)
                    current_speed = 0.8f;

                try {
                    if(video_player!=null && video_player.isPlaying())
                    {
                        PlaybackParameters param = new PlaybackParameters(current_speed);
                        video_player.setPlaybackParameters(param);
                        String temp_str ="";
                        try {
                            temp_str = String.format("%.1f", current_speed)+"X";
                        }catch (Exception e){
                            temp_str = ((float)current_speed)+"X";
                        }

                        ((TextView)findViewById(R.id.speed_tv)).setText(temp_str);
                    }

                }catch (Exception e){}
            }
        });

    }










    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(video_player!=null)
        {
            video_player.setPlayWhenReady(false);
            video_player.release();
            video_player=null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(video_player!=null)
        {
            video_player.setPlayWhenReady(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(video_player!=null)
        {
            video_player.setPlayWhenReady(false);
            video_player.release();
            video_player=null;
        }
    }
}
