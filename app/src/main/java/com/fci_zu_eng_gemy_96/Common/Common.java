package com.fci_zu_eng_gemy_96.Common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.fci_zu_eng_gemy_96.Model.Requests;
import com.fci_zu_eng_gemy_96.Model.User;
import com.fci_zu_eng_gemy_96.Remote.IGeoCoordinate;
import com.fci_zu_eng_gemy_96.Remote.RetrofitClient;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class Common {
    public static User currentUser ;
    public static Requests currentRequest ;

    public final static String UPDATE = "Update";
    public final static String DELETE = "Delete";
    public static final String baseUrl = "https://maps.googleapis.com";

    public static String convertCodeToStatus(String code){
        if (code.equals("0")){
            return "Placed";
        }
        else if (code.equals("1")){
            return "On My Way";
        }
        else
            return "Delivered";
    }

    public static IGeoCoordinate getGeoCodeService(){
        return RetrofitClient.getClient(baseUrl).create(IGeoCoordinate.class);
    }

    public static Bitmap scaleBitmap(Bitmap bitmap , int newWidth , int newHieght){
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHieght , Bitmap.Config.ARGB_8888);

        float scaleX = newWidth / (float)bitmap.getWidth();
        float scaleY = newHieght / (float)bitmap.getHeight();
        float pivotX = 0 , pivotY=0 ;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX,scaleY,pivotX,pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap ;
    }

    /*public static BitmapDescriptor bitmapDescriptorFactory(Context context , int vectorResId){
        Drawable vectorDrowable = ContextCompat.getDrawable(context,vectorResId);
        vectorDrowable.setBounds(0,0,vectorDrowable.getIntrinsicWidth(),vectorDrowable.getIntrinsicHeight());

        Bitmap bitmap = Bitmap.createBitmap(vectorDrowable.getIntrinsicWidth(),vectorDrowable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrowable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }*/
}
