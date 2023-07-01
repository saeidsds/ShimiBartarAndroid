package com.multiplatform.shimibartar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chootdev.recycleclick.RecycleClick;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.multiplatform.adapter.OfflineLessonAdapter;
import com.multiplatform.helper.Constant;
import com.multiplatform.helper.MultiplatformHelper;
import com.multiplatform.model.PostObject;

import java.lang.reflect.Type;
import java.util.LinkedList;

public class OfflineLessonActivity extends AppCompatActivity {



    SharedPreferences sp ;
    SharedPreferences.Editor spe;
    Context ctx;
    String webservice="";
    RecyclerView recycle_view;
    LinkedList<PostObject> mlist =new LinkedList<>();
    OfflineLessonAdapter adapter;


    TextView error_msg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.offline_lesson_layout);
        ctx=this;
        sp= getSharedPreferences("init", Activity.MODE_PRIVATE);
        spe = sp.edit();
        webservice= Constant.get_webservice();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        error_msg=(TextView) findViewById(R.id.error_msg);

        try {
            Type listType = new TypeToken<LinkedList<PostObject>>() {}.getType();
            mlist =new Gson().fromJson(sp.getString("offline_items",""), listType);
            if(mlist == null)
                mlist = new LinkedList<>();
        }catch (Exception e){}

        recycle_view=(RecyclerView) findViewById(R.id.recycle_view);

        adapter=new OfflineLessonAdapter(mlist,this);
        LinearLayoutManager layoutManager3= new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        recycle_view.setAdapter(adapter);
        recycle_view.setLayoutManager(layoutManager3);
        RecycleClick.addTo(recycle_view).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {


                if(mlist.get(position).file_type.equals("audio") || mlist.get(position).file_type.equals("video"))
                {
                    open_multimedia(position);
                }else if(mlist.get(position).file_type.equals("image") )
                {
//                    Intent intent=new Intent(ctx, ImageActivity.class);
//                    intent.putExtra("data",new Gson().toJson(mlist.get(position)));
//                    startActivity(intent);
                }else if(mlist.get(position).file_type.equals("pdf") )
                {
//                    open_or_download_file(position,false);
                }


            }
        });


        if(mlist.size()==0)
        {
            error_msg.setVisibility(View.VISIBLE);
            error_msg.setText(getString(R.string.no_data));
        }else{
            error_msg.setVisibility(View.GONE);
            error_msg.setText("");
        }
        adapter.mList = mlist ;
        adapter.notifyDataSetChanged();
    }








    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }



































    public void show_msg(String msg)
    {

        error_msg.setVisibility(View.VISIBLE);
        error_msg.setText(msg);
    }


    public void show_msg(String msg, String color)
    {

        //error_msg.setText(msg);
        MultiplatformHelper.show_msg(msg,color,ctx,error_msg);
    }






























    // =================== offline show ==========================================
    // =================== offline show ==========================================
    // =================== offline show ==========================================
    // =================== offline show ==========================================


    public void open_multimedia(int pos)
    {


        Intent intent=new Intent(ctx, PlayerActivity.class);
        intent.putExtra("data",new Gson().toJson(mlist.get(pos)));
        startActivity(intent);
    }





    // =================== offline show ==========================================
    // =================== offline show ==========================================
    // =================== offline show ==========================================
    // =================== offline show ==========================================








}
