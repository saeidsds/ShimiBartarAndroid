package com.multiplatform.helper;

import android.app.Activity;
import android.content.Context;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.multiplatform.helper.NewAsyncTask;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FireBaseTokenSender {

  public FireBaseTokenSender(final Context ctx)
  {
    try {


      FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
        @Override
        public void onComplete(@NonNull Task<String> task) {
          if (!task.isSuccessful()) {
            return;
          }
          // Get new FCM registration token
          String token = task.getResult();
          if(token!=null)
            new TokenSenderAsync(ctx,token).execute();
        }
      });
    }catch (Exception e){}
  }

  public class TokenSenderAsync extends NewAsyncTask<Boolean, Boolean, Boolean>{
    SharedPreferences sp ;
    SharedPreferences.Editor spe;
    Context ctx;
    String webservice="";
    String token = "";




    public TokenSenderAsync(Context ctx,String token)
    {
      this.ctx=ctx;
      sp= ctx.getSharedPreferences("init", Activity.MODE_PRIVATE);
      spe = sp.edit();
      webservice=Constant.get_webservice();
      this.token=token ;

    }
    //public boolean gcm_result=false ;
    int error=0;

    @Override
    protected Boolean doInBackground(Boolean... params) {

      String temp="";
      temp=postRequest(token);


      return true;
    }


    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      set_topic();
    }

    @Override
    protected void onPostExecute(Boolean result) {}




    public String postRequest(String token)
    {

      RequestBody formBody=null;
      FormBody.Builder fb = new FormBody.Builder();


      fb.add("num","18");
      fb.add("user_id",sp.getString("user_id",""));

      fb.add("token",token);
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
          //return convertStandardJSONString(result);
          return result;
        }
        else
          return result;

      }catch (Exception e)
      {
        return null ;
      }



    }



    public void set_topic()
    {
      FirebaseMessaging.getInstance().subscribeToTopic("com_multiplatform_shimibartar_android2");
    }

  }






}