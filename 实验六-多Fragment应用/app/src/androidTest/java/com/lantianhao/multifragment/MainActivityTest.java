package com.lantianhao.multifragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.lantianhao.multifragment.controller.MainActivity;
import com.lantianhao.multifragment.data.ObjectStore;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 主 Activity 的 UI 测试：默认显示个人信息 Fragment，底部按钮切换 2 张牌 /
 * 3 张牌 Fragment，菜单弹出关于对话框。
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Before
    public void clearSavedState() {
        Context context = ApplicationProvider.getApplicationContext();
        context.deleteFile(ObjectStore.PLAYER_FILE);
        context.deleteFile(ObjectStore.GAME_TWO_FILE);
        context.deleteFile(ObjectStore.GAME_THREE_FILE);
    }

    @Test
    public void showsPersonFragmentByDefault() {
        try (ActivityScenario<MainActivity> ignored =
                     ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.input_name)).check(matches(isDisplayed()));
            onView(withId(R.id.button_save)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void navTwoShowsTwoCardGame() {
        try (ActivityScenario<MainActivity> ignored =
                     ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.nav_two)).perform(click());
            onView(withId(R.id.game_title))
                    .check(matches(withText(R.string.title_two_game)));
            onView(withId(R.id.card_list)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void navThreeShowsThreeCardGame() {
        try (ActivityScenario<MainActivity> ignored =
                     ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.nav_three)).perform(click());
            onView(withId(R.id.game_title))
                    .check(matches(withText(R.string.title_three_game)));
            onView(withId(R.id.card_list)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void aboutMenuShowsDialog() {
        try (ActivityScenario<MainActivity> ignored =
                     ActivityScenario.launch(MainActivity.class)) {
            openActionBarOverflowOrOptionsMenu(
                    InstrumentationRegistry.getInstrumentation().getTargetContext());
            onView(withText(R.string.action_about)).perform(click());
            onView(withText(R.string.about_title)).check(matches(isDisplayed()));
        }
    }
}
