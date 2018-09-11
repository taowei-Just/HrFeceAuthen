package com.tao.camerview;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

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

import static android.R.attr.x;


/**
 * Created by Tao on 2018/5/26 0026.
 */

public class EngintHelper {

    public static String appid = "2czGDdCTQbBN4w8CdJUeGGfgizwgz1jVP97L2NbCseBG";
    public static String ft_key = "Ho6BRV7jpDhak7hrDP4pJMu2vpd72kbvs8zF3VJqCbrD";
    public static String fd_key = "Ho6BRV7jpDhak7hrDP4pJMuA6DtGxez8iodB9KQWEbuS";
    public static String fr_key = "Ho6BRV7jpDhak7hrDP4pJMuHFd9UwUUve21DhRPyJXwD";
    public static String age_key = "Ho6BRV7jpDhak7hrDP4pJMuu4dTLug6UEWmAoJuUGoY7";
    public static String gender_key = "Ho6BRV7jpDhak7hrDP4pJMv2E2iVwtYXaPVQh4Cgitey";


    String TAG = getClass().getSimpleName();

    SurfaceView faceView;
    SurfaceView frameView;
    private Camera camera;
    Activity activity;
    private MyThread myThread;
    //    FrameSurface faceFrameSurface;
    private float pass = 0.6f;
    private OnFaceCallback onFaceCallback;
    private long faceExTime = 60 * 100 * 1000;
    //   public static RgbRect firstRgbRect;
    public boolean bSdkInit = false;
    private byte[] bRgb24;
    private AfdData rgbRect;
    private SurfaceHolder faceHolder;
    private SurfaceHolder frameViewHolder;
    private boolean isDrawing;
    private Canvas mCanvas;
    private Paint mPaint;
    private Paint clearPaint;
    private AFD_FSDKEngine fsdkEngine;
    private Bitmap firstImage;
    private String firstImagePath;
    private CompareThread compareThread;

    public EngintHelper(AppCompatActivity activity, SurfaceView faceView, SurfaceView frameView, OnFaceCallback onFaceCallback) {
        this.activity = activity;
        this.faceView = faceView;
        this.frameView = frameView;
        this.onFaceCallback = onFaceCallback;
    }

    public void startMatch() {
        init();
    }

    public void setFirstImagePath(String firstImagePath) {
        this.firstImagePath = firstImagePath;
    }

    public void setPass(float pass) {
        this.pass = pass;
    }

    public void setbSdkInit(boolean bSdkInit) {
        this.bSdkInit = bSdkInit;
        if (myThread != null)
            myThread.setbSdkInit(bSdkInit);
    }

    public long getFaceExTime() {
        return faceExTime;
    }

    public void setFaceExTime(long faceExTime) {
        this.faceExTime = faceExTime;
    }

    public void setOnFaceCallback(OnFaceCallback onFaceCallback) {
        this.onFaceCallback = onFaceCallback;
    }


    public Camera getCamera() {
        return camera;
    }

    public Activity getActivity() {
        return activity;
    }

    public float getPass() {
        return pass;
    }

    public OnFaceCallback getOnFaceCallback() {
        return onFaceCallback;
    }

