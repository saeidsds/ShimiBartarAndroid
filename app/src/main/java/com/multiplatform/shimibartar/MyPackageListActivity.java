package com.multiplatform.shimibartar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.multiplatform.helper.NewAsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.multiplatform.adapter.PackageAdapter;
import com.multiplatform.helper.Constant;
import com.multiplatform.helper.MultiplatformHelper;
import com.multiplatform.helper.Parser;
import com.multiplatform.model.PostObject;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MyPackageListActivity extends AppCompatActivity {



    SharedPreferences sp ;
    SharedPreferences.Editor spe;
    Context ctx;
    String webservice="";
    GridView gridview;
    LinkedList<PostObject> mlist;
    PackageAdapter adapter;
    SwipyRefreshLayout refresh_layout;
    boolean refreshing =false;
    //View loading;
    TextView error_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_package_list_layout);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        ctx=this;
        sp= getSharedPreferences("init", Activity.MODE_PRIVATE);
        spe = sp.edit();
        webservice= Constant.get_webservice();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        //toolbar.setNavigationIcon(R.drawable.ic_back_icon);

        error_msg=(TextView) findViewById(R.id.error_msg);
        refresh_layout=(SwipyRefreshLayout)findViewById(R.id.swipyrefreshlayout);
        gridview=(GridView) findViewById(R.id.gridview);
        mlist=new LinkedList<PostObject>();
        adapter=new PackageAdapter(mlist,this);
        adapter.temp_view = gridview ;
        gridview.setAdapter(adapter);


        error_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(refreshing==false) {
                    new asynctask_get_data("0").execute();
                }
            }
        });

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent =new Intent(ctx,PackageActivity.class);
                intent.putExtra("data",new Gson().toJson(mlist.get(position)));
                startActivity(intent);
            }
        });
        refresh_layout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if(refreshing==false)
                {
                    if(direction== SwipyRefreshLayoutDirection.TOP)
                    {
                        new asynctask_get_data("0").execute();
                    }
                    else
                    {

                        try {
                            //String temp=mlist.get(mlist.size()-1).record_number;
                            String temp =mlist.size()+"";
                            new asynctask_get_data(temp).execute();
                        }catch (Exception e){}

                    }
                }
            }
        });

        new asynctask_get_data("0").execute();
    }








    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }












    public class asynctask_get_data extends NewAsyncTask<Boolean,Boolean,Boolean> {


        //public boolean gcm_result=false ;
        LinkedList<PostObject> temp_list;
        int error=0;
        String index="0";

        public asynctask_get_data(String index)
        {
            this.index=index;
        }
        @Override
        protected Boolean doInBackground(Boolean... params) {




            temp_list=new LinkedList<PostObject>();

            String temp=postRequest("24",index);
            if(temp==null)
            {

                error=1;
                return false;
            }
            temp= Parser.get_json(temp);
            if(temp!=null && !temp.trim().equals(""))
            {
                JSONArray jArray = null;
                JSONObject json_obj=null;
                try {

                    json_obj = new JSONObject(temp);

                    String temp_result=json_obj.getString("result");
                    if(temp_result.trim().equals("true"))
                    {
                        int temp_error=0;
                        try {
                            jArray = json_obj.getJSONArray("list");
                        }catch (Exception e)
                        {
                            error=4;
                            return false;
                        }
                        if(jArray==null || jArray.length()==0)
                        {
                            error=4;
                            return false;
                        }


                        try {
                            Parser.get_post_array(ctx,jArray,temp_list);
                        }catch (Exception e)
                        {}



                        error=0;
                        return true;
                    }
                    else
                    {
                        // message error from server
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

        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            if(index.equals("0"))
            {
                mlist.clear();
                adapter.notifyDataSetChanged();
            }
            show_loading();


        }



        @Override
        protected void onPostExecute(Boolean result) {

            dismiss_loading();
            refresh_layout.setRefreshing(false);
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
            else if(error==2)
            {
                if(mlist.size()==0)
                    show_msg(getString(R.string.no_package));

            }
            else if(error==4 )
            {
                if(index.equals("0")) {
                    show_msg(getString(R.string.no_package));
                    mlist.clear();
                    adapter.mList=mlist;
                    adapter.notifyDataSetChanged();
                }
            }

            else if(error==0)
            {
                if(index.equals("0"))
                {
                    mlist.clear();
                }
                mlist.addAll(temp_list);
                adapter.mList=mlist;
                if(mlist.size()==0)
                {
                    show_msg(getString(R.string.no_package));
                }
            }
            adapter.notifyDataSetChanged();
        }

    }



    public String postRequest(String num,String index)
    {

        RequestBody formBody=null;
        FormBody.Builder fb = new FormBody.Builder();


        fb.add("num",num);
        fb.add("id_num",index);
        fb.add("user_id",sp.getString("user_id",""));
        if(getIntent().getStringExtra("term_id")!=null)
            fb.add("category_id",getIntent().getStringExtra("term_id"));




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
        MultiplatformHelper.show_msg(msg,color,ctx,error_msg);
    }



    public void show_loading()
    {
        error_msg.setVisibility(View.GONE);
        //loading.setVisibility(View.VISIBLE);
        refresh_layout.post(new Runnable() {
            @Override
            public void run() {
                refresh_layout.setRefreshing(true);
            }
        });
    }
    public void dismiss_loading()
    {
        //loading.setVisibility(View.GONE);
        refresh_layout.setRefreshing(false);
        refresh_layout.post(new Runnable() {
            @Override
            public void run() {
                refresh_layout.setRefreshing(false);
            }
        });
    }








}
