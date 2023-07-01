package com.multiplatform.helper;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.content.ContextCompat;
import android.util.Base64;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.multiplatform.shimibartar.R;
import com.multiplatform.model.PostObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import es.dmoral.toasty.Toasty;

public class MultiplatformHelper {






  public   static String get_image_dir(Context ctx)
  {
    try {
      File directory = new File(ctx.getExternalFilesDir(null)+ File.separator+"MultiPlatform");
      if(!directory.exists())
        directory.mkdirs();
    }catch (Exception e){}

    return ctx.getExternalFilesDir(null)+ File.separator+"MultiPlatform";
  }


  public  static String get_image_path(Context ctx)
  {
    return ctx.getExternalFilesDir(null) + File.separator + "MultiPlatform/temp.jpeg";
  }




  public  static void savefile(Context ctx, Uri sourceuri)
  {
    File directory = new File(get_image_dir(ctx));
    if(!directory.exists())
      directory.mkdirs();
    File file = new File(get_image_path(ctx));
    if(file.exists())
    {
      file.delete();
    }

    String sourceFilename= getRealPathFromURI(ctx,sourceuri);
    String destinationFilename = get_image_path(ctx);

    BufferedInputStream bis = null;
    BufferedOutputStream bos = null;

    try {
      bis = new BufferedInputStream(new FileInputStream(sourceFilename));
      bos = new BufferedOutputStream(new FileOutputStream(destinationFilename, false));
      byte[] buf = new byte[1024];
      bis.read(buf);
      do {
        bos.write(buf);
      } while(bis.read(buf) != -1);
    } catch (IOException e) {

    } finally {
      try {
        if (bis != null) bis.close();
        if (bos != null) bos.close();
      } catch (IOException e) {

      }
    }


  }





  public  static String getRealPathFromURI(Context ctx, Uri contentUri) {
    String[] proj = { MediaStore.Images.Media.DATA };
    Cursor cursor = ((Activity) ctx).getContentResolver().query(contentUri, proj, null, null, null);
    String result = "";
    if(cursor.moveToFirst()){
      int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
      result = cursor.getString(column_index);
    }
    cursor.close();
    return result;
  }




  public static Bitmap get_bitmap_from_path(String path)
  {
    BitmapFactory.Options options = new BitmapFactory.Options();
    //options.inPreferredConfig = Bitmap.Config.ARGB_8888;
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(path, options);
    return reduce_size(options.outWidth,options.outHeight,path);
  }











  public static boolean save_bitmap(Context ctx, Bitmap src_bitmap, String dest, boolean is_square)
  {
    File directory = new File(get_image_dir(ctx));
    if(!directory.exists())
      directory.mkdirs();
    File file = new File(dest);
    if(file.exists())
    {
      file.delete();
    }


    Bitmap pic_croped=null;
///====================================== make square
   if(is_square)
   {
     try {
       if (src_bitmap.getWidth() >= src_bitmap.getHeight()){

         pic_croped = Bitmap.createBitmap(
                 src_bitmap,
                 src_bitmap.getWidth()/2 - src_bitmap.getHeight()/2,
                 0,
                 src_bitmap.getHeight(),
                 src_bitmap.getHeight()
         );

       }else{

         pic_croped = Bitmap.createBitmap(
                 src_bitmap,
                 0,
                 src_bitmap.getHeight()/2 - src_bitmap.getWidth()/2,
                 src_bitmap.getWidth(),
                 src_bitmap.getWidth()
         );
       }
     }
     catch (Exception e)
     {
       pic_croped=src_bitmap;
     }
     if (pic_croped==null)
     {
       pic_croped=src_bitmap;
     }
   }
   else
   {
     pic_croped=src_bitmap;
   }
    ///====================================== make square
    
    
    
    FileOutputStream out = null;
    try {

      out = new FileOutputStream(dest);
      pic_croped.compress(Bitmap.CompressFormat.JPEG, 70, out); // bmp is your Bitmap instance



    } catch (Exception e) {
      e.printStackTrace();


      return false;

    } finally {
      try {
        if (out != null) {
          out.close();
        }
      } catch (IOException e) {
        e.printStackTrace();

        return false;
      }
    }

    return true;


  }




  public  static Bitmap reduce_size(double w, double h, String path)
  {

    double pixel=(h*w)/3;
    int sample_rate=1;
    for(int i=1;i<=100;i++)
    {

      if((pixel/i)<=500000)
      {
        sample_rate=i;
        break ;
      }
    }


    BitmapFactory.Options options = new BitmapFactory.Options();

    options.inJustDecodeBounds = false;
    options.inSampleSize=sample_rate;
    return BitmapFactory.decodeFile(path, options);
  }












  public static void show_toast(String msg,String color,Context ctx)
  {
    if(color.equals("red"))
    {
      Toasty.error(ctx, msg, Toast.LENGTH_SHORT, true).show();
    }
    else if(color.equals("green"))
    {
      Toasty.success(ctx, msg, Toast.LENGTH_SHORT, true).show();
    }
    else
    {
      Toasty.normal(ctx, msg, Toast.LENGTH_SHORT).show();
    }
  }



  public static void show_msg(String msg,String color,Context ctx , View view )
  {
    Snackbar msg_snack= Snackbar.make(view, msg, Snackbar.LENGTH_LONG).setAction("Action", null);
    if(color.equals("red"))
      msg_snack.getView().setBackgroundColor(ContextCompat.getColor(ctx, R.color.material_red));
    if(color.equals("green"))
      msg_snack.getView().setBackgroundColor(ContextCompat.getColor(ctx, R.color.material_green));
    msg_snack.show();
  }






