package com.multiplatform.adapter;


import android.app.Activity;
import android.content.Context;

import com.bumptech.glide.Glide;
import com.multiplatform.model.CategoryObject;

import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;

import com.multiplatform.shimibartar.R;

import java.util.LinkedList;


public  class CategoryAdapter extends BaseAdapter {






    public Context ctx;
    public LinkedList<CategoryObject> mList;
    public LinkedList<CategoryObject> orlinal_mList;

    SharedPreferences sp ;
    SharedPreferences.Editor spe;
    private Filter Filter;
    private LayoutInflater mLayoutInflater = null;
    int h=0;

    public CategoryAdapter(LinkedList<CategoryObject> list, Context c) {

          ctx=c;
          //textsize=ts-2;
          mList = list;
          orlinal_mList = list;
          mLayoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        sp= ctx.getSharedPreferences("init", Activity.MODE_PRIVATE);
        spe = sp.edit();



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
              v = li.inflate(R.layout.row_category, null);
              viewHolder1 = new ListviewHolder(v);
              v.setTag(viewHolder1);
                
         } 
         else 
         {  
       	  viewHolder1 = (ListviewHolder) v.getTag();
         }



         viewHolder1.title.setText(mList.get(position).name);

         Glide.with(ctx).load(mList.get(position).image)
                 .error(R.drawable.small_def_img)
                 .placeholder(R.drawable.small_def_img)
                 .into(viewHolder1.image);



         return v;
          
          
          
          
     }









}  


//=========================== my filter ====================================================





//===================================================




























