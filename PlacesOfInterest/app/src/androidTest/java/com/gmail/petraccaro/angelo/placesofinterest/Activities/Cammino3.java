package com.gmail.petraccaro.angelo.placesofinterest.Activities;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.ViewActions;
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

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class Cammino3 {
    private String name = "testName";
    private String surname = "testSurname";
    private String email = "test@gmail.com";
    private String password = "lasaii";
    private String passwordShort = "lasai";
    private String username = "testUsername";

    // TESTARE FOTOCAMERA E GALLERIA

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void fallimentoRegistrazioneUtenteTestNoName() {
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

        ViewInteraction textView = onView(
                allOf(withId(R.id.registrati), withText("Registrati"),
                        withParent(allOf(withId(R.id.register),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        textView.check(matches(withText("Registrati")));

        ViewInteraction imageView = onView(
                allOf(withId(R.id.circleImageView),
                        withParent(allOf(withId(R.id.register),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));


        ViewInteraction textName = onView(withId(R.id.nome)).perform(typeText(""), ViewActions.closeSoftKeyboard());
        ViewInteraction textSurname = onView(withId(R.id.cognome)).perform(typeText(surname), ViewActions.closeSoftKeyboard());
        ViewInteraction textEmail = onView(withId(R.id.email1)).perform(typeText("test@gmail.com"),ViewActions.closeSoftKeyboard());
        ViewInteraction textPassword = onView(withId(R.id.password1)).perform(typeText(password), ViewActions.closeSoftKeyboard());
        ViewInteraction textUsername = onView(withId(R.id.username)).perform(typeText(username),ViewActions.closeSoftKeyboard());

        onView(withId(R.id.btnRegistrati)).perform(click());
        onView(withId(R.id.nome)).check(matches(hasErrorText("This field is required")));
    }


    @Test
    public void fallimentoRegistrazioneUtenteTestNoSurname() {
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

        ViewInteraction textView = onView(
                allOf(withId(R.id.registrati), withText("Registrati"),
                        withParent(allOf(withId(R.id.register),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        textView.check(matches(withText("Registrati")));
        ViewInteraction textName = onView(withId(R.id.nome)).perform(typeText(name), ViewActions.closeSoftKeyboard());
        ViewInteraction textSurname = onView(withId(R.id.cognome)).perform(typeText(""), ViewActions.closeSoftKeyboard());
        ViewInteraction textEmail = onView(withId(R.id.email1)).perform(typeText("test@gmail.com"),ViewActions.closeSoftKeyboard());
        ViewInteraction textPassword = onView(withId(R.id.password1)).perform(typeText(password), ViewActions.closeSoftKeyboard());
        ViewInteraction textUsername = onView(withId(R.id.username)).perform(typeText(username),ViewActions.closeSoftKeyboard());

        onView(withId(R.id.btnRegistrati)).perform(click());
        onView(withId(R.id.cognome)).check(matches(hasErrorText("This field is required")));
    }

    @Test
    public void fallimentoRegistrazioneUtenteTestNoEmail() {
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

        ViewInteraction textView = onView(
                allOf(withId(R.id.registrati), withText("Registrati"),
                        withParent(allOf(withId(R.id.register),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        textView.check(matches(withText("Registrati")));
        ViewInteraction textName = onView(withId(R.id.nome)).perform(typeText(name), ViewActions.closeSoftKeyboard());
        ViewInteraction textSurname = onView(withId(R.id.cognome)).perform(typeText(surname), ViewActions.closeSoftKeyboard());
        ViewInteraction textEmail = onView(withId(R.id.email1)).perform(typeText(""),ViewActions.closeSoftKeyboard());
        ViewInteraction textPassword = onView(withId(R.id.password1)).perform(typeText(password), ViewActions.closeSoftKeyboard());
        ViewInteraction textUsername = onView(withId(R.id.username)).perform(typeText(username),ViewActions.closeSoftKeyboard());

        onView(withId(R.id.btnRegistrati)).perform(click());
        onView(withId(R.id.email1)).check(matches(hasErrorText("This field is required")));
    }


    @Test
    public void fallimentoRegistrazioneUtenteTestNoPassword() {
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

        ViewInteraction textView = onView(
                allOf(withId(R.id.registrati), withText("Registrati"),
                        withParent(allOf(withId(R.id.register),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        textView.check(matches(withText("Registrati")));
        ViewInteraction textName = onView(withId(R.id.nome)).perform(typeText(name), ViewActions.closeSoftKeyboard());
        ViewInteraction textSurname = onView(withId(R.id.cognome)).perform(typeText(surname), ViewActions.closeSoftKeyboard());
        ViewInteraction textEmail = onView(withId(R.id.email1)).perform(typeText("test@gmail.com"),ViewActions.closeSoftKeyboard());
        ViewInteraction textPassword = onView(withId(R.id.password1)).perform(typeText(""), ViewActions.closeSoftKeyboard());
        ViewInteraction textUsername = onView(withId(R.id.username)).perform(typeText(username),ViewActions.closeSoftKeyboard());

        onView(withId(R.id.btnRegistrati)).perform(click());
        onView(withId(R.id.password1)).check(matches(hasErrorText("This field is required")));
    }

    @Test
    public void fallimentoRegistrazioneUtenteTestPassShort() {
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

        ViewInteraction textView = onView(
                allOf(withId(R.id.registrati), withText("Registrati"),
                        withParent(allOf(withId(R.id.register),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        textView.check(matches(withText("Registrati")));
        ViewInteraction textName = onView(withId(R.id.nome)).perform(typeText(name), ViewActions.closeSoftKeyboard());
        ViewInteraction textSurname = onView(withId(R.id.cognome)).perform(typeText(surname), ViewActions.closeSoftKeyboard());
        ViewInteraction textEmail = onView(withId(R.id.email1)).perform(typeText("test@gmail.com"),ViewActions.closeSoftKeyboard());
        ViewInteraction textPassword = onView(withId(R.id.password1)).perform(typeText(passwordShort), ViewActions.closeSoftKeyboard());
        ViewInteraction textUsername = onView(withId(R.id.username)).perform(typeText(username),ViewActions.closeSoftKeyboard());

        onView(withId(R.id.btnRegistrati)).perform(click());
        onView(withId(R.id.password1)).check(matches(hasErrorText("This password is too short")));
    }


    @Test
    public void fallimentoRegistrazioneUtenteTestNoUsername() {
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

        ViewInteraction textView = onView(
                allOf(withId(R.id.registrati), withText("Registrati"),
                        withParent(allOf(withId(R.id.register),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        textView.check(matches(withText("Registrati")));
        ViewInteraction textName = onView(withId(R.id.nome)).perform(typeText(name), ViewActions.closeSoftKeyboard());
        ViewInteraction textSurname = onView(withId(R.id.cognome)).perform(typeText(surname), ViewActions.closeSoftKeyboard());
        ViewInteraction textEmail = onView(withId(R.id.email1)).perform(typeText("test@gmail.com"),ViewActions.closeSoftKeyboard());
        ViewInteraction textPassword = onView(withId(R.id.password1)).perform(typeText(password), ViewActions.closeSoftKeyboard());
        ViewInteraction textUsername = onView(withId(R.id.username)).perform(typeText(""),ViewActions.closeSoftKeyboard());

        onView(withId(R.id.btnRegistrati)).perform(click());
        onView(withId(R.id.username)).check(matches(hasErrorText("This field is required")));
    }


    @Test
    public void fallimentoRegistrazioneUtenteTestNoPhoto() {
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

        ViewInteraction textView = onView(
                allOf(withId(R.id.registrati), withText("Registrati"),
                        withParent(allOf(withId(R.id.register),
                                withParent(withId(android.R.id.content)))),
                        isDisplayed()));
        textView.check(matches(withText("Registrati")));
        ViewInteraction textName = onView(withId(R.id.nome)).perform(typeText(name), ViewActions.closeSoftKeyboard());
        ViewInteraction textSurname = onView(withId(R.id.cognome)).perform(typeText(surname), ViewActions.closeSoftKeyboard());
        ViewInteraction textEmail = onView(withId(R.id.email1)).perform(typeText("test@gmail.com"),ViewActions.closeSoftKeyboard());
        ViewInteraction textPassword = onView(withId(R.id.password1)).perform(typeText(password), ViewActions.closeSoftKeyboard());
        ViewInteraction textUsername = onView(withId(R.id.username)).perform(typeText(username),ViewActions.closeSoftKeyboard());

        onView(withId(R.id.btnRegistrati)).perform(click());
         onView(withText("Foto non aggiunta")).inRoot(withDecorView(not(mActivityTestRule.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
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
