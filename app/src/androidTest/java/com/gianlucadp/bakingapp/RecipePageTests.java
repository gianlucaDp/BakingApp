package com.gianlucadp.bakingapp;

import android.content.Intent;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.gianlucadp.bakingapp.models.Ingredient;
import com.gianlucadp.bakingapp.models.Recipe;
import com.gianlucadp.bakingapp.models.Step;
import com.gianlucadp.bakingapp.utils.Constants;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class RecipePageTests {
    private static final String STEP_SHORT_DESCRIPTION = "Step short description";
    private static final String STEP_DESCRIPTION = "Step description";
    private String ingredientString;
    @Rule
    public ActivityTestRule<RecipeActivity> mActivityTestRule =
            new ActivityTestRule<>(RecipeActivity.class, false, false);


    @Before
    public void loadFakeData() {

        Recipe fakeRecipe = new Recipe();
        fakeRecipe.setName("RecipeTest");

        Ingredient ingredient = new Ingredient();
        ingredient.setIngredient("Ingredient");
        ingredient.setMeasure("Measure");
        ingredient.setQuantity(1);
        ingredientString = ingredient.toString();


        List<Ingredient> ingredientList = new ArrayList<>();
        ingredientList.add(ingredient);


        Step step = new Step();
        step.setShortDescription(STEP_SHORT_DESCRIPTION);
        step.setId(0);
        step.setDescription(STEP_DESCRIPTION);

        List<Step> stepList = new ArrayList<>();
        stepList.add(step);


        fakeRecipe.setIngredients(ingredientList);
        fakeRecipe.setSteps(stepList);


        Intent i = new Intent();
        i.putExtra(Constants.INTENT_RECIPE, fakeRecipe);

        mActivityTestRule.launchActivity(i);
    }

    @Test
    /** Verify that a recipe is loaded correctly */
    public void checkRecipeLoaded() {
        //List of steps
        onView(withId(R.id.rv_steps))
                .perform(RecyclerViewActions
                        .scrollToPosition(0));
        //Step summary
        onView(withText(STEP_SHORT_DESCRIPTION))
                .check(matches(isDisplayed()));

        //Step ingredient
        onView(withText(ingredientString))
                .check(matches(isDisplayed()));
    }

    @Test
    /** Verify that clicking on a step brings to Step page */
    public void checkClickOnRecipes() {
        onView(withId(R.id.rv_steps))
                .perform(RecyclerViewActions
                        .actionOnItemAtPosition(0, click()));

        //Step description view
        onView(withId(R.id.tv_step_description))
                .check(matches(isDisplayed()));
        //Step description value
        onView(withText(STEP_DESCRIPTION))
                .check(matches(isDisplayed()));
    }
}