  public static String get_price(String input)
  {
    String result="";


    if(input==null)
      input="";
    String temp=input.trim();
    temp=temp.replace(",", "");

    if(temp.length()>3)
    {
      int num=0;
      for(int i=temp.length();i>0;i--)
      {
        if(num==3)
        {
          num=0;
          result=","+result;
        }
        num++;

        result=temp.substring(i-1, i)+result;
      }

    }else
    {
      result=temp.replace(",", "");

    }



    //result=input;//============= tempreary
    return result;
  }


  static public String get_directory (Context ctx)
  {
    String path = ctx.getExternalFilesDir(null) + File.separator + "files";

    File directory = new File(path);
    try {
      if(directory.exists())
      {
        directory.delete();
        directory.mkdir();
      }
    }catch (Exception e){}
    return path;
  }
  //================================================


















  public static String base64encode(String normal)
  {
    String encode_str="";
    try {
      byte[] data = normal.getBytes("UTF-8");
      //byte[] data = normal.getBytes("ISO-8859-1");

      encode_str = Base64.encodeToString(data, Base64.NO_WRAP);
    }catch (Exception e)
    {
      encode_str=normal;
    }
    return encode_str;
  }




  public static String base64decode(String base64)
  {
    String decode_str="";
    try {
      byte[] data = Base64.decode(base64, Base64.NO_WRAP);
      decode_str = new String(data, "UTF-8");
      //decode_str = new String(data, "ISO-8859-1");
    }catch (Exception e)
    {
      decode_str=base64;
    }
    return decode_str;
  }
































  public static  void open_url(Context ctx,String link)
  {
    try {
      Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
      ctx.startActivity(myIntent);
    }catch (Exception e)
    {
      int i=0;
      i++ ;
    }
  }














  // ============================================================
// ============================================================
  // ============================================================
  // ============================================================
  // ============================================================


  public static boolean copyFile(String from, String to) {
    try {
      File sd = Environment.getExternalStorageDirectory();
      if (sd.canWrite()) {
        int end = from.toString().lastIndexOf("/");

        File source = new File(from);
        File destination= new File(to);
        if (source.exists()) {
          FileChannel src = new FileInputStream(source).getChannel();
          FileChannel dst = new FileOutputStream(destination).getChannel();
          dst.transferFrom(src, 0, src.size());
          src.close();
          dst.close();
        }
      }
      return true;
    } catch (Exception e) {
      return false;
    }
  }


























  public static String get_number_str(String number_str)
  {
    String result = number_str ;
    try {

      long temp = Long.valueOf(number_str.trim());
      if(temp>999999)
      {
        temp = temp/1000000 ;
        result = ((int)temp)+"M";
      }
      else if(temp>999)
      {
        temp = temp/1000 ;
        result = ((int)temp)+"K";
      }
      else
      {
        result =number_str ;
      }
    }catch (Exception e){}


    return result;
  }



  public static int get_file_type_drawable_id(Context ctx ,String type)
  {
    int drawable_id = 0;
    if(type.equals("video"))
      drawable_id=R.drawable.video_icon;
    else if(type.equals("audio"))
      drawable_id=R.drawable.audio_icon;
    else if(type.equals("pdf"))
      drawable_id=R.drawable.pdf_icon;
    else if(type.equals("image"))
      drawable_id=R.drawable.image_icon;
    else
      drawable_id=R.drawable.video_icon;
    return drawable_id;
  }

  public static String get_file_type_str(Context ctx ,String type)
  {
    String type_name = "";
    if(type.equals("video"))
      type_name=ctx.getString(R.string.video);
    else if(type.equals("audio"))
      type_name=ctx.getString(R.string.audio);
    else if(type.equals("pdf"))
      type_name=ctx.getString(R.string.pdf);
    else if(type.equals("image"))
      type_name=ctx.getString(R.string.image);
    else
      type_name=ctx.getString(R.string.video);
    return type_name;
  }



  public static boolean is_buy(Context ctx ,PostObject data)
  {
    boolean result = false;
    SharedPreferences sp= ctx.getSharedPreferences("init", Activity.MODE_PRIVATE);
    SharedPreferences.Editor spe = sp.edit();
    if(sp.getString("user_packages","").contains("*"+data.post_id+"*") || data.package_type.equals("free"))
    {
      result = true;
    }

    return result;
  }

  public static String get_buy_string(Context ctx ,PostObject data)
  {
    String result = "";
    SharedPreferences sp= ctx.getSharedPreferences("init", Activity.MODE_PRIVATE);
    SharedPreferences.Editor spe = sp.edit();
    if(data.package_type.equals("free")){
      result = ctx.getString(R.string.free);
    }else{
      if(is_buy(ctx,data))
        result = ctx.getString(R.string.buyed);
      else
        result =  ctx.getString(R.string.buy);
    }
    return result;
  }




  public static String get_reagent_code(Context ctx)
  {
    String result = "";
    SharedPreferences sp= ctx.getSharedPreferences("init", Activity.MODE_PRIVATE);
    SharedPreferences.Editor spe = sp.edit();
    try {
      result = (Integer.valueOf(sp.getString("user_id","0"))+1000)+"";
    }catch (Exception e){}
    return result;
  }


  public static void animate_arrow_buttom(Context ctx,View view)
  {
    try {
      ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0, 20,0);
      objectAnimator.setDuration(1500);
      objectAnimator.setRepeatCount(Animation.INFINITE);
      objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
      objectAnimator.start();
    }catch (Exception e){}

  }

}



