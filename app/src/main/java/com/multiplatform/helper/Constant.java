package com.multiplatform.helper;

/**
 * Created by Saeid on 10/2/2016.
 */
public class Constant {


    private static String site_url = "https://chempic.com/";
    public static String email = "https://info@chempic.com";
    public static String en_app_name = "Shimi Bartar";
    private static String webservice = "https://chempic.com/shimi_bartar/webservice/";

    private static String payment_webservice = "https://chempic.com/shimi_bartar/befor-payment/?";

    private static String market_url = "https://chempic.com/ShimiBartar.apk";



    public static String get_webservice()
    {
        return webservice ;
    }
    public static String get_pay_webservice()
    {
        return payment_webservice ;
    }
    public static String get_market_url()
    {
        return market_url ;
    }
    public static String get_site_url()
    {
        return site_url ;
    }

}


