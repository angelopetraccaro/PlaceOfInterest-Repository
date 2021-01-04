package com.gmail.petraccaro.angelo.placesofinterest.Activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.gmail.petraccaro.angelo.placesofinterest.Models.Post;
import com.gmail.petraccaro.angelo.placesofinterest.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.longClick;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;


@LargeTest
@RunWith(AndroidJUnit4.class)

public class Cammino5 {

    private static final String ARG_SECTION_NUMBER = "";
    @Rule
    public ActivityTestRule<LoginActivity> LoginActivityActivityTestRule = new ActivityTestRule<>(LoginActivity.class);
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    @Before
    public void CreatePost(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseStorage myStorage = FirebaseStorage.getInstance();
        StorageReference rootStorageRef = myStorage.getReference();
        StorageReference documentRef = rootStorageRef.child("images");
        final DatabaseReference myRefToDb = database.getReference("photos");

        String uploadId = myRefToDb.push().getKey();
        Post el = new Post("test", "test",
                Double.toString(120), Double.toString(120),
                "https://firebasestorage.googleapis.com/v0/b/placesofinterest-2bc8d.appspot.com/o/images%2F1325357829?alt=media&token=fd52c26c-cec7-45f5-8d13-bb1bde6e16ed",
                "test", (currentUser != null) ? currentUser.getUid() : null, uploadId, "francescosax");
        myRefToDb.child(uploadId).setValue(el);
        EspressoTestUtils.waitFor(700);
    }
    @Test
    public void cammino5() {
        ViewInteraction appCompatAutoCompleteTextView = onView(
                allOf(withId(R.id.email),
                        childAtPosition(
                                allOf(withId(R.id.layout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                2),
                        isDisplayed()));
        appCompatAutoCompleteTextView.perform(replaceText("francescosax@gmail.com"), closeSoftKeyboard());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.password),
                        childAtPosition(
                                allOf(withId(R.id.layout2),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                3),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("123456"), closeSoftKeyboard());

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


        mainActivityActivityTestRule.launchActivity(getActivityIntent());
       // ActivityScenario main = ActivityScenario.launch(getActivityIntent());
        EspressoTestUtils.waitFor(1500);

        ViewInteraction textView = onView(
                allOf(withText("PlacesOfInterest"),
                        isDisplayed()));
        textView.check(matches(withText("PlacesOfInterest")));

        //Matcher<View> matcher = allOf(isDescendantOfA(withId(R.id.PostList1)));
       // onView(withId(R.id.PostList1)).perform(RecyclerViewActions.actionOnItemAtPosition(2, click()));
       // onView(withText("Angelo_Petraccaro")).perform(ViewActions.click());
        //ViewInteraction view = onView(ViewMatchers.withId(R.id.PostList1));
        //view.check(matches(isDisplayed()));

       onData(anything()).inAdapterView(withId(R.id.PostList1)).atPosition(6).perform(click());
        EspressoTestUtils.waitFor(500);

        /*EspressoTestUtils.waitFor(100);
        onView(withText("Delete?"))
                .check(matches(isDisplayed()))
                .perform(click());*/
    }

    protected Intent getActivityIntent() {
        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent i = new Intent(targetContext, MainActivity.class);
        i.putExtra("nome","Francesco");
        i.putExtra("cognome","Rossi");
        i.putExtra("username","francescosax");
        i.putExtra("password","123456");
        i.putExtra("email","francescosax@gmail.com");
        i.putExtra("uriFotoDelProfilo","https://firebasestorage.googleapis.com/v0/b/placesofinterest-2bc8d.appspot.com/o/ProfileImages%2FBill_Gates_2018.jpg?alt=media&token=5213cce9-106e-4ecf-a99f-a585b322064b");
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
