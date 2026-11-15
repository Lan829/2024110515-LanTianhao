package com.lantianhao.multifragment;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.PerformException;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.matcher.BoundedMatcher;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.lantianhao.multifragment.controller.MainActivity;
import com.lantianhao.multifragment.data.ObjectStore;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 翻牌游戏 Fragment 的 UI 测试：RecyclerView 牌数、点击扣分与重置。
 */
@RunWith(AndroidJUnit4.class)
public class GameFragmentTest {

    @Before
    public void clearSavedState() {
        Context context = ApplicationProvider.getApplicationContext();
        context.deleteFile(ObjectStore.PLAYER_FILE);
        context.deleteFile(ObjectStore.GAME_TWO_FILE);
        context.deleteFile(ObjectStore.GAME_THREE_FILE);
    }

    private static Matcher<View> withAdapterItemCount(final int... counts) {
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("RecyclerView with expected adapter item count");
            }

            @Override
            protected boolean matchesSafely(RecyclerView view) {
                if (view.getAdapter() == null) {
                    return false;
                }
                int count = view.getAdapter().getItemCount();
                for (int expected : counts) {
                    if (count == expected) {
                        return true;
                    }
                }
                return false;
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
    public void twoCardFragmentShowsCardsAndCostsOnePoint() {
        try (ActivityScenario<MainActivity> ignored =
                     ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.nav_two)).perform(click());
            onView(withId(R.id.card_list)).check(matches(withAdapterItemCount(24, 32)));
            onView(withId(R.id.card_list)).perform(clickRecyclerItem(0));
            onView(withId(R.id.score_value)).check(matches(withText(scoreText(-1))));
        }
    }

    @Test
    public void threeCardFragmentShowsCardsAndCostsOnePoint() {
        try (ActivityScenario<MainActivity> ignored =
                     ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.nav_three)).perform(click());
            onView(withId(R.id.card_list)).check(matches(withAdapterItemCount(24, 32)));
            onView(withId(R.id.card_list)).perform(clickRecyclerItem(0));
            onView(withId(R.id.score_value)).check(matches(withText(scoreText(-1))));
        }
    }

    @Test
    public void resetStartsANewGame() {
        try (ActivityScenario<MainActivity> ignored =
                     ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.nav_two)).perform(click());
            onView(withId(R.id.card_list)).perform(clickRecyclerItem(0));
            onView(withId(R.id.button_reset)).perform(click());

            onView(withId(R.id.score_value)).check(matches(withText(scoreText(0))));
            onView(withId(R.id.card_list)).check(matches(withAdapterItemCount(24, 32)));
        }
    }
}
