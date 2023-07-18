package com.multiplatform.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.multiplatform.helper.MultiplatformHelper;
import com.multiplatform.model.PostObject;
import com.multiplatform.shimibartar.R;

import java.util.LinkedList;


//=========================== my filter ====================================================


public class HomePackageAdapter extends RecyclerView.Adapter<RecycleHolder> {

    public Context ctx;
    public LinkedList<PostObject> mList;
    private LayoutInflater mInflater;
    SharedPreferences sp ;
    SharedPreferences.Editor spe;

    // data is passed into the constructor
    public HomePackageAdapter(LinkedList<PostObject> list, Context c) {

        ctx=c ;
        mList = list ;
        mInflater = LayoutInflater.from(ctx);
        sp= ctx.getSharedPreferences("init", Activity.MODE_PRIVATE);
        spe = sp.edit();

    }

    @Override
    public RecycleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_home_package, parent, false);
        return new RecycleHolder(view);
    }


    @Override
    public void onBindViewHolder(RecycleHolder holder, int position) {
        // set data
        Glide.with(ctx).load(mList.get(position).image).into(holder.image);
        holder.title.setText(mList.get(position).title);
        holder.title.setSelected(true);
        String discount_str = "";
        if(!mList.get(position).price_discount.equals("") && !mList.get(position).price_discount.equals("0")){
            discount_str  = "("+mList.get(position).price_discount+"%-)";
        }
        holder.price_prev.setText(MultiplatformHelper.get_price(mList.get(position).price_prev)+" "+ctx.getString(R.string.toman));
        holder.price_prev.setPaintFlags(holder.price_prev.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        if(mList.get(position).package_type.equals("free")) {
            holder.price.setText(ctx.getString(R.string.free));
            holder.price.setTextColor(ContextCompat.getColor(ctx,R.color.material_green));
            holder.price_prev.setVisibility(View.GONE);
        }else {
            holder.price.setText(MultiplatformHelper.get_price(mList.get(position).price)+discount_str+" "+ctx.getString(R.string.toman));
            holder.price.setTextColor(ContextCompat.getColor(ctx,R.color.material_green));
            if(mList.get(position).price_prev.equals("") || mList.get(position).price_prev.equals("0")){
                holder.price_prev.setVisibility(View.GONE);
            }else{
                holder.price_prev.setVisibility(View.VISIBLE);
            }
        }

    }


    @Override
    public int getItemCount() {
        return mList.size();
    }




    Object getItem(int id) {
        return mList.get(id);
    }


}









