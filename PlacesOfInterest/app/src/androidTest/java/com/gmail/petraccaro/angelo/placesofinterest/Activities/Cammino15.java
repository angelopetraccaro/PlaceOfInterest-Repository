package com.gmail.petraccaro.angelo.placesofinterest.Activities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import com.gmail.petraccaro.angelo.placesofinterest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
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
public class Cammino15 {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);
    private String name = "testName";
    private String surname = "testSurname";
    private String email = "test@gmail.com";
    private String password = "secret";
    private String username = "testUsername";

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
    public void scattaFotoEPubblica() throws InterruptedException {
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
        ViewInteraction textEmail = onView(withId(R.id.email1)).perform(typeText(email),ViewActions.closeSoftKeyboard());
        ViewInteraction textPassword = onView(withId(R.id.password1)).perform(typeText(password), ViewActions.closeSoftKeyboard());
        ViewInteraction textUsername = onView(withId(R.id.username)).perform(typeText(username),ViewActions.closeSoftKeyboard());

        onView(withId(R.id.galleria)).perform(click());



        ViewInteraction button = onView(
                allOf(withId(R.id.btnRegistrati), withText("REGISTRATI"),
                        withParent(allOf(withId(R.id.register),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        button.check(matches(isDisplayed()));
        button.perform(click());


        EspressoTestUtils.waitFor(12000);
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
        EspressoTestUtils.waitFor(9000);

        ViewInteraction textView2 = onView(
                allOf(withText("PlacesOfInterest"),
                        withParent(allOf(withId(R.id.toolbar),
                                isDisplayed()))));
        textView2.check(matches(withText("PlacesOfInterest")));
    }


    @After
    public  void deleteUser(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        EspressoTestUtils.waitFor(300);
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            FirebaseFirestore.getInstance().collection("users").document("test@gmail.com").delete();

                            Log.e("eliminazione user","eliminato");
                        }
                    });
        }

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
