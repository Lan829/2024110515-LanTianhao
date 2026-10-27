package com.lantianhao.multiactivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 第 3 个二级 Activity：用隐式 Intent 启动系统相机拍照，
 * 拍照成功后把照片显示在当前页面。
 */
@SuppressWarnings("deprecation")
public class ThirdActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_PHOTO = 100;
    private static final String STATE_PHOTO_PATH = "state_photo_path";

    /** 照片保存在应用缓存目录的 images 子目录，需与 file_paths.xml 保持一致 */
    private static final String PHOTO_DIRECTORY = "images";
    private static final String PHOTO_PREFIX = "JPEG_";
    private static final String PHOTO_SUFFIX = ".jpg";
    private static final String TIME_STAMP_PATTERN = "yyyyMMdd_HHmmss";

    private ImageView ivPhoto;
    private TextView tvPhotoTip;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
        setTitle(R.string.third_title);

        ivPhoto = findViewById(R.id.iv_photo);
        tvPhotoTip = findViewById(R.id.tv_photo_tip);
        Button btnTakePhoto = findViewById(R.id.btn_take_photo);

        btnTakePhoto.setOnClickListener(v -> takePhoto());

        // 横竖屏切换导致 Activity 重建时，恢复已拍照片
        if (savedInstanceState != null) {
            currentPhotoPath = savedInstanceState.getString(STATE_PHOTO_PATH);
            if (currentPhotoPath != null) {
                showPhoto(currentPhotoPath);
            }
        }
    }

    /** 构造隐式 Intent，调用系统相机并把全尺寸照片写入 FileProvider 提供的 Uri */
    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File photoFile = createImageFile();
            currentPhotoPath = photoFile.getAbsolutePath();

            Uri photoUri = FileProvider.getUriForFile(
                    this, getPackageName() + ".fileprovider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            startActivityForResult(intent, REQUEST_TAKE_PHOTO);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.third_no_camera, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, R.string.third_create_file_failed, Toast.LENGTH_SHORT).show();
        }
    }

    /** 在缓存目录 images 下创建一个临时照片文件 */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat(TIME_STAMP_PATTERN, Locale.US).format(new Date());
        File storageDir = new File(getCacheDir(), PHOTO_DIRECTORY);
        if (!storageDir.exists() && !storageDir.mkdirs()) {
            throw new IOException("Cannot create photo directory");
        }
        return File.createTempFile(PHOTO_PREFIX + timeStamp + "_", PHOTO_SUFFIX, storageDir);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == RESULT_OK && currentPhotoPath != null) {
                showPhoto(currentPhotoPath);
            } else {
                Toast.makeText(this, R.string.third_camera_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /** 从文件路径解码 Bitmap 并显示 */
    private void showPhoto(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if (bitmap != null) {
            ivPhoto.setImageBitmap(bitmap);
            tvPhotoTip.setText(R.string.third_tip_success);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_PHOTO_PATH, currentPhotoPath);
    }
}
