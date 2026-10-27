package com.lantianhao.multiactivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 程序入口：提供 4 个按钮，分别演示正向传参、数据返回、
 * 隐式调用系统相机和秒表。
 */
@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity {

    /** 请求码：启动 SecondActivity 并等待它返回数据 */
    private static final int REQUEST_CODE_INPUT = 1;

    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tv_result);
        Button btnFirst = findViewById(R.id.btn_first);
        Button btnSecond = findViewById(R.id.btn_second);
        Button btnThird = findViewById(R.id.btn_third);
        Button btnFourth = findViewById(R.id.btn_fourth);

        btnFirst.setOnClickListener(v -> openFirstActivity());
        btnSecond.setOnClickListener(v -> openSecondActivity());
        btnThird.setOnClickListener(v -> startActivity(new Intent(this, ThirdActivity.class)));
        btnFourth.setOnClickListener(v -> startActivity(new Intent(this, FourthActivity.class)));
    }

    /** 启动第 1 个二级 Activity，并用 Intent 携带姓名、学号 */
    private void openFirstActivity() {
        Intent intent = new Intent(this, FirstActivity.class);
        intent.putExtra(FirstActivity.EXTRA_NAME, getString(R.string.student_name));
        intent.putExtra(FirstActivity.EXTRA_NUMBER, getString(R.string.student_number));
        startActivity(intent);
    }

    /** 启动第 2 个二级 Activity，等待它通过 setResult 返回数据 */
    private void openSecondActivity() {
        Intent intent = new Intent(this, SecondActivity.class);
        startActivityForResult(intent, REQUEST_CODE_INPUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_INPUT && resultCode == Activity.RESULT_OK && data != null) {
            String reply = data.getStringExtra(SecondActivity.EXTRA_REPLY);
            if (reply != null) {
                tvResult.setText(getString(R.string.main_result_format, reply));
            }
        }
    }
}
