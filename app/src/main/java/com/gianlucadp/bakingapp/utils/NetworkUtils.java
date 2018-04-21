package com.gianlucadp.bakingapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.test.espresso.IdlingResource;
import android.util.Log;

import com.gianlucadp.bakingapp.RecipesListFragment;
import com.gianlucadp.bakingapp.models.Recipe;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;


public class NetworkUtils{
    static Retrofit retrofit = null;

    public static void getRecipesList(final RecipesListFragment instance, final SimpleIdlingResource idlingResource) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


        retrofit = new Retrofit.Builder()
                .baseUrl("http://go.udacity.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        ApiEndPointInterface bakingNet = retrofit.create(ApiEndPointInterface.class);

        Call<List<Recipe>> call = bakingNet.getRecipes();
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful() && instance!=null) {
                    try {


                    instance.updateRecipesList(response.body());
                    }catch(Exception e){
                        Log.d(getClass().getName(),"Something went wrong");
                    }
                    if (idlingResource!=null){
                        idlingResource.setIdleState(true);
                    }
                } else {
                    // error response, no access to resource?
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                //Something went wrong (like no internet connection)
                Log.d("Error", t.getMessage());
            }
        });
    }



    interface ApiEndPointInterface {

    @GET("android-baking-app-json")
    Call<List<Recipe>> getRecipes();
}

    public static boolean checkConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
