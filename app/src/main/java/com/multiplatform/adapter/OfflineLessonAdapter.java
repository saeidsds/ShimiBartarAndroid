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


public class OfflineLessonAdapter extends RecyclerView.Adapter<RecycleHolder> {

    public Context ctx;
    public LinkedList<PostObject> mList;
    private LayoutInflater mInflater;
    SharedPreferences sp ;
    SharedPreferences.Editor spe;
    public PostObject package_data=new PostObject();
    // data is passed into the constructor
    public OfflineLessonAdapter(LinkedList<PostObject> list, Context c) {

        ctx=c ;
        mList = list ;
        mInflater = LayoutInflater.from(ctx);
        sp= ctx.getSharedPreferences("init", Activity.MODE_PRIVATE);
        spe = sp.edit();

    }

    @Override
    public RecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_lesson_offline, parent, false);
        return new RecycleHolder(view);
    }


    @Override
    public void onBindViewHolder(RecycleHolder holder, int position) {
//        // set data



        holder.title.setText(mList.get(position).title);
        holder.item1_tv.setText(mList.get(position).offline_parent_title+" "+ MultiplatformHelper.get_file_type_str(ctx,mList.get(position).file_type)+" - "+mList.get(position).duration);
        holder.image.setImageResource(MultiplatformHelper.get_file_type_drawable_id(ctx,mList.get(position).file_type));

        final String src_path = MultiplatformHelper.get_directory(ctx)+"/"+mList.get(position).post_id+"_"+mList.get(position).file_name;
        holder.delete_btn.setVisibility(View.VISIBLE);

        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_delete_dialog(position);
            }
        });







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
                        OfflineLessonAdapter.this.notifyDataSetChanged();

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
                        mList.remove(pos);
                        OfflineLessonAdapter.this.notifyDataSetChanged();
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









