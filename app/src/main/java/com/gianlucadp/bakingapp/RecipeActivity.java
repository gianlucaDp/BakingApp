package com.gianlucadp.bakingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.gianlucadp.bakingapp.models.Recipe;
import com.gianlucadp.bakingapp.utils.Constants;


public class RecipeActivity extends AppCompatActivity implements RecipeFragment.OnStepClickListener, StepFragment.OnStepSwipeListener {
    private Recipe mCurrentRecipe;
    private boolean mTwoPane;
    private RecipeFragment mRecipeFragment;
    private int mStepPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        SharedPreferences prefs = this.getSharedPreferences(Constants.APP_SHARED_PREFS, Context.MODE_PRIVATE);
        this.setTitle(Constants.DEFAULT_TITLE + ": " + prefs.getString(Constants.LAST_RECIPE_CLICKED_PREF, ""));
        //The element that is present on bigger screen and not in smaller one is the line separator
        mTwoPane = (findViewById(R.id.line_separator) != null);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mCurrentRecipe = extras.getParcelable(Constants.INTENT_RECIPE);
        } else {
            mCurrentRecipe = null;
            Log.d(this.getLocalClassName(), "Recipe not present in extras, check what happened it should not be possible");

        }

        mRecipeFragment = (RecipeFragment) getSupportFragmentManager().findFragmentById(R.id.recipe_fragment);
        // If a previous instance exists, load the correct values, otherwise the first one
        if (savedInstanceState != null) {
            mStepPosition = savedInstanceState.getInt(Constants.STEP_POSITION);
            mRecipeFragment.setLastViewedStep(mStepPosition);
        } else {
            mStepPosition = 0;
            // If two pane we start by default from the first, otherwise no one
            if (mTwoPane){
                mRecipeFragment.setLastViewedStep(0);
            }else {
                mRecipeFragment.setLastViewedStep(-1);
            }
        }
        mRecipeFragment.setStepsList(mCurrentRecipe.getSteps());
        mRecipeFragment.setIngredients(mCurrentRecipe.getIngredients());


        if (mTwoPane && savedInstanceState==null) {

            //Attach the fragment only if it is not already present

            StepFragment stepFragment = new StepFragment();
            stepFragment.setStep(mCurrentRecipe.getSteps().get(mStepPosition));
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.fm_step, stepFragment)
                    .commit();

        }

    }

    public void onStepSelected(int stepPosition) {
        mStepPosition = stepPosition;
        //If there is one pane we launch another activity
        if (!mTwoPane) {
            Bundle b = new Bundle();
            b.putParcelable(Constants.INTENT_RECIPE, mCurrentRecipe);
            b.putInt(Constants.INTENT_STEP_POSITION, stepPosition);
            final Intent intent = new Intent(this, StepActivity.class);
            intent.putExtras(b);
            startActivityForResult(intent, Constants.REQUEST_POSITION_CODE);
            // Otherwise we attach the correct fragment
        } else {
            StepFragment stepFragment = new StepFragment();
            stepFragment.setStep(mCurrentRecipe.getSteps().get(stepPosition));
            mRecipeFragment.setLastViewedStep(stepPosition);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fm_step, stepFragment)
                    .commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // When we go back form the activity step, take update the last seen step
        if (requestCode == Constants.REQUEST_POSITION_CODE) {

            if (resultCode == Activity.RESULT_OK) {
                int result = data.getIntExtra(Constants.INTENT_STEP_POSITION, -1);
                RecipeFragment recipeFragment = (RecipeFragment) getSupportFragmentManager().findFragmentById(R.id.recipe_fragment);
                recipeFragment.setLastViewedStep(result);
                mStepPosition = result;
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        currentState.putInt(Constants.STEP_POSITION, mStepPosition);
        super.onSaveInstanceState(currentState);
    }

    @Override
    public void OnStepSwipe(Constants.DIRECTION direction) {
        //Do Nothing since it's valid only in two pane mode
    }

    @Override
    public void OnNoInternetAvailable() {
        Toast.makeText(this, getString(R.string.no_internet_warning_step), Toast.LENGTH_LONG).show();
        finish();
    }

}
