package com.multiplatform.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;

import com.multiplatform.helper.Constant;
import com.multiplatform.helper.MultiplatformHelper;
import com.multiplatform.model.DiscountObject;
import com.multiplatform.shimibartar.R;

import java.util.LinkedList;


public  class DiscountAdapter extends BaseAdapter {






    public Context ctx;
    public LinkedList<DiscountObject> mList;
    public LinkedList<DiscountObject> orlinal_mList;
    SharedPreferences sp ;
    SharedPreferences.Editor spe;
    private Filter Filter;
    private LayoutInflater mLayoutInflater = null;
    int h=0;
    String webservice = "";
    public View  temp_view ;

    public DiscountAdapter(LinkedList<DiscountObject> list, Context c) {

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
              v = li.inflate(R.layout.row_discount, null);
              viewHolder1 = new ListviewHolder(v);
              v.setTag(viewHolder1);
                
         } 
         else 
         {  
       	  viewHolder1 = (ListviewHolder) v.getTag();
         }

         viewHolder1.title.setText(ctx.getString(R.string.discount_code2)+" : "+mList.get(position).discount_code);
         viewHolder1.price_tv.setText(MultiplatformHelper.get_price(mList.get(position).price)+" "+ctx.getString(R.string.toman));


         return v;
          
          
          
          
     }






}  


//=========================== my filter ====================================================





//===================================================




