    public void initCamera() {
        try {
            camera = Camera.open(Camera.getNumberOfCameras() - 1);
            if (camera == null)
                return;
            camera.setPreviewDisplay(faceHolder);
            camera.setPreviewCallback(new MyPreview(this));
            camera.getParameters().setPreviewFormat(ImageFormat.NV21);
            camera.getParameters().setPreviewSize(faceView.getWidth(), faceView.getHeight());
            camera.setDisplayOrientation(90);
            camera.startPreview();

            // 人脸获取线程
            myThread = new MyThread(this);
            myThread.setbSdkInit(bSdkInit);
            myThread.start();
            // 人脸比对线程
            compareThread = new CompareThread(this);
            compareThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void drowFaceFrame(AfdData afdData, int faceNum) {
        try {
            if (!isDrawing && frameView.isShown() && faceNum > 0 && camera != null) {
                long time = System.currentTimeMillis();
                isDrawing = true;
                mCanvas = frameViewHolder.lockCanvas();
                Camera.Parameters parameters = camera.getParameters();
                Camera.Size previewSize = parameters.getPreviewSize();
                int pwidth = previewSize.width;
                int pheight = previewSize.height;

                if (null != mCanvas) {
                    mCanvas.drawPaint(clearPaint);
                    // 横屏计算
                    float xRatio = (float) mCanvas.getWidth() / pwidth;
                    float yRatio = (float) mCanvas.getHeight() / pheight;
                    if (null != afdData && faceNum > 0 && afdData.getAfd_Resoults() != null) {
                        ArrayList<AFD_FSDKFace> thidFaceRects = afdData.getAfd_Resoults();
                        for (int i = 0; i < faceNum; i++) {
                            AFD_FSDKFace rect = thidFaceRects.get(i);
                            if (null != rect) {
                                float left;
                                float right;

                                Rect rec = rect.getRect();

                                if (Build.MODEL.equals("G1803") || Build.MODEL.equals("RK3288_V1R2")) {
                                    left = (rec.left) * xRatio;
                                    right = (rec.right) * xRatio;
                                } else {
                                    left = (pwidth - rec.left) * xRatio;
                                    right = (pwidth - rec.right) * xRatio;
//                                    Log.e(TAG, "onDetectResult left" + left);
//                                    Log.e(TAG, "onDetectResult right " + right);
                                }
                                
                                left = (pwidth - rec.left) * xRatio;
                                right = (pwidth - rec.right) * xRatio;
                                int top = (int) (rec.top * yRatio);
                                int bottom = (int) (rec.bottom * yRatio);
                                rec.left = (int) left;
                                rec.right = (int) right;
                                rec.top = top;
                                rec.bottom = bottom;
                                rec = orentation(rec, 90, mCanvas , previewSize);
//                                Log.e(TAG, "onDetectResult t " + rect.getRect());

                                Paint paint = new Paint();
                                paint.setColor(Color.BLUE);
                                paint.setStyle(Paint.Style.STROKE);
                                paint.setStrokeWidth(3);
                                mCanvas.drawRect(rec.left, rec.top, rec.right, rec.bottom, paint);

                            }
                        }
                    }

//                    Log.e(TAG, "onDetectResult consume = " + String.valueOf(System.currentTimeMillis() - time));
                    frameViewHolder.unlockCanvasAndPost(mCanvas);
                }
                isDrawing = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());

        } finally {

        }
    }

    private void init() {
        faceView.setVisibility(View.INVISIBLE);
        frameView.setVisibility(View.INVISIBLE);

        rgbRect = null;
        cameraData = null;

        faceHolder = faceView.getHolder();
        frameViewHolder = frameView.getHolder();

        faceView.setZOrderMediaOverlay(false);
//        frameView.setZOrderOnTop(true);

//        faceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        faceHolder.setFormat(PixelFormat.TRANSPARENT);

        frameViewHolder.setFormat(PixelFormat.TRANSPARENT);
        frameView.setKeepScreenOn(true);

        faceHolder.addCallback(new HolderCallBack(this, faceView));
        frameViewHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(final SurfaceHolder holder) {
                holder.setFormat(PixelFormat.TRANSPARENT);
//                Canvas canvas = holder.lockCanvas();
//                if (canvas!=null){
//                    Log.e(TAG, " frameViewHolder surfaceCreated");
//                    Paint paint = new Paint();
//                    paint.setColor(Color.RED);
//                    paint.setStyle(Paint.Style.STROKE);
//                    canvas.drawPaint(paint);
//
//                }
//                holder.unlockCanvasAndPost(canvas);
//
//                new Thread(new Runnable() {
//                    int text = 1 ;
//                    @Override
//                    public void run() {
//                        while (true){
//                            Canvas canvas = holder.lockCanvas();
//                            if (canvas!=null){
//                                Paint paint = new Paint();
//                                paint.setColor(Color.RED);
//                                paint.setStyle(Paint.Style.STROKE);
//                                paint.setTextSize(30);
//                                canvas.drawText((text++)+"",  text*10 ,text*5  ,paint);
//                            }
//
//                               holder.unlockCanvasAndPost(canvas);
//                            try {
//                                Thread.sleep(100);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }).start();
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);
        mPaint.setColor(Color.BLUE);
        mPaint.setAntiAlias(true); // 消除锯齿

        clearPaint = new Paint();
        clearPaint.setAntiAlias(true);// 抗锯齿
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));// 所有的图层都不会在画布上展示

        frameView.setVisibility(View.VISIBLE);
        faceView.setVisibility(View.VISIBLE);

    }

