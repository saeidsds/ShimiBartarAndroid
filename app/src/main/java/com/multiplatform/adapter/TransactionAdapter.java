package com.multiplatform.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;

import androidx.core.content.ContextCompat;

import com.multiplatform.helper.MultiplatformHelper;
import com.multiplatform.model.GeneralObject;
import com.multiplatform.shimibartar.R;

import java.util.LinkedList;


public  class TransactionAdapter extends BaseAdapter {






    public Context ctx;
    public LinkedList<GeneralObject> mList;
    public LinkedList<GeneralObject> orlinal_mList;

    SharedPreferences sp ;
    SharedPreferences.Editor spe;
    private Filter Filter;
    private LayoutInflater mLayoutInflater = null;
    int h=0;

    public TransactionAdapter(LinkedList<GeneralObject> list, Context c) {

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
              v = li.inflate(R.layout.row_transaction, null);
              viewHolder1 = new ListviewHolder(v);
              v.setTag(viewHolder1);
                
         } 
         else 
         {  
       	  viewHolder1 = (ListviewHolder) v.getTag();
         }



         viewHolder1.title.setText(ctx.getString(R.string.transaction_id)+" : "+mList.get(position).title);
         viewHolder1.date_tv.setText(mList.get(position).transaction_date);
         viewHolder1.item1_tv.setText(MultiplatformHelper.get_price(mList.get(position).price)+" "+ctx.getString(R.string.toman));
         if(mList.get(pos).state.equals("pending"))
         {
             viewHolder1.item2_tv.setText(ctx.getString(R.string.transaction_state_pending));
             viewHolder1.item2_tv.setTextColor(ContextCompat.getColor(ctx,R.color.transaction_pending));
         }else if(mList.get(pos).state.equals("complete"))
         {
             viewHolder1.item2_tv.setText(ctx.getString(R.string.transaction_state_complete));
             viewHolder1.item2_tv.setTextColor(ContextCompat.getColor(ctx,R.color.material_green2));
         }else
         {
             viewHolder1.item2_tv.setText(ctx.getString(R.string.transaction_state_failed));
             viewHolder1.item2_tv.setTextColor(ContextCompat.getColor(ctx,R.color.material_red));
         }


         return v;
          
          
          
          
     }









}  


//=========================== my filter ====================================================





//===================================================




























