package com.gmail.petraccaro.angelo.placesofinterest.Activities;


import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
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
import org.junit.AfterClass;
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
import static com.gmail.petraccaro.angelo.placesofinterest.Activities.Cammino5.childAtPosition;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class Cammino12 {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void cammino12() {
        ViewInteraction textView = onView(
                allOf(withId(R.id.login), withText("Login"),
                        withParent(allOf(withId(R.id.layout2),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        textView.check(matches(withText("Login")));

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

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.nome),
                        childAtPosition(
                                allOf(withId(R.id.register),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("test"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.cognome),
                        childAtPosition(
                                allOf(withId(R.id.register),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("test"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.email1),
                        childAtPosition(
                                allOf(withId(R.id.register),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                3),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("test2@gmail.com"), closeSoftKeyboard());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.password1),
                        childAtPosition(
                                allOf(withId(R.id.register),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                4),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText("123456"), closeSoftKeyboard());

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.username),
                        childAtPosition(
                                allOf(withId(R.id.register),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                5),
                        isDisplayed()));
        appCompatEditText5.perform(replaceText("testtest"), closeSoftKeyboard());

        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.Scattafoto),
                        childAtPosition(
                                allOf(withId(R.id.register),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                9),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        EspressoTestUtils.waitFor(900);


        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btnRegistrati), withText("Registrati"),
                        isDisplayed()));
        appCompatButton.perform(click());


        EspressoTestUtils.waitFor(9500);

        ViewInteraction textView6 = onView(
                allOf(withText("PlacesOfInterest"),
                        isDisplayed()));
        textView6.check(matches(withText("PlacesOfInterest")));

        onData(anything()).inAdapterView(withId(R.id.PostList1)).atPosition(1).perform(click());

        ViewInteraction textView12 = onView(
                allOf(withId(R.id.nome), withText("Nome"),
                        withParent(allOf(withId(R.id.item),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        textView12.check(matches(withText("Nome")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.breve_desc2), withText("Breve descrizione"),
                        withParent(allOf(withId(R.id.item),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        textView2.check(matches(withText("Breve descrizione")));
        EspressoTestUtils.waitFor(2000);

        pressBack();
        EspressoTestUtils.waitFor(3000);
        ViewInteraction textView8 = onView(
                allOf(withText("PlacesOfInterest"),
                        isDisplayed()));
        textView8.check(matches(withText("PlacesOfInterest")));


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
                            FirebaseFirestore.getInstance().collection("users").document("test2@gmail.com").delete();

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
