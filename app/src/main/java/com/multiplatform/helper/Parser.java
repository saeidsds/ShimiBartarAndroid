package com.multiplatform.helper;

import android.annotation.SuppressLint;
import android.content.Context;

import android.provider.Settings;

import com.multiplatform.model.CategoryObject;
import com.multiplatform.model.CommentObject;
import com.multiplatform.model.DiscountObject;
import com.multiplatform.model.PostObject;
import com.multiplatform.model.GeneralObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.regex.Pattern;

/**
 * Created by Saeid on 10/2/2016.
 */
public class Parser {


    public static String get_json(String input) {
        try {
            //return input.substring(8256,input.length()-3599);
            //return input.substring(8233,input.length()-3571);
            //return input.substring(8256,input.length()-3573);
            return input;
        } catch (Exception e) {
            return input;
        }
    }


    // ###################################################################################





    // ###################################################################################
    // ###################################################################################
    // ###################################################################################
    // ###################################################################################
    // ###################################################################################





    public static String decrypt(String data) {
        //String initVector = "fedcba9876543210"; // 16 bytes IV


        String orginal = data;
        try {
            String key = "AlVw1QKxzeeFA#qZrqtwS+yKw/s";
            CryptLib cryptLib = new CryptLib();
            data = cryptLib.decryptCipherTextWithRandomIV(data, key);
        }catch (Exception e){
            data = orginal ;
        }
        return data;
    }


