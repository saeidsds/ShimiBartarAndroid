package com.multiplatform.shimibartar;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.multiplatform.helper.Constant;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import com.multiplatform.helper.NewAsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.multiplatform.helper.FireBaseTokenSender;
import com.multiplatform.helper.Parser;
import com.multiplatform.helper.SSLCertificateHandler;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SplashActivity extends AppCompatActivity {



    SharedPreferences sp ;
    SharedPreferences.Editor spe;
    Context ctx;
    String webservice="";
    Handler handler;
    ImageView iv;
    String  update_link = "";
    boolean opr1=false,opr2=false;
    int dialog_error = -1 ;
    String fcm_token = "" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
        ctx=this;
        sp= getSharedPreferences("init", Activity.MODE_PRIVATE);
        spe = sp.edit();
        webservice=Constant.get_webservice();
        getSupportActionBar().hide();
        new FireBaseTokenSender(ctx);

        //getSupportActionBar().hide();
        //============ init =======================
        iv=(ImageView) findViewById(R.id.image_view);
        handler= new Handler(Looper.getMainLooper()){
            public void handleMessage(Message msg){
                opr1 = true ;
                if(opr1==true  && opr2==true)
                    goto_next();
            }
        };



//        try {
//            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
//            Date expire_date = sdf.parse("2020-08-05");
//            Date c = Calendar.getInstance().getTime();
//            String current_str = sdf.format(new Date());
//            if(expire_date.before(c))
//            {
//                Toast.makeText(ctx,"زمان استفاده از اپلیکیشن منقضی شده است",Toast.LENGTH_LONG).show();
//                finish();
//                return;
//            }
//        }catch (Exception e){}
        SSLCertificateHandler.nuke();
        Glide.with(ctx)
                .load(sp.getString("master_image",""))
                .placeholder(R.drawable.def_master_image)
                .error(R.drawable.def_master_image)
                .listener(new RequestListener<Drawable>() {

                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        int i = 0 ;
                        i++ ;
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(((ImageView)findViewById(R.id.master_image)));

        ((TextView)findViewById(R.id.master_name)).setText(sp.getString("master_name",getString(R.string.morteza_mohammadi)));


        handler.sendEmptyMessageDelayed(1,4000);
        new asynctask_get_data().execute();



        try {


            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        return;
                    }
                    // Get new FCM registration token
                    fcm_token = task.getResult();
                    if(fcm_token==null)
                        fcm_token="";
                }
            });
        }catch (Exception e){}

        try {
            PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = pinfo.versionName;
            ((TextView)findViewById(R.id.version_tv)).setText(getString(R.string.version)+" "+versionName);

        }catch (Exception e)
        {}




    }


    public void goto_next()
    {
        if(sp.getBoolean("login",false)==false)
        {
            startActivity(new Intent(ctx,RegisterSmsActivity.class));
        }
        else
        {
            Intent intent =new Intent(ctx, HomeActivity.class);
            startActivity(intent);

        }
        SplashActivity.this.finish();

    }

    public class asynctask_get_data extends NewAsyncTask<Boolean,Boolean,Boolean> {


        int error =0 ;// error =0  no error     error =1 connection error              error=3 parse error


        @Override
        protected Boolean doInBackground(Boolean... params) {




            String temp=postRequest();
            if(temp==null || temp.trim().equals(""))
            {
                error=1;
                return false;
            }
            temp= Parser.get_json(temp);
            if(temp!=null && !temp.trim().equals(""))
            {

                try {

                    JSONObject oneObject = new JSONObject(temp);
                    String temp_result=oneObject.getString("result").trim();
                    if(temp_result.equals("true"))
                    {
                        try {
                            spe.putString("master_image",oneObject.getString("master_image")).commit();
                            spe.putString("master_name",oneObject.getString("master_name")).commit();
                        }catch (Exception e){}
                        try {
                            // ============= user banned
                            if(sp.getBoolean("login",false))
                            {
                                if(oneObject.getString("user_block").equals("1") || oneObject.getString("user_block").equals("true"))
                                {
                                    error = 5 ;
                                    return true ;
                                }
                            }

                        }catch (Exception e){}

                        try {

                            spe.putString("name",oneObject.getString("user_name")).commit();
                            spe.putString("mobile",oneObject.getString("user_mobile")).commit();
                            spe.putString("school_type",oneObject.getString("school_type")).commit();
                            spe.putString("gender",oneObject.getString("gender")).commit();
                            spe.putString("class_type",oneObject.getString("class_type")).commit();
                            spe.putString("location",oneObject.getString("location")).commit();
                            spe.putString("email",oneObject.getString("email_app")).commit();
                            spe.putString("total_buy",oneObject.getString("total_buy")).commit();
                            spe.putString("min_price_for_question",oneObject.getString("min_price_for_question")).commit();
                            spe.putString("public_comment_post_id",oneObject.getString("public_comment_post_id")).commit();

                            spe.putString("invite_message",oneObject.getString("invite_message")).commit();
                            spe.putString("share_app_message",oneObject.getString("share_app_message")).commit();

                            PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                            int update_version =Integer.valueOf(oneObject.getString("android_version"));
                            int current_version = pinfo.versionCode;
                            if(current_version<update_version)
                            {
                                update_link = oneObject.getString("android_link") ;
                                error = 7 ; // update dialog
                                return true ;
                            }
                        }catch (Exception e)
                        {}
                        return true ;
                    }
                    else
                    {
                        // ============= deleted user  or not accessible
                        error=6;
                        return false ;
                    }


                }
                catch (JSONException e) {
                    error=3;
                    return false;
                }

            }
            else
            {
                error=1;
                return false;
            }

        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.loading_cnt).setVisibility(View.VISIBLE);

        }



        @Override
        protected void onPostExecute(Boolean result) {

            findViewById(R.id.loading_cnt).setVisibility(View.GONE);
            dialog_error = error ;
            if(error == 0)
            {
                opr2 = true ;
                if(opr1==true  && opr2==true)
                    goto_next();
            }
            else if(error == 5) /// user banned
            {
                Toast.makeText(ctx,getString(R.string.user_banned),Toast.LENGTH_LONG).show();
                spe.clear().commit();
                Intent intent = new Intent(ctx, SplashActivity.class);
                startActivity(intent);
                finishAffinity();


            }
            else if(error == 6) // ============= delete user
            {
                //Toast.makeText(ctx,getString(R.string.not_active_on_device),Toast.LENGTH_LONG).show();
                spe.clear().commit();
                Intent intent = new Intent(ctx, SplashActivity.class);
                startActivity(intent);
                finishAffinity();
            }
            else
            {
                show_dialog();
            }


        }





    }



    public String postRequest()
    {

        RequestBody formBody=null;
        FormBody.Builder fb = new FormBody.Builder();
        String imei="";
        try {
            imei=Parser.getDeviceUniqueID(ctx);
            if(imei==null)
                imei="";
        }catch (Exception e)
        {}

        fb.add("user_id",sp.getString("user_id", ""));
        fb.add("num","1");
        fb.add("token",fcm_token);
        fb.add("device","0");



        formBody=fb.build();
        try {
            //final MediaType JSON= MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient();

            //RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(webservice)
                    .post(formBody)
                    .build();
            Response response = client.newCall(request).execute();
            String result=response.body().string();
            if(result!=null)
            {
                return result;
                //return result;
            }
            else
                return result;

        }catch (Exception e)
        {
            return null ;
        }



    }



    int begin=0;
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);

        begin++;
        if(begin<=1)
        {
            if(iv!=null) {
                iv.setVisibility(View.VISIBLE);
                findViewById(R.id.logo_cnt).setVisibility(View.VISIBLE);
                ObjectAnimator.ofFloat(findViewById(R.id.logo_cnt), "alpha", 0, 1).setDuration(1100).start();
            }

            //start_anim2();
        }

    }



    public void show_retry_dialog(final String msg)
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:


                        new asynctask_get_data().execute();
                        dialog.dismiss();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        ActivityCompat.finishAffinity(SplashActivity.this);
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        Dialog dialog=builder.setMessage(msg).setPositiveButton(getString(R.string.retry), dialogClickListener)
                .setNegativeButton(getString(R.string.exit), dialogClickListener).create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(final DialogInterface arg0) {
                //show_retry_dialog(msg);
            }
        });
        dialog.show();

        try {
            ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(ctx,R.color.colorPrimaryDark));
            ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ctx,R.color.colorPrimaryDark));
            ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(ContextCompat.getColor(ctx,R.color.colorPrimaryDark));
        }catch (Exception e)
        {}
    }

    public void show_update_dialog(final String link)
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:


                        try {
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(link));
                            startActivity(i);


                        } catch (Exception e) {
                            dialog.dismiss();
                            finish();
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        ActivityCompat.finishAffinity(SplashActivity.this);
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        Dialog dialog=builder.setMessage(getString(R.string.update_desc)).setPositiveButton(getString(R.string.accept), dialogClickListener)
                .setNegativeButton(getString(R.string.exit), dialogClickListener).create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(final DialogInterface arg0) {
                //show_update_dialog(link);
            }
        });
        dialog.show();
        try {
            ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(ctx,R.color.colorPrimaryDark));
            ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ctx,R.color.colorPrimaryDark));
            ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(ContextCompat.getColor(ctx,R.color.colorPrimaryDark));
        }catch (Exception e)
        {}
    }

    public void show_dialog()
    {
        if(dialog_error == 1 )
        {
            show_retry_dialog(getString(R.string.connection_error));
        }
        else if(dialog_error == 2)
        {
            show_retry_dialog(getString(R.string.programm_error));
        }
        else if(dialog_error == 3)
        {
            show_retry_dialog(getString(R.string.programm_error));
        }
        else if(dialog_error == 6)
        {
            Toast.makeText(ctx,getString(R.string.not_active_on_device),Toast.LENGTH_LONG).show();
            spe.clear().commit();
            Intent intent = new Intent(ctx, SplashActivity.class);
            startActivity(intent);
            finishAffinity();

        }
        else if(dialog_error== 7)
        {
            show_update_dialog(update_link);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(dialog_error!=-1  && isFinishing()==false)
        {
            show_dialog();
        }

    }
}



