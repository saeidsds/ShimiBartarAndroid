package com.multiplatform.helper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MPDateHelper {

    public static class MPDate  {
        public int day=0;
        public int month=0;
        public int year = 0 ;

        public int persian_day=0;
        public int persian_month=0;
        public int persian_year = 0 ;

        public int hour = 0 ;
        public int min = 0 ;
        public int sec = 0 ;


        public String persian_date="";
        public String miladi_date="";

    }


    static public MPDate get_current_date(String input)
    {
        //yyyy-MM-dd HH:mm:ss
        if(input.equals(""))
        {
            input="yyyy-MM-dd";
        }
        MPDate result=new MPDate();
        Date date = new Date();  // to get the date
        SimpleDateFormat df = new SimpleDateFormat(input);
        String formattedDate = df.format(date.getTime());
        result = miladi_to_shamsi(formattedDate);
        return result;
    }


    static public MPDate miladi_to_shamsi(String input)
    {

        MPDate result=new MPDate();
        result.miladi_date = input;
        try {

            result.year = Integer.valueOf(input.split(" ")[0].split("-")[0].trim());
            result.month = Integer.valueOf(input.split(" ")[0].split("-")[1].trim());
            result.day = Integer.valueOf(input.split(" ")[0].split("-")[2].trim());


            try {
                result.hour = Integer.valueOf(input.split(" ")[1].split(":")[0].trim());
                result.min = Integer.valueOf(input.split(" ")[1].split(":")[1].trim());
                result.sec = Integer.valueOf(input.split(" ")[1].split(":")[2].trim());
            }catch (Exception e){}

            JalaliCalendar cal=new JalaliCalendar();
            JalaliCalendar.YearMonthDate date=cal.gregorianToJalali(new JalaliCalendar.YearMonthDate(result.year,result.month-1,result.day));


            int mm=date.getMonth();
            mm++;
            result.persian_month=mm;
            String y=date.getYear()+"";
            result.persian_year=date.getYear();
            String m=mm+"";
            String d=date.getDate()+"";
            result.persian_day=date.getDate();
            if(mm<10)
                m="0"+m;
            if(date.getDate()<10)
                d="0"+d;


            result.persian_date=y+"-"+m+"-"+d;
            try {
                result.persian_date= result.persian_date+"  "+input.split(" ")[1] ;
            }catch (Exception e){}

        }catch (Exception e)
        {
            result.persian_date = input;
            return result;
        }
        return result;
    }




    static public MPDate shamsi_to_miladi(String input)
    {

        MPDate result=new MPDate();
        result.persian_date = input;

        try {



            result.persian_year = Integer.valueOf(input.split("-")[0].trim());
            result.persian_month = Integer.valueOf(input.split("-")[1].trim());
            result.persian_day = Integer.valueOf(input.split("-")[2].trim());

            try {
                result.hour = Integer.valueOf(input.split(" ")[1].split(":")[0].trim());
                result.min = Integer.valueOf(input.split(" ")[1].split(":")[1].trim());
                result.sec = Integer.valueOf(input.split(" ")[1].split(":")[2].trim());
            }catch (Exception e){}


            JalaliCalendar fcal=new JalaliCalendar();
            fcal.set(result.persian_year,result.persian_month-1,result.persian_day);
            JalaliCalendar.YearMonthDate fdate = JalaliCalendar.jalaliToGregorian(new JalaliCalendar.YearMonthDate(result.persian_year,result.persian_month-1,result.persian_day));

            int fmm=fdate.getMonth();
            fmm++;
            result.month=fmm;
            String fy=fdate.getYear()+"";
            result.year=fdate.getYear();
            String fm=fmm+"";
            String fd=fdate.getDate()+"";
            result.day=fdate.getDate();
            if(fmm<10)
                fm="0"+fm;
            if(fdate.getDate()<10)
                fd="0"+fd;
            result.miladi_date=fy+"-"+fm+"-"+fd+"  ";


            String hour = result.hour+"";
            String min = result.min+"";
            String sec = result.sec+"";
            if(result.hour<10)
                hour="0"+result.hour;

            if(result.min<10)
                min="0"+result.min;

            if(result.sec<10)
                sec="0"+result.sec;


            if(result.hour!=0)
            {
                result.persian_date=result.persian_date+" "+hour;
            }
            if(result.min!=0)
            {
                result.persian_date=result.persian_date+":"+min;
            }
            if(result.sec!=0)
            {
                result.persian_date=result.persian_date+":"+sec;
            }

        }catch (Exception e)
        {
            result.miladi_date = input;
            return result;
        }


        return result;
    }
}






