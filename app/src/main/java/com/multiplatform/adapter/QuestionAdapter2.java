package com.multiplatform.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.multiplatform.model.GeneralObject;
import com.multiplatform.model.PostObject;
import com.multiplatform.shimibartar.R;

import java.util.LinkedList;


//=========================== my filter ====================================================


public class QuestionAdapter2 extends RecyclerView.Adapter<RecycleHolder> {

    public Context ctx;
    public LinkedList<GeneralObject> mList;
    private LayoutInflater mInflater;
    SharedPreferences sp ;
    SharedPreferences.Editor spe;
    public PostObject package_data=new PostObject();
    // data is passed into the constructor
    public QuestionAdapter2(LinkedList<GeneralObject> list, Context c) {

        ctx=c ;
        mList = list ;
        mInflater = LayoutInflater.from(ctx);
        sp= ctx.getSharedPreferences("init", Activity.MODE_PRIVATE);
        spe = sp.edit();

    }

    @Override
    public RecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_question, parent, false);
        return new RecycleHolder(view);
    }


    @Override
    public void onBindViewHolder(RecycleHolder holder, int position) {
        // set data
        holder.title.setText(mList.get(position).title);
        holder.content.setText(mList.get(position).content);



    }


    @Override
    public int getItemCount() {
        return mList.size();
    }




    Object getItem(int id) {
        return mList.get(id);
    }


}









