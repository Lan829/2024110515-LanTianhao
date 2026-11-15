package com.lantianhao.multifragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.lantianhao.multifragment.controller.MainActivity;
import com.lantianhao.multifragment.data.ObjectStore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 个人信息 Fragment 的 UI 测试：保存后切换 Fragment 再返回，
 * 资料应通过序列化文件恢复。
 */
@RunWith(AndroidJUnit4.class)
public class PersonFragmentTest {

    @Before
    public void clearSavedState() {
        Context context = ApplicationProvider.getApplicationContext();
        context.deleteFile(ObjectStore.PLAYER_FILE);
        context.deleteFile(ObjectStore.GAME_TWO_FILE);
        context.deleteFile(ObjectStore.GAME_THREE_FILE);
    }

    @Test
    public void savedProfileIsRestoredAfterSwitchingFragments() {
        try (ActivityScenario<MainActivity> ignored =
                     ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.input_name)).perform(replaceText("张三"));
            onView(withId(R.id.input_phone)).perform(replaceText("13912345678"));
            onView(withId(R.id.button_save)).perform(click());

            onView(withId(R.id.nav_two)).perform(click());
            onView(withId(R.id.nav_person)).perform(click());

            onView(withId(R.id.input_name)).check(matches(withText("张三")));
            onView(withId(R.id.input_phone)).check(matches(withText("13912345678")));
        }
    }
}
