package hr_face.tao.com.testshangtamface;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;

import com.tao.face.call.OnFaceCompareCallback;
import com.tao.face.data.FaceResoult;
import com.tao.face.helper.FaceHelper;

public class MainActivity extends AppCompatActivity {
    String TAG =getClass().getName() ;
    private FaceHelper faceHelper;
    private SurfaceView svFace;
    private SurfaceView svFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        svFace = (SurfaceView) findViewById(R.id.sv_face);
        svFrame = (SurfaceView) findViewById(R.id.sv_frame);


        faceHelper = new FaceHelper(this, svFace, svFrame, "", new  OnFaceCompareCallback() {
            @Override
            public void OnFacePass( FaceResoult faceResoult) {
                Log.e(TAG,"OnFacePass  " + faceResoult.getScro());
            }

            @Override
            public void OnNoMainFace() {
                Log.e(TAG,"OnNoMainFace");
            }

            @Override
            public void OnSdkUnInit() {
                Log.e(TAG,"OnSdkUnInit");
            }

            @Override
            public void OnFaceTimeOut() {
                Log.e(TAG,"OnFaceTimeOut  ");

            }

            @Override
            public void onFaceFiled( FaceResoult faceResoult) {
                Log.e(TAG,"onFaceFiled  "+ faceResoult.getScro());
            }

            @Override
            public void OnRuntimeError() {
                Log.e(TAG,"OnRuntimeError  ");

            }
        });

         faceHelper.startFace();
    }

    public  void  click(View v){
        if (faceHelper!=null)
            faceHelper. close();

        faceHelper = new FaceHelper(getApplicationContext(), svFace, svFrame, "", new  OnFaceCompareCallback() {
            @Override
            public void OnFacePass( FaceResoult faceResoult) {
                Log.e(TAG,"2OnFacePass  " + faceResoult.getScro());
            }

            @Override
            public void OnNoMainFace() {
                Log.e(TAG,"2OnNoMainFace");
            }

            @Override
            public void OnSdkUnInit() {
                Log.e(TAG,"2OnSdkUnInit");
            }

            @Override
            public void OnFaceTimeOut() {
                Log.e(TAG,"2OnFaceTimeOut  ");

            }
            @Override
            public void onFaceFiled( FaceResoult faceResoult) {
                Log.e(TAG,"2onFaceFiled  "+ faceResoult.getScro());
            }

            @Override
            public void OnRuntimeError() {
                Log.e(TAG,"2OnRuntimeError  ");



              runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      click(null);
                  }
              });
            }
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        faceHelper. startFace();

    }
}
