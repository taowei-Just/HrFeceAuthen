package com.tao.hr_faceLib;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.SurfaceView;

import com.tao.camerview.MatchData;

/**
 * Created by Tao on 2018/5/26 0026.
 */


public  class TailorHelper {

    public static Bitmap tailor(SurfaceView svC, MatchData featurData) throws  Exception {

        float swidth  = svC.getWidth();
        float sheight  = svC.getHeight();

        Log.e("view 的宽高  width1s " , + swidth+"  height1s " +sheight );
        

        int dw= featurData.getSourWidth() ;
        int dh=  featurData.getSourHeidht() ;

        float sd = dw / swidth;
        float sh = dh / sheight;


        int   picSideH ;
        int   picSideW ;
        int   offsetTop ;
        int   offsetBottm ;

        int left = featurData.getRect().left;
        int top = featurData.getRect().top;
        int right = featurData.getRect().right;
        int bottom =  featurData.getRect().bottom;
         
        
        int w = (int) ((right - left)*sd);
        int h = (int) ((bottom - top)*sh);

        int ch = (int) (2 * h);
        int cw = (int) (1.4 * w);
        picSideH = (ch - h) / 2;
        picSideW = (cw - w) / 2;

        left = left - picSideW > 0 ? left - picSideW : 0;

        int width = right - left;
        int height = bottom - top;
        offsetTop = (int) (0.2 * height);
        offsetBottm = (int) (0.1 * height);

        top = top - picSideH > 0 ? top - picSideH : 0;
        top = top - offsetTop > 0 ? top - offsetTop : top;

        right = right + picSideW >dw  ? dw : right + picSideW;
        bottom = bottom + picSideH + offsetBottm >  dh ?  dh: bottom + picSideH + offsetBottm;
        width = right - left;
        height = bottom - top;

        Bitmap bitmap = BitmapUtile.nv212Bitmap(featurData.getSoursData(), dw, dh);
        Bitmap faceBitmap = Bitmap.createBitmap(bitmap, left, top, width, height, null, false);
        
        return faceBitmap;
    }


}
