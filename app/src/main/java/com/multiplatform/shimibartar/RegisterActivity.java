package com.multiplatform.shimibartar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import com.multiplatform.helper.Constant;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import com.multiplatform.helper.NewAsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Handler;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.multiplatform.helper.MultiplatformHelper;
import com.multiplatform.helper.Parser;
import com.shawnlin.numberpicker.NumberPicker;

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

public class RegisterActivity extends AppCompatActivity {


    SharedPreferences sp ;
    SharedPreferences.Editor spe;
    Context ctx;
    String webservice="";
    EditText mobile_et,name_et,location_et,school_type_et,class_type_et,reagent_et,reshte_et;
    LinkedList<Pair<String, String>> class_type_list =new LinkedList();
    LinkedList<Pair<String, String>> school_type_list =new LinkedList();
    LinkedList<Pair<String, String>> gender_list =new LinkedList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("");
        ctx=this;
        sp= getSharedPreferences("init", Activity.MODE_PRIVATE);
        spe = sp.edit();
        webservice=Constant.get_webservice();
        mobile_et = ((EditText) findViewById(R.id.mobile_et));
        location_et = ((EditText) findViewById(R.id.location_et));
        name_et = ((EditText) findViewById(R.id.name_et));
        school_type_et = ((EditText) findViewById(R.id.school_type_et));
        class_type_et = ((EditText) findViewById(R.id.class_type_et));
        //gender_et = ((EditText) findViewById(R.id.gender_et));
        reagent_et = ((EditText) findViewById(R.id.reagent_et));
        reshte_et = ((EditText) findViewById(R.id.reshte_et));
        try {
            mobile_et.setText(getIntent().getStringExtra("mobile"));
            mobile_et.setEnabled(false);
        }catch (Exception e){}


        // === create value pair from string array
        for(int i =0 ;i<getResources().getStringArray(R.array.class_type).length;i++)
            class_type_list.add(Pair.create(getResources().getStringArray(R.array.class_type)[i],getResources().getStringArray(R.array.class_type_id)[i]));
        for(int i =0 ;i<getResources().getStringArray(R.array.school_type).length;i++)
            school_type_list.add(Pair.create(getResources().getStringArray(R.array.school_type)[i],getResources().getStringArray(R.array.school_type_id)[i]));
        for(int i =0 ;i<getResources().getStringArray(R.array.gender).length;i++)
            gender_list.add(Pair.create(getResources().getStringArray(R.array.gender)[i],getResources().getStringArray(R.array.gender_id)[i]));


