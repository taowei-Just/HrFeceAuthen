package com.tao.face;

import android.app.Application;
import android.content.Context;

import com.sensetime.facesdk.FaceDetector;
import com.sensetime.facesdk.LiveDetector;
import com.sensetime.facesdk.Verifier;

/**
 * Created by Tao on 2018/6/7 0007.
 */

public class FaceApplication extends Application {



    private static LiveDetector mLiveDetector;
    private static Verifier mVerifier;
    private static FaceDetector mFaceDetector;


    public static LiveDetector initLive(Context context) {
        if (mLiveDetector == null)
            mLiveDetector = new LiveDetector.Builder(context).build();
        return mLiveDetector;
    }

    public  static Verifier initerifier(Context context) {
        if (mVerifier == null)
            mVerifier = new Verifier.Builder(context).isIdcardVerify(true).build();

        return mVerifier;
    }

    public  static FaceDetector initFaceDetector(Context context) {
        if (mFaceDetector == null)
            mFaceDetector = new FaceDetector.Builder(context).build();

        return mFaceDetector;
    }

    public static  void destoryEngine() {

        try {
            releaseLive();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            releaseVer();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            releaseFace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void releaseFace() throws Exception{
        if (mFaceDetector != null) {
            mFaceDetector.release();
            mFaceDetector = null;
        }
    }

    private static void releaseVer()throws Exception {
        if (mVerifier != null) {
            mVerifier.release();
            mVerifier = null;
        }
    }

    private static void releaseLive()throws Exception {
        if (mLiveDetector != null) {
            mLiveDetector.release();
            mLiveDetector = null;
        }
    }
}
