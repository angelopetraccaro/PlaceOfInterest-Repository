package com.gmail.petraccaro.angelo.placesofinterest.Activities;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.gmail.petraccaro.angelo.placesofinterest.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class Cammino9 {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void loginActivityTest() throws InterruptedException {
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

            EspressoTestUtils.waitFor(1000);
            ViewInteraction textView = onView(
                    allOf(withText("PlacesOfInterest"),
                            withParent(allOf(withId(R.id.toolbar),
                            isDisplayed()))));
            textView.check(matches(withText("PlacesOfInterest")));


            EspressoTestUtils.waitFor(800);
            onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());

            ViewInteraction navigationMenuItemView = onView(
                    allOf(childAtPosition(
                            allOf(withId(R.id.design_navigation_view),
                                    childAtPosition(
                                            withId(R.id.nav_view),
                                            0)),
                            6),
                            isDisplayed()));
            navigationMenuItemView.perform(click());

            //lanciare intent da main a detector. intent da detector a convertitore e da detector a riconoscitore
            //dato che la testiamo singolarmente con test di unità, evitiamo di replicare il test.
            EspressoTestUtils.waitFor(400);

            pressBack();
            ViewInteraction textView5 = onView(
                allOf(withText("PlacesOfInterest"),
                        withParent(allOf(withId(R.id.toolbar),
                        isDisplayed()))));
            textView5.check(matches(withText("PlacesOfInterest")));

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
