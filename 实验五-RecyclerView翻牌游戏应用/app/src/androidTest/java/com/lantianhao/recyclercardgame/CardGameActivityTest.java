package com.lantianhao.recyclercardgame;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.lantianhao.recyclercardgame.controller.CardGameActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 翻牌游戏界面的 UI 测试：RecyclerView 牌数、点击扣分、重置游戏。
 *
 * <p>使用自定义 ViewAction 点击 RecyclerView 的指定位置，避免额外依赖 espresso-contrib。</p>
 */
@RunWith(AndroidJUnit4.class)
public class CardGameActivityTest {

    @Rule
    public ActivityScenarioRule<CardGameActivity> rule =
            new ActivityScenarioRule<>(CardGameActivity.class);

    private static Matcher<View> withAdapterItemCount(final int count) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("RecyclerView with " + count + " adapter items");
            }

            @Override
            protected boolean matchesSafely(RecyclerView view) {
                return view.getAdapter() != null
                        && view.getAdapter().getItemCount() == count;
            }
        };
    }

    private static ViewAction clickRecyclerItem(final int position) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(RecyclerView.class);
            }

            @Override
            public String getDescription() {
                return "click RecyclerView item at " + position;
            }

            @Override
            public void perform(UiController uiController, View view) {
                RecyclerView recyclerView = (RecyclerView) view;
                recyclerView.scrollToPosition(position);
                uiController.loopMainThreadUntilIdle();
                RecyclerView.ViewHolder holder =
                        recyclerView.findViewHolderForAdapterPosition(position);
                if (holder == null) {
                    throw new PerformException.Builder()
                            .withActionDescription(getDescription())
                            .withViewDescription("RecyclerView")
                            .build();
                }
                holder.itemView.performClick();
                uiController.loopMainThreadUntilIdle();
            }
        };
    }

    private static String scoreText(int score) {
        return ApplicationProvider.getApplicationContext()
                .getString(R.string.score_format, score);
    }

    @Test
    public void boardShowsTwentyFourCardsInPortrait() {
        onView(withId(R.id.card_list)).check(matches(withAdapterItemCount(24)));
    }

    @Test
    public void clickingCardFlipsItAndCostsOnePoint() {
        onView(withId(R.id.card_list)).perform(clickRecyclerItem(0));
        onView(withId(R.id.score_value)).check(matches(withText(scoreText(-1))));
    }

    @Test
    public void resetStartsANewGame() {
        onView(withId(R.id.card_list)).perform(clickRecyclerItem(0));
        onView(withId(R.id.card_list)).perform(clickRecyclerItem(1));
        onView(withId(R.id.button_reset)).perform(click());

        onView(withId(R.id.score_value)).check(matches(withText(scoreText(0))));
        onView(withId(R.id.card_list)).check(matches(withAdapterItemCount(24)));
    }
}
