package com.gianlucadp.bakingapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.gianlucadp.bakingapp.models.Recipe;
import com.gianlucadp.bakingapp.utils.Constants;


public class StepActivity extends AppCompatActivity implements StepFragment.OnStepSwipeListener {

    Recipe currentRecipe;
    int stepPosition=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();
        }else{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        setContentView(R.layout.activity_step);
        SharedPreferences prefs = this.getSharedPreferences(Constants.APP_SHARED_PREFS, Context.MODE_PRIVATE);

        this.setTitle(Constants.DEFAULT_TITLE + ": " + prefs.getString(Constants.LAST_RECIPE_CLICKED_PREF, ""));
        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            currentRecipe = extras.getParcelable(Constants.INTENT_RECIPE);
            stepPosition =extras.getInt(Constants.INTENT_STEP_POSITION);
        }else{
            currentRecipe = null;
            Log.d(this.getLocalClassName(), "Recipe not present in extras, check what happened it should not be possible");
        }


        if (savedInstanceState == null) {
            //Attach the step fragment
            StepFragment stepFragment = new StepFragment();
            stepFragment.setStep(currentRecipe.getSteps().get(stepPosition));
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.fm_step, stepFragment)
                    .commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_step_navigator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();

        if (itemThatWasClickedId == R.id.action_forward) {
            moveToNextStep();
            return true;
        }

        if (itemThatWasClickedId == R.id.action_backward) {

            moveToPreviousStep();
            return true;
        }

        if (itemThatWasClickedId == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void moveToPreviousStep() {
        if (stepPosition > 0) {
            stepPosition--;
            //Update the step fragment
            StepFragment stepFragment = new StepFragment();
            stepFragment.setStep(currentRecipe.getSteps().get(stepPosition));
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fm_step, stepFragment)
                    .commit();


        } else {
            returnStepPosition();
        }
    }

    private void moveToNextStep() {
        if (stepPosition < currentRecipe.getSteps().size() - 1) {
            stepPosition++;
            //Update the step fragment
            StepFragment stepFragment = new StepFragment();
            stepFragment.setStep(currentRecipe.getSteps().get(stepPosition));
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fm_step, stepFragment)
                    .commit();

        } else {
            returnStepPosition();
        }
    }

    @Override
    public void onBackPressed() {
        returnStepPosition();
        //super.onBackPressed(); Not needed
    }

    private void returnStepPosition() {
        Intent intent = new Intent();
        intent.putExtra(Constants.INTENT_STEP_POSITION, stepPosition);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


    @Override
    public void OnStepSwipe(Constants.DIRECTION direction) {
        switch (direction) {
            case LEFT_TO_RIGHT:
                moveToNextStep();
                break;
            case RIGHT_TO_LEFT:
                moveToPreviousStep();
                break;
        }
    }

    @Override
    public void OnNoInternetAvailable() {
        Toast.makeText(this, getString(R.string.no_internet_warning_step), Toast.LENGTH_LONG).show();
        finish();
    }


}

