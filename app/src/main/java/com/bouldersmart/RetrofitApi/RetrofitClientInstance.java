package com.bouldersmart.RetrofitApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {
    // http://bouldersmartapplication.space/Api/
    private static String HEADER_KEY = "2b223e5cee713615ha54ac203b24e9a1237031516mk";
    private static Retrofit retrofit;
    private static final String BASE_URL = "http://bouldersmartapplication.space/Api/";
    //    private static final String BASE_URL = "http://www.appmania.co.in/BoulderSmart/Api/";
    private static final String BASE_USER_URL = BASE_URL + "User/";

    public static Retrofit UserApiClient(final String token) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.addInterceptor(logging);
        builder.readTimeout(60, TimeUnit.SECONDS);
        builder.connectTimeout(5, TimeUnit.MINUTES);
        builder.addInterceptor(chain -> {
            Request request;
            if (token != null) {
                request = chain.request().newBuilder().addHeader("key", HEADER_KEY)
                        .addHeader("token", token).build();
            } else {
                request = chain.request().newBuilder().addHeader("key", HEADER_KEY).build();
            }
            return chain.proceed(request);
        });
        Gson gson = new GsonBuilder().setLenient().create();
        OkHttpClient httpClient = builder.build();
        retrofit = new Retrofit.Builder().baseUrl(BASE_USER_URL)
                .addConverterFactory(GsonConverterFactory.create(gson)).client(httpClient).build();
        return retrofit;
    }
}