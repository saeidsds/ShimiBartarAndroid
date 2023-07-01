package com.multiplatform.adapter;


import android.app.Activity;
import android.content.Context;

import com.multiplatform.model.CategoryObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.multiplatform.shimibartar.PackageInCategoryListActivity;
import com.multiplatform.shimibartar.R;

import java.util.LinkedList;


//=========================== my filter ====================================================


public class HomeCategotyAdapter extends RecyclerView.Adapter<RecycleHolder> {

    public Context ctx;
    public LinkedList<CategoryObject> mList;
    private LayoutInflater mInflater;
    SharedPreferences sp ;
    SharedPreferences.Editor spe;

    // data is passed into the constructor
    public HomeCategotyAdapter(LinkedList<CategoryObject> list, Context c) {

        ctx=c ;
        mList = list ;
        mInflater = LayoutInflater.from(ctx);
        sp= ctx.getSharedPreferences("init", Activity.MODE_PRIVATE);
        spe = sp.edit();

    }

    @Override
    public RecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_home_category, parent, false);
        return new RecycleHolder(view);
    }


    @Override
    public void onBindViewHolder(RecycleHolder holder, final int position) {
        // set data

        holder.title.setText(mList.get(position).name);
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i =new Intent(ctx, PackageInCategoryListActivity.class);
                i.putExtra("title",mList.get(position).name);
                i.putExtra("term_id",mList.get(position).term_id);
                ctx.startActivity(i);
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


}









