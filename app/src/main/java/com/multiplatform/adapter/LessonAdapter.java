package com.multiplatform.adapter;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.multiplatform.helper.MultiplatformHelper;
import com.multiplatform.model.PostObject;
import com.multiplatform.shimibartar.R;

import java.io.File;
import java.lang.reflect.Type;
import java.util.LinkedList;


//=========================== my filter ====================================================


public class LessonAdapter extends RecyclerView.Adapter<RecycleHolder> {

    public Context ctx;
    public LinkedList<PostObject> mList;
    private LayoutInflater mInflater;
    SharedPreferences sp ;
    SharedPreferences.Editor spe;
    public PostObject package_data=new PostObject();
    // data is passed into the constructor
    public LessonAdapter(LinkedList<PostObject> list, Context c) {

        ctx=c ;
        mList = list ;
        mInflater = LayoutInflater.from(ctx);
        sp= ctx.getSharedPreferences("init", Activity.MODE_PRIVATE);
        spe = sp.edit();

    }

    @Override
    public RecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_lesson, parent, false);
        return new RecycleHolder(view);
    }


    @Override
    public void onBindViewHolder(RecycleHolder holder, int position) {
//        // set data
        if(mList.get(position).is_downloading) {
            holder.sample_loading_view.setVisibility(View.VISIBLE);
            holder.sample_progress.setVisibility(View.VISIBLE);
            holder.loading_view.setVisibility(View.VISIBLE);
            holder.progress.setVisibility(View.VISIBLE);

            holder.delete_btn.setVisibility(View.GONE);
            holder.download_lable.setVisibility(View.VISIBLE);
        }
        else{
            holder.sample_loading_view.setVisibility(View.GONE);
            holder.sample_progress.setVisibility(View.GONE);
            holder.loading_view.setVisibility(View.GONE);
            holder.progress.setVisibility(View.GONE);

            holder.delete_btn.setVisibility(View.GONE);
            holder.download_lable.setVisibility(View.GONE);
        }




        final String src_path =MultiplatformHelper.get_directory(ctx)+"/"+mList.get(position).post_id+"_"+mList.get(position).file_name;
        if(new File(src_path).exists()==false){
            holder.delete_btn.setVisibility(View.GONE);
        }else{
            holder.delete_btn.setVisibility(View.VISIBLE);
        }


        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_delete_dialog(position);
            }
        });















        holder.loading_view.setProgress(mList.get(position).progress);
        holder.sample_loading_view.setProgress(mList.get(position).progress);


        holder.progress.setText(mList.get(position).progress+"");
        holder.sample_progress.setText(mList.get(position).progress+"");




        if(position==0){
            holder.header_view.setVisibility(View.VISIBLE);
            holder.container.setVisibility(View.GONE);
            if(package_data.content!=null &&
                    !package_data.content.trim().equals("") &&
                    !package_data.content.trim().equals("null"))
                holder.content_cnt.setVisibility(View.VISIBLE);
            else
                holder.content_cnt.setVisibility(View.GONE);
            holder.content.setText(package_data.content);
            holder.sample_title.setText(mList.get(position).title);
            //holder.sample_image.setImageResource(MultiplatformHelper.get_file_type_drawable_id(ctx,mList.get(position).file_type));

        }else {
            holder.header_view.setVisibility(View.GONE);
            holder.container.setVisibility(View.VISIBLE);
            holder.title.setText(mList.get(position).title);
            holder.item1_tv.setText(MultiplatformHelper.get_file_type_str(ctx,mList.get(position).file_type)+" - "+mList.get(position).duration);
            holder.image.setImageResource(MultiplatformHelper.get_file_type_drawable_id(ctx,mList.get(position).file_type));

        }


    }


    @Override
    public int getItemCount() {
        return mList.size();
    }




    Object getItem(int id) {
        return mList.get(id);
    }




    public void show_delete_dialog(int pos)
    {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        final String src_path =MultiplatformHelper.get_directory(ctx)+"/"+mList.get(pos).post_id+"_"+mList.get(pos).file_name;
                        new File(src_path).delete();
                        LessonAdapter.this.notifyDataSetChanged();

                        // for offline items.............
                        // for offline items.............
                        try {

                            Type listType = new TypeToken<LinkedList<PostObject>>() {}.getType();
                            LinkedList<PostObject> offline_items =new Gson().fromJson(sp.getString("offline_items",""), listType);
                            if(offline_items == null)
                                offline_items = new LinkedList<>();

                            for (int i=0 ; i<offline_items.size();i++){
                                if(offline_items.get(i).post_id.equals(mList.get(pos).post_id)){
                                    offline_items.remove(i);
                                    break;
                                }
                            }
                            spe.putString("offline_items", new Gson().toJson(offline_items, listType)).commit();

                        }catch (Exception e){}
                        // for offline items.............
                        // for offline items.............
                        dialog.dismiss();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(ctx.getString(R.string.delete_file_desc)).setPositiveButton(ctx.getString(R.string.yes), dialogClickListener)
                .setNegativeButton(ctx.getString(R.string.no), dialogClickListener);
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









