package com.multiplatform.helper;


import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class NewAsyncTask<Params, Progress, Result> {


    ExecutorService executor ;


    Handler handler ;





    public NewAsyncTask() {
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
    }




    protected abstract Result doInBackground(Params... params);

    /**
     * Runs on the UI thread before {@link #doInBackground}.
     *
     * @see #onPostExecute
     * @see #doInBackground
     */
    protected void onPreExecute() {
    }


    @SuppressWarnings({"UnusedDeclaration"})
    protected void onPostExecute(Result result) {
    }


    @SuppressWarnings({"UnusedDeclaration"})
    protected void onProgressUpdate(Progress... values) {
    }


    protected void onCancelled(Result result) {
        onCancelled();
    }


    protected void onCancelled() {
    }


    public final boolean isCancelled() {
        return true ;
    }



    public final NewAsyncTask<Params, Progress, Result> execute(Params... params) {

        onPreExecute();
        executor.execute(new Runnable() {
            @Override
            public void run() {

                //Background work here
                Result result = doInBackground(params);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //UI Thread work here
                        onPostExecute(result);
                    }
                });
            }
        });
        return this ;
    }





}
