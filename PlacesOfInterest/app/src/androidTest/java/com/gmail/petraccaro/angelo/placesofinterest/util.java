package com.gmail.petraccaro.angelo.placesofinterest;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import org.hamcrest.Matcher;
import java.util.Calendar;
import java.util.Date;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

public class util {
    public final static String TEST_USER_NAME = "Angelo";
    public final static String TEST_USER_SURNAME = "Petraccaro";
    public final static String TEST_USER_EMAIL = "petraccaro.angelo@gmail.com";
    public final static String TEST_USER_PASSWORD = "asd123";

    public final static String TEST_EVENT_TITLE = "TITLE_ETEST";
    public final static String TEST_EVENT_DESCRIPTION = "DESCRIPTION_ETEST";

    public final static int START_DATE_DAY;
    public final static int START_DATE_MONTH;
    public final static int START_DATE_YEAR;
    public final static int END_DATE_DAY;
    public final static int END_DATE_MONTH;
    public final static int END_DATE_YEAR;

    static {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 1); // tomorrow
        START_DATE_DAY = cal.get(Calendar.DATE);
        START_DATE_MONTH = cal.get(Calendar.MONTH) + 1;
        START_DATE_YEAR = cal.get(Calendar.YEAR);

        cal.add(Calendar.DATE, 1); // the day after tomorrow
        END_DATE_DAY = cal.get(Calendar.DATE);
        END_DATE_MONTH = cal.get(Calendar.MONTH) + 1;
        END_DATE_YEAR = cal.get(Calendar.YEAR);

        //Log.e("TAG", START_DATE_DAY+"-"+START_DATE_MONTH+"-"+START_DATE_YEAR+" -> "+END_DATE_DAY+"-"+END_DATE_MONTH+"-"+END_DATE_YEAR);
    }





    public static void resetSharedPref(){
        Context appContext = getInstrumentation().getTargetContext();

        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(appContext);

        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }





    public static String prependZeroToMonth(int monthNum) {
        if (monthNum < 10)
            return "0" + monthNum;
        else
            return "" + monthNum;
    }

    public static void waitFor(final long millis) {
        onView(isRoot()).perform(waitFor2(millis));
    }

    /**
     * Perform action of waiting for a specific time.
     *
     * Use it with subsequent code:
     *
     *      onView(isRoot()).perform(waitFor2(VALUE_IN_MILLIS));
     */
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
}

