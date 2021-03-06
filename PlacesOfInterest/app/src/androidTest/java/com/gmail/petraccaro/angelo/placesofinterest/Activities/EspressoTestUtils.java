package com.gmail.petraccaro.angelo.placesofinterest.Activities;

import android.view.View;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.intent.Intents;
import org.hamcrest.Matcher;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;

public class EspressoTestUtils {
    public final static String TEST_USER_NOME = "TEST_NOME";
    public final static String TEST_USER_COGNOME = "TEST_COGNOME";
    public final static String TEST_USER_EMAIL = "petraccaro.angelo@gmail.com";
    public final static String TEST_USER_PASSWORD = "asd123";
    public final static String TEST_USER_USER = "Angelo_Petraccaro";

    public static void waitFor(final long millis) {
        onView(isRoot()).perform(waitFor2(millis));
    }

    private static ViewAction waitFor2(final long millis) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isRoot();
            }

            @Override
            public String getDescription() {
                return "Wait for " + millis + " milliseconds.";
            }

            @Override
            public void perform(UiController uiController, final View view) {
                uiController.loopMainThreadForAtLeast(millis);
            }
        };
    }

    public static void initIntent() {
        Intents.init();
    }

    public static void releaseIntent() {
        Intents.release();
    }

}
