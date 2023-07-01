package com.multiplatform.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

public class LockHelper {
    static public int buffer_length = 14 ;







    static public void lock_file(String path)
    {
        if(is_lock(path)==true)
            return;
        // =========================== read n first chatacter of file
        int []buffer = new  int[buffer_length];
        for(int i=0 ; i<buffer.length;i++)
            buffer[i]=0;

        try {
            FileInputStream fis = new FileInputStream(path);
            InputStreamReader read = new InputStreamReader(fis, "UTF-8");

            int ch = read.read();
            int counter = 0;
            while (ch != -1 && counter < buffer.length) {
                buffer[counter]= ch ;
                counter ++ ;
                ch = read.read();
            }
            read.close();
            fis.close();


            RandomAccessFile f = new RandomAccessFile(new File(path), "rw");
            for(int i=0 ;i<buffer.length;i++)
            {
                f.seek(i);
                f.write(1);
            }
            long file_length = f.length() ;
            for(long i=file_length-1 ;i>=file_length-buffer_length;i--)
            {
                f.seek(i);
                f.write(buffer[(int)(i-file_length+buffer_length)]);
            }

            f.close();
        }catch (Exception e){
            int sds = 0 ;
            sds ++ ;
        }
    }


    static public void unlock_file(String path)
    {
        if(is_lock(path)==false)
            return;
        // =========================== read n last chatacter of file
        int []buffer = new  int[buffer_length];
        for(int i=0 ; i<buffer.length;i++)
            buffer[i]=0;

        try {
            RandomAccessFile f = new RandomAccessFile(new File(path), "rw");
            long file_length = f.length() ;
            for(long i=file_length-1 ;i>=file_length-buffer_length;i--)
            {
                f.seek(i);
                buffer[(int)(i-file_length+buffer_length)] = f.read();
            }
            for(int i=0 ;i<buffer.length;i++)
            {
                f.seek(i);
                f.write(buffer[i]);
            }
            f.close();
        }catch (Exception e){
            int sds = 0 ;
            sds ++ ;
        }
    }


    static public void init_file(String path)
    {
        try {
            RandomAccessFile f = new RandomAccessFile(new File(path), "rw");
            long file_length = f.length() ;
            for(long i=file_length ;i<file_length+buffer_length;i++)
            {
                f.seek(i);
                f.write(0);
            }
            f.close();
        }catch (Exception e){}
    }



    static public boolean is_lock(String path)
    {
        boolean result = true ;
        try {
            RandomAccessFile f = new RandomAccessFile(new File(path), "rw");
            for(int i=0 ; i<buffer_length;i++)
            {

                f.seek(i);
                if(f.read()!=1)
                {
                    result = false ;
                    break;
                }
            }
        }catch (Exception e){}


        return result ;
    }
}
