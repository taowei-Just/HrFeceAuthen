package com.tao.camerview;

import com.arcsoft.facedetection.AFD_FSDKFace;

/**
 * Created by Tao on 2018/5/26 0026.
 */

public  interface OnFaceCallback {
    void OnFacePass(MatchData rgbRect);
    void OnNoMainFace();
    void OnSdkUnInit();
    void OnFaceTimeOut();
    void  onFaceFiled(MatchData rgbRect);
    void onFaceFiled();
    void  onError(Exception e);
}

