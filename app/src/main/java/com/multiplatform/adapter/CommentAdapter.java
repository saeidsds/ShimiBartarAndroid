package com.multiplatform.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;

import com.multiplatform.model.CommentObject;
import com.multiplatform.shimibartar.CommentActivity;
import com.multiplatform.shimibartar.R;
import com.multiplatform.helper.MPDateHelper;

import java.util.LinkedList;


public  class CommentAdapter extends BaseAdapter  {






    public Context ctx;
    public LinkedList<CommentObject> mList;
    public LinkedList<CommentObject> orlinal_mList;

    SharedPreferences sp ;
    SharedPreferences.Editor spe;
    private Filter Filter;
    private LayoutInflater mLayoutInflater = null;
    int h=0;
    public String channel_id = "";

    public boolean show_reply = true ;

    public CommentAdapter(LinkedList<CommentObject> list, Context c) {

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
              v = li.inflate(R.layout.row_comment, null);
              viewHolder1 = new ListviewHolder(v);
              v.setTag(viewHolder1);
                
         } 
         else 
         {  
       	  viewHolder1 = (ListviewHolder) v.getTag();
         }


         if(show_reply){
             viewHolder1.replays_btn.setVisibility(View.VISIBLE);
             viewHolder1.replays_btn2.setVisibility(View.VISIBLE);
         }else{
             viewHolder1.replays_btn.setVisibility(View.GONE);
             viewHolder1.replays_btn2.setVisibility(View.GONE);
         }

         if(mList.get(pos).user_id.equals(sp.getString("user_id","")))
         {
             viewHolder1.comment_container1.setVisibility(View.VISIBLE);
             viewHolder1.comment_container2.setVisibility(View.GONE);
         }
         else{
             viewHolder1.comment_container1.setVisibility(View.GONE);
             viewHolder1.comment_container2.setVisibility(View.VISIBLE);
         }

         viewHolder1.title.setText(mList.get(position).comment_author);
         viewHolder1.item1_tv.setText(mList.get(position).comment_content);
         viewHolder1.date_tv.setText(MPDateHelper.miladi_to_shamsi(mList.get(pos).comment_date).persian_date);

         viewHolder1.title2.setText(mList.get(position).comment_author);
         viewHolder1.item2_tv.setText(mList.get(position).comment_content);
         viewHolder1.date_tv2.setText(MPDateHelper.miladi_to_shamsi(mList.get(pos).comment_date).persian_date);

         viewHolder1.post_title.setText(mList.get(position).post_title);
         viewHolder1.post_title2.setText(mList.get(position).post_title);


         viewHolder1.replays_btn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent i =new Intent(ctx, CommentActivity.class);
                 i.putExtra("parent_comment",mList.get(pos).comment_ID);
                 i.putExtra("post_id",mList.get(pos).comment_post_ID);
                 i.putExtra("title",ctx.getString(R.string.show_replys));
                 ctx.startActivity(i);
             }
         });

         viewHolder1.replays_btn2.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent i =new Intent(ctx, CommentActivity.class);
                 i.putExtra("parent_comment",mList.get(pos).comment_ID);
                 i.putExtra("post_id",mList.get(pos).comment_post_ID);
                 i.putExtra("title",ctx.getString(R.string.show_replys));
                 ctx.startActivity(i);
             }
         });
         return v;
          
          
          
          
     }


//=========================== async and dialog  ====================================================






}  








//===================================================




























