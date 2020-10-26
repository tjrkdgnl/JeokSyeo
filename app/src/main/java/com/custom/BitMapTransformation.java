package com.custom;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;


public  class BitMapTransformation extends BitmapTransformation {

   private float radiusRatio ;

    public BitMapTransformation(float radiusRatio) {
        this.radiusRatio =radiusRatio;
    }

    @Override protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {

        //imageView 사이즈의 bitmap 생성
        Bitmap bitmap =Bitmap.createBitmap(outWidth,outHeight, Bitmap.Config.ARGB_8888);

        //아무 그림없는 bitmap을 도화지로 셋팅
        Canvas canvas =new Canvas(bitmap);

        //모서리 라운드값
        float radius = Math.min(outWidth,outHeight) /radiusRatio;

        //도화지에 칠할 크레파스생성
        Paint paint = new Paint();

        //모서리 경로 생성
        Path path =RoundedRect(0,0,bitmap.getWidth(), bitmap.getHeight(),radius,radius,true);

        //도화지에 path모양을 따르는 도형 생성
        canvas.drawPath(path,paint);

        //두 도형의 상호작용을 위한 기능
        //SRC_IN이면 두 도형이 겹친부분을 선택함
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        //imageView사이즈의 사각형 생성
        RectF rectf =new RectF(0,0,bitmap.getWidth(),bitmap.getHeight());

        //bitmap인 toTransform을 기존의 모양 말고(null) 새로운 모양인 recf로 paint칠을 시작
        canvas.drawBitmap(toTransform,null,rectf,paint);

        int color = 0xff424242;
        paint.setColor(color);


        return bitmap;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {

    }


    static public Path RoundedRect(float left, float top, float right, float bottom, float rx, float ry, boolean conformToOriginalPost) {
        Path path = new Path();
        if (rx < 0) rx = 0;
        if (ry < 0) ry = 0;
        float width = right - left;
        float height = bottom - top;
        if (rx > width/2) rx = width/2;
        if (ry > height/2) ry = height/2;
        float widthMinusCorners = (width - (2 * rx));
        float heightMinusCorners = (height - (2 * ry));

        path.moveTo(right, top + ry);
        path.arcTo(right - 2*rx, top, right, top + 2*ry, 0, -90, false); //top-right-corner
        path.rLineTo(-widthMinusCorners, 0);
        path.arcTo(left, top, left + 2*rx, top + 2*ry, 270, -90, false);//top-left corner.
        path.rLineTo(0, heightMinusCorners);
        if (conformToOriginalPost) {
            path.rLineTo(0, ry);
            path.rLineTo(width, 0);
            path.rLineTo(0, -ry);
        }
        else {
            path.arcTo(left, bottom - 2 * ry, left + 2 * rx, bottom, 180, -90, false); //bottom-left corner
            path.rLineTo(widthMinusCorners, 0);
            path.arcTo(right - 2 * rx, bottom - 2 * ry, right, bottom, 90, -90, false); //bottom-right corner
        }

        path.rLineTo(0, -heightMinusCorners);

        path.close();//Given close, last lineto can be removed.
        return path;
    }
}