package com.sensetime.sample;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.sensetime.facesdk.FaceDetector;
import com.sensetime.facesdk.FaceUtils;
import com.sensetime.facesdk.LiveDetector;
import com.sensetime.facesdk.SDKManager;
import com.sensetime.facesdk.Verifier;
import com.sensetime.mid.faceapi.model.FaceInfo;
import com.sensetime.mid.faceapi.model.MidPixelFormat;
import com.sensetime.mid.faceapi.util.FaceQualityUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static android.os.Looper.loop;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Verifier mVerifier;
    private FaceDetector mFaceDetector;
    private LiveDetector mLiveDetector;
    private ProgressDialog mProgressDialog;
    private Context mContext;
    private CameraTextureView mCameraView;
    private SurfaceView mOverlapView;
    private boolean mNv21GetReady;
    private boolean mIsPaused;
    private boolean mFaceGetReady;
    private byte[] mNv21; // nv21的数据用来track face
    private byte[] mFaceData; //保存当前face所对应的nv21
    private byte[] mFeature;
    private FaceInfo mFaceInfo;


    private ExecutorService mWorkerThreads = Executors.newFixedThreadPool(2);
    private Future mTrackThread;
    private Future mRecognizeThread;
    private CountDownLatch mCountDownLatch;
    private Paint mPaint = new Paint(); // 绘制人脸框和点的画笔
    private TextView mScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "序列号 1 ：" +getSerialNumber());
        Log.e(TAG, "序列号 12：" +Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        mContext = this;
        initViews();
        initData();
    }

    private void initViews() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("初始化中...");

        mCameraView = (CameraTextureView) findViewById(R.id.csv_camera);
        mOverlapView = (SurfaceView) findViewById(R.id.sv_overlap);
        mScore = (TextView) findViewById(R.id.tv_score);

        mOverlapView.setZOrderOnTop(true);
        mOverlapView.getHolder().setFormat(android.graphics.PixelFormat.TRANSLUCENT);
        mOverlapView.setLayoutParams(mCameraView.getLayoutParams());

        mCameraView.setPreviewCallback(new Camera.PreviewCallback() {
                                           @Override
                                           public void onPreviewFrame(byte[] data, Camera camera) {
                                               mCameraView.addCallbackBuffer();
                                               if (mIsPaused)
                                                   return;
                                               if (mNv21 == null || data.length != mNv21.length) {
                                                   mNv21 = new byte[data.length];
                                                   mFaceData = new byte[data.length];
                                                   mPaint.setStrokeWidth(480 >> 7);
                                               }
                                               if (!mNv21GetReady) {
                                                   System.arraycopy(data, 0, mNv21, 0, data.length);
                                                   mNv21GetReady = true;
                                               }
                                           }
                                       }

        );
        mCameraView.startPreview();

    }


    public class TrackTask implements Runnable { //track 消费nv21
        @Override
        public void run() {
            while (!mIsPaused) {
                if (mNv21GetReady) {

                    FaceInfo faceInfo = mFaceDetector.detect(mNv21);

                    if (faceInfo == null) {
                        clearCanvas();
                        mNv21GetReady = false;
                        continue;
                    }
                    float quality = FaceQualityUtil.detectQuality(mNv21, MidPixelFormat.NV21,
                            mCameraView.mPreviewWidth, mCameraView.mPreviewHeight, faceInfo.facePoints);

                    if (quality < 0.001) { //该帧数据质量太差，过滤
                        clearCanvas();
                        mNv21GetReady = false;
                        continue;
                    }

                    drawFace(faceInfo);
                    if (!mFaceGetReady) {
                        saveImage(mFaceData,"没有获取到特征的_Liveness_score_" + "__"+ System.currentTimeMillis() , 0,0);
                        mFaceInfo = FaceUtils.clone(faceInfo);
                        System.arraycopy(mNv21, 0, mFaceData, 0, mNv21.length);
                        mFaceGetReady = true;
                    }
                    mNv21GetReady = false;
                }
            }
            mCountDownLatch.countDown();
        }
    }

    private void drawFace(FaceInfo faceInfo) {
        if (faceInfo != null) {
            Canvas canvas = mOverlapView.getHolder().lockCanvas();
            if (canvas == null) {
                return;
            }
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            canvas.save();
            canvas.setMatrix(mCameraView.getMatrix());
            // 如果需要用到属性提取、认证功能，建议使用cvface.clone()方法复制一份数据进行旋转操作和绘制，可以不改变原始数据
            FaceInfo face = FaceUtils.clone(faceInfo);
            FaceUtils.rotateFace(face, mCameraView.mPreviewWidth, mCameraView.mPreviewHeight,
                    mCameraView.isFrontCamera(), mCameraView.mDegrees);
            FaceUtils.drawFace(canvas, face, mPaint);
            canvas.restore();
            mOverlapView.getHolder().unlockCanvasAndPost(canvas);
        } else {
            clearCanvas();
        }
    }

    //将身份证图片放大，宽&高的像素均 > 150
    public static Bitmap scaledBitmap(Bitmap bitmap) {
        int WIDTH = 640;
        int width = bitmap.getWidth(), height = bitmap.getHeight();
        int dstWidth, dstHeight;
        if (width > height) {
            dstWidth = WIDTH;
            dstHeight = WIDTH * height / width;
        } else {
            dstHeight = WIDTH;
            dstWidth = WIDTH * width / height;
        }
        return Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, true);
    }

    private class FaceRecognizeTask implements Runnable {
        @Override
        public void run() {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test);
            bitmap = scaledBitmap(bitmap);

            //获取特征
            mFeature = mVerifier.getFeature(bitmap);
            while (!mIsPaused) {
                if (mFaceGetReady) {
                    Camera.Parameters parameters = mCameraView.getCamera().getParameters();
                    if (mFaceInfo != null && mFeature != null && mFeature.length > 0) {
                        final float livenessScore = mLiveDetector.detect(mFaceData, mFaceInfo);
                        int verifyScore = 0;
                        byte[] feature1 = mVerifier.getFeature(mFaceData, mFaceInfo);
                        verifyScore = mVerifier.compareFeature(mFeature, feature1);
                        final int finalVerifyScore = verifyScore;
                        Log.e(TAG, " 抓到人脸了 "+livenessScore);
                        saveImage(mFaceData," 能抓到特征的_Liveness_score_" +livenessScore+"__"+ System.currentTimeMillis()  ,parameters.getPictureSize().height,parameters.getPreviewSize().width);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mScore.setText("Liveness score : " + livenessScore + ",  Verify score: " + finalVerifyScore);
                            }
                        });
                        Log.d(TAG, "Liveness score : " + livenessScore + ", Verify score: " + verifyScore);
                    } else {
                        saveImage(mFaceData,"没有获取到特征的_Liveness_score_" + "__"+ System.currentTimeMillis() , parameters.getPreviewSize().width,parameters.getPictureSize().height);
                        Log.e(TAG, " 没有人脸 " );
                        mScore.setText("");
                    }
                    mFaceInfo = null;
                    mFaceGetReady = false;
                }
            }
            mCountDownLatch.countDown();
        }
    }

  public  void   saveImage(byte[]data , String name , int w,int h){
      try{
          w = 640;
          h = 480 ;
          YuvImage yuvImage = new YuvImage(data, ImageFormat.NV21, w,h , null);
          ByteArrayOutputStream bos = new ByteArrayOutputStream();
          yuvImage.compressToJpeg(new Rect(0,0,w,h),100,bos);
          byte[] bytes = bos.toByteArray();
          Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
          File file = new File(Environment.getExternalStorageDirectory().toString() + "/FaceImage/" + name + ".jpg");
          if (!file.exists())
              file.getParentFile().mkdirs();
          file.createNewFile();
          FileOutputStream fos = new FileOutputStream(file);
          bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
          bos.close();
          fos.close();
      }catch (Exception e){
          e.printStackTrace();}

    }


    private void initData() {

        mPaint.setColor(Color.rgb(57, 138, 243));
        mPaint.setStyle(Paint.Style.STROKE);

        // SDKManager.initWithLeafLicense
        SDKManager.initWithMiddleLicense(this, new SDKManager.SDKInitListener() {
            @Override
            public void onStart() {
                mProgressDialog.show();
            }

            @Override
            public void onSucceed() {
                Log.d(TAG, "证书校验成功");
                Toast.makeText(mContext, "证书校验成功", Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
                mLiveDetector = new LiveDetector.Builder(mContext)
                        .build();
                mVerifier = new Verifier.Builder(mContext)
                        .isIdcardVerify(true)
                        .build();
                mFaceDetector = new FaceDetector.Builder(mContext)
                        .build();
                mIsPaused = false;
                mTrackThread = mWorkerThreads.submit(new TrackTask());
                mRecognizeThread = mWorkerThreads.submit(new FaceRecognizeTask());
            }

            @Override
            public void onFailed(int code, String message) {
                Log.d(TAG, "证书校验失败:" + code);
                Toast.makeText(mContext, "证书校验失败:" + code, Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
            }
        });

        mCountDownLatch = new CountDownLatch(2);
    }

    private void clearCanvas() {
        Canvas canvas = mOverlapView.getHolder().lockCanvas();
        if (canvas != null) {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            mOverlapView.getHolder().unlockCanvasAndPost(canvas);
        }
    }

    @Override
    protected void onDestroy() {
        mIsPaused = true;
        if (mTrackThread != null)
            mTrackThread.cancel(true);

        if (mRecognizeThread != null)
            mRecognizeThread.cancel(true);

        try {
            mCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (mVerifier != null) //释放人脸检测对象
            mVerifier.release();

        if (mFaceDetector != null)
            mFaceDetector.release();

        if (mLiveDetector != null)
            mLiveDetector.release();

        super.onDestroy();
    }

    private String getSerialNumber(){

        String serial = null;

        try {

            Class<?> c =Class.forName("android.os.SystemProperties");

            Method get =c.getMethod("get", String.class);

            serial = (String)get.invoke(c, "ro.serialno");

        } catch (Exception e) {

            e.printStackTrace();

        }
        return serial;

    }


}
