package com.multiplatform.helper;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.RequiresApi;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

public class CircleImageView extends androidx.appcompat.widget.AppCompatImageView {

       public CircleImageView(Context ctx, AttributeSet attrs) {
              super(ctx, attrs);
       }

       @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
       @Override
       protected void onDraw(Canvas canvas) {

              Drawable drawable = getDrawable();

              if (drawable == null) {
                     return;
              }

              if (getWidth() == 0 || getHeight() == 0) {
                     return;
              }
              try {
                     Bitmap b = ((BitmapDrawable) drawable).getBitmap();
                     Bitmap bitmap = b.copy(Config.ARGB_8888, true);
                     int w = getWidth(), h = getHeight();
                     Bitmap roundBitmap = getRoundedCroppedBitmap(bitmap, w);
                     canvas.drawBitmap(roundBitmap, 0, 0, null);
              }catch (Exception e){
                     try {
                            if(drawable!=null)
                            {
                                   Bitmap b=null ;
                                   if (drawable instanceof VectorDrawableCompat) {
                                          b = getBitmap((VectorDrawableCompat) drawable);
                                   } else if (drawable instanceof VectorDrawable) {
                                          b = getBitmap((VectorDrawable) drawable);
                                   }
                                   Bitmap bitmap = b.copy(Config.ARGB_8888, true);
                                   int w = getWidth(), h = getHeight();
                                   Bitmap roundBitmap = getRoundedCroppedBitmap(bitmap, w);
                                   canvas.drawBitmap(roundBitmap, 0, 0, null);
                            }
                     }catch (Exception e2){
                            int i=0;
                            i++ ;
                     }

                     return;
              }


       }

       public static Bitmap getRoundedCroppedBitmap(Bitmap bitmap, int radius) {
              Bitmap finalBitmap;
              if (bitmap.getWidth() != radius || bitmap.getHeight() != radius)
                     finalBitmap = Bitmap.createScaledBitmap(bitmap, radius, radius,
                                  false);
              else
                     finalBitmap = bitmap;
              Bitmap output = Bitmap.createBitmap(finalBitmap.getWidth(),
                           finalBitmap.getHeight(), Config.ARGB_8888);
              Canvas canvas = new Canvas(output);

              final Paint paint = new Paint();
              final Rect rect = new Rect(0, 0, finalBitmap.getWidth(),
                           finalBitmap.getHeight());

              paint.setAntiAlias(true);
              paint.setFilterBitmap(true);
              paint.setDither(true);
              canvas.drawARGB(0, 0, 0, 0);
              paint.setColor(Color.parseColor("#BAB399"));
              canvas.drawCircle(finalBitmap.getWidth() / 2 + 0.7f,
                           finalBitmap.getHeight() / 2 + 0.7f,
                           finalBitmap.getWidth() / 2 + 0.1f, paint);
              paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
              canvas.drawBitmap(finalBitmap, rect, rect, paint);

              return output;
       }



       @TargetApi(Build.VERSION_CODES.LOLLIPOP)
       private static Bitmap getBitmap(VectorDrawable vectorDrawable) {
              Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                      vectorDrawable.getIntrinsicHeight(), Config.ARGB_8888);
              Canvas canvas = new Canvas(bitmap);
              vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
              vectorDrawable.draw(canvas);
              return bitmap;
       }

       private static Bitmap getBitmap(VectorDrawableCompat vectorDrawable) {
              Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                      vectorDrawable.getIntrinsicHeight(), Config.ARGB_8888);
              Canvas canvas = new Canvas(bitmap);
              vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
              vectorDrawable.draw(canvas);
              return bitmap;
       }

}


