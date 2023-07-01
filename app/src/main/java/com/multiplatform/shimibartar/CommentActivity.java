package com.multiplatform.shimibartar;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import com.multiplatform.helper.NewAsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.multiplatform.adapter.CommentAdapter;
import com.multiplatform.helper.Constant;
import com.multiplatform.helper.Parser;
import com.multiplatform.model.CommentObject;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommentActivity extends AppCompatActivity {

    SharedPreferences sp ;
    SharedPreferences.Editor spe;
    Context ctx;
    String webservice="";
    ListView listview;
    LinkedList<CommentObject> mlist;
    CommentAdapter adapter;
    TextView error_msg;
    boolean refreshing =false;
    SwipyRefreshLayout refresh_layout;
    EditText cm_et;
    boolean send_refreshing=false;
    String parent_comment = "0" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        try {
            ((TextView) findViewById(R.id.title)).setText(getIntent().getStringExtra("title"));
        }catch (Exception e){}


        parent_comment = getIntent().getStringExtra("parent_comment");
        if(parent_comment==null)
            parent_comment="0";
        ctx=this;
        sp= getSharedPreferences("init", Activity.MODE_PRIVATE);
        spe = sp.edit();

        webservice= Constant.get_webservice();
        cm_et=(EditText) findViewById(R.id.cm_et);
        listview=(ListView) findViewById(R.id.listview);
        error_msg=(TextView) findViewById(R.id.error_msg);
        refresh_layout=(SwipyRefreshLayout)findViewById(R.id.swipyrefreshlayout);
        mlist=new LinkedList<CommentObject>();
        adapter=new CommentAdapter(mlist,ctx);
        error_msg.setVisibility(View.GONE);
        listview.setAdapter(adapter);
        error_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(refreshing==false) {
                    new comment_asynctask("0").execute();
                }
            }
        });








        refresh_layout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if(refreshing==false)
                {
                    if(direction== SwipyRefreshLayoutDirection.TOP)
                    {
                        new comment_asynctask("0").execute();
                    }
                    else
                    {
                        //String temp=mlist.get(mlist.size()-1).post_id;
                        String temp=mlist.size()+"";
                        new comment_asynctask(""+temp).execute();
                    }
                }
            }
        });

        new comment_asynctask("0").execute();


        findViewById(R.id.send_cnt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(send_refreshing==false && cm_et.getText().toString().trim().length()!=0)
                    new SendAsynctask().execute();
            }
        });


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }







    //======================================================================



    public class comment_asynctask extends NewAsyncTask<Boolean, Boolean, Boolean> {


        //public boolean gcm_result=false ;
        int error=0;  // 0= good      1= connection error      2= programm error      3 = programm error
        LinkedList<CommentObject> temp_array;
        String id="0";
        public comment_asynctask(String id )
        {
            this.id=id;
        }

        @Override
        protected Boolean doInBackground(Boolean... params) {



            String temp=postRequest("10",id);
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
                    temp_array=new LinkedList<CommentObject>();
                    try {
                        jArray=json_obj.getJSONArray("list");
                    }catch (Exception e)
                    {
                        error=2;
                        return false;
                    }
                    try {
                        if(jArray.length()==0)
                        {
                            error=2;
                            return false;
                        }
                    }catch (Exception e)
                    {
                        error=2;
                        return false;
                    }
                    Parser.get_comment_array(ctx,jArray,temp_array);



                }
                catch (Exception e) {


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
            super.onPreExecute();
            refreshing=true;
            refresh_layout.post(new Runnable() {
                @Override
                public void run() {
                    refresh_layout.setRefreshing(true);
                }
            });
            if(mlist.size()==0)
                show_loading();

        }



        @Override
        protected void onPostExecute(Boolean result) {

            refreshing=false;
            refresh_layout.setRefreshing(false);
            refresh_layout.post(new Runnable() {
                @Override
                public void run() {
                    refresh_layout.setRefreshing(false);
                }
            });
            if(error==1)
            {
                if(mlist.size()==0)
                    show_msg(getString(R.string.connection_error));
                else
                    show_msg(getString(R.string.connection_error),"red");
            } else if(error==3)
            {
                if(mlist.size()==0)
                    show_msg(getString(R.string.programm_error));
            }
            else if(error==2 )
            {
                if(id.equals("0")) {
                    show_msg(getString(R.string.comment_not_exist));
                    mlist.clear();
                    adapter.mList=mlist;
                    adapter.notifyDataSetChanged();
                }

            }
            else if(error==0)
            {

                if(id.equals("0"))
                {
                    mlist.clear();
                }
                for(int i=0;i<temp_array.size();i++)
                {
                    mlist.add(temp_array.get(i));
                }
                adapter.mList=mlist;
                adapter.notifyDataSetChanged();

                if(mlist.size()==0)
                {
                    show_msg(getString(R.string.comment_not_exist));
                }

            }
            adapter.notifyDataSetChanged();
        }





    }





    public class SendAsynctask extends NewAsyncTask<Boolean, Boolean, Boolean> {



        int error=0;  // 0= good      1= connection error      2= no comment      3 = programm error

        @Override
        protected Boolean doInBackground(Boolean... params) {




            String temp=postRequest("9","");
            if(temp==null)
            {

                error=1;
                return false;
            }

            if(temp!=null && !temp.trim().equals(""))
            {
                JSONArray jArray = null ;
                JSONArray jArray2 = null ;
                try {

                    //jArray2=new JSONArray(temp);
                    JSONObject json_obj = new JSONObject(temp);


                    String temp_result=json_obj.getString("result");
                    if(temp_result.trim().equals("true"))
                    {
                        return  true;
                    }
                    else
                    {
                        error=2;
                        return false;
                    }




                }
                catch (Exception e) {


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
            send_refreshing=true;
            findViewById(R.id.loading_send).setVisibility(View.VISIBLE);
            findViewById(R.id.send_img).setVisibility(View.GONE);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        }



        @Override
        protected void onPostExecute(Boolean result) {

            send_refreshing=false;
            findViewById(R.id.loading_send).setVisibility(View.GONE);
            findViewById(R.id.send_img).setVisibility(View.VISIBLE);


            String error_string="";
            if(error==1)
            {
                error_string=getString(R.string.connection_error);
            } else if(error==3)
            {
                error_string=getString(R.string.programm_error);
            }
            else if(error==2 )
            {
                error_string=getString(R.string.programm_error);
            }
            else if(error==0)
            {
                cm_et.setText("");
                error_string=getString(R.string.comment_insert_complete);
            }

            if(error==1 || error==2 || error==3)
            {
                show_msg(error_string,true,"red");
            }
            else
                show_msg(error_string,true,"green");









        }





    }



    public void show_msg(String msg, boolean show_snack, String color)
    {

        //error_msg.setText(msg);
        if(show_snack)
        {   Snackbar msg_snack=null;
            msg_snack= Snackbar.make(findViewById(R.id.listview), msg, Snackbar.LENGTH_LONG).setAction("Action", null);
            if(color.equals("red"))
                msg_snack.getView().setBackgroundColor(ContextCompat.getColor(ctx, R.color.material_red));
            if(color.equals("green")) {
                msg_snack= Snackbar.make(findViewById(R.id.listview), msg, Snackbar.LENGTH_SHORT).setAction("Action", null);
                msg_snack.getView().setBackgroundColor(ContextCompat.getColor(ctx, R.color.material_green2));
            }
            msg_snack.show();
        }
        else
        {
            error_msg.setVisibility(View.VISIBLE);
            error_msg.setText(msg);
        }
    }






    public String postRequest(String num , String id)
    {

        RequestBody formBody=null;
        FormBody.Builder fb = new FormBody.Builder();

        fb.add("num",num);
        fb.add("id_num", id);

        fb.add("user_id",sp.getString("user_id","0"));
        fb.add("post_id", getIntent().getStringExtra("post_id"));
        fb.add("comment_content", cm_et.getText().toString().trim());
        fb.add("parent_comment", parent_comment);


        formBody=fb.build();
        try {
            //final MediaType JSON= MediaType.parse("application/json; charset=utf-8");
            OkHttpClient client = new OkHttpClient()
                    .newBuilder()
                    .connectTimeout(55, TimeUnit.SECONDS)
                    .writeTimeout(55, TimeUnit.SECONDS)
                    .readTimeout(55, TimeUnit.SECONDS)
                    .build();

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















    public void show_msg(String msg)
    {

        //error_msg.setText(msg);
        /*Snackbar msg_snack=Snackbar.make(findViewById(R.id.login_login_btn), msg, Snackbar.LENGTH_LONG).setAction("Action", null);
        msg_snack.getView().setBackgroundColor(ContextCompat.getColor(ctx, R.color.color_primary));
        msg_snack.show();*/
        error_msg.setVisibility(View.VISIBLE);
        error_msg.setText(msg);
    }


    public void show_msg(String msg, String color)
    {

        error_msg.setText(msg);
        Snackbar msg_snack= Snackbar.make(findViewById(R.id.listview), msg, Snackbar.LENGTH_LONG).setAction("Action", null);
        if(color.equals("red"))
            msg_snack.getView().setBackgroundColor(ContextCompat.getColor(ctx, R.color.material_red));
        if(color.equals("green"))
            msg_snack.getView().setBackgroundColor(ContextCompat.getColor(ctx, R.color.material_green2));

        msg_snack.show();
    }



    public void show_loading()
    {
        error_msg.setVisibility(View.GONE);
    }











}
