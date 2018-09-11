package com.tao.face.data;

import com.sensetime.mid.faceapi.model.FaceInfo;

/**
 * Created by Tao on 2018/6/7 0007.
 */
public   class FeatureBeen {


    int cameraIndex ;
    byte[] faceData;
    FaceInfo[] faceInfos;

    public int getCameraIndex() {
        return cameraIndex;
    }

    public void setCameraIndex(int cameraIndex) {
        this.cameraIndex = cameraIndex;
    }

    public byte[] getFaceData() {
        return faceData;
    }

    public void setFaceData(byte[] faceData) {
        this.faceData = faceData;
    }

    public FaceInfo[] getFaceInfos() {
        return faceInfos;
    }

    public void setFaceInfos(FaceInfo[] faceInfos) {
        this.faceInfos = faceInfos;
    }
}
