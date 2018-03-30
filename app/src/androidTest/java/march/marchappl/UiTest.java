package march.marchappl;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.MediumTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


/**
 * Created by Alexei on 3/30/2018.
 */
@RunWith(AndroidJUnit4.class)
@MediumTest
public class UiTest {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testLogin() throws InterruptedException {
        try {
            ViewInteraction nextButton = onView(withId(R.id.toolbar_bt_next)).check(matches(isDisplayed()));
            nextButton.perform(click());

            if (doesViewExist(R.id.results_srl)) {
                pressBack();
            }

            onView(ViewMatchers.withId(R.id.rv_matches))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

            if (doesViewExist(R.id.add_dialog_tv_score1_plus)) {
                pressBack();
            }

            onView(ViewMatchers.withId(R.id.rv_matches))
                    .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

            onView(withId(R.id.add_dialog_tv_score1_plus)).perform(click()).check(matches(isDisplayed()));

            onView(withId(R.id.add_dialog_tv_score2_plus)).perform(click()).check(matches(isDisplayed()));

            onView(withId(R.id.add_dialog_bt_add)).perform(click());

            Thread.sleep(1000);

            ViewInteraction nextButton2 = onView(withId(R.id.toolbar_bt_next)).perform(click());
            nextButton2.check(matches(isDisplayed()));

            Thread.sleep(1000);

        } catch (NoMatchingViewException e) {
            e.printStackTrace();
        }
    }


    public boolean doesViewExist(int id) {
        try {
            onView(withId(id)).check(matches(isDisplayed()));
            return true;
        } catch (NoMatchingViewException e) {
            return false;
        }
    }

}
