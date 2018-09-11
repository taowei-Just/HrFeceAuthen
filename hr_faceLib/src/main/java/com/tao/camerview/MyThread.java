package com.tao.camerview;

import android.app.Activity;
import android.hardware.Camera;
import android.util.Log;

import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facedetection.AFD_FSDKVersion;
import com.arcsoft.genderestimation.ASGE_FSDKEngine;
import com.arcsoft.genderestimation.ASGE_FSDKError;
import com.arcsoft.genderestimation.ASGE_FSDKFace;
import com.arcsoft.genderestimation.ASGE_FSDKGender;
import com.tao.AfdData;
import com.tao.Utile.SharedUtlis;

import java.util.ArrayList;
import java.util.List;

import static com.tao.camerview.EngintHelper.appid;
import static com.tao.camerview.EngintHelper.fd_key;

/**
 * Created by Tao on 2018/7/4 0004.
 */

public class MyThread extends Thread {


    String TAG = getClass().getName();
    Activity activity;
    int faceIndex = 0;
    //        private CompareThread compareThread;
    EngintHelper engintHelper;
    private CompareThread compareThread;

    AFD_FSDKEngine afd_fsdkEngine = new AFD_FSDKEngine();
    AFD_FSDKVersion afd_fsdkVersion = new AFD_FSDKVersion();
    ArrayList<AFD_FSDKFace> afd_Resoults = new ArrayList<>();
//    ASGE_FSDKEngine asge_fsdkEngine = new ASGE_FSDKEngine();


    public MyThread(EngintHelper engintHelper) {
        this.engintHelper = engintHelper;
        this.activity = engintHelper.getActivity();
        engintHelper.setFsdkEngine(afd_fsdkEngine);
    }
    public void run() {
        try {
            // 初始化人脸检测
            AFD_FSDKError afd_fsdkError = afd_fsdkEngine.AFD_FSDK_InitialFaceEngine(appid, fd_key, AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT, 16, 25);
            int code = afd_fsdkError.getCode();
            Log.e(TAG, " 初始化人脸引擎 " + code);
//            ASGE_FSDKError asge_fsdkError = asge_fsdkEngine.ASGE_FSDK_InitgGenderEngine(appid, EngintHelper.gender_key);

//            Log.e(TAG, " 初始化 年龄引擎 " + asge_fsdkError.getCode());

//            afd_fsdkError = afd_fsdkEngine.AFD_FSDK_GetVersion(afd_fsdkVersion);
            while (!isInterrupted() && !isStop && !isRelease && code == AFD_FSDKError.MOK) {
                faceIndex++;
                byte[] cameraData = engintHelper.operaterData(null, false);
                if (cameraData == null)
                    continue;

                Camera.Parameters parameters = engintHelper.getCamera().getParameters();
                Camera.Size previewSize = parameters.getPreviewSize();
                int width = previewSize.width;
                int height = previewSize.height;

                SharedUtlis.putInt(activity, "config", "width", width);
                SharedUtlis.putInt(activity, "config", "height", height);

                afd_fsdkError = afd_fsdkEngine.AFD_FSDK_StillImageFaceDetection(cameraData, width, height, AFD_FSDKEngine.CP_PAF_NV21, afd_Resoults);

//                Log.e(TAG, "  获取人脸位置  " +afd_Resoults.size());

                if (afd_Resoults.size() > 0) {
                    List<ASGE_FSDKGender> result = new ArrayList<>();
                    List<ASGE_FSDKFace> input = new ArrayList<>();
                    for (AFD_FSDKFace face : afd_Resoults)
                        input.add(new ASGE_FSDKFace(face.getRect(), ASGE_FSDKEngine.ASGE_FOC_0));
                    ASGE_FSDKError asge_fsdkError1 = null;
//                        if ( asge_fsdkError.getCode()==asge_fsdkError.MOK)
//                          asge_fsdkError1 = asge_fsdkEngine.ASGE_FSDK_GenderEstimation_Image(cameraData, width, height, ASGE_FSDKEngine.CP_PAF_NV21, input, result);
                    AfdData afdData = new AfdData();

                    if (asge_fsdkError1 != null && asge_fsdkError1.getCode() == asge_fsdkError1.MOK)
                        afdData.setAges(result);

                    afdData.setData(cameraData);
                    afdData.setAfd_Resoults(afd_Resoults);
                    afdData.setIndex(faceIndex);
                    engintHelper.updataRgbRect(afdData, true);
                    engintHelper.drowFaceFrame(afdData, afd_Resoults.size());

                } else {
                    engintHelper.clrarFrame();
                }
                afd_Resoults.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            engintHelper.clrarFrame();
            engintHelper.hideCameraView();
            if (isRelease) {
                Log.e(TAG, "stop__");
            }
        }
    }

    public boolean bSdkInit = false;

    public void setbSdkInit(boolean bSdkInit) {
        this.bSdkInit = bSdkInit;
    }


    boolean isStop = false;
    boolean isRelease = false;

    public void stopT() {
        isStop = true;
        Thread.interrupted();
    }

    public void realseEngint() {
        isRelease = true;
//            if (compareThread != null ) {
//                compareThread.stopRun();
//            }
    }

}
