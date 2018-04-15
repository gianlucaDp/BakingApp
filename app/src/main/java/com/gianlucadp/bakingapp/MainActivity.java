package com.gianlucadp.bakingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;

import com.gianlucadp.bakingapp.models.Recipe;
import com.gianlucadp.bakingapp.utils.Constants;
import com.gianlucadp.bakingapp.utils.SimpleIdlingResource;

public class MainActivity extends AppCompatActivity implements RecipesListFragment.OnRecipeClickListener {
    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @VisibleForTesting
    @NonNull
    public SimpleIdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    protected void onStart() {
        super.onStart();
        getIdlingResource().setIdleState(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(Constants.DEFAULT_TITLE);
        setContentView(R.layout.activity_main);


    }

    /**
     * If a recipe is Selected pass the recipe and also store its value in shared prefs
     */
    public void onRecipeSelected(Recipe recipe) {
        Bundle b = new Bundle();
        b.putParcelable(Constants.INTENT_RECIPE, recipe);
        final Intent intent = new Intent(this, RecipeActivity.class);
        final SharedPreferences prefs = this.getSharedPreferences(Constants.APP_SHARED_PREFS, Context.MODE_PRIVATE);
        prefs.edit().putString(Constants.LAST_RECIPE_CLICKED_PREF, recipe.getName()).apply();
        intent.putExtras(b);
        startActivity(intent);
    }

}
