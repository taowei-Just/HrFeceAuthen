package com.tao.face.camera;

/**
 * Created by Tao on 2018/6/7 0007.
 */

public class CameraFaceData {

    byte[] data;
    int cameraIndex =0 ;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getCameraIndex() {
        return cameraIndex;
    }

    public void setCameraIndex(int cameraIndex) {
        this.cameraIndex = cameraIndex;
    }
}
