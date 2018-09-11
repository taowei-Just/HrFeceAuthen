package com.tao.face.call;

import android.util.Log;
import android.view.SurfaceHolder;

import com.tao.face.data.FaceResoult;
import com.tao.face.helper.FaceHelper;

/**
 * Created by Tao on 2018/6/7 0007.
 */

/**
 * 人脸识别回调
 * 通过
 * 不通过
 * 错误
 */

public   interface OnFaceCompareCallback {


    void OnFacePass(FaceResoult faceResoult);

    void OnNoMainFace();

    void OnSdkUnInit();

    void OnFaceTimeOut();

    void onFaceFiled(FaceResoult faceResoult);

    void OnRuntimeError();
}