    @SuppressLint("MissingPermission")
    public static String getDeviceUniqueID(Context activity) {
        String imei = "";

        try {


//        try {
//
//            TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
//            if (android.os.Build.VERSION.SDK_INT >= 26) {
//                imei=telephonyManager.getImei();
//            }
//            else
//            {
//                imei=telephonyManager.getDeviceId();
//            }
//        } catch (Exception e) {
//        }
            if(imei == null || imei.equals(""))
            {
                imei = Settings.Secure.getString(activity.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
        }catch (Exception e){}


        //AdvertisingIdClient.getAdvertisingIdInfo(activity).getId();



        return imei;
    }























































    static public PostObject get_post_object(Context ctx, JSONObject jclient) throws JSONException {
        PostObject obj=new PostObject();

        try {
            try {
                obj.post_id = jclient.getString("post_id");
            }catch (Exception e){}


            try {
                obj.post_type = jclient.getString("post_type");
            }catch (Exception e){}

            try {
                obj.link_slider = jclient.getString("link_slider");
            }catch (Exception e){}

            try {
                obj.title = jclient.getString("title").replace("&#8211;","-");
            }catch (Exception e){}

            try {
                obj.content = jclient.getString("content").replace("&#8211;","-");
            }catch (Exception e){}

            try {
                obj.image = jclient.getString("image");
            }catch (Exception e){}

            try {
                obj.image_slider = jclient.getString("image_slider");
            }catch (Exception e){}



            try {
                obj.package_type = jclient.getString("package_type");
            }catch (Exception e){}

            try {
                obj.price = jclient.getString("price");
            }catch (Exception e){}

            try {
                obj.price_prev = jclient.getString("price_prev");
                try {
                    double price = Double.valueOf(obj.price);
                    double price_prev = Double.valueOf(obj.price_prev);
                    double discount = 100*(price_prev-price)/price_prev;
                    //obj.price_discount = ((int)discount)+"";
                }catch (Exception e2){}
            }catch (Exception e){}


            try {
                obj.durations = jclient.getString("durations");
            }catch (Exception e){}

            try {
                obj.file_type = jclient.getString("file_type");
            }catch (Exception e){}

            try {
                obj.slider_type = jclient.getString("slider_type");
            }catch (Exception e){}


            try {
                obj.slider_category_id = jclient.getString("slider_category_id");
            }catch (Exception e){}


            try {
                obj.file_link =Parser.decrypt( jclient.getString("file_link"));
                //obj.file_link =jclient.getString("file_link");
            }catch (Exception e){}

            try {
                obj.duration = jclient.getString("duration");
            }catch (Exception e){}


            try {
                obj.sample = get_post_object(ctx, jclient.getJSONObject("sample"));
            }catch (Exception e){}


            try {
                get_post_array(ctx,jclient.getJSONArray("lessons"),obj.lessons);

            }catch (Exception e){}
            if(obj.lessons==null)
                obj.lessons=new LinkedList<>();







            try {
                int split_size = Parser.decrypt(obj.file_link).split("/").length;
                obj.file_name = Parser.decrypt(obj.file_link).split("/")[split_size-1];
                //obj.file_name=obj.file_name.split(Pattern.quote("."))[0];
                obj.file_name=obj.file_name.replace(" ","");
                try {
                    obj.file_name = URLDecoder.decode( obj.file_name, "UTF-8" );
                }catch (Exception e){}
            }catch (Exception e){
            }

            try {
                obj.real_file_name=obj.file_name.split(Pattern.quote("."))[0];
                obj.extention=obj.file_name.split(Pattern.quote("."))[1];
            }catch (Exception e){}

        } catch (Exception e) {
            return null ;
        }


        return obj ;
    }






    static public void get_post_array(Context ctx, JSONArray jArray,
                                          LinkedList<PostObject> temp_array) throws JSONException {

        for (int i = 0; i < jArray.length(); i++) {

            JSONObject jclient = jArray.getJSONObject(i);
            try {
                PostObject obj=new PostObject();
                obj=get_post_object(ctx,jclient);
                if(obj!=null)
                    temp_array.add(obj);
            } catch (Exception e) {

            }

        }
    }



    static public void get_category_array(Context ctx, JSONArray jArray,
                                      LinkedList<CategoryObject> temp_array) throws JSONException {

        for (int i = 0; i < jArray.length(); i++) {

            JSONObject jclient = jArray.getJSONObject(i);
            try {
                CategoryObject obj=new CategoryObject();
                try {
                    obj.term_id = jclient.getString("term_id");
                    obj.name = jclient.getString("name");
                    try {
                        obj.image = jclient.getString("image");
                    }catch (Exception e){}

                }catch (Exception e){}
                if(obj!=null)
                    temp_array.add(obj);
            } catch (Exception e) {

            }

        }
    }


    static public void get_general_array(Context ctx, JSONArray jArray,
                                          LinkedList<GeneralObject> temp_array) throws JSONException {

        for (int i = 0; i < jArray.length(); i++) {

            JSONObject jclient = jArray.getJSONObject(i);
            try {
                GeneralObject obj=new GeneralObject();
                try {
                    try {
                        obj.post_id = jclient.getString("post_id");
                    }catch (Exception e){}
                    try {
                        obj.title = jclient.getString("title");
                    }catch (Exception e){}
                    try {
                        obj.content = jclient.getString("content");
                    }catch (Exception e){}
                    try {
                        obj.user_id = jclient.getString("user_id");
                    }catch (Exception e){}
                    try {
                        obj.user_name = jclient.getString("user_name");
                    }catch (Exception e){}
                    try {
                        obj.answer = jclient.getString("answer");
                        if(obj.answer==null || obj.answer.trim().equals("null"))
                            obj.answer="";
                    }catch (Exception e){}

                    try {
                        obj.transaction_date = jclient.getString("transaction_date");
                    }catch (Exception e){}

                    try {
                        obj.price = jclient.getString("price");
                    }catch (Exception e){}

                    try {
                        obj.state = jclient.getString("state");
                    }catch (Exception e){}

                    try {
                        obj.date = jclient.getString("date");
                    }catch (Exception e){}

                    try {
                        obj.is_answered = jclient.getString("is_answered");
                    }catch (Exception e){}

                }catch (Exception e){}
                if(obj!=null)
                    temp_array.add(obj);
            } catch (Exception e) {

            }

        }
    }

    static public DiscountObject get_discount_object(Context ctx, JSONObject jclient) throws JSONException {
        DiscountObject obj=new DiscountObject();
        try {
            try {
                try {
                    obj.discount_code = jclient.getString("discount_code");
                }catch (Exception e){}

                try {
                    obj.discount_user = jclient.getString("discount_user");
                }catch (Exception e){}

                try {
                    obj.reagent_user = jclient.getString("reagent_user");
                }catch (Exception e){}

                try {
                    obj.register_user = jclient.getString("register_user");
                }catch (Exception e){}

                try {
                    obj.is_used = jclient.getString("is_used");
                }catch (Exception e){}

                try {
                    obj.price = jclient.getString("price");
                }catch (Exception e){}
            }catch (Exception e){}
        }catch (Exception e){
            return null;
        }

        return obj;
    }


    static public void get_discount_array(Context ctx, JSONArray jArray,
                                          LinkedList<DiscountObject> temp_array) throws JSONException {

        for (int i = 0; i < jArray.length(); i++) {

            JSONObject jclient = jArray.getJSONObject(i);
            try {
                DiscountObject obj=new DiscountObject();
                try {
                    obj = get_discount_object(ctx,jclient);
                }catch (Exception e){}
                if(obj!=null)
                    temp_array.add(obj);
            } catch (Exception e) {

            }

        }
    }




    static public void get_comment_array(Context ctx, JSONArray jArray,
                                          LinkedList<CommentObject> temp_array) throws JSONException {

        for (int i = 0; i < jArray.length(); i++) {

            JSONObject jclient = jArray.getJSONObject(i);
            try {
                CommentObject obj=new CommentObject();
                try {

                    try {
                        obj.comment_post_ID = jclient.getString("comment_post_ID");
                    }catch (Exception e){}

                    try {
                        obj.comment_ID = jclient.getString("comment_ID");
                    }catch (Exception e){}

                    try {
                        obj.user_id = jclient.getString("user_id");
                    }catch (Exception e){}

                    try {
                        obj.comment_author = jclient.getString("comment_author");
                    }catch (Exception e){}

                    try {
                        obj.comment_content = jclient.getString("comment_content");
                    }catch (Exception e){}

                    try {
                        obj.comment_date = jclient.getString("comment_date");
                    }catch (Exception e){}

                    try {
                        obj.post_title = jclient.getString("post_title");
                    }catch (Exception e){}

                }catch (Exception e){}
                if(obj!=null)
                    temp_array.add(obj);
            } catch (Exception e) {

            }

        }
    }








    public static String html2text(String html) {
        return Jsoup.parse(html).text();
    }

}


