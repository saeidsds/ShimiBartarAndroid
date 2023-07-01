package com.multiplatform.shimibartar;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.multiplatform.helper.Constant;
import com.multiplatform.model.GeneralObject;

public class QuestionActivity extends AppCompatActivity {


    SharedPreferences sp ;
    SharedPreferences.Editor spe;
    Context ctx;
    String webservice="";
    String content_str = "";
    TextView desc_tv,title_tv,answer_tv;

    GeneralObject data=new GeneralObject();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        //toolbar.setNavigationIcon(R.drawable.ic_back_icon);
        ctx=this;
        sp= getSharedPreferences("init", Activity.MODE_PRIVATE);
        spe = sp.edit();
        webservice= Constant.get_webservice();
        title_tv = (TextView) findViewById(R.id.title_tv);
        desc_tv = (TextView) findViewById(R.id.desc_tv);
        answer_tv = (TextView) findViewById(R.id.answer_tv);



        data=new Gson().fromJson(getIntent().getStringExtra("data"),GeneralObject.class);
        title_tv.setText(data.title);
        desc_tv.setText(data.content);
        answer_tv.setText(data.answer);

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
