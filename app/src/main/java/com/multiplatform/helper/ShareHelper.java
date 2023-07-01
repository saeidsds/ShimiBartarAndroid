package com.multiplatform.helper;

import android.app.Activity;
import android.content.Context;
import com.multiplatform.helper.Constant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import com.multiplatform.helper.NewAsyncTask;
import android.os.Build;
import android.view.View;

import androidx.core.content.FileProvider;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.File;

public class ShareHelper extends NewAsyncTask<Boolean, Boolean, Boolean> {

  SharedPreferences sp ;
  SharedPreferences.Editor spe;
  Context ctx;
  String link="",text;
  View loading,btn;
  static ImageLoader imageLoader ;
  String path="" ;
  boolean is_image_file=true;
  boolean force_download=true ;



  public ShareHelper(Context ctx, String file_link_path, String text, View loading, View btn,boolean is_image_file,boolean force_download)
  {
    this.ctx=ctx;
    sp= ctx.getSharedPreferences("init", Activity.MODE_PRIVATE);
    spe = sp.edit();
    this.link=file_link_path;
    this.text=text;
    this.loading=loading;
    this.btn=btn;
    this.is_image_file=is_image_file;
    this.force_download=force_download;
    if(force_download==false)
    {
      path=file_link_path;
    }

    try {
      new File(MultiplatformHelper.get_image_dir(ctx) + "/user_temp.jpg").delete();
    }catch (Exception e){}
  }

  @Override
  protected Boolean doInBackground(Boolean... params) {

    boolean result = false ;
    String temp="";

    if(is_image_file==true)
    {
      if(force_download==true)
      {
        try {
          Bitmap bitmap =imageLoader.loadImageSync(link);
          if(bitmap!=null)
          {
            result = MultiplatformHelper.save_bitmap(ctx,bitmap,path,false);
          }
          //result = MultiplatformHelper.download_image(ctx,link,path);
        }catch (Exception e){}
      }
      else
      {
        result=true;
      }

    }
    else  {

      if(force_download==true)
      {
        // download file ...
        result=true;
      }
      else
      {
        result=true;
      }

    }
    return true;
  }


  @Override
  protected void onPreExecute() {


    if(imageLoader==null)
    {
      ImageLoaderConfiguration config= new ImageLoaderConfiguration.Builder(ctx)
              .memoryCacheSize(41943040)
              .diskCacheSize(104857600)
              .threadPoolSize(10)
              .build();
      imageLoader= ImageLoader.getInstance();
      imageLoader.init(config);
    }
    if(force_download==true) {
      path = MultiplatformHelper.get_image_dir(ctx) + "/image.jpg";
    }
    if(loading!=null)
      loading.setVisibility(View.VISIBLE);
    if(btn!=null)
      btn.setVisibility(View.GONE);
    super.onPreExecute();
    }






  @Override
  protected void onPostExecute(Boolean result) {
    if(loading!=null)
      loading.setVisibility(View.GONE);
    if(btn!=null)
      btn.setVisibility(View.VISIBLE);

    if(new File(path).exists() )
    {
      share_with_file();
    }
    else
    {
      share_text();
    }



  }




  private void share_with_file()
  {
    try {

      Intent sharingIntent = new Intent(Intent.ACTION_SEND);
      Uri fileUri = null ;
      if(Build.VERSION.SDK_INT>=24){
          fileUri = FileProvider.getUriForFile(ctx, ctx.getApplicationContext().getPackageName() + ".com.multiplatform.shimibartar", new File(path));
      }
      else {
          fileUri= Uri.fromFile(new File(path));
      }
      sharingIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
      sharingIntent.setType("text/html");
      sharingIntent.putExtra(Intent.EXTRA_TEXT, text);
      ctx.startActivity(Intent.createChooser(sharingIntent,"Share"));

    }catch (Exception e){
    }

  }


  private void share_text()
  {
    Intent shareIntent = new Intent();
    shareIntent.setAction(Intent.ACTION_SEND);
    shareIntent.putExtra(Intent.EXTRA_TEXT, text);
    shareIntent.setType("text/html");
    ctx.startActivity(Intent.createChooser(shareIntent, "Share"));
  }




  public static void share_text(Context ctx , String text)
  {
    try {
      Intent shareIntent = new Intent();
      shareIntent.setAction(Intent.ACTION_SEND);
      shareIntent.putExtra(Intent.EXTRA_TEXT, text);
      shareIntent.setType("text/html");
      ctx.startActivity(Intent.createChooser(shareIntent, "Share"));
    }catch (Exception e){}

  }














}