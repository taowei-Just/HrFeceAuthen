package com.tao.face.helper;

/**
 * Created by Tao on 2018/6/7 0007.
 */

import android.graphics.ImageFormat;
import android.hardware.Camera;

/**
 * 摄像头操作
 * <p>
 * 打开
 * 关闭
 * 设置参数
 */
public   class CameraHelper {

    Camera mCamera;
    FaceHelper faceHelper;

    int pwidth = 640;
    int pheight = 480;

    public CameraHelper(FaceHelper faceHelper) {
        this.faceHelper = faceHelper;
    }


    public Camera getmCamera() {
        return mCamera;
    }

    public Camera openCamera(int num) throws Exception {
        if (mCamera == null)
            mCamera = Camera.open(num);

        return mCamera;
    }

    // 打开前置摄像头
    public Camera openBeforeCamera() throws Exception {
        if (mCamera == null)
            mCamera = Camera.open(0);
        else {
            realseCamera();
            mCamera = Camera.open(0);
        }

        return mCamera;
    }

    // 打开后置摄像头
    public Camera openAfterCamera() throws Exception {
        if (mCamera == null)
            mCamera = Camera.open(Camera.getNumberOfCameras() - 1);
        else {
            realseCamera();
            mCamera = Camera.open(Camera.getNumberOfCameras() - 1);
        }
        return mCamera;

    }

    public void startPreview() throws Exception {

        mCamera.setDisplayOrientation(0);
        mCamera.setPreviewDisplay(faceHelper.getFaceHolder());
        mCamera.setPreviewCallback(faceHelper.getPreview());
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(pwidth, pheight);
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setPreviewFormat(ImageFormat.NV21);
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }


    public void realseCamera() throws Exception {

        faceHelper.getFaceHolder().removeCallback(faceHelper.getFaceHolderCallback());

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    public void changeSurface() {
        if (mCamera != null) {

        }
    }
}