        findViewById(R.id.gender_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_select_dialog(getString(R.string.select_gender),gender_list,"gender");
            }
        });

        findViewById(R.id.school_type_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_select_dialog(getString(R.string.select_school_type),school_type_list,"school");
            }
        });


        findViewById(R.id.class_type_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_select_dialog(getString(R.string.select_class_type),class_type_list,"class");
            }
        });

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_back_dialog();
            }
        });

        findViewById(R.id.next_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (name_et.getText().toString().trim().length() == 0) {
                    show_msg(ctx.getString(R.string.name_error),"red");
                    return;
                }

                if (mobile_et.getText().toString().trim().length() == 0) {
                    show_msg(ctx.getString(R.string.mobile_error),"red");
                    return;
                }
                if (location_et.getText().toString().trim().length() == 0) {
                    show_msg(ctx.getString(R.string.location_error),"red");
                    return;
                }


                if (reshte_et.getText().toString().trim().length() == 0) {
                    show_msg(ctx.getString(R.string.error_reshte),"red");
                    return;
                }


                if (selected_school == null) {
                    show_msg(ctx.getString(R.string.school_error),"red");
                    return;
                }


                if (selected_class == null) {
                    show_msg(ctx.getString(R.string.class_error),"red");
                    return;
                }



                new asynctask_register().execute();
            }
        });

    }








    public class asynctask_register extends NewAsyncTask<Boolean,Boolean,Boolean> {


        int error=0;

        @Override
        protected Boolean doInBackground(Boolean... params) {



            String temp="";
            temp=postRequest("4");

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
                        spe.putString("user_id",json_obj.getString("user_id")).commit();
                        spe.putString("name",name_et.getText().toString().trim()).commit();
                        spe.putString("mobile",mobile_et.getText().toString().trim()).commit();
                        spe.putString("school_type",selected_school.second).commit();
                       // spe.putString("gender",selected_gender.second).commit();
                        spe.putString("class_type",selected_class.second).commit();
                        spe.putString("location",location_et.getText().toString()).commit();
                        spe.putString("total_buy","0").commit();

                        spe.putString("min_price_for_question",json_obj.getString("min_price_for_question")).commit();
                        spe.putString("public_comment_post_id",json_obj.getString("public_comment_post_id")).commit();


                        spe.putBoolean("login",true).commit();
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
            else if(error==2 )
            {
                show_msg(ctx.getString(R.string.register_errro),"red");
            }

            else if(error==0 )
            {
                show_toast(ctx.getString(R.string.register_successfull),"green");
                startActivity(new Intent(ctx, HomeActivity.class));
                ((Activity)ctx).finishAffinity();
            }

        }




    }












    public String postRequest(String num)
    {

        RequestBody formBody=null;
        FormBody.Builder fb = new FormBody.Builder();
        String imei="";
        try {
            imei=Parser.getDeviceUniqueID(ctx);
            if(imei==null)
                imei="";
        }catch (Exception e)
        {}


        fb.add("num",num);

        fb.add("user_mobile",mobile_et.getText().toString().trim());
        fb.add("user_name",name_et.getText().toString().trim());
        fb.add("location",location_et.getText().toString());
        fb.add("reagent",reagent_et.getText().toString());
        fb.add("user_imei",imei);
        fb.add("reshte",reshte_et.getText().toString());

        if(selected_class!=null)
            fb.add("class_type",selected_class.second);
        if(selected_school!=null)
            fb.add("school_type",selected_school.second);
//        if(selected_gender!=null)
//            fb.add("gender",selected_gender.second);



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
    public void onBackPressed() {
        show_back_dialog();
    }











    Dialog dialog ;
    NumberPicker type_picker ;
    Pair<String,String> selected_school,selected_class;//,selected_gender;
    public void show_select_dialog(String title, LinkedList<Pair<String,String>> items,String type)
    {

        final View view = View.inflate(ctx, R.layout.dialog_select_layout, null);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(ctx,R.style.CustomDialog);
        builder.setView(view);
        view.findViewById(R.id.bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialog!=null)
                    dialog.dismiss();
            }
        });
        view.findViewById(R.id.scroll).setVisibility(View.INVISIBLE);
        ((TextView)view.findViewById(R.id.title_tv)).setText(title);



        type_picker = ((NumberPicker) view.findViewById(R.id.type_picker));




        type_picker.setMinValue(1);
        type_picker.setMaxValue(items.size());
        type_picker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return items.get(value-1).first;
            }
        });





        view.findViewById(R.id.select_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                type_picker.clearFocus();
                if(type.equals("school")){
                    selected_school = items.get(type_picker.getValue()-1);
                    school_type_et.setText(selected_school.first);
                } else if(type.equals("class")){
                    selected_class = items.get(type_picker.getValue()-1);
                    class_type_et.setText(selected_class.first);
                }
//                else if(type.equals("gender")){
//                    selected_gender = items.get(type_picker.getValue()-1);
//                    gender_et.setText(selected_gender.first);
//                }
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




    public void show_back_dialog()
    {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        finish();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(getString(R.string.dialog_back_desc)).setPositiveButton(getString(R.string.yes), dialogClickListener)
                .setNegativeButton(getString(R.string.no), dialogClickListener);
        Dialog d=builder.create();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        d.show();

        try {
            ((AlertDialog)d).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(ctx,R.color.btn_dark_gray));
            ((AlertDialog)d).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ctx,R.color.btn_dark_gray));
            ((AlertDialog)d).getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(ContextCompat.getColor(ctx,R.color.btn_dark_gray));
        }catch (Exception e)
        {}
    }
}