    public void reStartFace() {
        if (!faceView.isShown())
            return;
        if (camera == null) {
            initCamera();
        } else {
            if (myThread != null && !myThread.isInterrupted())
                myThread.interrupt();
            myThread = new MyThread(this);
            myThread.setbSdkInit(bSdkInit);
            myThread.start();
        }
    }

    public void stopEngint() {
        if (myThread != null && !myThread.isInterrupted()) {
            myThread.stopT();
        }
        if (compareThread != null && !compareThread.isInterrupted()) {
            compareThread.stopT();
        }
    }

    public void realseEngint() {
        try {
            reaseCamere();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void reaseCamere() throws Exception {
        stopEngint();
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    byte[] cameraData;

    public synchronized byte[] operaterData(byte[] data, boolean isWrite) {
        if (isWrite)
            cameraData = data;
        return cameraData;
    }

    private synchronized byte[] updataFaceexRgb(byte[] bRgb24, boolean b) {
        if (b) this.bRgb24 = bRgb24;
        return bRgb24;
    }

    public synchronized AfdData updataRgbRect(AfdData rgbRect, boolean isChange) {
        if (isChange)
            this.rgbRect = rgbRect;
        return this.rgbRect;
    }


    private Rect orentation(Rect rect, int o, Canvas mCanvas, Camera.Size previewSize) {

        int top = rect.top;
        int left = rect.left;
        int right = rect.right;
        int bottom = rect.bottom;

        Rect rect1 = new Rect();

        int pW = previewSize.width;
        int pH = previewSize.height;

        int height = mCanvas.getHeight();
        int width = mCanvas.getWidth();

        int rW = right - left;
        int rH = bottom - top;

        float sH = rH / height;
        float sW = rW / width;

        float wh = ((float) width / height);
        float hw =  ((float)height / width);


        switch (o) {
            case 90:

                rect1.top = (int) (left*hw);
                rect1.left = (int) ((height-bottom)*wh);
                rect1.right = (int) ((height-top)*wh);
                rect1.bottom = (int) (right*hw);

                return rect1;
            case 180:
                return rect1;

            case 270:

                rect1.top = (int) (left * wh);
                rect1.left = (int) (bottom * hw);
                rect1.right = (int) (top * hw);
                rect1.bottom = (int) (left * wh);

                return rect1;
        }
        return rect1;
    }

    public void clrarFrame() {
        drowFaceFrame(null, 1);
    }

    public void hideCameraView() {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                faceView.setVisibility(View.INVISIBLE);
                frameView.setVisibility(View.INVISIBLE);
            }
        });

    }


    public AFD_FSDKEngine getFsdkEngine() {
        return fsdkEngine;
    }

    public void setFsdkEngine(AFD_FSDKEngine fsdkEngine) {
        this.fsdkEngine = fsdkEngine;
    }

    public Bitmap getFirstImage() {
        return firstImage;
    }

    public String getFirstImagePath() {
        return firstImagePath;
    }
    
    
    public static class EngintType {
        final static int V1 = 1;
        final static int VN = 2;
        final static int NVN = 3;
    }

}
