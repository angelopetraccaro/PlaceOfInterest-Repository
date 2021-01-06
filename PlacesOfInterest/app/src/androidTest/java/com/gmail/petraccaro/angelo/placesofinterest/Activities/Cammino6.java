package com.gmail.petraccaro.angelo.placesofinterest.Activities;


import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.gmail.petraccaro.angelo.placesofinterest.Models.Post;
import com.gmail.petraccaro.angelo.placesofinterest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class Cammino6 {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference myRefToDb = database.getReference("photos");
    private String uploadId;
    @Test
    public void Cammino6() {
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



        EspressoTestUtils.waitFor(12000);
        CreatePost();
        EspressoTestUtils.waitFor(4000);
        ViewInteraction textView6 = onView(
                allOf(withText("PlacesOfInterest"),
                        isDisplayed()));
        textView6.check(matches(withText("PlacesOfInterest")));

        onData(anything()).inAdapterView(withId(R.id.PostList1)).atPosition(12).perform(longClick());
        EspressoTestUtils.waitFor(500);
        onView(withText("Delete?"))
                .check(matches(isDisplayed()));
        EspressoTestUtils.waitFor(300);

        onView(withText(R.string.Ok))
                .check(matches(isDisplayed())).perform(click());


        EspressoTestUtils.waitFor(300);

        ViewInteraction textView69= onView(
                allOf(withText("PlacesOfInterest"),
                        isDisplayed()));
        textView69.check(matches(withText("PlacesOfInterest")));
        EspressoTestUtils.waitFor(300);

    }

    @After
    public void deleteUser(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        EspressoTestUtils.waitFor(300);
        if (user != null) {
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            myRefToDb.child(uploadId).removeValue();


                            FirebaseFirestore.getInstance().collection("users").document("test2@gmail.com").delete();

                            Log.e("eliminazione user","eliminato");
                        }
                    });
        }

    }

    public void CreatePost(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        uploadId = myRefToDb.push().getKey();
        Post el = new Post("test", "test",
                Double.toString(120), Double.toString(120),
                "https://firebasestorage.googleapis.com/v0/b/placesofinterest-2bc8d.appspot.com/o/images%2F1325357829?alt=media&token=fd52c26c-cec7-45f5-8d13-bb1bde6e16ed",
                "test", (user != null) ? user.getUid() : null, uploadId, "testtest");
        myRefToDb.child(uploadId).setValue(el);

        EspressoTestUtils.waitFor(700);
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
