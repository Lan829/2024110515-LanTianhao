package com.lantianhao.multifragment.controller;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.lantianhao.multifragment.R;
import com.lantianhao.multifragment.fragment.PersonFragment;
import com.lantianhao.multifragment.fragment.ThreeCardGameFragment;
import com.lantianhao.multifragment.fragment.TwoCardGameFragment;

/**
 * 主 Activity：作为 Fragment 容器，底部三个按钮在个人信息、2 张牌游戏、
 * 3 张牌游戏三个 Fragment 之间切换；选项菜单可弹出关于对话框。
 */
public class MainActivity extends AppCompatActivity {

    private static final int TAB_PERSON = 0;
    private static final int TAB_TWO = 1;
    private static final int TAB_THREE = 2;
    private static final String STATE_TAB = "state_tab";

    private int currentTab = TAB_PERSON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.nav_person).setOnClickListener(v -> showTab(TAB_PERSON));
        findViewById(R.id.nav_two).setOnClickListener(v -> showTab(TAB_TWO));
        findViewById(R.id.nav_three).setOnClickListener(v -> showTab(TAB_THREE));

        if (savedInstanceState != null) {
            currentTab = savedInstanceState.getInt(STATE_TAB, TAB_PERSON);
            updateNavState();
        } else {
            showTab(TAB_PERSON);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_TAB, currentTab);
    }

    /**
     * 切换 Fragment；每个 Fragment 的数据都从序列化文件恢复。
     */
    private void showTab(int tab) {
        currentTab = tab;
        Fragment fragment;
        if (tab == TAB_TWO) {
            fragment = new TwoCardGameFragment();
        } else if (tab == TAB_THREE) {
            fragment = new ThreeCardGameFragment();
        } else {
            fragment = new PersonFragment();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        updateNavState();
    }

    private void updateNavState() {
        int active = getColor(R.color.primary);
        int inactive = getColor(R.color.text_secondary);
        ((MaterialButton) findViewById(R.id.nav_person))
                .setTextColor(currentTab == TAB_PERSON ? active : inactive);
        ((MaterialButton) findViewById(R.id.nav_two))
                .setTextColor(currentTab == TAB_TWO ? active : inactive);
        ((MaterialButton) findViewById(R.id.nav_three))
                .setTextColor(currentTab == TAB_THREE ? active : inactive);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_about) {
            showAboutDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAboutDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.about_title)
                .setMessage(getString(R.string.about_message, getString(R.string.app_name)))
                .setPositiveButton(R.string.action_ok, null)
                .show();
    }
}
