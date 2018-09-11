package hr_face.tao.com.hrfeceauthen;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Tao on 2018/7/5 0005.
 */

public class FirstAtivity extends Activity {
    String[] prem = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.INTERNET", "android.permission.INTERNET"};
    private AlertDialog alertDialog;
    String picPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(prem ,100);
        }
    }

    String TAG = getClass().getSimpleName();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult requestCode " + requestCode + " resultCode " + resultCode + "   " +picPath);
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case 100:
                    try {
                        Bitmap bitmap = BitmapFactory.decodeFile(picPath);
                        if (bitmap == null)
                            picPath = null;
                    } catch (Exception e) {
                        picPath = null;
                        e.printStackTrace();
                    }
                    if (picPath != null) {
                        Log.e(TAG, "" + picPath);
                    } else {
                        Toast.makeText(this, "照片无效请重试！", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 101:

                    String pathFromUri = RealPathFromUriUtils.getRealPathFromUri(this, data.getData());
                    if (pathFromUri != null && new File(pathFromUri).exists()) {
                        picPath = pathFromUri;
                        Log.e(TAG , "picPath " + picPath);
                        Toast.makeText(this, " 图片地址 "+picPath, Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        else
            picPath = "";
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && permissions[0].equals(prem[0]) && grantResults[0] == 0 &&
                permissions[1].equals(prem[1]) && grantResults[1] == 0 &&
                permissions[2].equals(prem[2]) && grantResults[2] == 0 &&
                permissions[3].equals(prem[3]) && grantResults[3] == 0
                ) {
//           Toast.makeText(this,"授权成功！" ,Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "请允许应用需要授权！", Toast.LENGTH_SHORT).show();
        }
    }

    public void pic(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        ViewGroup vg = (ViewGroup) View.inflate(this, R.layout.dialog_select, null);
        Button bt_camera = vg.findViewById(R.id.bt_camera);
        Button bt_cancle = vg.findViewById(R.id.bt_cancle);
        Button bt_pic = vg.findViewById(R.id.bt_pic);
        builder.setView(vg);
        alertDialog = builder.create();
        alertDialog.show();
        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        bt_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera();
                alertDialog.dismiss();
            }
        });

        bt_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPicture();
                alertDialog.dismiss();
            }
        });
    }

    private void startCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory() + "/DIUM/" + new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_s").format(new Date(System.currentTimeMillis())) + ".png");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        picPath = file.getAbsolutePath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            doTakePhotoIn7(picPath, 100);
        } else {
          intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            startActivityForResult(intent, 100);
        }
    }

    private void startPicture() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), 101);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, 101);

        }

    }

    public void start(View v) {
        if (picPath == null || picPath.isEmpty()) {
            Toast.makeText(this, " 请选择需比对的照片！ ", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("picPath", picPath);
        startActivity(intent);
    }

    //在Android7.0以上拍照
    private void doTakePhotoIn7(String path,int requestCode) {
        Uri mCameraTempUri;
        try {
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            values.put(MediaStore.Images.Media.DATA, path);
            mCameraTempUri =  getContentResolver()
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            takePhoto(  requestCode, mCameraTempUri);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void takePhoto(int requestCode, Uri uri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        if (uri != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        }
        startActivityForResult(intent, requestCode);
    }
}
