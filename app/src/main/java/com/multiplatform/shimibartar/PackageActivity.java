package com.multiplatform.shimibartar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;

import com.google.gson.reflect.TypeToken;
import com.multiplatform.helper.EncryptHelper;
import com.multiplatform.helper.NewAsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chootdev.recycleclick.RecycleClick;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.multiplatform.adapter.LessonAdapter;
import com.multiplatform.helper.Constant;
import com.multiplatform.helper.MultiplatformHelper;
import com.multiplatform.helper.Parser;
import com.multiplatform.model.DiscountObject;
import com.multiplatform.model.PostObject;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.LinkedList;

import jp.wasabeef.glide.transformations.BlurTransformation;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class PackageActivity extends AppCompatActivity {



    SharedPreferences sp ;
    SharedPreferences.Editor spe;
    Context ctx;
    String webservice="";
    RecyclerView recycle_view;
    LinkedList<PostObject> mlist;
    LessonAdapter adapter;

    boolean refreshing =false;
    View loading;
    //TextView error_msg;
    PostObject data =new PostObject();
    TextView error_msg;
    DiscountObject discount_data = null ;
    LinkedList<String> user_permissions = new LinkedList<>();
    LinkedList<String> package_permissions = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.package_layout);
        ctx=this;
        sp= getSharedPreferences("init", Activity.MODE_PRIVATE);
        spe = sp.edit();
        webservice= Constant.get_webservice();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        error_msg=(TextView) findViewById(R.id.error_msg);
        loading= findViewById(R.id.loading_view);


        try {
            data = new Gson().fromJson(getIntent().getStringExtra("data"),PostObject.class);
            if(data==null)
            {
                data=new PostObject();
                finish();
            }
        }catch (Exception e){
            finish();
        }
        //error_msg=(TextView) findViewById(R.id.error_msg);
        recycle_view=(RecyclerView) findViewById(R.id.recycle_view);
        mlist=new LinkedList<PostObject>();
        adapter=new LessonAdapter(mlist,this);
        LinearLayoutManager layoutManager3= new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false);
        recycle_view.setAdapter(adapter);
        recycle_view.setLayoutManager(layoutManager3);
        RecycleClick.addTo(recycle_view).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                boolean is_sample =false ;
                if(data.sample!=null && data.sample.post_id.equals(mlist.get(position).post_id))
                    is_sample=true ;

                if(have_permission==false && is_sample == false && MultiplatformHelper.is_buy(ctx,data)==false)
                {
                    MultiplatformHelper.show_toast(getString(R.string.dont_have_permission),"red",ctx);
                    return;
                }
                mlist.get(position).cover_image = data.image;
                if(mlist.get(position).file_type.equals("audio") || mlist.get(position).file_type.equals("video"))
                {
                    final String src_path =MultiplatformHelper.get_directory(ctx)+"/"+mlist.get(position).post_id+"_"+mlist.get(position).file_name;// encrypted file
                    if(new File(src_path).exists()==false){
                        online_offline_dialog(position);
                    }else{
                        open_or_download_file(position,true);
                    }

                }else if(mlist.get(position).file_type.equals("image") )
                {
                    Intent intent=new Intent(ctx, ImageActivity.class);
                    intent.putExtra("data",new Gson().toJson(mlist.get(position)));
                    startActivity(intent);
                }else if(mlist.get(position).file_type.equals("pdf") )
                {
                    open_or_download_file(position,false);
                }


            }
        });

        error_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(refreshing==false) {
                    new asynctask_get_data("0").execute();
                }
            }
        });
        findViewById(R.id.support_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(ctx,SupportActivityOld.class));
                Intent intent=new Intent(ctx,QuestionListActivity.class);
                intent.putExtra("category_name","general");
                intent.putExtra("title",getString(R.string.support));
                startActivity(intent);
            }
        });

        if(isDestroyed()==false)
        {
            Glide.with(ctx).load(data.image)
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3)))
                    .into((ImageView)findViewById(R.id.bg_image));
            Glide.with(ctx).load(data.image).into((ImageView)findViewById(R.id.image));
        }

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
        int error=0;
        String index="0";

        public asynctask_get_data(String index)
        {
            this.index=index;
        }
        @Override
        protected Boolean doInBackground(Boolean... params) {





            String temp=postRequest("8",index);
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
                        user_permissions.clear();
                        package_permissions.clear();
                        try {
                            for(int i=0 ; i<json_obj.getJSONArray("package_permissions").length();i++){
                                package_permissions.add(json_obj.getJSONArray("package_permissions").get(i).toString());
                            }
                        }catch (Exception e){}

                        try {
                            for(int i=0 ; i<json_obj.getJSONArray("user_permissions").length();i++){
                                user_permissions.add(json_obj.getJSONArray("user_permissions").get(i).toString());
                            }
                        }catch (Exception e){}


                        spe.putString("user_packages",json_obj.getString("user_packages")).commit();
                        spe.putString("total_buy",json_obj.getString("total_buy")).commit();
                        try {
                            data=Parser.get_post_object(ctx,json_obj.getJSONObject("package"));
                        }catch (Exception e)
                        {
                            error=3;
                            return false;
                        }

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

            show_loading();

            if(index.equals("0"))
            {
                mlist.clear();
                adapter.notifyDataSetChanged();
            }



        }



        @Override
        protected void onPostExecute(Boolean result) {

            dismiss_loading();
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
                    show_msg(getString(R.string.no_lesson_item));

            }


            else if(error==0)
            {
                if(index.equals("0"))
                {
                    mlist.clear();
                }
                if (data.sample!=null)
                    mlist.add(data.sample);
                adapter.package_data=data ;
                mlist.addAll(data.lessons);
                adapter.mList=mlist;
                if(mlist.size()==0)
                {
                    show_msg(getString(R.string.no_lesson_item));
                }
                init();
            }
            adapter.notifyDataSetChanged();
        }

    }

    public class asynctask_discount_code extends NewAsyncTask<Boolean,Boolean,Boolean> {


        //public boolean gcm_result=false ;
        int error=0;

        @Override
        protected Boolean doInBackground(Boolean... params) {





            String temp=postRequest("11","");
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
                        try {
                            discount_data=Parser.get_discount_object(ctx,json_obj.getJSONObject("data"));
                        }catch (Exception e)
                        {
                            error=2;
                            return false;
                        }

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

            if(code_loading!=null)
                code_loading.setVisibility(View.VISIBLE);



        }



        @Override
        protected void onPostExecute(Boolean result) {

            if(code_loading!=null)
                code_loading.setVisibility(View.GONE);
            if(error==1)
            {
                Toast.makeText(ctx,getString(R.string.connection_error),Toast.LENGTH_SHORT).show();
            } else if(error==3)
            {
                Toast.makeText(ctx,getString(R.string.programm_error),Toast.LENGTH_SHORT).show();
            }
            else if(error==2)
            {
                Toast.makeText(ctx,getString(R.string.error_discount_code),Toast.LENGTH_SHORT).show();
            }


            else if(error==0)
            {
                if(discount_data!=null){
                    try {
                        get_discount_price();
                        Toast.makeText(ctx,getString(R.string.discount_code_accepted),Toast.LENGTH_SHORT).show();
                        discount_price_tv.setText(getString(R.string.price)+" "
                                +MultiplatformHelper.get_price(get_discount_price())+" "+getString(R.string.toman));
                        discount_price_tv.setVisibility(View.VISIBLE);
                        discount_line.setVisibility(View.VISIBLE);
                        dialog_price_tv.setTextColor(ContextCompat.getColor(ctx,R.color.material_red));




                        code_et.setEnabled(false);
                        selected_discount_code = discount_data.discount_code ;
                    }catch (Exception e){}
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
        fb.add("user_id",sp.getString("user_id",""));
        fb.add("post_id",data.post_id);
        if(code_et!=null)
            fb.add("discount_code",code_et.getText().toString());




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






    boolean have_permission = false ;
    public void init()
    {
        //**************************** permissions****************
        //**************************** permissions****************
        //**************************** permissions****************
        have_permission = false;
        if(package_permissions.size()==0)
            have_permission=true ;

        for(int i=0;i<user_permissions.size();i++)
        {
            for (int j=0 ; j<package_permissions.size();j++)
            {
                if(user_permissions.get(i).equals(package_permissions.get(j)))
                {
                    have_permission = true ;
                    break;
                }
            }
        }
        if(have_permission)
            data.package_type = "free";
        //****************************
        //****************************
        //****************************



        if(isDestroyed()==false)
        {
            Glide.with(ctx).load(data.image)
                    .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3)))
                    .into((ImageView)findViewById(R.id.bg_image));
            Glide.with(ctx).load(data.image).into((ImageView)findViewById(R.id.image));
        }

        ((TextView)findViewById(R.id.title_tv)).setText(data.title);
        ((TextView)findViewById(R.id.toolbar_title_tv)).setText(data.title);
        findViewById(R.id.toolbar_title_tv).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.toolbar_title_tv)).setSelected(true);
        ((TextView)findViewById(R.id.duration_tv)).setText(data.durations);
        ((TextView)findViewById(R.id.title_tv)).setSelected(true);

        ((TextView)findViewById(R.id.price_prev_tv)).setText(MultiplatformHelper.get_price(data.price_prev)+" "+ctx.getString(R.string.toman));
        ((TextView)findViewById(R.id.price_prev_tv)).setPaintFlags(((TextView)findViewById(R.id.price_prev_tv)).getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        if(data.package_type.equals("free") || data.price_prev.equals("0")|| data.price_prev.equals(""))
            ((TextView)findViewById(R.id.price_prev_tv)).setVisibility(View.GONE);

        if(!data.package_type.equals("free"))
            ((TextView)findViewById(R.id.price_tv)).setText(getString(R.string.buy)+"( "+MultiplatformHelper.get_price(data.price)+" "+getString(R.string.toman)+" )");
        if(have_permission)
            ((TextView)findViewById(R.id.buy_tv)).setText(getString(R.string.have_permission));
        else
            ((TextView)findViewById(R.id.buy_tv)).setText(MultiplatformHelper.get_buy_string(ctx,data));


        ((AppBarLayout)findViewById(R.id.app_bar_layout)).addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                final boolean isCollapsed = (verticalOffset == (-1 * appBarLayout.getTotalScrollRange()));
                if(isCollapsed) {
                    ((TextView)findViewById(R.id.toolbar_title_tv)).setVisibility(View.VISIBLE);

                }
                else {
                    ((TextView)findViewById(R.id.toolbar_title_tv)).setVisibility(View.GONE);
                }

            }
        });

        findViewById(R.id.comment_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ctx,CommentActivity.class).putExtra("post_id",data.post_id));
            }
        });

        findViewById(R.id.buy_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!have_permission){
                    if(MultiplatformHelper.is_buy(ctx,data)==false)
                    {
                        show_buy_dialog();
                    }
                }

            }
        });
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



    public void show_loading()
    {
        error_msg.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);

    }
    public void dismiss_loading()
    {
        loading.setVisibility(View.GONE);
    }






















    Dialog dialog ;
    EditText code_et ;
    TextView dialog_price_tv ;
    TextView discount_price_tv ;
    View discount_line ;
    View code_loading ;
    boolean code_refreshing=false ;
    String selected_discount_code = "";


    public void show_buy_dialog()
    {

        final View view = View.inflate(ctx, R.layout.dialog_buy_layout, null);
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
        ((TextView)view.findViewById(R.id.title_tv)).setText(data.title);
        if(isDestroyed()==false)
        {
            Glide.with(ctx).load(data.image).into((ImageView)view.findViewById(R.id.image));
        }
        view.findViewById(R.id.my_discount_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ctx,DiscountListActivity.class));
            }
        });

        view.findViewById(R.id.paste_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                String pasteData = "";
                // If it does contain data, decide if you can handle the data.
                if (!(clipboard.hasPrimaryClip())) {
                } else if (!(clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {
                    // since the clipboard has data but it is not plain text
                } else {
                    //since the clipboard contains plain text.
                    ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                    // Gets the clipboard as text.
                    pasteData = item.getText().toString();
                    if(code_et.isEnabled())
                        code_et.setText(pasteData);
                }

            }
        });

        code_et = ((EditText) view.findViewById(R.id.code_et));
        dialog_price_tv = ((TextView) view.findViewById(R.id.price_tv));
        discount_price_tv = ((TextView) view.findViewById(R.id.discount_price_tv));
        discount_line = view.findViewById(R.id.discount_line);
        discount_price_tv.setVisibility(View.GONE);
        discount_line.setVisibility(View.GONE);


        code_loading = view.findViewById(R.id.code_loading);
        dialog_price_tv.setText(getString(R.string.price)+" "
                +MultiplatformHelper.get_price(data.price)+" "+getString(R.string.toman));

        view.findViewById(R.id.code_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(code_refreshing==false && code_et!=null && code_et.isEnabled()==true && !code_et.getText().toString().trim().equals(""))
                    new asynctask_discount_code().execute();

            }
        });
        view.findViewById(R.id.buy_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                try {
                    spe.putBoolean("reload_payment",true).commit();
                    String values = sp.getString("user_id","")+"***"+data.post_id+"***"+selected_discount_code;
                    String v1 = "token=" + URLEncoder.encode(MultiplatformHelper.base64encode(values), "UTF-8");
                    MultiplatformHelper.open_url(ctx,Constant.get_pay_webservice()+v1);
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


    public String get_discount_price()
    {
        String result = data.price ;
        int temp = Integer.valueOf(data.price)-Integer.valueOf(discount_data.price);
        if(temp<0)
            temp = 0;
        result = temp+"";
        return result ;
    }






    public void open_or_download_file(int pos,boolean is_encrypt)
    {

        final String src_path =MultiplatformHelper.get_directory(ctx)+"/"+mlist.get(pos).post_id+"_"+mlist.get(pos).file_name;// encrypted file
        final String directory =MultiplatformHelper.get_directory(ctx);
        final String name=mlist.get(pos).post_id+"_"+mlist.get(pos).real_file_name;// normal file
        final String extention=mlist.get(pos).extention;// normal file
        if(new File(src_path).exists()==false)
        {

            mlist.get(pos).progress = 0 ;
            if(mlist.get(pos).is_downloading==true)
            {
                mlist.get(pos).is_downloading =false ;
                if(mlist.get(pos).download_id!=-1)
                    PRDownloader.pause(mlist.get(pos).download_id);
            }
            else
            {

                mlist.get(pos).is_downloading = true ;
                if(mlist.get(pos).download_id!=-1) {
                    PRDownloader.resume(mlist.get(pos).download_id);
                    adapter.notifyItemChanged(pos);
                    return;
                }
                mlist.get(pos).download_id = PRDownloader.download(mlist.get(pos).file_link,
                        directory, name+"."+extention)
                        .build()
                        .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                            @Override
                            public void onStartOrResume() {
                                mlist.get(pos).is_downloading =true ;
                                adapter.notifyItemChanged(pos);
                            }
                        })
                        .setOnPauseListener(new OnPauseListener() {
                            @Override
                            public void onPause() {
                                mlist.get(pos).is_downloading =false ;
                                adapter.notifyItemChanged(pos);

                            }
                        })
                        .setOnCancelListener(new OnCancelListener() {
                            @Override
                            public void onCancel() {
                                mlist.get(pos).is_downloading =false ;
                                adapter.notifyItemChanged(pos);
                            }
                        })
                        .setOnProgressListener(new OnProgressListener() {
                            @Override
                            public void onProgress(Progress progress) {
                                double downloaded=progress.currentBytes;
                                double total=progress.totalBytes;

                                double temp=downloaded/total;
                                float t=(float)temp ;
                                temp=temp*100;
                                int p=(int)temp;

                                mlist.get(pos).progress  = p ;
                                adapter.notifyItemChanged(pos);

                            }
                        })
                        .start(new OnDownloadListener() {
                            @Override
                            public void onDownloadComplete() {

                                // download complete
                                mlist.get(pos).is_downloading =false ;
                                adapter.notifyItemChanged(pos);
                                if(is_encrypt){
                                    new EncryptHelper(ctx, src_path,null,true) {
                                        @Override
                                        public void completed(boolean result) {
                                            if(result){
                                                MultiplatformHelper.show_toast(ctx.getString(R.string.download_complete),"green",ctx);
                                                mlist.get(pos).is_downloading =false ;
                                                adapter.notifyItemChanged(pos);


                                                // for offline items.............
                                                // for offline items.............
                                                try {
                                                    Type listType = new TypeToken<LinkedList<PostObject>>() {}.getType();
                                                    LinkedList<PostObject> offline_items =new Gson().fromJson(sp.getString("offline_items",""), listType);
                                                    if(offline_items == null)
                                                        offline_items = new LinkedList<>();
                                                    mlist.get(pos).offline_parent_id = data.post_id;
                                                    mlist.get(pos).offline_parent_title = data.title;
                                                    offline_items.add(mlist.get(pos));
                                                    spe.putString("offline_items", new Gson().toJson(offline_items, listType)).commit();

                                                }catch (Exception e){}
                                                // for offline items.............
                                                // for offline items.............
                                            }else{
                                                mlist.get(pos).is_downloading =false ;
                                                adapter.notifyItemChanged(pos);
                                            }

                                        }
                                    }.execute();

                                }else {
                                    MultiplatformHelper.show_toast(ctx.getString(R.string.download_complete),"green",ctx);

                                }



                            }
                            @Override
                            public void onError(Error error) {
                                mlist.get(pos).is_downloading =false ;
                                adapter.notifyItemChanged(pos);
                                //show_msg(getString(R.string.programm_error),"red");
                            }
                        });
            }
        }else
        {
            if(mlist.get(pos).file_type.equals("audio") || mlist.get(pos).file_type.equals("video")){
                open_multimedia(pos);
            }
            else if(mlist.get(pos).file_type.equals("pdf") ){
                open_pdf(src_path);
            }
        }
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

    public void online_offline_dialog(final int pos)
    {
        final String src_path =MultiplatformHelper.get_directory(ctx)+"/"+mlist.get(pos).post_id+"_"+mlist.get(pos).file_name+".temp";
        String download_btn_label = getString(R.string.download) ;
        if(new File(src_path).exists())
            download_btn_label = getString(R.string.continue_downloading) ;
        if(mlist.get(pos).is_downloading==true)
            download_btn_label = getString(R.string.stop_downloading) ;
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        open_multimedia(pos);
                        dialog.dismiss();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        open_or_download_file(pos,true);
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        Dialog dialog=builder.setMessage(getString(R.string.download_dialog_desc)).setPositiveButton(getString(R.string.online_show), dialogClickListener)
                .setNegativeButton(download_btn_label, dialogClickListener).create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();

        try {
            ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(ctx,R.color.colorPrimaryDark));
            ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(ctx,R.color.colorPrimaryDark));
            ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(ContextCompat.getColor(ctx,R.color.colorPrimaryDark));
        }catch (Exception e)
        {}
    }



    // =================== offline show ==========================================
    // =================== offline show ==========================================
    // =================== offline show ==========================================
    // =================== offline show ==========================================





    public void open_pdf(String path)
    {
        File file = new File(path);
        if (file.exists())
        {
            try {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri fileUri = null ;
                if(Build.VERSION.SDK_INT>=24){
                    fileUri = FileProvider.getUriForFile(ctx, ctx.getApplicationContext().getPackageName() + ".com.multiplatform.shimibartar", new File(path));
                }
                else {
                    fileUri= Uri.fromFile(new File(path));
                }

                intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(fileUri, "application/pdf");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(intent, "chose"));
                ctx.startActivity(Intent.createChooser(intent,"Share"));

            }catch (Exception e){
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(refreshing==false && sp.getBoolean("reload_payment",false))
        {
            spe.putBoolean("reload_payment",false).commit();
            new asynctask_get_data("0").execute();
        }
    }
}
