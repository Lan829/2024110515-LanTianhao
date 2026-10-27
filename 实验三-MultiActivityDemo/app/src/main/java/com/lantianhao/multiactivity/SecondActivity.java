package com.lantianhao.multiactivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 第 2 个二级 Activity：输入内容，点击确定后通过 setResult 把数据返回给 MainActivity。
 */
public class SecondActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "extra_reply";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        setTitle(R.string.second_title);

        EditText etInput = findViewById(R.id.et_input);
        Button btnConfirm = findViewById(R.id.btn_confirm);

        btnConfirm.setOnClickListener(v -> {
            String input = etInput.getText().toString().trim();
            if (input.isEmpty()) {
                Toast.makeText(this, R.string.second_empty_warning, Toast.LENGTH_SHORT).show();
                return;
            }

            // 把输入内容封装进 Intent，作为结果返回给调用者
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_REPLY, input);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}
