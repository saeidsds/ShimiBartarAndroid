package com.multiplatform.shimibartar;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;

import com.google.gson.Gson;
import com.multiplatform.adapter.HomePackageAdapter;
import com.multiplatform.helper.Constant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import com.multiplatform.helper.NewAsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.chootdev.recycleclick.RecycleClick;
import com.multiplatform.adapter.HomeCategotyAdapter;
import com.multiplatform.fragment.ImageFragment;

import com.multiplatform.helper.CustomSwipeToRefresh;
import com.multiplatform.helper.FireBaseTokenSender;
import com.multiplatform.helper.MultiplatformHelper;
import com.multiplatform.helper.Parser;
import com.multiplatform.helper.SearchHandler;
import com.multiplatform.helper.ShareHelper;
import com.multiplatform.model.CategoryObject;
import com.multiplatform.model.PostObject;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

import io.codetail.animation.ViewAnimationUtils;
import me.relex.circleindicator.CircleIndicator;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity  {


    SharedPreferences sp ;
    SharedPreferences.Editor spe;
    Context ctx;
    String webservice="";
    TextView error_msg ;

    boolean refreshing = false ;



    ExpandableLayout menu_layout;
    ImageView menu_open_iv , menu_close_iv ;
    //SwipeRefreshLayout refresh_layout;
    static LinkedList<PostObject> slider_list=new LinkedList();
    LinkedList<CategoryObject> cat_list=new LinkedList();


    RecyclerView cat_recycle;
    ViewPager viewpager ;


    ImageSliderAdapter slider_adapter;
    HomeCategotyAdapter cat_adapter ;


    LinkedList<LinkedList<PostObject>> mlist_array=new LinkedList();
    LinkedList<View> row_view_array=new LinkedList();
    LinkedList<String> title_row_array=new LinkedList();
    LinkedList<HomePackageAdapter> adapter_array=new LinkedList();

    CustomSwipeToRefresh refresh_layout;
    CircleIndicator indicator;


    Handler exit_handler ;
    Handler slider_handler ;
    int exit_try = 0 ;
    SearchHandler search_handler;
    String selected_string = "" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout2);
        getSupportActionBar().hide();
        ctx=this;

        sp= getSharedPreferences("init", Activity.MODE_PRIVATE);
        spe = sp.edit();
        webservice= Constant.get_webservice();
        menu_layout =(ExpandableLayout) findViewById(R.id.expandable_menu);
        menu_open_iv =(ImageView) findViewById(R.id.drawer_open);
        menu_close_iv =(ImageView) findViewById(R.id.drawer_close);
        error_msg =(TextView) findViewById(R.id.error_msg);

        exit_handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                exit_try= 0 ;
            }
        };

        slider_handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                slider_handler.sendEmptyMessageDelayed(0,5000);
                try {
                    int index = viewpager.getCurrentItem();
                    index++;
                    if(index>=slider_adapter.getCount())
                        index = 0 ;
                    if(slider_adapter.getCount()>0)
                    {
                        viewpager.setCurrentItem(index,true);
                    }
                }catch (Exception e){}
                //viewpager.setCurrentItem(xxx);
            }
        };
        slider_handler.sendEmptyMessageDelayed(0,5000);


        refresh_layout=(CustomSwipeToRefresh)findViewById(R.id.swipyrefreshlayout);
        viewpager=findViewById(R.id.image_pager);
        slider_adapter=new ImageSliderAdapter(getSupportFragmentManager());
        viewpager.setAdapter(slider_adapter);
        indicator =findViewById(R.id.indicator);
        //slider_list.add(new PostObject());
        indicator.setViewPager(viewpager);



        refresh_layout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if(refreshing==false)
                {
                    if(direction== SwipyRefreshLayoutDirection.TOP)
                    {
                        new asynctask_getdata().execute();
                    }

                }
            }
        });

        search_handler=new SearchHandler(ctx);
        search_handler.setSearchListener(new SearchHandler.SearchListener() {
            @Override
            public void callback(String search) {
                selected_string=search;
                if(!selected_string.trim().equals("")) {
                    animate_search_bar();
                    startActivity(new Intent(ctx,SearchPackageListActivity.class).putExtra("search_text",selected_string));
                }
            }
        });





        cat_adapter=new HomeCategotyAdapter(cat_list,ctx);
        cat_recycle = (RecyclerView) findViewById(R.id.cat_recycle_view);
        LinearLayoutManager layoutManager2= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        layoutManager2.setReverseLayout(true);
        cat_recycle.setLayoutManager(layoutManager2);
        cat_recycle.setAdapter(cat_adapter);
        RecycleClick.addTo(cat_recycle).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                // ok in adapter
            }
        });

        for (int i =0;i<10;i++)
        {
            mlist_array.add(new LinkedList<PostObject>());
            title_row_array.add("");
            adapter_array.add(new HomePackageAdapter(mlist_array.get(i),ctx));

            LinearLayout recycle_container = (LinearLayout)findViewById(R.id.recycle_container);
            View parent_view = getLayoutInflater().inflate(R.layout.row_home_recycle, null);
            row_view_array.add(parent_view);
            LinearLayoutManager layoutManager3= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
            layoutManager3.setReverseLayout(true);
            ((RecyclerView)row_view_array.get(i).findViewById(R.id.recycle_view)).setLayoutManager(layoutManager3);
            ((RecyclerView)row_view_array.get(i).findViewById(R.id.recycle_view)).setAdapter(adapter_array.get(i));
            int finalI = i;
            RecycleClick.addTo(((RecyclerView)row_view_array.get(i).findViewById(R.id.recycle_view))).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                    Intent intent=new Intent(ctx, PackageActivity.class);
                    intent.putExtra("data",new Gson().toJson(mlist_array.get(finalI).get(position)));
                    startActivity(intent);

                }
            });
            recycle_container.addView(parent_view);
        }




