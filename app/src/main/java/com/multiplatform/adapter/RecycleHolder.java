package com.multiplatform.adapter;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.multiplatform.shimibartar.R;


//=========================== my filter ====================================================


//===================================================


public  class RecycleHolder extends RecyclerView.ViewHolder  {
    public TextView title;
    public TextView price;
    public TextView item1_tv;
    public ImageView image;
    public CircularProgressView loading_view;
    public View container;
    public TextView content;
    public TextView progress;
    public TextView download_lable;
    public TextView delete_btn;


    public View header_view;
    public View content_cnt;
    public CircularProgressView sample_loading_view;
    public TextView sample_title;
    public TextView sample_progress;
    public ImageView sample_image;




    public RecycleHolder(View v) {
        super(v);
        title = (TextView) v.findViewById(R.id.title_tv);
        item1_tv = (TextView) v.findViewById(R.id.item1_tv);
        price = (TextView) v.findViewById(R.id.price_tv);
        image = (ImageView) v.findViewById(R.id.image);
        loading_view =  v.findViewById(R.id.loading_view);
        container =  v.findViewById(R.id.container);
        content = (TextView) v.findViewById(R.id.content_tv);
        progress = (TextView) v.findViewById(R.id.progress_tv);
        sample_progress = (TextView) v.findViewById(R.id.sample_progress_tv);
        download_lable = (TextView) v.findViewById(R.id.download_lable);
        delete_btn = (TextView) v.findViewById(R.id.delete_btn);

        header_view =  v.findViewById(R.id.header_view);
        content_cnt =  v.findViewById(R.id.content_cnt);
        sample_title = (TextView) v.findViewById(R.id.sample_title_tv);
        sample_image = (ImageView) v.findViewById(R.id.sample_image);
        sample_loading_view =  v.findViewById(R.id.sample_loading_view);
    }


}









