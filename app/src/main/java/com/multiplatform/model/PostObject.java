package com.multiplatform.model;

import com.google.gson.annotations.Expose;

import java.util.LinkedList;

/**
 * Created by Saeid on 10/2/2016.
 */
public class PostObject {





    
    public String post_id="";
    public String post_type="";
    public String link_slider="";

    
    public String title="";

    
    public String content="";

    
    public String image="";

    
    public String image_slider="";


    
    public String package_type="";

    
    public String price="0";
    public String price_prev="0";

    
    public String durations="";


    
    public String file_type="";

    
    public String file_link="";

    
    public String duration="";


    
    public LinkedList<PostObject> lessons=new LinkedList<>();


    
    public PostObject sample=null;


    
    public String cover_image="";

    public boolean is_loading=false;

    public boolean is_downloading=false;
    public int progress=0;

    
    public int download_id=-1;





    public String file_name="";
    public String real_file_name="";
    public String extention="";


    public String offline_parent_id="";
    public String offline_parent_title="";


    public String slider_type="";
    public String slider_category_id = "";

}
