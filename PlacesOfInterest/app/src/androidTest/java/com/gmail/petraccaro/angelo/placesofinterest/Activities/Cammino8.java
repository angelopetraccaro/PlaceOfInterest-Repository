package com.gmail.petraccaro.angelo.placesofinterest.Activities;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.gmail.petraccaro.angelo.placesofinterest.Models.Post;
import com.gmail.petraccaro.angelo.placesofinterest.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class Cammino8 {
    private ArrayList<Post> PublicList = new ArrayList<Post>();
    private DatabaseReference myRef;
    private FirebaseDatabase db;

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void cammino8() {
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

        /*ActivityScenario.launch(getActivityIntent());
        ViewInteraction textView = onView(
                allOf(withText("PlacesOfInterest"),
                        withParent(allOf(withId(R.id.toolbar),
                                withParent(withId(R.id.appbar)))),
                        isDisplayed()));
        textView.check(matches(withText("PlacesOfInterest")));*/
        onView(withText("PlacesOfInterest")).inRoot(withDecorView(
                not(mActivityTestRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
        EspressoTestUtils.waitFor(3000);

        DataInteraction constraintLayout = onData(anything())
                .inAdapterView(allOf(withId(R.id.lista),
                        childAtPosition(
                                withId(R.id.constraintLayout),
                                0)))
                .atPosition(0);
        constraintLayout.perform(click());

       /* ActivityScenario.launch(getActivityIntent1());
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.didascalia), withText("Didascalia"),
                        withParent(allOf(withId(R.id.item),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        textView2.check(matches(withText("Didascalia")));

        ViewInteraction imageButton = onView(
                allOf(withId(R.id.gps),
                        withParent(allOf(withId(R.id.item),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        imageButton.check(matches(isDisplayed()));

        pressBack();

        ViewInteraction textView3 = onView(
                allOf(withText("PlacesOfInterest"),
                        withParent(allOf(withId(R.id.toolbar),
                                withParent(withId(R.id.appbar)))),
                        isDisplayed()));
        textView3.check(matches(withText("PlacesOfInterest")));*/
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
        PublicList.clear();
        db = FirebaseDatabase.getInstance();
        myRef  = db.getReference("photos");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PublicList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Post els = ds.getValue(Post.class);
                    if (els.getAvailable() == true)
                        PublicList.add(els);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //
            }
        });

        Context targetContext = InstrumentationRegistry.getInstrumentation()
                .getTargetContext();
        Intent i = new Intent(targetContext, CreateActivity.class);
        i.putExtra("name",PublicList.get(0).getNome());
        i.putExtra("b_desc",PublicList.get(0).getBreve_descrizione());
        i.putExtra("latitude",PublicList.get(0).getLatitude());
        i.putExtra("longitude",PublicList.get(0).getLongitude());
        i.putExtra("foto",PublicList.get(0).getUrl_foto());
        i.putExtra("didascalia",PublicList.get(0).getDidascalia());
        i.putExtra("owner",PublicList.get(0).getOwner());
        i.putExtra("keyondb",PublicList.get(0).getKeyOnDb());

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
