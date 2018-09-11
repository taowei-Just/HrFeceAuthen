package com.tao.face.call;

import android.hardware.Camera;

import com.tao.face.camera.CameraFaceData;
import com.tao.face.helper.FaceHelper;

/**
 * Created by Tao on 2018/6/7 0007.
 */

public   class MyPreviewCall implements Camera.PreviewCallback {

    FaceHelper faceHelper;
    int index =0 ;

    public MyPreviewCall(FaceHelper faceHelper) {
        this.faceHelper = faceHelper;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

        CameraFaceData faceData = new CameraFaceData();
        faceData.setData(data);
        faceData.setCameraIndex(index++);
        faceHelper.writerCameraData(faceData);
    }
}
