package com.multiplatform.adapter;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.multiplatform.shimibartar.R;


//=========================== my filter ====================================================


class ListviewHolder {

    public TextView title;
    public TextView title2;
    public TextView price_tv;
    public TextView price_prev_tv;
    public View comment_container1;
    public View comment_container2;
    public TextView item1_tv;
    public TextView item2_tv;
    public TextView date_tv;
    public TextView date_tv2;
    public ImageView image;
    public TextView content;

    public TextView post_title;
    public TextView post_title2;
    public TextView replays_btn;
    public TextView replays_btn2;


     public ListviewHolder(View base) {
    	 title = (TextView) base.findViewById(R.id.title_tv);
    	 title2 = (TextView) base.findViewById(R.id.title2_tv);
         price_tv = (TextView) base.findViewById(R.id.price_tv);
         price_prev_tv = (TextView) base.findViewById(R.id.price_prev_tv);
         comment_container1 =  base.findViewById(R.id.comment_container1);
         comment_container2 =  base.findViewById(R.id.comment_container2);
         item1_tv = (TextView) base.findViewById(R.id.item1_tv);
         item2_tv = (TextView) base.findViewById(R.id.item2_tv);
         date_tv = (TextView) base.findViewById(R.id.date_tv);
         date_tv2 = (TextView) base.findViewById(R.id.date_tv2);
         content = (TextView) base.findViewById(R.id.content_tv);
         image = (ImageView) base.findViewById(R.id.image);

         post_title =  base.findViewById(R.id.post_title_tv);
         post_title2 =  base.findViewById(R.id.post_title_tv2);
         replays_btn =  base.findViewById(R.id.replays_btn);
         replays_btn2 =  base.findViewById(R.id.replays_btn2);

     }  
     
     
     
     
     
     
     

     
     
     
     
  
}  


//===================================================




























