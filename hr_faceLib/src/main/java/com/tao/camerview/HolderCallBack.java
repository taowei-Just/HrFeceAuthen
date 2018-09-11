package com.tao.camerview;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;




/**
 * Created by Tao on 2018/5/26 0026.
 */

public    class HolderCallBack implements SurfaceHolder.Callback {

    
    EngintHelper helper;
    SurfaceView surfaceView;
    String TAG = "HolderCallBack" ;

    public HolderCallBack(EngintHelper helper, SurfaceView surfaceView) {
        this.helper = helper;
        this.surfaceView = surfaceView;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        helper.initCamera();
        Log.e(TAG ,"surfaceCreated");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        holder.setFixedSize(surfaceView.getWidth(), surfaceView.getHeight());
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            helper.reaseCamere();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}