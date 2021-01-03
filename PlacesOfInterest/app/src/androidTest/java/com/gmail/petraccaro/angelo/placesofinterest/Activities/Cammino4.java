package com.gmail.petraccaro.angelo.placesofinterest.Activities;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.gmail.petraccaro.angelo.placesofinterest.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class Cammino4 {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void loginActivityTest2() throws InterruptedException {
        ViewInteraction appCompatAutoCompleteTextView = onView(
                allOf(withId(R.id.email),
                        childAtPosition(
                                allOf(withId(R.id.layout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatAutoCompleteTextView.perform(replaceText("petraccaro.angelo@gmail.com"), closeSoftKeyboard());


        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.password),
                        childAtPosition(
                                allOf(withId(R.id.layout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                3),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("asd123"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.email_sign_in_button), withText("Login"),
                        childAtPosition(
                                allOf(withId(R.id.layout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                4),
                        isDisplayed()));
        appCompatButton.perform(click());
        ActivityScenario.launch(getActivityIntent());
        //Thread.sleep(700);
        EspressoTestUtils.waitFor(700);
        ViewInteraction textView = onView(
                allOf(withText("PlacesOfInterest"),
                        withParent(allOf(withId(R.id.toolbar),
                                withParent(withId(R.id.appbar)))),
                        isDisplayed()));
        textView.check(matches(withText("PlacesOfInterest")));
        ViewInteraction textView1 = onView(
                allOf(withText("PUBLIC"),
                        withParent(allOf(withContentDescription("Public"),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class)))),
                        isDisplayed()));
        textView1.check(matches(withText("Public")));

        ViewInteraction textView2 = onView(
                allOf(withText("PRIVATE"),
                        withParent(allOf(withContentDescription("Private"),
                                withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class)))),
                        isDisplayed()));
        textView2.check(matches(withText("Private")));

        EspressoTestUtils.waitFor(800);
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

        ViewInteraction ItemFun = onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.itemfun)).perform(NavigationViewActions.navigateTo(R.id.action_settings));
        EspressoTestUtils.waitFor(800);


        ViewInteraction textView5 = onView(
                allOf(withId(R.id.login), withText("Login"),
                        withParent(allOf(withId(R.id.layout2),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        textView5.check(matches(withText("Login")));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.txt_no_account), withText("Non hai un account? Registrati"),
                        withParent(allOf(withId(R.id.layout2),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        textView4.check(matches(withText("Non hai un account? Registrati")));
    }
    public static ViewAction swipeUp() {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER_LEFT,
                GeneralLocation.CENTER_RIGHT, Press.FINGER);
    }

    protected Intent getActivityIntent() {
        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent i = new Intent(targetContext, MainActivity.class);
        i.putExtra("nome","Angelo");
        i.putExtra("cognome","Petraccaro");
        i.putExtra("username","Angelo_Petraccaro");
        i.putExtra("password","asd123");
        i.putExtra("email","petraccaro.angelo@gmail.com");

        i.putExtra("uriFotoDelProfilo","https://firebasestorage.googleapis.com/v0/b/placesofinterest-2bc8d.appspot.com/o/ProfileImages%2F20ACC158-687A-4411-906E-0333D49FE6E7.jpeg?alt=media&token=5d8e9d45-3db7-47c3-9f5e-e02aa2d55fbd");

        return i;
    }


    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}