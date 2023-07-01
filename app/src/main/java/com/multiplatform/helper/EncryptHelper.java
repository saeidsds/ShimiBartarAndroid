package com.multiplatform.helper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Saeid on 10/2/2016.
 */


public abstract class EncryptHelper extends NewAsyncTask<Boolean, Boolean, Boolean> {

    SharedPreferences sp ;
    SharedPreferences.Editor spe;
    Context ctx;
    String webservice="";

    String src="";
    String dest="";
    boolean is_encrypt =true ;
    public EncryptHelper(Context ctx, String src, String dest, boolean is_encrypt)
    {
        this.ctx=ctx;
        sp= ctx.getSharedPreferences("init", Activity.MODE_PRIVATE);
        spe = sp.edit();
        webservice= Constant.get_webservice();
        this.src=src;
        this.dest=dest;
        this.is_encrypt=is_encrypt;
    }
    //public boolean gcm_result=false ;
    int error=0;

    @Override
    protected Boolean doInBackground(Boolean... params) {

        String temp="";
        if(dest==null) {
            temp = src ;
            new File(src).renameTo(new File(src+".temp"));
            src = src+".temp";
        }
        else
            temp = dest;
        boolean result=encrypt_decrypt_file(src,temp,is_encrypt);
        if(result==true && dest==null)
        {
            new File(src).delete();

        }
        return result;
    }




    @Override
    protected void onPostExecute(Boolean result) {
        completed(result);
    }


    abstract public void completed(boolean result);


    public boolean encrypt_decrypt_file(String path, String dest, boolean is_encrypt)
    {
        try {
            new File(dest).delete();
            FileInputStream fis = new FileInputStream(path);
            FileOutputStream fos = new FileOutputStream(dest);


            int mode= Cipher.ENCRYPT_MODE;
            if(is_encrypt==false)
                mode= Cipher.DECRYPT_MODE;

            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec("0123456789012345".getBytes(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec("0123459876543210".getBytes());
            cipher.init(mode, keySpec, ivSpec);
            CipherOutputStream cipher_outputStream = new CipherOutputStream(fos, cipher);



            int b;
            byte[] d = new byte[1024 * 1024];
            while((b = fis.read(d)) != -1) {
                cipher_outputStream.write(d, 0, b);
            }
            cipher_outputStream.flush();
            cipher_outputStream.close();
            fis.close();
        }catch (Exception e){
            return false;
        }
        return true;

    }







}