//        final RatingDialog ratingDialog = new RatingDialog.Builder(this)
////                .session(5)
////                .title(getString(R.string.rate_us_title))
////                .titleTextColor(R.color.black)
////                .positiveButtonText(getString(R.string.not_now))
////                .negativeButtonText(getString(R.string.never))
////                .positiveButtonTextColor(R.color.color_primary)
////                .negativeButtonTextColor(R.color.grey_500)
////                .ratingBarColor(R.color.ratingbar_color)
////                .playstoreUrl(Constant.get_market_url())
////                .build();
////
////        ratingDialog.show();

















        


        findViewById(R.id.drawer_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animate_drawer_icon();
                menu_layout.toggle(true);
            }
        });
        findViewById(R.id.menu_background).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animate_drawer_icon();
                menu_layout.toggle(true);
            }
        });

        findViewById(R.id.search_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(ctx,SearchPodcastActivity.class));
                animate_search_bar();
            }
        });

        findViewById(R.id.close_search_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animate_search_bar();
            }
        });

        new FireBaseTokenSender(ctx);






        //=======================    Buttom bar      ===========================================

        findViewById(R.id.bottom_profile_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ctx,ProfileActivity.class));
            }
        });

        findViewById(R.id.bottom_category_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ctx,CategoryListActivity1.class));
            }
        });

        findViewById(R.id.bottom_all_comment_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ctx,AllCommentActivity.class));
            }
        });
