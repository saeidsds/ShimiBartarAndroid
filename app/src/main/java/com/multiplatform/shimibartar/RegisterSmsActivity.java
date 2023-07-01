package com.multiplatform.shimibartar;

import android.app.Activity;
import android.content.Context;
import com.multiplatform.helper.Constant;

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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

public class RegisterSmsActivity extends AppCompatActivity {


    SharedPreferences sp ;
    SharedPreferences.Editor spe;
    Context ctx;
    String webservice="";
    EditText mobile_et;
    Handler time_handler ;
    int max_sec_to_wait = 30 ;
    TextView time_tv ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_sms_layout);


//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("");
        ctx=this;
        sp= getSharedPreferences("init", Activity.MODE_PRIVATE);
        spe = sp.edit();
        webservice=Constant.get_webservice();
        mobile_et = ((EditText) findViewById(R.id.mobile_et));
        time_tv = ((TextView) findViewById(R.id.time_tv));




        findViewById(R.id.next_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if(can_send_sms()==true)
                {
                    if (mobile_et.getText().toString().trim().length() == 0) {
                        show_msg(ctx.getString(R.string.mobile_error),"red");
                        return;
                    }
                    if (mobile_et.getText().toString().trim().length() != 11) {
                        show_msg(ctx.getString(R.string.mobile_error),"red");
                        return;
                    }
                    new asynctask_send_code().execute();
                }



            }
        });

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });

        time_handler =new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                set_next_btn(can_send_sms());
                if(can_send_sms()==false)
                {
                    time_handler.sendEmptyMessageDelayed(0,1000);
                }
            }
        };


    }







    public class asynctask_send_code extends NewAsyncTask<Boolean,Boolean,Boolean> {


        int error=0;
        String code = "";
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
                        code=json_obj.getString("code");
                        error = 0 ;
                        return true;
                    }
                    else
                    {

                        error=2; // user not exist
                        try {
                            if(json_obj.getString("error").equals("5"))
                                error=5; // blocked
                        }catch (Exception e){}
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

                findViewById(R.id.register_loading).setVisibility(View.VISIBLE);
            }catch (Exception e)
            {}


        }



        @Override
        protected void onPostExecute(Boolean result) {


            findViewById(R.id.register_loading).setVisibility(View.GONE);
            if(error==1)
            {
                show_msg(ctx.getString(R.string.connection_error),"red");
            } else if(error==3)
            {
                show_msg(ctx.getString(R.string.programm_error),"red");
            }
            else if(error==2)
            {
                //show_msg(ctx.getString(R.string.error_login),"red");
                show_msg(ctx.getString(R.string.register_errro),"red");
            }
            else if(error == 5) /// user banned
            {
//                Toast.makeText(ctx,getString(R.string.user_banned),Toast.LENGTH_LONG).show();
//                spe.clear().commit();
//                Intent intent = new Intent(ctx, SplashActivity.class);
//                startActivity(intent);
//                finishAffinity();
                show_msg(ctx.getString(R.string.user_banned),"red");

            }
            else if(error==0)
            {
                spe.putLong("time_to_send",System.currentTimeMillis()).commit();
                show_toast(ctx.getString(R.string.sms_will_be_send),"green");
                Intent intent = new Intent(ctx, RegisterCodeActivity.class);
                intent.putExtra("mobile",mobile_et.getText().toString().trim());
                intent.putExtra("code",code);
                //intent.putExtra("code","111111");
                startActivity(intent);
            }

        }




    }












    public String postRequest(String num)
    {

        RequestBody formBody=null;
        FormBody.Builder fb = new FormBody.Builder();



        fb.add("num",num);
        fb.add("user_mobile",mobile_et.getText().toString().trim());
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


    @Override
    protected void onResume() {
        super.onResume();
        if(can_send_sms()==true)
        {
            set_next_btn(true);
        }
        else
        {
            set_next_btn(false);
            time_handler.sendEmptyMessageDelayed(0,1000);
        }


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

    public void set_next_btn(boolean state )
    {
        if(state == true )
        {
            findViewById(R.id.next_btn).setBackgroundResource(R.drawable.btn_background_green);
            time_tv.setText("");
        }
        else
        {

            long temp = sp.getLong("time_to_send",0);
            temp = System.currentTimeMillis()-temp ;
            temp = temp/1000 ;
            temp = max_sec_to_wait - temp ;
            int temp2 = (int)temp;
            findViewById(R.id.next_btn).setBackgroundResource(R.drawable.btn_background_gray_diactive);
            time_tv.setText(getString(R.string.time_to_send_sms).replace("***",temp2+""));
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(time_handler !=null)
            time_handler.removeCallbacksAndMessages(null);
    }
}
