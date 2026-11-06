package com.lantianhao.recyclercardgame;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.lantianhao.recyclercardgame.controller.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 主界面的 UI 测试。
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> rule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void showsPlayerInformation() {
        onView(withId(R.id.value_name)).check(matches(withText(R.string.default_player_name)));
        onView(withId(R.id.value_gender)).check(matches(withText(R.string.default_player_gender_male)));
        onView(withId(R.id.value_student_id)).check(matches(withText(R.string.default_student_id)));
        onView(withId(R.id.value_phone)).check(matches(withText(R.string.default_phone)));
        onView(withId(R.id.value_email)).check(matches(withText(R.string.default_email)));
        onView(withId(R.id.value_hobbies)).check(matches(withText(R.string.default_hobbies)));
        onView(withId(R.id.value_high_score)).check(matches(isDisplayed()));
    }

    @Test
    public void editButtonOpensPersonInfoActivity() {
        onView(withId(R.id.button_edit_info)).perform(click());
        onView(withId(R.id.input_name)).check(matches(isDisplayed()));
        onView(withId(R.id.button_save)).check(matches(isDisplayed()));
    }

    @Test
    public void editedNameIsShownAfterSaving() {
        onView(withId(R.id.button_edit_info)).perform(click());
        onView(withId(R.id.input_name)).perform(replaceText("张三"));
        onView(withId(R.id.input_phone)).perform(replaceText("13912345678"));
        onView(withId(R.id.button_save)).perform(click());

        onView(withId(R.id.value_name)).check(matches(withText("张三")));
        onView(withId(R.id.value_phone)).check(matches(withText("13912345678")));
    }

    @Test
    public void cancellingEditKeepsOriginalInformation() {
        onView(withId(R.id.button_edit_info)).perform(click());
        onView(withId(R.id.input_name)).perform(replaceText("李四"));
        onView(withId(R.id.button_cancel)).perform(click());

        onView(withId(R.id.value_name)).check(matches(withText(R.string.default_player_name)));
    }

    @Test
    public void playGameButtonOpensCardGameActivity() {
        onView(withId(R.id.button_play_game)).perform(click());
        onView(withId(R.id.card_list)).check(matches(isDisplayed()));
        onView(withId(R.id.score_value)).check(matches(isDisplayed()));
    }

    @Test
    public void aboutMenuShowsDialog() {
        openActionBarOverflowOrOptionsMenu(
                InstrumentationRegistry.getInstrumentation().getTargetContext());
        onView(withText(R.string.action_about)).perform(click());
        onView(withText(R.string.about_title)).check(matches(isDisplayed()));
    }
}
