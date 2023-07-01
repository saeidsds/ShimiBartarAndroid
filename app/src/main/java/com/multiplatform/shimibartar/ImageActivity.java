package com.multiplatform.shimibartar;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.gson.Gson;
import com.multiplatform.helper.Constant;
import com.multiplatform.model.PostObject;

public class ImageActivity extends AppCompatActivity {


    SharedPreferences sp ;
    SharedPreferences.Editor spe;
    Context ctx;
    String webservice="";
    PostObject data=new PostObject();
    PhotoView image ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        //toolbar.setNavigationIcon(R.drawable.ic_back_icon);
        ctx=this;
        sp= getSharedPreferences("init", Activity.MODE_PRIVATE);
        spe = sp.edit();
        webservice= Constant.get_webservice();
        data=new Gson().fromJson(getIntent().getStringExtra("data"),PostObject.class);


        init();





    }




    public void init(){

        image = findViewById(R.id.image);
        if(isDestroyed()==false)
        {
            Glide.with(ctx).load(data.file_link).into(image);
        }

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
