package com.multiplatform.shimibartar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.multiplatform.helper.NewAsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.multiplatform.helper.Constant;
import com.multiplatform.helper.MultiplatformHelper;
import com.multiplatform.helper.Parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterCodeActivity extends AppCompatActivity {


    SharedPreferences sp ;
    SharedPreferences.Editor spe;
    Context ctx;
    String webservice="";
    EditText code_et;
    int max_sec_to_wait = 30 ;
    Handler time_handler ;
    String current_code ="";
    boolean refreshing=false ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_code_layout);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("");
        ctx=this;
        sp= getSharedPreferences("init", Activity.MODE_PRIVATE);
        spe = sp.edit();
        webservice= Constant.get_webservice();
        code_et = ((EditText) findViewById(R.id.code_et));
        //code_et.setText(getIntent().getStringExtra("code"));
        current_code=getIntent().getStringExtra("code");



        findViewById(R.id.next_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(current_code.equals(code_et.getText().toString().trim()))
                    {
                        if(refreshing==false)
                            new asynctask_login().execute();
                    }
                    else
                    {
                        show_msg(ctx.getString(R.string.code_error),"red");
                    }
                }catch (Exception e){
                    show_msg(ctx.getString(R.string.code_error),"red");
                }


            }
        });

        findViewById(R.id.retry_sms_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(can_send_sms()==true)
                {
                    new asynctask_send_sms().execute();
                }
            }
        });


        time_handler =new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                set_retry_btn(can_send_sms());
                if(can_send_sms()==false)
                {
                    time_handler.sendEmptyMessageDelayed(0,1000);
                }
            }
        };

    }



    public void set_retry_btn(boolean state )
    {
        if(state == true )
        {
            findViewById(R.id.retry_sms_btn).setBackgroundResource(R.drawable.btn_background_red);
            ((TextView)findViewById(R.id.time_tv)).setText("");
        }
        else
        {

            long temp = sp.getLong("time_to_send",0);
            temp = System.currentTimeMillis()-temp ;
            temp = temp/1000 ;
            temp = max_sec_to_wait - temp ;
            int temp2 = (int)temp;
            findViewById(R.id.retry_sms_btn).setBackgroundResource(R.drawable.btn_background_gray_diactive);
            ((TextView)findViewById(R.id.time_tv)).setText(getString(R.string.time_to_send_sms).replace("***",temp2+""));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(can_send_sms()==true)
        {
            set_retry_btn(true);
        }
        else
        {
            set_retry_btn(false);
            time_handler.sendEmptyMessageDelayed(0,1000);
        }


    }


    @Override
    protected void onPause() {
        super.onPause();
        if(time_handler !=null)
            time_handler.removeCallbacksAndMessages(null);
    }


    public  boolean can_send_sms()
    {
        boolean result = false ;
        long temp = sp.getLong("time_to_send",0);
        temp = System.currentTimeMillis()-temp ;
        temp = temp/1000 ;
        if(temp>max_sec_to_wait)
        {
            result = true ;
        }
        else
        {
            result = false ;
        }
        return result ;
    }





    public class asynctask_login extends NewAsyncTask<Boolean,Boolean,Boolean> {


        int error=0;
        @Override
        protected Boolean doInBackground(Boolean... params) {



            String temp="";

            temp=postRequest("2");

            if(temp==null)
            {

                error=1;
                return false;
            }
            temp= Parser.get_json(temp);
            if(temp!=null && !temp.trim().equals(""))
            {
                JSONArray jArray = null ;
                JSONObject json_obj=null;
                try {

                    json_obj = new JSONObject(temp);
                    String temp_result=json_obj.getString("result");
                    try {
                        // ============= user banned
                        if(json_obj.getString("user_block").equals("1") || json_obj.getString("user_block").equals("true"))
                        {
                            error = 5 ;
                            return true ;
                        }
                    }catch (Exception e){}
                    if(temp_result.trim().equals("true"))
                    {
                        spe.putString("user_id",json_obj.getString("user_id")).commit();
                        spe.putString("name",json_obj.getString("user_name")).commit();
                        spe.putString("mobile",json_obj.getString("user_mobile")).commit();
                        spe.putString("school_type",json_obj.getString("school_type")).commit();
                        spe.putString("gender",json_obj.getString("gender")).commit();
                        spe.putString("class_type",json_obj.getString("class_type")).commit();
                        spe.putString("total_buy",json_obj.getString("total_buy")).commit();
                        spe.putString("location",json_obj.getString("location")).commit();
                        spe.putString("email",json_obj.getString("email_app")).commit();
                        spe.putString("min_price_for_question",json_obj.getString("min_price_for_question")).commit();
                        spe.putString("public_comment_post_id",json_obj.getString("public_comment_post_id")).commit();


                        spe.putBoolean("login",true).commit();
                        return true;
                    }
                    else
                    {
                        error=2; // user not exist then register that
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

        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                refreshing=true ;
                findViewById(R.id.register_loading).setVisibility(View.VISIBLE);
            }catch (Exception e)
            {}


        }



        @Override
        protected void onPostExecute(Boolean result) {

            refreshing=false ;
            findViewById(R.id.register_loading).setVisibility(View.GONE);
            if(error==1)
            {
                show_msg(ctx.getString(R.string.connection_error),"red");
            } else if(error==3)
            {
                show_msg(ctx.getString(R.string.programm_error),"red");
            }
            else if(error==2 )
            {
                Intent intent = new Intent(ctx, RegisterActivity.class);
                intent.putExtra("mobile",getIntent().getStringExtra("mobile"));
                startActivity(intent);
                finish();
                //show_msg(ctx.getString(R.string.error_login),"red");
            }
            else if(error==5 ) // ========= user banned
            {
                show_msg(ctx.getString(R.string.user_banned),"red");
            }
            else if(error==6 ) // ========= imei error
            {
                show_msg(ctx.getString(R.string.not_active_on_device),"red");
            }

            else if(error==0 || error==4)
            {
                show_toast(ctx.getString(R.string.login_successfull),"green");
                startActivity(new Intent(ctx, HomeActivity.class));
                ((Activity)ctx).finishAffinity();
            }

        }




    }



    public class asynctask_send_sms extends NewAsyncTask<Boolean,Boolean,Boolean> {


        int error=0;
        String message = "" ;
        String errorCode="0";
        @Override
        protected Boolean doInBackground(Boolean... params) {



            String temp="";
            temp=postRequest("3");

            if(temp==null)
            {

                error=1;
                return false;
            }
            temp= Parser.get_json(temp);
            if(temp!=null && !temp.trim().equals(""))
            {
                JSONArray jArray = null ;
                JSONObject json_obj=null;
                try {

                    json_obj = new JSONObject(temp);
                    String temp_result=json_obj.getString("result");
                    if(temp_result.trim().equals("true"))
                    {
                        current_code=json_obj.getString("code");
                        error = 0 ;
                        return true;
                    }
                    else
                    {
                        error=2; // server error
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

        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {

                findViewById(R.id.loading_retry_sms).setVisibility(View.VISIBLE);
            }catch (Exception e)
            {}


        }



        @Override
        protected void onPostExecute(Boolean result) {


            findViewById(R.id.loading_retry_sms).setVisibility(View.GONE);
            if(error==1)
            {
                show_msg(ctx.getString(R.string.connection_error),"red");
            } else if(error==3)
            {
                show_msg(ctx.getString(R.string.programm_error),"red");
            }
            else if(error==2)
            {
                show_msg(ctx.getString(R.string.register_errro),"red");
            }
            else if(error==0)
            {
                spe.putLong("time_to_send",System.currentTimeMillis()).commit();
                show_toast(ctx.getString(R.string.sms_will_be_send),"green");
                if(can_send_sms()==true)
                {
                    set_retry_btn(true);
                }
                else
                {
                    set_retry_btn(false);
                    time_handler.removeCallbacksAndMessages(null);
                    time_handler.sendEmptyMessageDelayed(0,1000);
                }
            }

        }




    }




    public String postRequest(String num)
    {

        RequestBody formBody=null;
        FormBody.Builder fb = new FormBody.Builder();



        fb.add("num",num);
        fb.add("user_mobile",getIntent().getStringExtra("mobile"));
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








    public void show_toast(String msg,String color)
    {
        //Toast.makeText(ctx,msg,Toast.LENGTH_SHORT).show();
        MultiplatformHelper.show_toast(msg,color,ctx);
    }

    public void show_msg(String msg,String color)
    {
        MultiplatformHelper.show_msg(msg,color,ctx,findViewById(R.id.main_container));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
