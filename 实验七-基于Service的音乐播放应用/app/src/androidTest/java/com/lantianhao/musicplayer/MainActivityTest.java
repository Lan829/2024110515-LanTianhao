package com.lantianhao.musicplayer;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.lantianhao.musicplayer.controller.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 音乐播放界面的 UI 测试。
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> rule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void showsPlayerControls() {
        onView(withId(R.id.current_title)).check(matches(isDisplayed()));
        onView(withId(R.id.current_artist)).check(matches(isDisplayed()));
        onView(withId(R.id.seek_bar)).check(matches(isDisplayed()));
        onView(withId(R.id.button_play_pause)).check(matches(isDisplayed()));
        onView(withId(R.id.song_list)).check(matches(isDisplayed()));
    }

    @Test
    public void playButtonClickable() {
        onView(withId(R.id.button_play_pause)).perform(click());
    }

    @Test
    public void aboutMenuShowsDialog() {
        openActionBarOverflowOrOptionsMenu(
                InstrumentationRegistry.getInstrumentation().getTargetContext());
        onView(withText(R.string.action_about)).perform(click());
        onView(withText(R.string.about_title)).check(matches(isDisplayed()));
    }
}
