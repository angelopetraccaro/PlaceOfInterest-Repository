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
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
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
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class Cammino7 {

    //da settare foto in circleimageview

    private String name = "testName";
    private String surname = "testSurname";
    private String email = "test@gmail.com";
    private String password = "lasaii";
    private String passwordShort = "lasai";
    private String username = "testUsername";

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
    public void fallimentoRegistrazioneUtenteTestGalleria() {
        //nome non presente

            ViewInteraction appCompatTextView = onView(
                    allOf(withId(R.id.txt_no_account), withText("Non hai un account? Registrati"),
                            childAtPosition(
                                    allOf(withId(R.id.layout2),
                                            childAtPosition(
                                                    withId(android.R.id.content),
                                                    0)),
                                    5),
                            isDisplayed()));
            appCompatTextView.perform(click());



        ViewInteraction textName = onView(withId(R.id.nome)).perform(typeText(name), ViewActions.closeSoftKeyboard());
        ViewInteraction textSurname = onView(withId(R.id.cognome)).perform(typeText(surname), ViewActions.closeSoftKeyboard());
        ViewInteraction textEmail = onView(withId(R.id.email1)).perform(typeText("test@gmail.com"),ViewActions.closeSoftKeyboard());
        ViewInteraction textPassword = onView(withId(R.id.password1)).perform(typeText(password), ViewActions.closeSoftKeyboard());
        ViewInteraction textUsername = onView(withId(R.id.username)).perform(typeText(username),ViewActions.closeSoftKeyboard());


        ViewInteraction button = onView(
                allOf(withId(R.id.btnRegistrati), withText("REGISTRATI"),
                        withParent(allOf(withId(R.id.register),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        button.check(matches(isDisplayed()));
        button.perform(click());
        ActivityScenario.launch(getActivityIntent());


        EspressoTestUtils.waitFor(700);
        ViewInteraction textView2 = onView(
                allOf(withText("PlacesOfInterest"),
                        withParent(allOf(withId(R.id.toolbar),
                        isDisplayed()))));
        textView2.check(matches(withText("PlacesOfInterest")));


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

    private Intent getActivityIntent() {
        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent i = new Intent(targetContext, MainActivity.class);
        i.putExtra("nome",name);
        i.putExtra("cognome",surname);
        i.putExtra("username",username);
        i.putExtra("password",password);
        i.putExtra("email",email);
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