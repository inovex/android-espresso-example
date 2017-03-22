package de.inovex.testthingy;


import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static de.inovex.testthingy.RestServiceTestHelper.getStringFromFile;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    private static MockWebServer server;

    @BeforeClass
    public static void startMockServer() throws Exception {
        server = new MockWebServer();
        server.start(43210);
    }

    @AfterClass
    public static void shutdownMockServer() throws Exception {
        server.shutdown();
    }

    @Test
    public void login_fail_should_display_error_dialog() throws Exception {
        MockResponse loginResponse = new MockResponse().setResponseCode(403);
        server.enqueue(loginResponse);

        ViewInteraction appCompatEditText = onView(withId(R.id.email));
        appCompatEditText.perform(scrollTo(), replaceText("as@asd.de"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(withId(R.id.password));
        appCompatEditText2.perform(scrollTo(), replaceText("12345"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(withId(R.id.email_sign_in_button));
        appCompatButton.perform(scrollTo(), click());

        ViewInteraction textView = onView(allOf(withId(R.id.alertTitle), isDisplayed()));
        textView.check(matches(withText(R.string.dialog_title_error)));

        ViewInteraction textView2 = onView(allOf(withId(android.R.id.message), isDisplayed()));
        textView2.check(matches(withText(R.string.dialog_message_login_failed)));
    }

    @Test
    public void login_success_should_lead_to_mainactivity_and_display_correct_string() throws Exception {
        MockResponse loginResponse = new MockResponse().setResponseCode(200);
        MockResponse someresponse = new MockResponse().setResponseCode(200)
                .setBody(getStringFromFile(InstrumentationRegistry.getContext(),
                        "some_response_200_ok_true.json"));
        server.enqueue(loginResponse);
        server.enqueue(someresponse);

        ViewInteraction appCompatEditText = onView(withId(R.id.email));
        appCompatEditText.perform(scrollTo(), replaceText("as@asd.de"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(withId(R.id.password));
        appCompatEditText2.perform(scrollTo(), replaceText("12345"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(withId(R.id.email_sign_in_button));
        appCompatButton.perform(scrollTo(), click());

        ViewInteraction textViewWithTextFromApi = onView(allOf(withId(R.id.textView), isDisplayed()));
        textViewWithTextFromApi.check(matches(withText("Android testing is funny: true")));
    }

    @Test
    public void login_success_should_lead_to_mainactivity_and_display_correct_string2() throws Exception {
        MockResponse loginResponse = new MockResponse().setResponseCode(200);
        MockResponse someresponse = new MockResponse().setResponseCode(200)
                .setBody(getStringFromFile(InstrumentationRegistry.getContext(),
                        "some_response_200_ok_false.json"));
        server.enqueue(loginResponse);
        server.enqueue(someresponse);

        ViewInteraction appCompatEditText = onView(withId(R.id.email));
        appCompatEditText.perform(scrollTo(), replaceText("as@asd.de"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(withId(R.id.password));
        appCompatEditText2.perform(scrollTo(), replaceText("12345"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(withId(R.id.email_sign_in_button));
        appCompatButton.perform(scrollTo(), click());

        ViewInteraction textViewWithTextFromApi = onView(allOf(withId(R.id.textView), isDisplayed()));
        textViewWithTextFromApi.check(matches(withText("Android testing is funny: false")));
    }
}
