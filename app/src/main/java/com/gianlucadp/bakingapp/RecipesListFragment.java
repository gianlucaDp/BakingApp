package com.gianlucadp.bakingapp;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gianlucadp.bakingapp.models.Ingredient;
import com.gianlucadp.bakingapp.models.Recipe;
import com.gianlucadp.bakingapp.utils.Constants;
import com.gianlucadp.bakingapp.utils.JSonUtils;
import com.gianlucadp.bakingapp.utils.NetworkUtils;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.Context.MODE_PRIVATE;

public class RecipesListFragment extends Fragment {

    @BindView(R.id.recipes_recycler_view)
    RecyclerView mRecipesRecView;
    private RecipesAdapter mRecipesAdapter;
    @BindView(R.id.recipe_refresh)
    SwipeRefreshLayout refreshLayout;
    private List<Recipe> mRecipesList;
    OnRecipeClickListener mCallback;
    private Unbinder unbinder;
    private RecipesListFragment mCurrentInstance;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;


    //Empty Constructor
    public RecipesListFragment() {
    }

    public interface OnRecipeClickListener {
        void onRecipeSelected(Recipe recipe);
    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (OnRecipeClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnRecipeClickListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_recipes_list, container, false);

        ButterKnife.bind(this, rootView);
        refreshLayout.setRefreshing(true);

        unbinder = ButterKnife.bind(this, rootView);
        mCurrentInstance = this;
        // create the layout grid manager
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), numberOfColumns());
        mRecipesRecView.setLayoutManager(layoutManager);

        // Create and set the adapter
        mRecipesAdapter = new RecipesAdapter(getContext(), mCurrentInstance, mRecipesList);
        mRecipesRecView.setAdapter(mRecipesAdapter);
        //Handle no internet connection
        final MainActivity activity = (MainActivity) getActivity();

        if (NetworkUtils.checkConnection(getContext())) {
            NetworkUtils.getRecipesList(mCurrentInstance,activity.getIdlingResource());
        } else {
            showNoInternetWarning();
            refreshLayout.setRefreshing(false);
        }
        // Add a listener to refresh the data if swipe up
        refreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (NetworkUtils.checkConnection(getContext())) {
                            NetworkUtils.getRecipesList(mCurrentInstance,activity.getIdlingResource());
                            refreshLayout.setRefreshing(false);

                        } else {

                            showNoInternetWarning();
                            refreshLayout.setRefreshing(false);
                        }
                    }
                }
        );

        // Return the root view
        return rootView;
    }

    private void showNoInternetWarning() {
        Snackbar snackbar = Snackbar.make(mCoordinatorLayout, R.string.no_internet_warning, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void updateRecipesList(List<Recipe> recipes) {
        this.mRecipesList = recipes;
        mRecipesAdapter.setRecipesList(recipes);
        mRecipesAdapter.notifyDataSetChanged();
        if (refreshLayout!=null) {
            refreshLayout.setRefreshing(false);
        }
        HashMap<String, List<Ingredient>> ingredientsMap = new HashMap<>();
        for (Recipe recipe : recipes) {
            ingredientsMap.put(recipe.getName(), recipe.getIngredients());
        }

        String hashMapString = JSonUtils.serialize(ingredientsMap);
        SharedPreferences prefs = getContext().getSharedPreferences(Constants.APP_SHARED_PREFS, MODE_PRIVATE);
        prefs.edit().putString(Constants.INGREDIENTS_PREF, hashMapString).apply();
    }

    public OnRecipeClickListener getCallback() {
        return mCallback;
    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthDivider = 800;
        int width = displayMetrics.widthPixels;
        return width / widthDivider;
    }

}
