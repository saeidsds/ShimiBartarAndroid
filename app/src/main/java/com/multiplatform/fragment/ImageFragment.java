package com.multiplatform.fragment;




import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.multiplatform.helper.MultiplatformHelper;
import com.multiplatform.shimibartar.PackageActivity;
import com.multiplatform.shimibartar.R;
import com.multiplatform.model.PostObject;


public class ImageFragment extends Fragment {







	PostObject data;
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		try {
			data=new Gson().fromJson(getArguments().getString("data"),PostObject.class);
		}catch (Exception e){}

        View view = inflater.inflate(R.layout.row_slider, container, false);
		try {

		}catch (Exception e){}


        ImageView img=(ImageView) view.findViewById(R.id.img);
		TextView title_tv=(TextView) view.findViewById(R.id.title_tv);
		try {
			title_tv.setText(data.title);
			Glide.with(getActivity()).load(data.image_slider).into(img);
		}catch (Exception e){}
		img.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				try {
					if(data!=null)
					{
						if(data.post_type.equals("package_post"))
						{
							Intent intent=new Intent(getActivity(), PackageActivity.class);
							intent.putExtra("data",new Gson().toJson(data));
							startActivity(intent);
						}
						else if(data.slider_type.equals("in_app")){
							PostObject obj = new PostObject();
							obj.post_id = data.slider_post_id;
							Intent intent=new Intent(getActivity(), PackageActivity.class);
							intent.putExtra("data",new Gson().toJson(obj));
							startActivity(intent);
						}
						else{
							MultiplatformHelper.open_url(getActivity(),data.link_slider);
						}

					}
				}catch (Exception e){}
			}
		});
        return view;
    }







	
	
	
	
	
}