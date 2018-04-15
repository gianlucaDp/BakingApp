package com.gianlucadp.bakingapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MainPageTests {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    private IdlingResource mIdlingResource;
    private String[] recipes ={"Nutella Pie","Brownies","Yellow Cake","Cheesecake"};


    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        Espresso.registerIdlingResources(mIdlingResource);
    }

    @Test
    /** Verify that each recipe is loaded */
    public void checkRecipesLoaded() {
        for (int i = 0; i < recipes.length; i++){
            onView(withId(R.id.recipes_recycler_view))
                    .perform(RecyclerViewActions
                            .scrollToPosition(i));

        onView(withText(recipes[i]))
                .check(matches(isDisplayed()));
    }
    }

    @Test
    /** Verify that clicking on a recipe brings to Recipe page */
    public void checkClickOnRecipes() {
        onView(withId(R.id.recipes_recycler_view))
                .perform(RecyclerViewActions
                        .actionOnItemAtPosition(1, click()));

        //Ingredients title
        onView(withId(R.id.tv_ingredient_title))
                .check(matches(isDisplayed()));

            //Ingredients list
            onView(withId(R.id.lv_ingredients))
                    .check(matches(isDisplayed()));

            //Steps List
            onView(withId(R.id.rv_steps))
                    .check(matches(isDisplayed()));

    }


    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }
}
