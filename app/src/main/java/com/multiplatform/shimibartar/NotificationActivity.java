package com.multiplatform.shimibartar;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import com.multiplatform.helper.NewAsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.multiplatform.helper.Constant;
import com.multiplatform.helper.Parser;
import com.multiplatform.model.GeneralObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NotificationActivity extends AppCompatActivity {


    SharedPreferences sp ;
    SharedPreferences.Editor spe;
    Context ctx;
    String webservice="";
    boolean refreshing = false ;
    SwipeRefreshLayout refresh_layout;
    TextView error_msg ;
    GeneralObject data = new GeneralObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        //toolbar.setNavigationIcon(R.drawable.ic_back_icon);
        ctx=this;
        sp= getSharedPreferences("init", Activity.MODE_PRIVATE);
        spe = sp.edit();
        webservice= Constant.get_webservice();
        refresh_layout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        error_msg = (TextView) findViewById(R.id.error_msg);
        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new asynctask_get_data().execute();
            }
        });
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        if(getIntent().getBooleanExtra("load",false)==true)
        {
            data.post_id=getIntent().getStringExtra("post_id");
            new asynctask_get_data().execute();
        }
        else
        {
            data=new Gson().fromJson(getIntent().getStringExtra("data"), GeneralObject.class);
            ((TextView)findViewById(R.id.content_tv)).setText(data.content);
            ((TextView)findViewById(R.id.title_tv)).setText(data.title);
        }
        //new asynctask_get_data().execute();
    }





    public class asynctask_get_data extends NewAsyncTask<Boolean,Boolean,Boolean> {


        int error=0;

        @Override
        protected Boolean doInBackground(Boolean... params) {



            String temp="";
            temp=postRequest("");
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
                        data.title = json_obj.getString("title");
                        data.content = json_obj.getString("content").replace("&nbsp;"," ");
                        data.content= Parser.html2text(data.content).replace("&nbsp;"," ");
                        return true;
                    }
                    else
                    {
                        error=2; // user exist
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
                error_msg.setText("");
                error_msg.setVisibility(View.GONE);
                refreshing = true ;
                ((TextView)findViewById(R.id.content_tv)).setText("");
                ((TextView)findViewById(R.id.title_tv)).setText("");
                refresh_layout.post(new Runnable() {
                    @Override
                    public void run() {
                        refresh_layout.setRefreshing(true);
                    }
                });
            }catch (Exception e)
            {}


        }



        @Override
        protected void onPostExecute(Boolean result) {


            refresh_layout.setRefreshing(false);
            refresh_layout.post(new Runnable() {
                @Override
                public void run() {
                    refresh_layout.setRefreshing(false);
                }
            });
            refreshing = false ;
            if(error==1)
            {
                show_toast(ctx.getString(R.string.connection_error),"red");
            } else if(error==3)
            {
                show_toast(ctx.getString(R.string.programm_error),"red");
            }
            else if(error==2 )
            {
                show_toast(ctx.getString(R.string.programm_error),"red");
            }

            else if(error==0)
            {
                ((TextView)findViewById(R.id.content_tv)).setText(data.content);
                ((TextView)findViewById(R.id.title_tv)).setText(data.title);
            }

        }




    }






    public String postRequest(String num)
    {

        RequestBody formBody=null;
        FormBody.Builder fb = new FormBody.Builder();



        fb.add("id_num",0+"");
        fb.add("num",num);
        fb.add("user_id",sp.getString("user_id",""));
        fb.add("get_type","1");
        fb.add("post_id", data.post_id);


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








    public void show_toast(String msg, String color)
    {
        //MultiplatformHelper.show_toast(msg,color,ctx);
        error_msg.setVisibility(View.VISIBLE);
        error_msg.setText(msg);
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
