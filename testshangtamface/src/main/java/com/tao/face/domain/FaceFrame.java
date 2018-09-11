package com.tao.face.domain;

/**
 * Created by Tao on 2018/6/7 0007.
 */


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;

import com.sensetime.mid.faceapi.model.FaceInfo;
import com.sensetime.mid.faceapi.model.MidPixelFormat;
import com.sensetime.mid.faceapi.util.FaceQualityUtil;
import com.tao.face.camera.CameraFaceData;
import com.tao.face.data.FeatureBeen;
import com.tao.face.helper.EngineHelper;
import com.tao.face.helper.FaceHelper;

/**
 * 人脸框展示线程
 */
public   class FaceFrame extends Thread {
    String TAG = "FaceFrame" ;
    FaceHelper faceHelper;
    EngineHelper engineHelper;

    private Paint mPaint;
    private Paint clearPaint;
    private Canvas mCanvas;
    private boolean isDrawing;
    boolean isOver = false;

    public FaceFrame(FaceHelper faceHelper) {
        this.engineHelper = faceHelper.getEngineHelper();
        this.faceHelper = faceHelper;
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);
        mPaint.setColor(Color.BLUE);
        mPaint.setAntiAlias(true); // 消除锯齿

        clearPaint = new Paint();
        clearPaint.setAntiAlias(true);// 抗锯齿
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));// 所有的图层都不会在画布上展示
    }

    public void run() {
        try {

//                Log.e(TAG, " 绘制开启 " + Thread.currentThread() + "  faceHelper :" + faceHelper.toString());
            while (!isInterrupted() && !isOver) {
                // 获取视频帧
                // 获取人脸参数
                // 绘制人脸框
                CameraFaceData cameradata = faceHelper.getCameraData();
//                    Log.e(TAG, " draw frame");
                if (cameradata == null || cameradata.getData()==null)
                    continue;

                FaceInfo[]   faceInfos  = engineHelper.detectQuality(cameradata.getData(),engineHelper.faceDetect(cameradata.getData()));

                if (faceInfos != null && faceInfos.length > 0) {
                    FeatureBeen featureBeen = new FeatureBeen();
                    featureBeen.setFaceData(cameradata.getData());
                    featureBeen.setFaceInfos(faceInfos);
                    featureBeen.setCameraIndex(cameradata.getCameraIndex());
                    faceHelper.operatFeatureData(featureBeen, true);

                }
                drowFaceFrame(faceInfos);
                Thread.sleep(1);
            }

        } catch (Exception e) {

        //    Log.e(TAG, " 绘制异常  " + e.toString()+"  "+ currentThread().toString());

            e.printStackTrace();
        } finally {

            Log.e(TAG, " 绘制结束  ");

        }

    }

    private void drowFaceFrame(FaceInfo[] faceInfos) throws InterruptedException {

//            Log.e(TAG,"drowFaceFrame");

        try {
            if (!isDrawing && faceHelper.getCameraSuface().isShown() && faceHelper.getCameraHelper().getmCamera() != null) {
                long time = System.currentTimeMillis();
                isDrawing = true;
                mCanvas = faceHelper.getFrameViewHolder().lockCanvas();
                Camera.Parameters parameters = faceHelper.getCameraHelper().getmCamera().getParameters();
                Camera.Size previewSize = parameters.getPreviewSize();
                int pwidth = previewSize.width;
                int pheight = previewSize.height;
//                    Log.e(TAG, "pwidth " + pwidth + " pheight" + pheight);
                if (null != mCanvas) {
                    mCanvas.drawPaint(clearPaint);
                    float xRatio = (float) mCanvas.getWidth() / pwidth;
                    float yRatio = (float) mCanvas.getHeight() / pheight;
                    if (null != faceInfos && faceInfos.length > 0) {
                        for (int i = 0; i < faceInfos.length; i++) {
                            FaceInfo rect = faceInfos[i];
                            if (null != rect) {

                                float left;
                                float right;
//                                    Log.e(TAG, "Build.MODEL " + Build.MODEL);

                                if (Build.MODEL.equals("G1803") || Build.MODEL.equals("RK3288_V1R2")) {
                                    left = (rect.faceRect.left) * xRatio;
                                    right = (rect.faceRect.right) * xRatio;
                                } else {
                                    left = (pwidth - rect.faceRect.left) * xRatio;
                                    right = (pwidth - rect.faceRect.right) * xRatio;
                                    Log.e(TAG, "onDetectResult left" + left);
                                    Log.e(TAG, "onDetectResult right " + right);
                                }
                                mCanvas.drawRect(left, rect.faceRect.top * yRatio, right, rect.faceRect.bottom * yRatio, mPaint);
                            }
                        }
                    }
                    faceHelper.getFrameViewHolder().unlockCanvasAndPost(mCanvas);
                    Log.d(TAG, "onDetectResult consume = " + String.valueOf(System.currentTimeMillis() - time));
                }
                isDrawing = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
            if (e instanceof InterruptedException)
                throw new InterruptedException(e.getMessage());
        }
    }

    public synchronized void start() {
        isOver = false;
        super.start();
    }

    public synchronized void close() {
        isOver = true;
        if (!isInterrupted() && isAlive())
            interrupt();
    }
}
