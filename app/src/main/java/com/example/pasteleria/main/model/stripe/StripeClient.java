package com.example.pasteleria.main.model.stripe;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StripeClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://localhost:8080") //IP local provisional
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
