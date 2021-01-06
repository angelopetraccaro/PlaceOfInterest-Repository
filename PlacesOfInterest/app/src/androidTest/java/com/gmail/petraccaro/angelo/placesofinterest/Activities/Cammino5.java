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
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
@LargeTest
@RunWith(AndroidJUnit4.class)

public class Cammino5 {

    private static final String ARG_SECTION_NUMBER = "";
    @Rule
    public ActivityTestRule<LoginActivity> LoginActivityActivityTestRule = new ActivityTestRule<>(LoginActivity.class);
    public ActivityTestRule<MainActivity> mainActivityActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseStorage myStorage = FirebaseStorage.getInstance();
    StorageReference rootStorageRef = myStorage.getReference();
    StorageReference documentRef = rootStorageRef.child("images");
    final DatabaseReference myRefToDb = database.getReference("photos");
    private String uploadId;
    @Before
    public void CreatePost(){


        uploadId = myRefToDb.push().getKey();
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



        EspressoTestUtils.waitFor(9000);


        ViewInteraction textView = onView(
                allOf(withText(  "PlacesOfInterest"),
                        isDisplayed()));
        textView.check(matches(withText("PlacesOfInterest")));

        onData(anything()).inAdapterView(withId(R.id.PostList1)).atPosition(12).perform(longClick());
        EspressoTestUtils.waitFor(500);

        onView(withText("Delete?"))
                .check(matches(isDisplayed()));

        onView(withText(R.string.Ok))
                .check(matches(isDisplayed())).perform(click());

        deletePost();
        EspressoTestUtils.waitFor(1000);

        ViewInteraction textView6 = onView(
                allOf(withText("PlacesOfInterest"),
                        isDisplayed()));
        textView6.check(matches(withText("PlacesOfInterest")));
        EspressoTestUtils.waitFor(300);



    }

    public void deletePost(){
        myRefToDb.child(uploadId).removeValue();
    }




    static Matcher<View> childAtPosition(

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
