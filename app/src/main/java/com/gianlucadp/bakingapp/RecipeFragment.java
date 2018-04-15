package com.gianlucadp.bakingapp;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.gianlucadp.bakingapp.models.Ingredient;
import com.gianlucadp.bakingapp.models.Step;
import com.gianlucadp.bakingapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class RecipeFragment extends Fragment {

    @BindView(R.id.rv_steps)
    RecyclerView mRecipesRecView;
    private StepsAdapter mStepsAdapter;
    @BindView(R.id.lv_ingredients)
    ListView mIngredientsListView;
    private ArrayAdapter<String> mListAdapter;
    private List<Step> mStepsList;
    @BindView(R.id.btn_favorite_ingredient)
    FloatingActionButton mFavoriteButton;
    private boolean mIsFavorite;
    private String mRecipeName;

    RecipeFragment.OnStepClickListener mCallback;
    private Unbinder unbinder;

    //Empty Constructor
    public RecipeFragment() {
    }

    public interface OnStepClickListener {
        void onStepSelected(int stepPosition);
    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (RecipeFragment.OnStepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnStepClickListener");
        }
    }



    // Inflates the RecyclerView for the recipes
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_recipe, container, false);

        ButterKnife.bind(this, rootView);

        unbinder = ButterKnife.bind(this, rootView);

        // Add layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecipesRecView.setLayoutManager(layoutManager);
        // Add divider
        DividerItemDecoration verticalDivider = new DividerItemDecoration(mRecipesRecView.getContext(), LinearLayoutManager.VERTICAL);
        mRecipesRecView.addItemDecoration(verticalDivider);

        // Create and set the adapter for steps
        mStepsAdapter = new StepsAdapter(getContext(), this, mStepsList);
        mRecipesRecView.setAdapter(mStepsAdapter);
        mStepsAdapter.setStepsList(null);

        //Add and set the adapter for ingredients
        mListAdapter = new ArrayAdapter<>(getContext(), R.layout.listview_ingredient_item);
        mIngredientsListView.setAdapter(mListAdapter);
        final SharedPreferences prefs = getContext().getSharedPreferences(Constants.APP_SHARED_PREFS,Context.MODE_PRIVATE);

        //Check if the current recipe is the favorite
        String favRecipe = prefs.getString(Constants.FAVORITE_RECIPE_PREF,getString(R.string.none));
        mRecipeName = prefs.getString(Constants.LAST_RECIPE_CLICKED_PREF, "");
        if (favRecipe.equals(mRecipeName)){
            setFavorite();
        }else{
            unsetFavorite();
        }
        //Set Action on favorite click button
        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mIsFavorite){
                    prefs.edit().putString(Constants.FAVORITE_RECIPE_PREF, mRecipeName).apply();
                    setFavorite();
                    updateWidget();
                }else{
                    prefs.edit().putString(Constants.FAVORITE_RECIPE_PREF, getString(R.string.no_recipe_selected)).apply();
                    unsetFavorite();
                    updateWidget();

                }

            }
        });
        // Return the root view
        return rootView;
    }

    private void updateWidget() {
        Intent intent = new Intent(getContext(), IngredientsWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getActivity().getApplication()).getAppWidgetIds(new ComponentName(getActivity().getApplication(), IngredientsWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        getActivity().sendBroadcast(intent);
    }

    private void unsetFavorite() {
        mFavoriteButton.setImageResource(R.drawable.ic_star);
        mIsFavorite =false;
    }

    private void setFavorite() {
        mFavoriteButton.setImageResource(R.drawable.ic_done);
        mIsFavorite = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void setStepsList(List<Step> stepsList) {
        this.mStepsList = stepsList;
        mStepsAdapter.setStepsList(stepsList);
        mStepsAdapter.notifyDataSetChanged();
    }

    public void setIngredients(List<Ingredient> ingredients){
        List<String> ingredientsStrings = new ArrayList<>();
        for (Ingredient ingredient:
             ingredients) {
            ingredientsStrings.add(ingredient.toString());

        }
        mListAdapter.addAll(ingredientsStrings);
        mListAdapter.notifyDataSetChanged();
    }

    public RecipeFragment.OnStepClickListener getCallback() {
        return mCallback;
    }


    public void setLastViewedStep(int position){
        mStepsAdapter.setLastViewedStep(position);
        mStepsAdapter.notifyDataSetChanged();
    }
}
