package com.gianlucadp.bakingapp.utils;

public class Constants {
    // Intents
    public static final String INTENT_RECIPE = "intent_recipe";
    public static final String INTENT_STEP = "intent_step";
    public static final String INTENT_STEP_POSITION = "intent_step";

    //Prefs
    public static final String APP_SHARED_PREFS = "baking_app_prefs";
    public static final String LAST_RECIPE_CLICKED_PREF = "last_recipe_clicked";
    public static final String INGREDIENTS_PREF = "ingredients";
    public static final String FAVORITE_RECIPE_PREF ="fav_recipe";

    //Other (Bundles & co.)
    public static final String DEFAULT_TITLE = "Baking App";
    public static final String STEP_POSITION ="step_position";
    public static final int REQUEST_POSITION_CODE = 111;
    public static final String VIDEO_TIME = "video_time";
    public static final String VIDEO_STATUS = "video_status";
    public static final String CURRENT_RECIPE_INSTANCE = "current_recipe";

    public enum DIRECTION{LEFT_TO_RIGHT, RIGHT_TO_LEFT};

}
