package com.tao.face.domain;

/**
 * Created by Tao on 2018/6/7 0007.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.sensetime.mid.faceapi.model.FaceInfo;
import com.tao.face.call.OnFaceCompareCallback;
import com.tao.face.data.FaceResoult;
import com.tao.face.data.FeatureBeen;
import com.tao.face.helper.EngineHelper;
import com.tao.face.helper.FaceHelper;

import hr_face.tao.com.testshangtamface.R;

/**
 * 人脸比对线程
 */
public   class FaceFeature extends Thread {
    private  int lastIndex =-1 ;
    FaceHelper faceHelper;
    EngineHelper engineHelper;
    OnFaceCompareCallback callback;
    Bitmap firstBm;
    String path;
    boolean isOver = false;

    String TAG = "FaceFeature" ;

    public FaceFeature(FaceHelper faceHelper, Bitmap firstBm, OnFaceCompareCallback callback) {
        this.faceHelper = faceHelper;
        engineHelper = faceHelper.getEngineHelper();
        this.callback = callback;
        this.firstBm = firstBm;
    }

    public FaceFeature(FaceHelper faceHelper, String path, OnFaceCompareCallback callback) {
        this.faceHelper = faceHelper;
        engineHelper = faceHelper.getEngineHelper();
        this.callback = callback;
        this.path = path;
    }

    @Override
    public void run() {
        try {

//                Log.e(TAG, " 比对开启 " + Thread.currentThread() + "  faceHelper :" + faceHelper.toString());

            long firstTime = System.currentTimeMillis();
//            firstBm = BitmapFactory.decodeResource(faceHelper.getContext().getResources(), R.mipmap.test);
            byte[] bytes1 = null;
            if (firstBm != null)
                bytes1 = engineHelper.acquireFeature(firstBm);
            else if (path != null && !path.isEmpty()) {
                try {
                    firstBm = BitmapFactory.decodeFile(path);
                    bytes1 = engineHelper.acquireFeature(firstBm);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (e instanceof InterruptedException)
                        throw e;
                }
            }

            if (bytes1 == null) {
                if (callback != null)
                    callback.OnNoMainFace();
            }
            while (!isInterrupted() && !isOver) {
                if (System.currentTimeMillis() - firstTime > faceHelper.getTimeOut()) {
                    if (callback != null&&!isOver)
                        callback.OnFaceTimeOut();
                    break;
                }
                FeatureBeen featureBeen = faceHelper.operatFeatureData(null, false);
                if (featureBeen == null || bytes1 == null || featureBeen.getCameraIndex() == lastIndex) {
                    continue;
                }

                lastIndex = featureBeen.getCameraIndex();

//                    Log.e(TAG, "比对 " +lastIndex + "  " + currentThread().toString());

                boolean isSuccess = false;
                FaceResoult faceResoult = new FaceResoult();
                int feat = 0;
                byte[] bytes = null;
                for (FaceInfo info : featureBeen.getFaceInfos()) {
                    bytes = engineHelper.acquireFeature(featureBeen.getFaceData(), info);
                    if (bytes==null)
                        continue;
                    feat = engineHelper.compareFeature(bytes1, bytes);
                    if (feat >= faceHelper.getThreshould()) {
                        faceResoult.setScro(feat);
                        faceResoult.setFeatureBeen(featureBeen);
                        faceResoult.setSourceFeacture(bytes1);
                        faceResoult.setSceneFeacture(bytes);

                        if (callback != null&&!isOver)
                            callback.OnFacePass(faceResoult);
                        faceHelper.operateUI(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(faceHelper.getContext(), " 认证成功！", Toast.LENGTH_SHORT).show();
                            }
                        });
                        isSuccess = true;
                        break;
                    }
//                        Log.e(TAG, " 认证结果 ：" + feat + "  : " +currentThread().toString());
                }
                if (!isSuccess) {
                    faceResoult.setScro(feat);
                    faceResoult.setFeatureBeen(featureBeen);
                    faceResoult.setSourceFeacture(bytes1);
                    faceResoult.setSceneFeacture(bytes);
                    if (callback != null&&!isOver)
                        callback.onFaceFiled(faceResoult);
                }
                faceHelper.operatFeatureData(null, true);
                Thread.sleep(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof  RuntimeException &&callback!=null)
                callback.OnRuntimeError();
//                Log.e(TAG, " 比对异常  " + e.toString() + "   " + currentThread().toString());
        } finally {
            Log.e(TAG, " 比对结束  " + currentThread().toString());
        }
    }

    public synchronized void start() {
        isOver = false;
        super.start();
    }

    public synchronized void close() {
        isOver = true;
        Log.e(TAG , " close  "+ "  " + currentThread().toString()) ;
//            if (!isInterrupted() && isAlive())
//                interrupt();
    }
}


