package hr_face.tao.com.hrfeceauthen;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tao.camerview.EngintHelper;
import com.tao.camerview.MatchData;
import com.tao.camerview.OnFaceCallback;
import com.tao.hr_faceLib.TailorHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    String TAG = getClass().getSimpleName();
    private ImageView iv;

    String[] prem = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.INTERNET", "android.permission.INTERNET"};

    private SurfaceView svC;
    private SurfaceView svH;
    private TextView tv_scrol;
    private TextureView ttv_camera;
    Context context;
    private String picPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
//        getSupportActionBar()
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        svC = (SurfaceView) findViewById(R.id.sv_camera);
        svH = (SurfaceView) findViewById(R.id.sv_head);
        iv = (ImageView) findViewById(R.id.iv_head);
        tv_scrol = (TextView) findViewById(R.id.tv_scrol);

        picPath = getIntent().getStringExtra("picPath");
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(prem, 100);
        } else {
            sta(svC, svH);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && permissions[0].equals(prem[0]) && grantResults[0] == 0 &&
                permissions[1].equals(prem[1]) && grantResults[1] == 0 &&
                permissions[2].equals(prem[2]) && grantResults[2] == 0 &&
                permissions[3].equals(prem[3]) && grantResults[3] == 0
                ) {
            sta(svC, svH);
        }
    }

    private void sta(final SurfaceView svC, SurfaceView svH) {
        EngintHelper engintHelper = new EngintHelper(this, svC, svH, new OnFaceCallback() {
            @Override
            public void OnFacePass(final MatchData rgbRect) {
                Log.e(TAG, "OnFacePass " + rgbRect.toString());
                try {
                    final Bitmap bitmap = TailorHelper.tailor(svC,rgbRect);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iv.setImageBitmap(bitmap);
                            tv_scrol.setText("对比度： " + rgbRect.getmScore());     }
                    });
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iv.setImageBitmap(null);
                            tv_scrol.setText("对比度： " + rgbRect.getmScore());
                        }
                    });
                    
                }
            }

            @Override
            public void OnNoMainFace() {
                Log.e(TAG, "OnNoMainFace ");
//                Toast.makeText(context, "第一张照片特征获取失败！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnSdkUnInit() {
                Log.e(TAG, "OnSdkUnInit ");
            }

            @Override
            public void OnFaceTimeOut() {
                Log.e(TAG, "OnFaceTimeOut ");
            }

            @Override
            public void onFaceFiled(final MatchData rgbRect) {
                Log.e(TAG, "onFaceFiled " + rgbRect.toString());
                try {
//                    final Bitmap bitmap = TailorHelper.tailor(rgbRect);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iv.setImageBitmap(null);
                            tv_scrol.setText("对比度： " + rgbRect.getmScore());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFaceFiled() {
                Log.e(TAG, "onFaceFiled ");
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "onError " + e.getMessage());
            }
        });

        engintHelper.setPass(0.5f);
        engintHelper.setFirstImagePath(picPath);
        engintHelper.startMatch();
    }


    static abstract class LoopThread extends Thread {
        private Handler handler;
        private FileInputStream inputStream;
        private FileOutputStream outputStream;
        onReceiverCallback callnack;
        private Looper looper;
        public void setCallnack(onReceiverCallback callnack) {
            this.callnack = callnack;
        }
        public boolean isloop = false;
        @Override
        public void run() {
            Looper.prepare();
            looper = Looper.myLooper();
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {

                    switch (msg.what) {
                        case 1:
                            Log.e("seril", "1");
                            File file = new File("/sdcard/txt");
                            if (!file.exists()) {
                                file.getParentFile().mkdirs();
                                try {
                                    file.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            try {
                                inputStream = new FileInputStream("/sdcard/txt");
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            try {
                                outputStream = new FileOutputStream("/sdcard/txt", true);
                                outputStream.write("aaaaaaaaaaa".getBytes());
                                outputStream.flush();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 0:
                            Log.e("seril", "0");
                            Bundle data = msg.getData();
                            String cmd = data.getString("cmd");
                            try {
                                byte[] bytes1 = cmd.getBytes();
                                outputStream.write(bytes1, 0, bytes1.length);
                                outputStream.flush();
                                byte[] bytes = loopRead(10);

                                if (callnack != null)
                                    callnack.onDataReceiverEnd();
                                Log.e("seril", "onDataReceiverEnd   0");
                                if (callnack != null)
                                    callnack.onDataReceiver(bytes);

                                Log.e("seril", "onDataReceiver   0");

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                }
            };
            isloop = true;
            if (callnack != null)
                callnack.onLoop();
            Looper.loop();
            isloop = false;
        }

        public void destory() {
            if (looper != null)
                looper.quit();
        }

        protected byte[] loopRead(int len) {
            byte[] data = new byte[0];
            byte[] buff;
            if (callnack != null)
                callnack.onDataReceiverBegin();
            while (!isInterrupted()) {
                int available = 0;
                try {
                    available = inputStream.available();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (available > 0) {
                    try {
                        buff = new byte[available];
                        inputStream.read(buff, 0, available);
                        byte[] datacach = new byte[(data.length + available)];

                        System.arraycopy(data, 0, datacach, 0, data.length);

                        System.arraycopy(buff, 0, datacach, data.length, buff.length);

                        data = new byte[datacach.length];

                        System.arraycopy(data, 0, datacach, 0, datacach.length);

                        if (callnack != null)
                            callnack.onDataReceiverDataChange(data, buff);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (data.length >= len)
                    return data;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    destory();
                }
            }
            return null;
        }


        public void openPort() {
            handler.sendEmptyMessage(1);
        }

        public void sendCmd(String str, onReceiverCallback callback) {
            callnack = callback;
            handler.sendEmptyMessage(0);
        }

        public void sendCmd(String str) {
            Message message = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("cmd", str);
            message.setData(bundle);
            message.what = 0;
            handler.sendMessage(message);
        }
    }

    class serilPrtrHandler extends LoopThread {
        @Override
        public void openPort() {
            if (isloop)
                super.openPort();
            Log.e("seril", "openPort");
        }

        @Override
        public void sendCmd(String str, onReceiverCallback callback) {
            if (isloop)
                super.sendCmd(str, callback);
            Log.e("seril", "sendCmd");
        }

        public void sendCmd(String str) {
            if (isloop)
                super.sendCmd(str);

            Log.e("seril", "sendCmd");
        }
    }


    interface onReceiverCallback {
        void onDataReceiverBegin();

        void onDataReceiverDataChange(byte[] data, byte[] buff);

        void onDataReceiver(byte[] data);

        void onDataReceiverEnd();

        void onLoop();
    }


}
