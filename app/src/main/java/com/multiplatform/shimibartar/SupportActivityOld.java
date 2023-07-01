package com.multiplatform.shimibartar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.multiplatform.helper.Constant;
import com.multiplatform.helper.MultiplatformHelper;
import com.multiplatform.helper.Parser;
import com.shawnlin.numberpicker.NumberPicker;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SupportActivityOld extends AppCompatActivity {


    SharedPreferences sp ;
    SharedPreferences.Editor spe;
    Context ctx;
    String webservice="";
    EditText desc_et;
    TextView title_tv;
    String selected_title = "";
    Dialog dialog ;
    NumberPicker type_picker ;
    ArrayList<String> type_array ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.support_layout_old);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        ctx=this;
        sp= getSharedPreferences("init", Activity.MODE_PRIVATE);
        spe = sp.edit();
        webservice= Constant.get_webservice();
        desc_et = ((EditText) findViewById(R.id.desc_et));
        title_tv = ((TextView) findViewById(R.id.support_type_tv));
        type_array = new ArrayList<>();
        findViewById(R.id.send_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selected_title.equals(""))
                {
                    show_msg(getString(R.string.error_support_title),"red");
                    return;
                }
                if(desc_et.getText().toString().trim().equals(""))
                {
                    show_msg(getString(R.string.error_support_desc),"red");
                    return;
                }
                new support_async().execute();
            }
        });


        findViewById(R.id.support_type_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_title_dialog();
            }
        });


        findViewById(R.id.call_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:05138117"));
                    callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(callIntent);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        });

        findViewById(R.id.contact_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{Constant.email});
                i.putExtra(Intent.EXTRA_SUBJECT, "");
                i.putExtra(Intent.EXTRA_TEXT   , "");
                try {

                    startActivity(Intent.createChooser(i, ""));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ctx, getString(R.string.email_error1), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }





    public class support_async extends NewAsyncTask<Boolean,Boolean,Boolean> {


        int error=0;

        @Override
        protected Boolean doInBackground(Boolean... params) {



            String temp="";
            temp=postRequest("23");

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

                findViewById(R.id.loading_send).setVisibility(View.VISIBLE);
            }catch (Exception e)
            {}


        }



        @Override
        protected void onPostExecute(Boolean result) {


            findViewById(R.id.loading_send).setVisibility(View.GONE);
            if(error==1)
            {
                show_msg(ctx.getString(R.string.connection_error),"red");
            } else if(error==3)
            {
                show_msg(ctx.getString(R.string.programm_error),"red");
            }
            else if(error==2 )
            {
                show_msg(ctx.getString(R.string.programm_error),"red");
            }

            else if(error==0)
            {
                show_toast(ctx.getString(R.string.support_send_successful),"green");
                finish();
            }

        }




    }












    public String postRequest(String num)
    {

        RequestBody formBody=null;
        FormBody.Builder fb = new FormBody.Builder();



        fb.add("num",num);
        fb.add("user_id",sp.getString("user_id",""));
        fb.add("title",selected_title);
        fb.add("content",desc_et.getText().toString());
        fb.add("mobile_app",sp.getString("mobile",""));
        fb.add("name_app",sp.getString("mobile",""));

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



    public void show_title_dialog()
    {

        final View view = View.inflate(ctx, R.layout.dialog_support_type_layout, null);
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



        type_picker = ((NumberPicker) view.findViewById(R.id.type_picker));

        type_array.clear();
        String []temp_array=getResources().getStringArray(R.array.support_types);
        for (int i=0;i<temp_array.length;i++)
            type_array.add(temp_array[i]);



        type_picker.setMinValue(1);
        type_picker.setMaxValue(type_array.size());
        type_picker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return type_array.get(value-1);
            }
        });





        view.findViewById(R.id.select_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                type_picker.clearFocus();
                selected_title = type_array.get(type_picker.getValue()-1);
                title_tv.setText(selected_title);
                dialog.dismiss();

            }
        });
        view.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
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



    public void show_msg(String msg, String color)
    {
        MultiplatformHelper.show_msg(msg,color,ctx,findViewById(R.id.support_type_btn));
    }

    public void show_toast(String msg,String color)
    {
        //Toast.makeText(ctx,msg,Toast.LENGTH_SHORT).show();
        MultiplatformHelper.show_toast(msg,color,ctx);
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