//
//
        findViewById(R.id.bottom_question_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show_select_question_dialog();
                //Intent intent=new Intent(ctx,QuestionListActivity.class);
                Intent intent=new Intent(ctx,SupportListActivity.class);
                intent.putExtra("category_name","general");
                intent.putExtra("title",getString(R.string.support));
                startActivity(intent);
            }
        });

        findViewById(R.id.bottom_comment_room_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(ctx,CommentActivity.class);
                intent.putExtra("title",getString(R.string.comment_room));
                intent.putExtra("post_id",sp.getString("public_comment_post_id",""));
                startActivity(intent);

            }
        });












        //=======================    Menu     ===========================================

        findViewById(R.id.profile_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ctx,ProfileActivity.class));
            }
        });



        findViewById(R.id.my_package_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(ctx,MyPackageListActivity.class));
            }
        });

        findViewById(R.id.offline_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(ctx, OfflineLessonActivity.class));
            }
        });


        findViewById(R.id.notification_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ctx,NotificationListActivity.class));
            }
        });

        findViewById(R.id.question_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ctx, QuestionListActivity.class));
            }
        });
        findViewById(R.id.support_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ctx, SupportListActivity.class));
            }
        });



        findViewById(R.id.terms_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ctx,TermsAndCondisionActivity.class));
            }
        });



        findViewById(R.id.about_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ctx,AboutActivity.class));
            }
        });


        findViewById(R.id.share_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareHelper.share_text(ctx,sp.getString("share_app_message",""));
            }
        });


        findViewById(R.id.developer_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ctx,DeveloperActivity.class));
            }
        });




        Toast.makeText(ctx,sp.getString("name","")+" "+getString(R.string.welcome_message),Toast.LENGTH_SHORT).show();
        new asynctask_getdata().execute();

    }






