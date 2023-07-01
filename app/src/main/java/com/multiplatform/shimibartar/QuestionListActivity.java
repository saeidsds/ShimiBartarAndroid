package com.multiplatform.shimibartar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import com.multiplatform.helper.NewAsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chootdev.recycleclick.RecycleClick;
import com.google.gson.Gson;
import com.multiplatform.adapter.QuestionAdapter2;
import com.multiplatform.helper.Constant;
import com.multiplatform.helper.MultiplatformHelper;
import com.multiplatform.helper.Parser;
import com.multiplatform.model.GeneralObject;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class QuestionListActivity extends AppCompatActivity {



    SharedPreferences sp ;
    SharedPreferences.Editor spe;
    Context ctx;
    String webservice="";
    RecyclerView listview;
    LinkedList<GeneralObject> mlist;
    QuestionAdapter2 adapter;
    SwipyRefreshLayout refresh_layout;
    boolean refreshing =false;
    //View loading;
    TextView error_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_list_layout);
        ctx=this;
        sp= getSharedPreferences("init", Activity.MODE_PRIVATE);
        spe = sp.edit();
        webservice= Constant.get_webservice();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        try {
            ((TextView) findViewById(R.id.title)).setText(getIntent().getStringExtra("title"));
        }catch (Exception e){}

        error_msg=(TextView) findViewById(R.id.error_msg);
        refresh_layout=(SwipyRefreshLayout)findViewById(R.id.swipyrefreshlayout);
        listview=(RecyclerView) findViewById(R.id.listview);
        mlist=new LinkedList<GeneralObject>();
        adapter=new QuestionAdapter2(mlist,this);
        listview.setAdapter(adapter);
        LinearLayoutManager layoutManager3= new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        listview.setLayoutManager(layoutManager3);


        error_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(refreshing==false) {
                    new asynctask_get_data("0").execute();
                }
            }
        });

        findViewById(R.id.add_question).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_report_dialog();
            }
        });

        RecycleClick.addTo(listview).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Intent intent =new Intent(ctx, QuestionActivity.class);
                intent.putExtra("data",new Gson().toJson(mlist.get(position)));
                startActivity(intent);
            }
        });


        findViewById(R.id.my_question_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(ctx, MyQuestionListActivity.class);
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
        LinkedList<GeneralObject> temp_list;
        int error=0;
        String index="0";

        public asynctask_get_data(String index)
        {
            this.index=index;
        }
        @Override
        protected Boolean doInBackground(Boolean... params) {




            temp_list=new LinkedList<GeneralObject>();

            String temp=postRequest("15",index);
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
                        }catch (Exception e) {
                            error=4;
                            return false;
                        }
                        if(jArray==null || jArray.length()==0) {
                            error=4;
                            return false;
                        }


                        try {
                            Parser.get_general_array(ctx,jArray,temp_list);
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
                    show_msg(getString(R.string.no_faq_item));

            }
            else if(error==4 )
            {
                if(index.equals("0")) {
                    show_msg(getString(R.string.no_faq_item));
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
                    show_msg(getString(R.string.no_faq_item));
                }
            }
            adapter.notifyDataSetChanged();
        }

    }



// =====================

    public class asynctask_send_question extends NewAsyncTask<Boolean,Boolean,Boolean> {


        int error=0;


        @Override
        protected Boolean doInBackground(Boolean... params) {


            String temp=postRequest("17","");
            if(temp==null)
            {

                error=1;
                return false;
            }
            temp= Parser.get_json(temp);
            if(temp!=null && !temp.trim().equals(""))
            {
                JSONObject json_obj=null;
                try {

                    json_obj = new JSONObject(temp);
                    String temp_result=json_obj.getString("result");
                    if(temp_result.trim().equals("true"))
                    {
                        error=0;
                        return true;
                    }
                    else
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

        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                loading_question.setVisibility(View.VISIBLE);
                question_refreshing=true ;
            }catch (Exception e){}

        }



        @Override
        protected void onPostExecute(Boolean result) {
            try {
                loading_question.setVisibility(View.GONE);
                question_refreshing=false ;
            }catch (Exception e){}

            if(error==1)
            {
                show_msg(getString(R.string.connection_error),"red");
            } else if(error==3)
            {
                show_msg(getString(R.string.programm_error),"red");
            }
            else if(error==2 )
            {
                show_msg(getString(R.string.programm_error),"red");
            }

            else if(error==0)
            {
                show_msg(getString(R.string.add_question_successful),"green");
                try {
                    dialog.dismiss();
                }catch (Exception e){}
            }
        }

    }



















    public String postRequest(String num,String index)
    {

        RequestBody formBody=null;
        FormBody.Builder fb = new FormBody.Builder();


        fb.add("num",num);
        fb.add("id_num",index);
        fb.add("user_id",sp.getString("user_id",""));
        if(getIntent().getStringExtra("category_name")!=null)
            fb.add("category_name",getIntent().getStringExtra("category_name"));

        if(dialog_title_et!=null)
            fb.add("title",dialog_title_et.getText().toString());
        if(dialog_content_et!=null)
            fb.add("content",dialog_content_et.getText().toString());

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




    Dialog dialog ;
    View loading_question ;
    boolean question_refreshing=false ;
    EditText dialog_title_et,dialog_content_et;


    public void show_report_dialog()
    {

        final View view = View.inflate(ctx, R.layout.dialog_send_question_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx,R.style.CustomDialog);
        builder.setView(view);
        view.findViewById(R.id.bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialog!=null)
                    dialog.dismiss();
            }
        });
        view.findViewById(R.id.scroll).setVisibility(View.INVISIBLE);

        loading_question = view.findViewById(R.id.loading_report) ;
        dialog_title_et = view.findViewById(R.id.title_et) ;
        dialog_content_et = view.findViewById(R.id.content_et) ;



        view.findViewById(R.id.submit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if(dialog_content_et.getText().toString().trim().equals("") ||
                            dialog_title_et.getText().toString().trim().equals(""))
                    {
                      MultiplatformHelper.show_toast(getString(R.string.question_error),"red",ctx);
                      return;
                    }
                    if(question_refreshing==false)
                        new asynctask_send_question().execute();
                }catch (Exception e){}

            }
        });

        ((ExpandableLayout)view.findViewById(R.id.expandable_layout)).collapse(false);
        dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        Animation horizontal_anim = AnimationUtils.loadAnimation(ctx,R.anim.dialog_in);
        horizontal_anim.setFillAfter(true);
        horizontal_anim.setStartOffset(200);
        view.findViewById(R.id.scroll).startAnimation(horizontal_anim);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                ((ExpandableLayout)view.findViewById(R.id.expandable_layout)).toggle();
            }
        }, 550);



    }



}
