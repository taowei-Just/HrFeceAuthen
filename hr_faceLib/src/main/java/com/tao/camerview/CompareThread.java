package com.tao.camerview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKMatching;
import com.guo.android_extend.image.ImageConverter;
import com.tao.AfdData;
import com.tao.Utile.SharedUtlis;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.width;
import static android.content.ContentValues.TAG;

/**
 * Created by Tao on 2018/5/25 0025.
 */

public class CompareThread extends Thread {

    long lastFaceIndex = 0;
    OnFaceCallback onFaceCallback;
    EngintHelper engintHelper;
    private AFD_FSDKEngine fsdkEngine;
    private final AFR_FSDKEngine afr_fsdkEngine;

    public CompareThread(EngintHelper engintHelper) {
        this.engintHelper = engintHelper;
        this.onFaceCallback = engintHelper.getOnFaceCallback();
        fsdkEngine = engintHelper.getFsdkEngine();
        afr_fsdkEngine = new AFR_FSDKEngine();
    }
    public void run() {
        try {
            Log.e(TAG, " 比对线程开始 ！");
            long startTime = System.currentTimeMillis();
            AFR_FSDKError fsdkError = afr_fsdkEngine.AFR_FSDK_InitialEngine(EngintHelper.appid, EngintHelper.fr_key);
            if (fsdkError.getCode() != fsdkError.MOK) {
                if (onFaceCallback != null)
                    onFaceCallback.OnSdkUnInit();
                return;
            }
            long time1 = System.currentTimeMillis();
            Log.e(TAG, " 初始化引擎 ！耗时 ： " + (time1 - startTime));
            String path = engintHelper.getFirstImagePath();
            File file = new File(path);
            if (!file.exists()) {
                if (onFaceCallback != null)
                    onFaceCallback.OnNoMainFace();
                return;
            }
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            byte[] data = new byte[bitmap.getWidth() * bitmap.getHeight() * 3 / 2];
            try {
                ImageConverter convert = new ImageConverter();
                convert.initial(bitmap.getWidth(), bitmap.getHeight(), ImageConverter.CP_PAF_NV21);
                if (convert.convert(bitmap, data)) {
                    Log.d(TAG, "convert ok!");
                }
                convert.destroy();
            } catch (Exception e) {
                e.printStackTrace();
                if (e instanceof InterruptedException)
                    throw e;
            }
            long time2 = System.currentTimeMillis();
            Log.e(TAG, " 图片转换  耗时 ： " + (time2 - time1));

            List<AFD_FSDKFace> result = new ArrayList<>();
            Log.e(TAG, "  获取比对人脸 ！");
            AFD_FSDKError error = fsdkEngine.AFD_FSDK_StillImageFaceDetection(data, bitmap.getWidth(), bitmap.getHeight(), AFD_FSDKEngine.CP_PAF_NV21, result);
            ArrayList<AFR_FSDKFace> list = new ArrayList<>();
            long time3 = System.currentTimeMillis();
            Log.e(TAG, "  获取比对人脸特征 ！" + result.size() + "  耗时 " + (time3 - time2));
            for (AFD_FSDKFace re : result) {
                long time4 = System.currentTimeMillis();
                AFR_FSDKFace afr_fsdkFace = new AFR_FSDKFace();
                AFR_FSDKError afr_fsdkError = afr_fsdkEngine.AFR_FSDK_ExtractFRFeature(data, bitmap.getWidth(), bitmap.getHeight(), AFD_FSDKEngine.CP_PAF_NV21, re.getRect(), AFR_FSDKEngine.AFR_FOC_0, afr_fsdkFace);
                long time5 = System.currentTimeMillis();
                Log.e(TAG, " 获取特征耗时 " + (time5 - time4));
                if (afr_fsdkError.getCode() == afr_fsdkError.MOK) {
                    list.add(afr_fsdkFace);
                }
            }
            if (list.size() <= 0) {
                if (onFaceCallback != null)
                    onFaceCallback.OnNoMainFace();
                return;
            }
            int lastIndex = -1;
            Log.e(TAG, "  第一张人脸有特征  开启轮训匹配  ！ " + list.size());

            int code = fsdkError.getCode();
            int code1 = error.getCode();

            while (!isInterrupted() && !isStop && code == fsdkError.MOK && code1 == error.MOK) {
                AfdData afd_fsdkFaces = engintHelper.updataRgbRect(null, false);

                if (afd_fsdkFaces != null)
                    Log.e(TAG, "    进入！" + afd_fsdkFaces.getAfd_Resoults().size()  +  " faceindex " + afd_fsdkFaces.getIndex());

                if (afd_fsdkFaces == null || afd_fsdkFaces.getAfd_Resoults().size() <= 0 || afd_fsdkFaces.getIndex() == lastIndex) {
                    Thread.sleep(200);
                    continue;
                }

                lastIndex = afd_fsdkFaces.getIndex();
                long time10 = System.currentTimeMillis();
                boolean pass = false;

                for (AFR_FSDKFace fac1 : list) {
                    ArrayList<AFD_FSDKFace> afd_resoults = afd_fsdkFaces.getAfd_Resoults();
                    for (AFD_FSDKFace face : afd_resoults) {
                        long time6 = System.currentTimeMillis();
                        Log.e(TAG, "   匹配人脸 ！");
                        AFR_FSDKFace afr_fsdkFace = new AFR_FSDKFace();
                        int width = SharedUtlis.getInt(engintHelper.getActivity(), "config", "width", 0);
                        int height = SharedUtlis.getInt(engintHelper.getActivity(), "config", "height", 0);

                        AFR_FSDKError afr_fsdkError = afr_fsdkEngine.AFR_FSDK_ExtractFRFeature(afd_fsdkFaces.getData(), width, height, AFD_FSDKEngine.CP_PAF_NV21, face.getRect(), AFR_FSDKEngine.AFR_FOC_270, afr_fsdkFace);
                        long time7 = System.currentTimeMillis();

                        Log.e(TAG, "   afr_fsdkError code  " + afr_fsdkError.getCode() + "  获取摄像头特征耗时 ： " + (time7 - time6));

                        if (afr_fsdkError.getCode() == afr_fsdkError.MOK) {
                            AFR_FSDKMatching matching = new AFR_FSDKMatching();
                            afr_fsdkEngine.AFR_FSDK_FacePairMatching(fac1, afr_fsdkFace, matching);
                            long time8 = System.currentTimeMillis();
                            Log.e(TAG, "  人脸匹配值： " + matching.getScore() + "   特征匹配耗时 ： " + (time8 - time7));
                            MatchData matchData = new MatchData();
                            matchData.setSourWidth(width);
                            matchData.setSourHeidht(height);
                            matchData.setSoursData(afd_fsdkFaces.getData());
                            matchData.setmScore(matching.getScore());
                            matchData.setRect(face.getRect());
                            if (matching.getScore() > engintHelper.getPass()) {
                                if (onFaceCallback != null)
                                    onFaceCallback.OnFacePass(matchData);
                                pass = true;
                            } else {
                                if (onFaceCallback != null)
                                    onFaceCallback.onFaceFiled(matchData);
                            }
                        } else {
                            Log.e(TAG, "  未获取到人脸特征 ");
                        }
                    }
                }

                if (!pass) {
                    if (onFaceCallback != null)
                        onFaceCallback.onFaceFiled();
                }
                long time9 = System.currentTimeMillis();
                Log.e(TAG, "   总耗时 " + (time9 - time10));
                long nanoTime = System.currentTimeMillis();
                if (nanoTime - startTime > engintHelper.getFaceExTime()) {
                    if (onFaceCallback != null) {
                        onFaceCallback.OnFaceTimeOut();
                    }
                    Log.e(TAG, "   匹配超时 ！");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "" + e.toString());
        }
    }

    boolean isStop = false;

    public void stopRun() {
        isStop = true;
    }

    public void stopT() {
        Thread.interrupted();
        isStop = true;
    }
}
