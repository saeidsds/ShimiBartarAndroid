package com.multiplatform.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Toast;


import com.multiplatform.helper.Constant;
import com.multiplatform.helper.MPDateHelper;
import com.multiplatform.helper.MultiplatformHelper;
import com.multiplatform.model.GeneralObject;
import com.multiplatform.shimibartar.R;

import java.util.LinkedList;


public  class SupportAdapter extends BaseAdapter  {






    public Context ctx;
    public LinkedList<GeneralObject> mList;
    public LinkedList<GeneralObject> orlinal_mList;
    SharedPreferences sp ;
    SharedPreferences.Editor spe;
    private Filter Filter;
    private LayoutInflater mLayoutInflater = null;

    int h=0;
    String webservice = "";
    public View  temp_view ;

    public SupportAdapter(LinkedList<GeneralObject> list, Context c) {

          ctx=c;
          //textsize=ts-2;
          mList = list;
          orlinal_mList = list;
          mLayoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


          

        sp= ctx.getSharedPreferences("init", Activity.MODE_PRIVATE);
        spe = sp.edit();
        webservice = Constant.get_webservice();


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
              v = li.inflate(R.layout.row_support, null);
              viewHolder1 = new ListviewHolder(v);
              v.setTag(viewHolder1);
                
         } 
         else 
         {  
       	  viewHolder1 = (ListviewHolder) v.getTag();
         }



         viewHolder1.item1_tv.setText(mList.get(position).title);
         viewHolder1.item2_tv.setText(mList.get(position).content);
         viewHolder1.date_tv.setText(MPDateHelper.miladi_to_shamsi(mList.get(position).date).persian_date);


         return v;
          
          
          
          
     }



    public void show_msg(String msg, String color)
    {
        try {
            if(temp_view!=null)
                MultiplatformHelper.show_msg(msg,color,ctx,temp_view);
            else
                Toast.makeText(ctx,msg,Toast.LENGTH_SHORT).show();
        }catch (Exception e){}

    }







    //=========================== my filter ====================================================



}  


//=========================== my filter ====================================================





//===================================================




























