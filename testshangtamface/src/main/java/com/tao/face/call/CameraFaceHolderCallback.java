package com.tao.face.call;

import android.util.Log;
import android.view.SurfaceHolder;

import com.tao.face.helper.CameraHelper;
import com.tao.face.helper.FaceHelper;

import static com.sensetime.mid.faceapi.util.ColorConvertUtil.TAG;

/**
 * Created by Tao on 2018/6/7 0007.
 */


public   class CameraFaceHolderCallback implements SurfaceHolder.Callback {

    CameraHelper cameraHelper;
    FaceHelper faceHelper;

    public CameraFaceHolderCallback(FaceHelper faceHelper) {

        this.faceHelper = faceHelper;
        cameraHelper = faceHelper.getCameraHelper();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            cameraHelper.openAfterCamera();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            cameraHelper.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        cameraHelper.changeSurface();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        Log.e(TAG ," surfaceDestroyed ");
        try {
            cameraHelper.realseCamera();
        } catch (Exception e) {
            e.printStackTrace();
        }
        faceHelper.close();
    }
}