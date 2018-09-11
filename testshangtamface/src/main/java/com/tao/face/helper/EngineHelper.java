package com.tao.face.helper;

/**
 * Created by Tao on 2018/6/7 0007.
 */


import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.sensetime.facesdk.FaceDetector;
import com.sensetime.facesdk.LiveDetector;
import com.sensetime.facesdk.SDKManager;
import com.sensetime.facesdk.Verifier;
import com.sensetime.mid.faceapi.model.FaceInfo;
import com.sensetime.mid.faceapi.model.FaceOrientation;
import com.sensetime.mid.faceapi.model.MidPixelFormat;
import com.sensetime.mid.faceapi.util.FaceQualityUtil;
import com.tao.face.FaceApplication;
import com.tao.face.call.OnFaceCompareCallback;
import com.tao.face.domain.FaceFeature;
import com.tao.face.domain.FaceFrame;

import java.text.NumberFormat;
import java.util.ArrayList;

import static com.sensetime.mid.faceapi.util.ColorConvertUtil.TAG;

/**
 * 引擎帮助类
 * 初始化引擎
 * 销毁引擎
 * 抓取人脸
 * 获取特征值
 * 人脸比对  II  IN  切换
 * 开启关闭 人脸线程
 */


public class EngineHelper {

    FaceHelper faceHelper;
    Context context;
    private LiveDetector mLiveDetector;
    private Verifier mVerifier;
    private FaceDetector mFaceDetector;
    private FaceFrame faceFrame;

    private String path;
    private OnFaceCompareCallback callback;
    private FaceFeature faceFeature;
    String TAG = "EngineHelper";

    public EngineHelper(FaceHelper faceHelper, String path, OnFaceCompareCallback callback) {
        this.faceHelper = faceHelper;
        this.callback = callback;
        context = faceHelper.getContext();
        initEngine(path);
    }


    public EngineHelper(FaceHelper faceHelper, Bitmap bitmap, OnFaceCompareCallback callback) {
        this.faceHelper = faceHelper;

        this.callback = callback;
        context = faceHelper.getContext();
        initEngine(bitmap);
    }

