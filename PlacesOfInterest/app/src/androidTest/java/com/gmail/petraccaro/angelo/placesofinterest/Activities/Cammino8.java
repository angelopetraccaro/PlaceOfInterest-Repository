package com.gmail.petraccaro.angelo.placesofinterest.Activities;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.gmail.petraccaro.angelo.placesofinterest.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
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
import static org.hamcrest.Matchers.anything;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class Cammino8 {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void cammino8io() {
        ViewInteraction appCompatAutoCompleteTextView = onView(
                allOf(withId(R.id.email),
                        childAtPosition(
                                allOf(withId(R.id.layout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatAutoCompleteTextView.perform(replaceText(EspressoTestUtils.TEST_USER_EMAIL), closeSoftKeyboard());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.password),
                        childAtPosition(
                                allOf(withId(R.id.layout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                3),
                        isDisplayed()));
        appCompatEditText.perform(replaceText(EspressoTestUtils.TEST_USER_PASSWORD), closeSoftKeyboard());

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

        EspressoTestUtils.waitFor(3000);
        ViewInteraction textView = onView(
                allOf(withText("PlacesOfInterest"),
                        withParent(allOf(withId(R.id.toolbar),
                                isDisplayed()))));
        textView.check(matches(withText("PlacesOfInterest")));



        onData(anything()).inAdapterView(withId(R.id.PostList1)).atPosition(0).perform(click());

        EspressoTestUtils.waitFor(1000);


        ViewInteraction textView9 = onView(
                allOf(withId(R.id.nome), withText("Nome"),
                        withParent(allOf(withId(R.id.item),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        textView9.check(matches(withText("Nome")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.breve_desc2), withText("Breve descrizione"),
                        withParent(allOf(withId(R.id.item),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        textView2.check(matches(withText("Breve descrizione")));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.didascalia), withText("Didascalia"),
                        withParent(allOf(withId(R.id.item),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        textView3.check(matches(withText("Didascalia")));

        ViewInteraction imageButton = onView(
                allOf(withId(R.id.gps),
                        withParent(allOf(withId(R.id.item),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        imageButton.check(matches(isDisplayed()));

        ViewInteraction imageButton2 = onView(
                allOf(withId(R.id.gps),
                        withParent(allOf(withId(R.id.item),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        imageButton2.check(matches(isDisplayed()));

        pressBack();

        EspressoTestUtils.waitFor(2000);
        ViewInteraction textView15 = onView(
                allOf(withText("PlacesOfInterest"),
                        withParent(allOf(withId(R.id.toolbar),
                                isDisplayed()))));
        textView15.check(matches(withText("PlacesOfInterest")));
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
