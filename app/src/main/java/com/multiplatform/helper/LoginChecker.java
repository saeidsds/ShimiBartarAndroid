package com.multiplatform.helper;

import android.app.Activity;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import com.multiplatform.helper.NewAsyncTask;


import com.multiplatform.shimibartar.SplashActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginChecker {

  SharedPreferences sp ;
  SharedPreferences.Editor spe;
  Context ctx;
  String webservice="";
  boolean refreshing=false;

  public LoginChecker(Context ctx)
  {
    this.ctx=ctx;
    sp= ctx.getSharedPreferences("init", Activity.MODE_PRIVATE);
    spe = sp.edit();
    webservice=
            Constant.get_webservice();
  }




  public void check_login()
  {
    if(refreshing==false)
    {
      new asynctask().execute();
    }
  }









  public class asynctask extends NewAsyncTask<Boolean,Boolean,Boolean> {


    //public boolean gcm_result=false ;
    int error=0;

    @Override
    protected Boolean doInBackground(Boolean... params) {

      String temp="";
      temp=postRequest(40);
      if(temp==null)
      {

        error=1;
        return false;
      }

      if(temp!=null && !temp.trim().equals(""))
      {
        JSONArray jArray = null ;
        try {

          JSONObject json_obj = new JSONObject(temp);
          String temp_result=json_obj.getString("result");
          if(temp_result.trim().equals("false"))
          {

            error=2;
            return false;
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
      return true;
    }


    @Override
    protected void onPreExecute() {
      refreshing=true;
      super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Boolean result) {

      refreshing=false;
      if(error==2 )
      {
        logout();
      }

    }

  }



  public void logout()
  {

    spe.clear().commit();
    Intent intent = new Intent(ctx, SplashActivity.class);
    ctx.startActivity(intent);
    ((Activity)ctx).finishAffinity();
  }










  public String postRequest(int num)
  {

    FormBody.Builder fb =new FormBody.Builder();
    fb.add("num", num+"" );
    fb.add("password", sp.getString("pass",""));
    fb.add("email", sp.getString("email","") );
    RequestBody formBody =fb.build();
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
      }
      else
        return result;

    }catch (Exception e)
    {
      return null ;
    }

  }


}

