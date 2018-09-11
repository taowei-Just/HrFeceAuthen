package com.tao.hr_faceLib;

import android.graphics.Bitmap;

import com.tao.camerview.MatchData;

/**
 * Created by Tao on 2018/5/26 0026.
 */


public  class TailorHelper {

    public static Bitmap tailor(MatchData featurData) throws  Exception {

        int dw= featurData.getSourWidth() ;
        int dh=  featurData.getSourHeidht() ;

        int   picSideH ;
        int   picSideW ;
        int   offsetTop ;
        int   offsetBottm ;

        int left = featurData.getRect().left;
        int top = featurData.getRect().top;
        int right = featurData.getRect().right;
        int bottom =  featurData.getRect().bottom;
        int w = right - left;
        int h = bottom - top;

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
