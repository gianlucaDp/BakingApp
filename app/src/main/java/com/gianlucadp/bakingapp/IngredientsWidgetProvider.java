package com.gianlucadp.bakingapp;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.gianlucadp.bakingapp.models.Ingredient;
import com.gianlucadp.bakingapp.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;


public class IngredientsWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        //Get the stored ingredients
        SharedPreferences prefs = context.getSharedPreferences(Constants.APP_SHARED_PREFS, Context.MODE_PRIVATE);
        String storedHashMapString = prefs.getString(Constants.INGREDIENTS_PREF, context.getString(R.string.generic_error));
        // Convert Object from String to List
        java.lang.reflect.Type type = new TypeToken<HashMap<String, List<Ingredient>>>() {
        }.getType();
        Gson gson = new Gson();
        HashMap<String, List<Ingredient>> ingredients = gson.fromJson(storedHashMapString, type);
        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget);
        String recipeName = prefs.getString(Constants.FAVORITE_RECIPE_PREF, context.getString(R.string.no_recipe_selected));

        views.setTextViewText(R.id.appwidget_text, recipeName);
        // Convert the list to String (list not needed in this version of App)
        if (ingredients.containsKey(recipeName)) {
            List<Ingredient> ingredientList = ingredients.get(recipeName);
            StringBuilder sb = new StringBuilder();
            for (Ingredient ingredient : ingredientList) {

                sb.append(ingredient.toString() + "\n");
            }
            views.setTextViewText(R.id.tv_ingredients_widget, sb.toString());
        } else {
            views.setTextViewText(R.id.tv_ingredients_widget, "Please select a recipe to display it here");
        }
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Nothing to do
    }

    @Override
    public void onDisabled(Context context) {
        // Nothing to do
    }
}

