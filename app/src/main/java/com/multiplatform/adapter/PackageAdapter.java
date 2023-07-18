package com.multiplatform.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.multiplatform.helper.Constant;
import com.multiplatform.helper.MultiplatformHelper;
import com.multiplatform.model.PostObject;
import com.multiplatform.shimibartar.R;

import java.util.LinkedList;


public  class PackageAdapter extends BaseAdapter  {






    public Context ctx;
    public LinkedList<PostObject> mList;
    public LinkedList<PostObject> orlinal_mList;
    SharedPreferences sp ;
    SharedPreferences.Editor spe;
    private Filter Filter;
    private LayoutInflater mLayoutInflater = null;
    int h=0;
    String webservice = "";
    public View  temp_view ;

    public PackageAdapter(LinkedList<PostObject> list, Context c) {

          ctx=c;
          mList = list;
          orlinal_mList = list;
          mLayoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);




        sp= ctx.getSharedPreferences("init", Activity.MODE_PRIVATE);
        spe = sp.edit();
        webservice = Constant.get_webservice() ;


     }  
     

     
     @Override  
     public int getCount() {  
          return mList.size();  
     }  
     
     @Override  
     public Object getItem(int pos) {  
          return mList.get(pos);  
     }  
     
     @Override  
     public long getItemId(int position) {  
          return position;  
     }  
     
     

     
     
     
     
     
     @Override  
     public View getView(int position, View convertView, ViewGroup parent) {  
          View v = convertView;
          final int pos=position;
          //int type = getItemViewType(position);

		 ListviewHolder viewHolder1 = null;
          
          
          
          if (convertView == null) {  
        	  
              LayoutInflater li = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
              v = li.inflate(R.layout.row_package, null);
              viewHolder1 = new ListviewHolder(v);
              v.setTag(viewHolder1);
                
         } 
         else 
         {  
       	  viewHolder1 = (ListviewHolder) v.getTag();
         }

         viewHolder1.title.setText(mList.get(position).title);
         viewHolder1.title.setSelected(true);
         Glide.with(ctx).load(mList.get(position).image).into(viewHolder1.image);
         viewHolder1.price_prev_tv.setText(MultiplatformHelper.get_price(mList.get(position).price_prev)+" "+ctx.getString(R.string.toman));
         viewHolder1.price_prev_tv.setPaintFlags(viewHolder1.price_prev_tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
         String discount_str = "";
         if(!mList.get(position).price_discount.equals("") && !mList.get(position).price_discount.equals("0")){
             discount_str  = "("+mList.get(position).price_discount+"%-)";
         }
         if(mList.get(position).package_type.equals("free")) {
             viewHolder1.price_tv.setText(ctx.getString(R.string.free));
             viewHolder1.price_tv.setTextColor(ContextCompat.getColor(ctx,R.color.material_green));
             viewHolder1.price_prev_tv.setVisibility(View.GONE);
         }else {
             viewHolder1.price_tv.setText(MultiplatformHelper.get_price(mList.get(position).price)+discount_str+" "+ctx.getString(R.string.toman));
             viewHolder1.price_tv.setTextColor(ContextCompat.getColor(ctx,R.color.material_green));

             if(mList.get(position).price_prev.equals("")  || mList.get(position).price_prev.equals("0")){
                 viewHolder1.price_prev_tv.setVisibility(View.GONE);
             }else{
                 viewHolder1.price_prev_tv.setVisibility(View.VISIBLE);
             }
         }
         return v;
          
          
          
          
     }





}  


//=========================== my filter ====================================================





//===================================================




