//============================================= Asynctask ================================
//============================================= Asynctask ================================
//============================================= Asynctask ================================
//============================================= Asynctask ================================


    public class asynctask_getdata extends NewAsyncTask<Boolean,Boolean,Boolean> {


        //public boolean gcm_result=false ;
        //LinkedList<PostObject> temp_list;

        LinkedList<PostObject> temp_slider;
        LinkedList<CategoryObject> temp_cat;
        LinkedList<LinkedList<PostObject>> temp_mlist_array=new LinkedList();
        LinkedList<String> temp_title_array=new LinkedList();
        int error=0;


        @Override
        protected Boolean doInBackground(Boolean... params) {



            temp_mlist_array=new LinkedList<>();
            temp_slider=new LinkedList<PostObject>();
            temp_cat=new LinkedList<CategoryObject>();
            String temp=postRequest();
            if(temp==null)
            {

                error=1;
                return false;
            }
            temp= Parser.get_json(temp);
            if(temp!=null && !temp.trim().equals(""))
            {
                JSONArray jArray = null ;
                JSONArray jArray2 = null ;
                JSONObject json_obj=null;
                try {

                    json_obj = new JSONObject(temp);
                    String temp_result=json_obj.getString("result");
                    if(temp_result.trim().equals("true"))
                    {

                        try {
                            jArray = json_obj.getJSONArray("list_slider");
                            if(jArray!=null && jArray.length()!=0)
                            {
                                try {
                                    temp_slider=new LinkedList<PostObject>();
                                    Parser.get_post_array(ctx,jArray,temp_slider);

                                }catch (Exception e)
                                {}
                            }
                        }catch (Exception e)
                        {}


                        try {
                            jArray = json_obj.getJSONArray("list_category");
                            if(jArray!=null && jArray.length()!=0)
                            {
                                try {
                                    temp_cat=new LinkedList<CategoryObject>();
                                    Parser.get_category_array(ctx,jArray,temp_cat);


                                }catch (Exception e)
                                {}
                            }
                        }catch (Exception e)
                        {}



                        try {
                            jArray = json_obj.getJSONArray("list_row");
                            if(jArray!=null && jArray.length()!=0)
                            {
                                for (int i=0;i<jArray.length();i++)
                                {
                                    try {
                                        String title_row = jArray.getJSONObject(i).getString("title_row");
                                        if(title_row==null)
                                            title_row="";
                                        temp_title_array.add(title_row);
                                        temp_mlist_array.add(new LinkedList<>());
                                        if(jArray.getJSONObject(i).getJSONArray("list")!=null) {
                                            Parser.get_post_array(ctx, jArray.getJSONObject(i).getJSONArray("list"), temp_mlist_array.get(i));
                                        }
                                    }catch (Exception e){}
                                }
                            }
                        }catch (Exception e)
                        {}

                        error=0;
                        return true;
                    }
                    else
                    {
                        error=3;
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

            slider_list.clear();
            cat_list.clear();
            for (int i=0;i<adapter_array.size();i++){
                mlist_array.get(i).clear();
                adapter_array.get(i).notifyDataSetChanged();
            }
            slider_adapter.notifyDataSetChanged();
            cat_adapter.notifyDataSetChanged();


            refreshing=true ;
            show_loading();


        }



        @Override
        protected void onPostExecute(Boolean result) {

            //dismiss_loading();
            refreshing=false ;
            refresh_layout.setRefreshing(false);
            refresh_layout.post(new Runnable() {
                @Override
                public void run() {
                    refresh_layout.setRefreshing(false);
                }
            });
            findViewById(R.id.loading).setVisibility(View.GONE);
            if(error==1)
            {

                show_msg(getString(R.string.connection_error));
            } else if(error==3)
            {

                show_msg(getString(R.string.programm_error));
            }
            else if(error==2 )
            {
                findViewById(R.id.loading_cnt).setVisibility(View.VISIBLE);
                findViewById(R.id.loading).setVisibility(View.GONE);
                show_msg(getString(R.string.no_data));
            }

            else if(error==0)
            {
                findViewById(R.id.loading_cnt).setVisibility(View.GONE);
                if(temp_slider.size()>0)
                {
                    slider_list=temp_slider;
                    slider_adapter.notifyDataSetChanged();
                    findViewById(R.id.image_main_container).setVisibility(View.VISIBLE);
                }else {
                    findViewById(R.id.image_main_container).setVisibility(View.GONE);
                }



                if(temp_cat.size()>0)
                {
                    cat_list=temp_cat;
                    cat_adapter.mList=cat_list;
                    cat_adapter.notifyDataSetChanged();
                    findViewById(R.id.cat_cnt).setVisibility(View.VISIBLE);
                }else {
                    findViewById(R.id.cat_cnt).setVisibility(View.GONE);
                }


                mlist_array = temp_mlist_array;
                title_row_array = temp_title_array;
                for (int i=0;i<mlist_array.size();i++){
                    adapter_array.get(i).mList=mlist_array.get(i);
                    adapter_array.get(i).notifyDataSetChanged();
                    if (mlist_array.get(i)==null || mlist_array.get(i).size()==0)
                    {
                        row_view_array.get(i).setVisibility(View.GONE);
                    }
                    try {
                        ((TextView)row_view_array.get(i).findViewById(R.id.title_tv)).setText(title_row_array.get(i));
                    }catch (Exception e){}
                }





            }

            slider_adapter.notifyDataSetChanged();
            cat_adapter.notifyDataSetChanged();
            indicator.setViewPager(viewpager);

        }

    }



    public String postRequest()
    {

        RequestBody formBody=null;
        FormBody.Builder fb = new FormBody.Builder();

        fb.add("num","6");
        fb.add("user_id",sp.getString("user_id",""));
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

        error_msg.setVisibility(View.VISIBLE);
        error_msg.setText(msg);
    }



    public void show_msg(String msg,String color)
    {
        error_msg.setVisibility(View.VISIBLE);
        error_msg.setText(msg);
        MultiplatformHelper.show_msg(msg,color,ctx,findViewById(R.id.toolbar));
    }

    public void show_loading()
    {
        error_msg.setText("");
        error_msg.setVisibility(View.GONE);
        findViewById(R.id.loading_cnt).setVisibility(View.VISIBLE);
        findViewById(R.id.loading).setVisibility(View.VISIBLE);
        refresh_layout.post(new Runnable() {
            @Override
            public void run() {
                refresh_layout.setRefreshing(true);
            }
        });

    }
    public void dismiss_loading()
    {
        //refresh_layout.setRefreshing(false);
        findViewById(R.id.loading_cnt).setVisibility(View.GONE);
    }

    public void animate_drawer_icon()
    {

        final int duration = 170 ;
        final int alpha_duration = 30 ;
        View v1 ,v2 ;
        if(menu_layout.isExpanded()==false)
        {
            v1 = menu_open_iv ;
            v2 = menu_close_iv ;
            findViewById(R.id.menu_background).setVisibility(View.VISIBLE);
            ObjectAnimator.ofFloat(findViewById(R.id.menu_background), "alpha", 0f, 1f).setDuration(100).start();
        }
        else {
            v1 = menu_close_iv ;
            v2 = menu_open_iv ;
            findViewById(R.id.menu_background).setVisibility(View.GONE);
            ObjectAnimator.ofFloat(findViewById(R.id.menu_background), "alpha", 1f, 0f).setDuration(100).start();
        }
        final View view1=v1 ;
        final View view2=v2 ;
        view1.setPivotX(view1.getWidth()/2);
        view1.setPivotY(view1.getHeight()/2);
        view2.setPivotX(view2.getWidth()/2);
        view2.setPivotY(view2.getHeight()/2);

        ObjectAnimator anim1x= ObjectAnimator.ofFloat(view1, "scaleX", 1f, 0f).setDuration(duration);
        ObjectAnimator anim1y= ObjectAnimator.ofFloat(view1, "scaleY", 1f, 0f).setDuration(duration);
        ObjectAnimator anim1alpha= ObjectAnimator.ofFloat(view1, "alpha", 1f, 0.0f).setDuration(alpha_duration);
        anim1alpha.setStartDelay(duration-alpha_duration);

        view1.setVisibility(View.VISIBLE);
        anim1alpha.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                view2.setVisibility(View.VISIBLE);
                ObjectAnimator anim2x= ObjectAnimator.ofFloat(view2, "scaleX", 0f, 1f).setDuration(duration);
                ObjectAnimator anim2y= ObjectAnimator.ofFloat(view2, "scaleY", 0f, 1f).setDuration(duration);
                ObjectAnimator anim2alpha= ObjectAnimator.ofFloat(view2, "alpha", 0f, 1f).setDuration(alpha_duration);
                anim2x.start();
                anim2y.start();
                anim2alpha.start();
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        anim1x.start();
        anim1y.start();
        anim1alpha.start();
    }

    @Override
    public void onBackPressed() {
        if(menu_layout.isExpanded() == false)
        {
            exit_try ++ ;
            if(exit_try<=1)
            {
                exit_handler.sendEmptyMessageDelayed(0,3000);
                Toast.makeText(ctx,getString(R.string.exit_try),Toast.LENGTH_SHORT).show();
            }
            else
            {
                if(exit_handler!=null)
                    exit_handler.removeCallbacksAndMessages(null);
                super.onBackPressed();
            }
        }
        else
        {
            animate_drawer_icon();
            menu_layout.toggle(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            ((TextView)findViewById(R.id.drawer_name_tv)).setText(sp.getString("name",""));
            ((TextView)findViewById(R.id.drawer_mobile_tv)).setText(sp.getString("mobile",""));
        }catch (Exception e){}


    }



    //=============================== Image adapter
    public static class ImageSliderAdapter extends FragmentPagerAdapter {


        public ImageSliderAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public int getCount() {
            return slider_list.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }
        @Override
        public Fragment getItem(int position) {


            Bundle bundle = new Bundle();
            bundle.putString("data", new Gson().toJson(slider_list.get(position)));
            ImageFragment frag=new ImageFragment();
            frag.setArguments(bundle);
            return frag;

        }
    }



    int first=0;
    public void onWindowFocusChanged(boolean hasFocus) {

        super.onWindowFocusChanged(hasFocus);

        first++;
        if(first<=1)
        {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            //double height=metrics.heightPixels;
            double width=metrics.widthPixels;
            double height=(width*9)/16;
            int h=(int)height;
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)findViewById(R.id.image_main_container).getLayoutParams();
            if (params == null) {
                params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, h);
            } else {
                params.height = h;
            }
            if(h!=0)
                findViewById(R.id.image_main_container).setLayoutParams(params);
        }

    }








    public void animate_search_bar()
    {

        float drawer_radius =(float) Math.hypot(findViewById(R.id.toolbar_container).getWidth(), findViewById(R.id.toolbar_container).getHeight());

        View start_view = findViewById(R.id.search_btn);
        View search_bar = findViewById(R.id.search_bar);
        boolean is_expanded =false;
        if(search_bar.getVisibility()==View.VISIBLE)
            is_expanded=true;

        int cx = (start_view.getLeft() + start_view.getRight()) / 2;
        int cy = (start_view.getTop() + start_view.getBottom()) / 2;

        search_bar.setVisibility(View.VISIBLE);
        if(is_expanded==false)
        {

            Animator drawer_animator = ViewAnimationUtils.createCircularReveal(search_bar, cx, cy, 0,drawer_radius );
            drawer_animator.setInterpolator(new AccelerateInterpolator());
            drawer_animator.setDuration(500);
            drawer_animator.start();

        }
        else
        {
            search_handler.search_et.setText("");
            Animator drawer_animator = ViewAnimationUtils.createCircularReveal(search_bar, cx, cy, drawer_radius, 0);
            drawer_animator.setInterpolator(new AccelerateInterpolator());
            drawer_animator.setDuration(500);
            drawer_animator.addListener(new Animator.AnimatorListener() {
                @Override public void onAnimationStart(Animator animation) {}
                @Override public void onAnimationCancel(Animator animation) {}
                @Override public void onAnimationRepeat(Animator animation) {}
                @Override public void onAnimationEnd(Animator animation) {
                    search_bar.setVisibility(View.GONE);
                }
            });
            drawer_animator.start();
        }
    }











    Dialog dialog ;
    String selected_question_category="";

    public void show_select_question_dialog()
    {

        selected_question_category="";
        final View view = View.inflate(ctx, R.layout.dialog_select_question_cactegory_layout, null);
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
        view.findViewById(R.id.special_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_question_category = "special";
                ((ImageView)view.findViewById(R.id.special_iv)).setImageResource(R.drawable.chemistery_on);
                ((ImageView)view.findViewById(R.id.general_iv)).setImageResource(R.drawable.general);
            }
        });

        view.findViewById(R.id.general_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_question_category = "general";
                ((ImageView)view.findViewById(R.id.special_iv)).setImageResource(R.drawable.chemistery_off);
                ((ImageView)view.findViewById(R.id.general_iv)).setImageResource(R.drawable.general_on);
            }
        });


        view.findViewById(R.id.enter_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.findViewById(R.id.error_msg).setVisibility(View.VISIBLE);
                if(selected_question_category.equals("")) {
                    ((TextView)view.findViewById(R.id.error_msg)).setText(R.string.select_question_category_error1);
                    return;
                }
                try {
                    int total_buy = Integer.valueOf(sp.getString("total_buy","0"));
                    int min_price_for_question = Integer.valueOf(sp.getString("min_price_for_question","0"));
                    if(total_buy<min_price_for_question) {
                        ((TextView)view.findViewById(R.id.error_msg))
                                .setText(getString(R.string.select_question_category_error2)
                                        .replace("***",MultiplatformHelper.get_price(sp.getString("min_price_for_question","0"))));
                        return;
                    }
                }catch (Exception e){
                    ((TextView)view.findViewById(R.id.error_msg))
                            .setText(getString(R.string.select_question_category_error2)
                                    .replace("***",MultiplatformHelper.get_price(sp.getString("min_price_for_question","0"))));
                    return;
                }


                Intent intent=new Intent(ctx,QuestionListActivity.class);
                intent.putExtra("category_name",selected_question_category);
                if(selected_question_category.equals("general"))
                    intent.putExtra("title",getString(R.string.general));
                else
                    intent.putExtra("title",getString(R.string.special));
                startActivity(intent);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            slider_handler.removeCallbacksAndMessages(null);
        }catch (Exception e){}
    }

}
