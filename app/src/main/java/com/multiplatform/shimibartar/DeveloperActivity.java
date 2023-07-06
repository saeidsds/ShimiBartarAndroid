package com.multiplatform.shimibartar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import com.multiplatform.helper.Constant;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.multiplatform.helper.NewAsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.multiplatform.helper.DataBaseHelper;
import com.multiplatform.helper.Parser;
import com.multiplatform.model.MultiPlatformObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DeveloperActivity extends AppCompatActivity {


    SharedPreferences sp ;
    SharedPreferences.Editor spe;
    Context ctx;
    String webservice="";
    String content_str = "";
    boolean refreshing = false ;
    //SwipeRefreshLayout refresh_layout;
    TextView error_msg ;
    WebView webview ;
    MultiPlatformObject data =new MultiPlatformObject();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.developer_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        ctx=this;
        sp= getSharedPreferences("init", Activity.MODE_PRIVATE);
        spe = sp.edit();
        webservice="http://multiplatform.ir/server/"+Constant.en_app_name+"/"+Constant.en_app_name+".php";
        //refresh_layout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        error_msg = (TextView) findViewById(R.id.error_msg);
        webview = (WebView) findViewById(R.id.webview);
        webview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        webview.setLongClickable(false);
//        refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                new asynctask_get_data().execute();
//            }
//        });
        findViewById(R.id.call_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_contact_dialog("call");
            }
        });

        findViewById(R.id.contact_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_contact_dialog("email");
            }
        });
        data = get_about();
        set_to_webview(get_html(data.content));
        new asynctask_get_data().execute();
    }





    public class asynctask_get_data extends NewAsyncTask<Boolean,Boolean,Boolean> {


        int error=0;

        @Override
        protected Boolean doInBackground(Boolean... params) {



            String temp="";
            temp=postRequest("14");
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
                        data.content=json_obj.getString("content").replace("/n", "\n").replace("/r", "\r").replace("/t", "\t");
                        data.email=json_obj.getString("email");
                        data.tel=json_obj.getString("tel");
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
        }



        @Override
        protected void onPostExecute(Boolean result) {


            if(error==0)
            {
                set_to_webview(get_html(data.content));
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


//&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
    //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
    //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
    //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
    //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
    //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&

    public void set_to_webview (String html)
    {
        WebSettings settings = webview.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        webview.loadDataWithBaseURL (null,html, "text/html", "UTF-8",null);
    }
    public MultiPlatformObject get_about()
    {
        MultiPlatformObject result=new MultiPlatformObject();
        DataBaseHelper database=new DataBaseHelper(ctx);
        try
        {
            database.createDataBase();
        }
        catch (IOException ioe)
        {
            return result;
            //throw new Error("Unable to create database");

        }
        try
        {

            database.openDataBase();

        }catch(SQLException sqle){
            return result;
            //throw sqle;

        }


        String query ="";
        query = "SELECT  content , tel , logo , email   FROM   about" ;
        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor;
        cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        if (cursor.moveToFirst())
        {

            do {
                result.content =cursor.getString(0);
                result.tel =cursor.getString(1);
                result.logo =cursor.getString(2);
                result.email =cursor.getString(3);




            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        database.close();
        return result ;


    }


    public void show_contact_dialog(final String mode)
    {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        if(mode.equals("call"))
                        {
                            try {
                                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                callIntent.setData(Uri.parse("tel:"+data.tel));
                                callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(callIntent);
                            } catch (Exception e) {
                                // TODO: handle exception
                            }
                        }
                        else
                        {
                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("message/rfc822");
                            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{data.email});
                            i.putExtra(Intent.EXTRA_SUBJECT, Constant.en_app_name+"_app");
                            i.putExtra(Intent.EXTRA_TEXT   , "");
                            try {

                                startActivity(Intent.createChooser(i, ""));
                            } catch (android.content.ActivityNotFoundException ex) {
                                Toast.makeText(ctx, getString(R.string.email_error1), Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                    case DialogInterface.BUTTON_NEUTRAL:
                        dialog.dismiss();
                        startActivity(new Intent(ctx, AddSupportActivity.class));
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(getString(R.string.call_developer)).setPositiveButton(getString(R.string.developer_continue), dialogClickListener)
                .setNegativeButton(getString(R.string.developer_cancel), dialogClickListener).setNeutralButton(getString(R.string.developer_support), dialogClickListener);
        Dialog d=builder.create();
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);

        d.show();

        try {
            ((AlertDialog)d).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(ctx,R.color.colorAccent));
            ((AlertDialog)d).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ctx,R.color.colorAccent));
            ((AlertDialog)d).getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(ContextCompat.getColor(ctx,R.color.colorAccent));
        }catch (Exception e)
        {}


    }



    public String get_html(String body)
    {
        String head1 = "<head><style>@font-face {font-family: 'font1';src: url('file:///android_asset/font1.ttf');}body {font-family: 'font1';}</style></head>";

        String text = "<html>"
                +head1
                + "<body dir=RTL style=\"text-align:justify;color: #000000;direction:rtl\" >"
                + "<p dir=RTL align=\"justify\">"
                + body
                + "</p> "
                + "</body></html>";
        return text ;
    }

    //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
    //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
    //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
    //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
    //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
    //&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&






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
