package com.tao.face.helper;

/**
 * Created by Tao on 2018/6/6 0006.
 */


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.tao.face.call.CameraFaceHolderCallback;
import com.tao.face.call.MyPreviewCall;
import com.tao.face.call.OnFaceCompareCallback;
import com.tao.face.camera.CameraFaceData;
import com.tao.face.data.FeatureBeen;

import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * 人脸识别帮助类
 * <p>
 * 根据传入窗口参数实现人脸框展示，摄像头实时展示
 * 设置人脸识别阈值
 * 设置人脸识别距离
 * 设置人脸识别使用的引擎
 */

public class FaceHelper {


    SurfaceView cameraSuface;
    SurfaceView frameSuface;

    float faceQuality = 0.00001f;
    float threshould = 60;
    int distance = 3;
    int compareType = 1;
    long timeOut = 100 * 1000;
    private SurfaceHolder faceHolder;
    private SurfaceHolder frameViewHolder;
    private EngineHelper engineHelper;
    Context context;
    Handler mHandler;
    private Camera.PreviewCallback preview;
    private CameraHelper cameraHelper;
    private Canvas mCanvas;
    String path;
    OnFaceCompareCallback callback;
    private CameraFaceHolderCallback faceHolderCallback;

    boolean useQuality =false ;
    private Bitmap bitmap;


    public boolean isUseQuality() {
        return useQuality;
    }

    public void setUseQuality(boolean useQuality) {
        this.useQuality = useQuality;
    }

    public FaceHelper(Context context, SurfaceView cameraSuface, SurfaceView frameSuface, String path, OnFaceCompareCallback callback) {
        this.cameraSuface = cameraSuface;
        this.frameSuface = frameSuface;
        this.context = context;
        this.path = path;
        this.callback = callback;
    }

    public FaceHelper(Context context, SurfaceView cameraSuface, SurfaceView frameSuface, Bitmap bitmap, OnFaceCompareCallback callback) {
        this.cameraSuface = cameraSuface;
        this.frameSuface = frameSuface;
        this.context = context;
        this.bitmap = bitmap;
        this.callback = callback;
    }


    CameraFaceData cameraData;
    ReentrantReadWriteLock mLock = new ReentrantReadWriteLock();
    public CameraFaceData getCameraData() {
//        Log.e(TAG,"获取数据  当前对象" + FaceHelper.this.toString());
        try {
            mLock.readLock().lock();
            return cameraData;
        } finally {
            mLock.readLock().unlock();
        }
    }

    public void writerCameraData(CameraFaceData data) {
//       Log.e(TAG,"写入数据  当前对象" + FaceHelper.this.toString());
       mLock.writeLock().lock();
        cameraData = data;
       mLock.writeLock().unlock();
    }




    public CameraFaceHolderCallback getFaceHolderCallback() {
        return faceHolderCallback;
    }

    public void startFace() {
        mHandler = new Handler();
        preview = new MyPreviewCall(this);
        cameraHelper = new CameraHelper(this);

        cameraSuface.setVisibility(View.INVISIBLE);
        frameSuface.setVisibility(View.INVISIBLE);

        faceHolder = cameraSuface.getHolder();
        faceHolderCallback = new CameraFaceHolderCallback(this);
        faceHolder.addCallback(faceHolderCallback);
        cameraSuface.setKeepScreenOn(true);
        cameraSuface.setZOrderOnTop(false);
        faceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        frameViewHolder = frameSuface.getHolder();
        frameSuface.setKeepScreenOn(true);
        frameSuface.setZOrderOnTop(true);
        frameViewHolder.setFormat(PixelFormat.TRANSPARENT);

        cameraSuface.setVisibility(View.VISIBLE);
        frameSuface.setVisibility(View.VISIBLE);

        if (path!=null&&!path.isEmpty())
        engineHelper = new EngineHelper(this, path, this.callback);
        else if (bitmap!=null)
        engineHelper = new EngineHelper(this, bitmap, this.callback);
        else
            engineHelper = new EngineHelper(this, "", this.callback);

    }

    public void close() {
        engineHelper.closeFrame();
        engineHelper.closeFeature();
    }


    public void release() {
        close();
        engineHelper.destoryEngine();
    }

    public float getFaceQuality() {
        return faceQuality;
    }

    public void setFaceQuality(float faceQuality) {
        this.faceQuality = faceQuality;
    }

    public long getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }

    public float getThreshould() {
        return threshould;
    }

    public void setThreshould(float threshould) {
        this.threshould = threshould;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setCameraSuface(SurfaceView cameraSuface) {
        this.cameraSuface = cameraSuface;
    }

    public void setFrameSuface(SurfaceView frameSuface) {
        this.frameSuface = frameSuface;
    }

    public SurfaceView getCameraSuface() {
        return cameraSuface;
    }

    public SurfaceView getFrameSuface() {
        return frameSuface;
    }

    public CameraHelper getCameraHelper() {
        return cameraHelper;
    }

    public Context getContext() {
        return context;
    }

    public SurfaceHolder getFaceHolder() {
        return faceHolder;
    }

    public SurfaceHolder getFrameViewHolder() {
        return frameViewHolder;
    }

    public EngineHelper getEngineHelper() {
        return engineHelper;
    }

    public void operateUI(Runnable runnable) {
        mHandler.post(runnable);
    }

    public Camera.PreviewCallback getPreview() {
        return preview;
    }


    boolean isDrawing = false;

    public static class FrameHolderCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    }

    FeatureBeen faceInfo;

    public synchronized FeatureBeen operatFeatureData(FeatureBeen data, boolean b) {
        if (b) {
            this.faceInfo = data;
        }
        return faceInfo;
    }


}
