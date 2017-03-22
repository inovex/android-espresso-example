package de.inovex.testthingy;

import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.TextInputLayout;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;
import android.widget.Checkable;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static org.hamcrest.Matchers.isA;

public class TestHelper {

    public static Matcher<View> withBottomNavItemCheckedStatus(final boolean isChecked) {
        return new BoundedMatcher<View, BottomNavigationItemView>(BottomNavigationItemView.class) {
            boolean triedMatching;

            @Override
            public void describeTo(Description description) {
                if (triedMatching) {
                    description.appendText("with BottomNavigationItem check status: " + String.valueOf(isChecked));
                    description.appendText("But was: " + String.valueOf(!isChecked));
                }
            }

            @Override
            protected boolean matchesSafely(BottomNavigationItemView item) {
                triedMatching = true;
                //noinspection RestrictedApi
                return item.getItemData().isChecked() == isChecked;
            }
        };
    }

    public static ViewAction betterScrollTo() {
        return ViewActions.actionWithAssertions(new NestedScrollToAction());
    }

    public static ViewAction setChecked(final boolean checked) {
        return new ViewAction() {
            @Override
            public BaseMatcher<View> getConstraints() {
                return new BaseMatcher<View>() {
                    @Override
                    public boolean matches(Object item) {
                        return isA(Checkable.class).matches(item);
                    }

                    @Override
                    public void describeMismatch(Object item, Description mismatchDescription) {
                    }

                    @Override
                    public void describeTo(Description description) {
                    }
                };
            }

            @Override
            public String getDescription() {
                return null;
            }

            @Override
            public void perform(UiController uiController, View view) {
                Checkable checkableView = (Checkable) view;
                checkableView.setChecked(checked);
            }
        };
    }

    public static Matcher<View> hasTextInputLayoutErrorText(final String expectedErrorText) {
        return new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof TextInputLayout)) {
                    return false;
                }

                CharSequence error = ((TextInputLayout) view).getError();
                if (error == null)
                    return false;

                String hint = error.toString();
                return expectedErrorText.equals(hint);
            }

            @Override
            public void describeTo(Description description) {
            }
        };
    }
}
