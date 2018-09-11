package com.tao.camerview;

import android.hardware.Camera;

/**
 * Created by Tao on 2018/5/26 0026.
 */

public    class MyPreview implements Camera.PreviewCallback {
    EngintHelper helper;

    public MyPreview(EngintHelper helper) {
        this.helper = helper;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        helper.operaterData(data, true);
    }
}
