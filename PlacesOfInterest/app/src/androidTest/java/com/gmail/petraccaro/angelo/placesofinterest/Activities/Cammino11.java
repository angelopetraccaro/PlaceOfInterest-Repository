package com.gmail.petraccaro.angelo.placesofinterest.Activities;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import com.gmail.petraccaro.angelo.placesofinterest.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class Cammino11 {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.WRITE_EXTERNAL_STORAGE");
    @Rule
    public GrantPermissionRule m1GrantPermissionRule =
            GrantPermissionRule.grant(
                    "com.google.android.providers.gsf.permission.READ_GSERVICES");
    @Rule
    public GrantPermissionRule m2GrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION");
    @Rule
    public GrantPermissionRule m3GrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_COARSE_LOCATION");


    @Test
    public void scattaFotoCampoMancante() throws InterruptedException {
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
        ActivityScenario.launch(getActivityIntent());

        EspressoTestUtils.waitFor(3000);
        ViewInteraction textView = onView(
                allOf(withText("PlacesOfInterest"),
                        withParent(allOf(withId(R.id.toolbar),
                                isDisplayed()))));
        textView.check(matches(withText("PlacesOfInterest")));


        ViewInteraction imageButton = onView(
                allOf(withId(R.id.fab),
                        withParent(allOf(withId(R.id.main_content),
                                withParent(withId(R.id.drawer_layout)))),
                        isDisplayed()));
        imageButton.perform(click());
        ActivityScenario.launch(getActivityIntent1());

        EspressoTestUtils.waitFor(1000);

        ViewInteraction textView5= onView(
                allOf(withText("Nome"), isDisplayed()));
        textView5.check(matches(withText("Nome")));

        ViewInteraction textView6= onView(
                allOf(withText("Breve descrizione"), isDisplayed()));
        textView6.check(matches(withText("Breve descrizione")));

        onView(withId(R.id.take)).perform(click());
        ViewInteraction didascalia = onView(withId(R.id.Didascalia)).perform(typeText(""), ViewActions.closeSoftKeyboard());
        ViewInteraction nomeFoto = onView(withId(R.id.NomeText)).perform(typeText("Benevento"), ViewActions.closeSoftKeyboard());
        ViewInteraction  bdesc = onView(withId(R.id.b_desc)).perform(typeText("Sede Unisannio"),ViewActions.closeSoftKeyboard());

        onView(withId(R.id.imageButton)).perform(click());
        onView(withId(R.id.Didascalia)).check(matches(hasErrorText("Attenzione, aggiungi una didascalia!")));
        EspressoTestUtils.waitFor(3000);
    }

    @Test
    public void scegliFotoCampoMancante() throws InterruptedException {
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
        ActivityScenario.launch(getActivityIntent());

        EspressoTestUtils.waitFor(4000);
        ViewInteraction textView = onView(
                allOf(withText("PlacesOfInterest"),
                        withParent(allOf(withId(R.id.toolbar),
                                isDisplayed()))));
        textView.check(matches(withText("PlacesOfInterest")));


        ViewInteraction imageButton = onView(
                allOf(withId(R.id.fab),
                        withParent(allOf(withId(R.id.main_content),
                                withParent(withId(R.id.drawer_layout)))),
                        isDisplayed()));
        imageButton.perform(click());
        ActivityScenario.launch(getActivityIntent1());

        EspressoTestUtils.waitFor(1500);

        ViewInteraction textView5= onView(
                allOf(withText("Nome"), isDisplayed()));
        textView5.check(matches(withText("Nome")));

        ViewInteraction textView6= onView(
                allOf(withText("Breve descrizione"), isDisplayed()));
        textView6.check(matches(withText("Breve descrizione")));

        onView(withId(R.id.apri)).perform(click());
        ViewInteraction didascalia = onView(withId(R.id.Didascalia)).perform(typeText("Didascalia di test"), ViewActions.closeSoftKeyboard());
        ViewInteraction nomeFoto = onView(withId(R.id.NomeText)).perform(typeText("Benevento"), ViewActions.closeSoftKeyboard());
        ViewInteraction  bdesc = onView(withId(R.id.b_desc)).perform(typeText(""),ViewActions.closeSoftKeyboard());

        onView(withId(R.id.imageButton)).perform(click());
        onView(withId(R.id.b_desc)).check(matches(hasErrorText("Attenzione, aggiungi una descrizione!")));
        EspressoTestUtils.waitFor(3000);
    }

    @Test
    public void scattaFotoEPubblica() throws InterruptedException {
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
        ActivityScenario.launch(getActivityIntent());

        EspressoTestUtils.waitFor(3000);
        ViewInteraction textView = onView(
                allOf(withText("PlacesOfInterest"),
                        withParent(allOf(withId(R.id.toolbar),
                                isDisplayed()))));
        textView.check(matches(withText("PlacesOfInterest")));


        ViewInteraction imageButton = onView(
                allOf(withId(R.id.fab),
                        withParent(allOf(withId(R.id.main_content),
                                withParent(withId(R.id.drawer_layout)))),
                        isDisplayed()));
        imageButton.perform(click());
        ActivityScenario.launch(getActivityIntent1());

        EspressoTestUtils.waitFor(1000);

        ViewInteraction textView5= onView(
                allOf(withText("Nome"), isDisplayed()));
        textView5.check(matches(withText("Nome")));

        ViewInteraction textView6= onView(
                allOf(withText("Breve descrizione"), isDisplayed()));
        textView6.check(matches(withText("Breve descrizione")));

        onView(withId(R.id.take)).perform(click());
        ViewInteraction didascalia = onView(withId(R.id.Didascalia)).perform(typeText("Didascalia di test"), ViewActions.closeSoftKeyboard());
        ViewInteraction nomeFoto = onView(withId(R.id.NomeText)).perform(typeText("Benevento"), ViewActions.closeSoftKeyboard());
        ViewInteraction  bdesc = onView(withId(R.id.b_desc)).perform(typeText("Sede Unisannio"),ViewActions.closeSoftKeyboard());

        onView(withId(R.id.imageButton)).perform(click());
        EspressoTestUtils.waitFor(4000);

        ActivityScenario.launch(getActivityIntent());
        EspressoTestUtils.waitFor(800);
        ViewInteraction textView2 = onView(
                allOf(withText("PlacesOfInterest"),
                        withParent(allOf(withId(R.id.toolbar),
                                isDisplayed()))));
        textView2.check(matches(withText("PlacesOfInterest")));
        EspressoTestUtils.waitFor(3000);
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

    protected Intent getActivityIntent1() {
        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent i = new Intent(targetContext, CreateActivity.class);
        i.putExtra("username","Angelo_Petraccaro");
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
