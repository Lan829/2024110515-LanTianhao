package com.lantianhao.multiactivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 第 4 个二级 Activity：秒表。
 *
 * <p>计时基于 SystemClock.elapsedRealtime()，并通过 onSaveInstanceState
 * 保存“已累计时长 + 是否运行”，因此横竖屏切换导致 Activity 重建后
 * 计时仍能连续进行。</p>
 */
public class FourthActivity extends AppCompatActivity {

    private static final String STATE_ACCUMULATED = "state_accumulated";
    private static final String STATE_START_ELAPSED = "state_start_elapsed";
    private static final String STATE_RUNNING = "state_running";

    /** 界面刷新间隔（毫秒），不影响计时精度 */
    private static final long TICK_INTERVAL_MS = 50L;

    private TextView tvTime;
    private Button btnStart;
    private Button btnPause;

    /** 已累计的毫秒数（不含当前正在运行的这一段） */
    private long accumulatedMs;
    /** 本次开始运行时的 elapsedRealtime */
    private long startElapsedMs;
    private boolean running;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            updateTimeDisplay();
            if (running) {
                handler.postDelayed(this, TICK_INTERVAL_MS);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);
        setTitle(R.string.fourth_title);

        tvTime = findViewById(R.id.tv_time);
        btnStart = findViewById(R.id.btn_start);
        btnPause = findViewById(R.id.btn_pause);
        Button btnReset = findViewById(R.id.btn_reset);

        // 横竖屏切换后恢复计时状态
        if (savedInstanceState != null) {
            accumulatedMs = savedInstanceState.getLong(STATE_ACCUMULATED);
            startElapsedMs = savedInstanceState.getLong(STATE_START_ELAPSED);
            running = savedInstanceState.getBoolean(STATE_RUNNING);
        }

        btnStart.setOnClickListener(v -> startTimer());
        btnPause.setOnClickListener(v -> pauseTimer());
        btnReset.setOnClickListener(v -> resetTimer());

        updateTimeDisplay();
        updateButtonState();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 页面可见时恢复刷新（旋转屏幕后同样适用）
        if (running) {
            handler.post(ticker);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 页面不可见时停止刷新，计时数据仍然保留
        handler.removeCallbacks(ticker);
    }

    private void startTimer() {
        if (running) {
            return;
        }
        startElapsedMs = SystemClock.elapsedRealtime();
        running = true;
        handler.post(ticker);
        updateButtonState();
    }

    private void pauseTimer() {
        if (!running) {
            return;
        }
        accumulatedMs += SystemClock.elapsedRealtime() - startElapsedMs;
        running = false;
        handler.removeCallbacks(ticker);
        updateTimeDisplay();
        updateButtonState();
    }

    private void resetTimer() {
        running = false;
        accumulatedMs = 0L;
        startElapsedMs = 0L;
        handler.removeCallbacks(ticker);
        updateTimeDisplay();
        updateButtonState();
    }

    /** 当前总计时（毫秒）：已累计 + 正在运行的这一段 */
    private long getElapsedMs() {
        if (running) {
            return accumulatedMs + (SystemClock.elapsedRealtime() - startElapsedMs);
        }
        return accumulatedMs;
    }

    private void updateTimeDisplay() {
        long elapsed = getElapsedMs();
        long totalSeconds = elapsed / 1000L;
        int minutes = (int) (totalSeconds / 60L);
        int seconds = (int) (totalSeconds % 60L);
        int hundredths = (int) ((elapsed % 1000L) / 10L);
        tvTime.setText(getString(R.string.fourth_time_format, minutes, seconds, hundredths));
    }

    private void updateButtonState() {
        btnStart.setEnabled(!running);
        btnPause.setEnabled(running);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 保存当前总时长；若正在运行，同时记录本段起点，重建后可继续累加
        outState.putLong(STATE_ACCUMULATED, getElapsedMs());
        outState.putBoolean(STATE_RUNNING, running);
        outState.putLong(STATE_START_ELAPSED, running ? SystemClock.elapsedRealtime() : 0L);
    }
}
