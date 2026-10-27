package com.lantianhao.multiactivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 第 1 个二级 Activity：接收 MainActivity 通过 Intent 正向传入的参数并显示。
 */
public class FirstActivity extends AppCompatActivity {

    public static final String EXTRA_NAME = "extra_name";
    public static final String EXTRA_NUMBER = "extra_number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        setTitle(R.string.first_title);

        Intent intent = getIntent();
        String name = intent.getStringExtra(EXTRA_NAME);
        String number = intent.getStringExtra(EXTRA_NUMBER);

        // 若未收到参数，用字符串资源中的“未知”兜底
        if (name == null) {
            name = getString(R.string.unknown_value);
        }
        if (number == null) {
            number = getString(R.string.unknown_value);
        }

        TextView tvMessage = findViewById(R.id.tv_message);
        tvMessage.setText(getString(R.string.first_message_format, name, number));
    }
}
