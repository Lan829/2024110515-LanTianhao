package com.lantianhao.cardgame;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.lantianhao.cardgame.controller.CardGameActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 翻牌游戏界面的 UI 测试：4×4 牌面、选牌扣分、重置游戏。
 */
@RunWith(AndroidJUnit4.class)
public class CardGameActivityTest {

    @Rule
    public ActivityScenarioRule<CardGameActivity> rule =
            new ActivityScenarioRule<>(CardGameActivity.class);

    private static Matcher<View> withCardIndex(final int index) {
        return new BoundedMatcher<View, View>(View.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("card with tag index " + index);
            }

            @Override
            protected boolean matchesSafely(View view) {
                return view.getTag() instanceof Integer && (Integer) view.getTag() == index;
            }
        };
    }

    private static String scoreText(int score) {
        return ApplicationProvider.getApplicationContext()
                .getString(R.string.score_format, score);
    }

    @Test
    public void boardShowsSixteenCards() {
        onView(withId(R.id.card_grid)).check(matches(hasChildCount(16)));
    }

    @Test
    public void clickingCardFlipsItAndCostsOnePoint() {
        onView(withCardIndex(0)).perform(click());
        onView(withId(R.id.score_value)).check(matches(withText(scoreText(-1))));
        onView(withCardIndex(0)).check(matches(withText("-1")));
    }

    @Test
    public void clickingSameCardTwiceUnchoosesWithoutExtraCost() {
        onView(withCardIndex(0)).perform(click());
        onView(withCardIndex(0)).perform(click());
        onView(withId(R.id.score_value)).check(matches(withText(scoreText(-1))));
    }

    @Test
    public void resetStartsANewGame() {
        onView(withCardIndex(0)).perform(click());
        onView(withCardIndex(1)).perform(click());
        onView(withId(R.id.button_reset)).perform(click());

        onView(withId(R.id.score_value)).check(matches(withText(scoreText(0))));
        onView(withId(R.id.card_grid)).check(matches(hasChildCount(16)));
    }
}
