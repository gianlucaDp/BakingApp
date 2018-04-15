package com.gianlucadp.bakingapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gianlucadp.bakingapp.models.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder> {
    private Context context;
    private List<Recipe> recipesList;
    private RecipesListFragment  masterFragment;


    public RecipesAdapter(Context context,RecipesListFragment master, List<Recipe> recipes) {
        this.context = context;
        this.recipesList = recipes;
        this.masterFragment = master;
    }

    @Override
    public int getItemCount() {
        if (recipesList == null) {
            return 0;
        } else {
            return recipesList.size();
        }
    }
    public void setRecipesList(List<Recipe> recipes){
        this.recipesList = recipes;
    }

    @Override
    public RecipeViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.recyclerview_recipe_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecipeViewHolder holder, int position) {
        holder.loadRecipe(position);
    }


    public class RecipeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.im_recipe_figure) ImageView mImageViewRecipeFigure;
        @BindView(R.id.tv_recipe_name) TextView mTextViewRecipeName;
        @BindView(R.id.tv_recipe_serving) TextView mTextViewRecipeServingNumb;

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = getAdapterPosition();
                Recipe currentRecipe = recipesList.get(currentPosition);
                masterFragment.getCallback().onRecipeSelected(currentRecipe);
            }
        };

        public RecipeViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(mOnClickListener);

        }

        public void loadRecipe(int position) {
            if (recipesList != null && recipesList.size()>= position) {
            Recipe currentRecipe = recipesList.get(position);
            if (!currentRecipe.getImage().isEmpty()) {
                Picasso.with(context).load(currentRecipe.getImage()).placeholder(R.drawable.ic_restaurant_menu).error(R.drawable.ic_restaurant_menu).into(mImageViewRecipeFigure);
            }
                mTextViewRecipeName.setText(currentRecipe.getName());
            String serving = context.getString(R.string.serving) + String.valueOf(currentRecipe.getServings());
            mTextViewRecipeServingNumb.setText(serving);

            }
        }
    }

}