    public void initEngine(final String path) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                initShangtamEngine(path, callback);
            }
        }).start();
    }

    public void initEngine(final Bitmap bitmap) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                initShangtamEngine(bitmap, callback);
            }
        }).start();
    }

    private void initShangtamEngine(final String path, final OnFaceCompareCallback callback) {
        SDKManager.initWithMiddleLicense(context, new SDKManager.SDKInitListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onSucceed() {
                Log.e(TAG, "证书校验成功");
                faceHelper.operateUI(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "证书校验成功", Toast.LENGTH_SHORT).show();
                    }
                });

                long l = System.currentTimeMillis();
                initEngine();
                Log.e(TAG, "初始化耗时 ： " + (System.currentTimeMillis() - l));
                closeFeature();
                openFrame(faceHelper);
                openFeature(faceHelper, path, callback);
            }

            @Override
            public void onFailed(final int code, String message) {
                Log.e(TAG, "证书校验失败:" + code);
                faceHelper.operateUI(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "证书校验失败:" + code, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void initShangtamEngine(final Bitmap bm, final OnFaceCompareCallback callback) {
        SDKManager.initWithMiddleLicense(context, new SDKManager.SDKInitListener() {

            @Override
            public void onStart() {
            }

            @Override
            public void onSucceed() {
                Log.e(TAG, "证书校验成功");
                faceHelper.operateUI(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "证书校验成功", Toast.LENGTH_SHORT).show();
                    }
                });
                initEngine();
                closeFeature();
                openFrame(faceHelper);
                openFeature(faceHelper, bm, callback);
            }

            @Override
            public void onFailed(final int code, String message) {
                Log.e(TAG, "证书校验失败:" + code);
                faceHelper.operateUI(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "证书校验失败:" + code, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void destoryEngine() {
        FaceApplication.destoryEngine();
    }

    public void initEngine() {
        mVerifier = FaceApplication.initerifier(context);
        mFaceDetector = FaceApplication.initFaceDetector(context);
//            mLiveDetector = FaceApplication.initLive(context);
    }

    public void openFrame(FaceHelper faceHelper) {
        faceFrame = new FaceFrame(faceHelper);
        faceFrame.start();
    }

    public void closeFrame() {
        if (faceFrame != null)
            faceFrame.close();

    }

    public void openFeature(FaceHelper faceHelper, String path, OnFaceCompareCallback callback) {
        faceFeature = new FaceFeature(faceHelper, path, callback);
        faceFeature.start();

    }

    public void openFeature(FaceHelper faceHelper, Bitmap bm, OnFaceCompareCallback callback) {
        faceFeature = new FaceFeature(faceHelper, bm, callback);
        faceFeature.start();
    }

    public void closeFeature() {
        if (faceFeature != null)
            faceFeature.close();
        if (faceFrame != null)
            faceFrame.close();
    }


    public synchronized void switchFaceCompareType(int type) {
    }

    // 活体检测
    public synchronized void liveness() {
    }

    // 人脸检测
    public synchronized FaceInfo[] faceDetect(byte[] data) {
//            FaceInfo []  detect = new FaceInfo[]{mFaceDetector.detect(data , MidPixelFormat.NV21 ,faceHelper.getCameraHelper().getmCamera().getParameters().getPreviewSize().width,faceHelper.getCameraHelper().getmCamera().getParameters().getPreviewSize().height, FaceOrientation.UP)};

        if (data == null
                || faceHelper == null
                || faceHelper.getCameraHelper() == null
                || faceHelper.getCameraHelper().getmCamera() == null
                || faceHelper.getCameraHelper().getmCamera().getParameters() == null
                || faceHelper.getCameraHelper().getmCamera().getParameters() == null
                )
            return null;

        FaceInfo[] detect = mFaceDetector.detect(data, MidPixelFormat.NV21, faceHelper.getCameraHelper().getmCamera().getParameters().getPreviewSize().width, faceHelper.getCameraHelper().getmCamera().getParameters().getPreviewSize().height, FaceOrientation.UP);
        return detect;
    }

    // 获取特征
    public synchronized byte[] acquireFeature(byte[] data, FaceInfo info) {
        if (data == null || info == null)
            return null;
        return mVerifier.getFeature(data, info);
    }

    // 获取特征
    public synchronized byte[] acquireFeature(Bitmap bitmap) {
        return mVerifier.getFeature(bitmap);
    }

    //  特征比对
    public synchronized int compareFeature(byte[] feature1, byte[] feature2) {
        return mVerifier.compareFeature(feature1, feature2);
    }

    public synchronized FaceInfo[] detectQuality(byte[] data, FaceInfo[] faceInfos) {

        if (data == null || faceInfos == null || faceInfos.length <= 0)
            return null;
        if (!faceHelper.isUseQuality())
            return faceInfos;

        ArrayList<FaceInfo> infos = new ArrayList<>();
        for (int i = 0; i < faceInfos.length; i++) {

            float quality = FaceQualityUtil.detectQuality(data, MidPixelFormat.NV21,
                    faceHelper.getCameraHelper().getmCamera().getParameters().getPictureSize().width,
                    faceHelper.getCameraHelper().getmCamera().getParameters().getPictureSize().height,
                    faceInfos[i].facePoints);


            NumberFormat instance = NumberFormat.getInstance();
            instance.setMaximumFractionDigits(5);
            String format = instance.format(quality);

            Log.e(TAG , " 当前质量：" +format);

            if (quality >= faceHelper.getFaceQuality()) {
                infos.add(faceInfos[i]);
            }
        }

        if (infos.size()>0)
        return infos.toArray(new FaceInfo[infos.size()]);

        return null;
    }
}
