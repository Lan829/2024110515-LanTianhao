package com.lantianhao.cardgame;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.lantianhao.cardgame.controller.PersonInfoActivity;
import com.lantianhao.cardgame.model.Player;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 个人信息界面的 UI 测试：修改后点击“保存”，检查回传的数据。
 */
@RunWith(AndroidJUnit4.class)
public class PersonInfoActivityTest {

    private Intent buildIntent(Context context) {
        Intent intent = new Intent(context, PersonInfoActivity.class);
        intent.putExtra(Player.EXTRA_NAME, context.getString(R.string.default_player_name));
        intent.putExtra(Player.EXTRA_GENDER,
                context.getString(R.string.default_player_gender_male));
        intent.putExtra(Player.EXTRA_STUDENT_ID, context.getString(R.string.default_student_id));
        intent.putExtra(Player.EXTRA_PHONE, context.getString(R.string.default_phone));
        intent.putExtra(Player.EXTRA_EMAIL, context.getString(R.string.default_email));
        intent.putExtra(Player.EXTRA_HOBBIES, context.getString(R.string.hobby_reading));
        return intent;
    }

    @Test
    public void savingReturnsEditedProfile() {
        Context context = ApplicationProvider.getApplicationContext();
        try (ActivityScenario<PersonInfoActivity> scenario =
                     ActivityScenario.launch(buildIntent(context))) {
            onView(withId(R.id.input_name)).perform(replaceText("王五"));
            onView(withId(R.id.radio_female)).perform(click());
            onView(withId(R.id.check_reading)).perform(click());
            onView(withId(R.id.check_game)).perform(click());
            onView(withId(R.id.button_save)).perform(click());

            Instrumentation.ActivityResult activityResult = scenario.getResult();
            assertNotNull(activityResult);
            Intent data = activityResult.getResultData();
            assertNotNull(data);
            assertEquals("王五", data.getStringExtra(Player.EXTRA_NAME));
            assertEquals(context.getString(R.string.default_player_gender_female),
                    data.getStringExtra(Player.EXTRA_GENDER));
            assertEquals(context.getString(R.string.hobby_game),
                    data.getStringExtra(Player.EXTRA_HOBBIES));
        }
    }

    @Test
    public void cancelReturnsWithoutResult() {
        Context context = ApplicationProvider.getApplicationContext();
        try (ActivityScenario<PersonInfoActivity> scenario =
                     ActivityScenario.launch(buildIntent(context))) {
            onView(withId(R.id.input_name)).perform(replaceText("赵六"));
            onView(withId(R.id.button_cancel)).perform(click());

            Instrumentation.ActivityResult activityResult = scenario.getResult();
            assertNotNull(activityResult);
            assertEquals(android.app.Activity.RESULT_CANCELED, activityResult.getResultCode());
        }
    }
}
