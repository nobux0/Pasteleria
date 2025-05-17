package com.example.pasteleria.main.model.stripe;

import java.util.Map;

import retrofit2.*;
import retrofit2.http.*;


public interface StripeApi {
    @POST("/api/pagos/crear-intencion")
    Call<Map<String, String>> crearIntentoPago(@Body Map<String, Object> body);
    @POST("/api/stripe/create-customer")
    Call<Map<String, String>> createCustomer(@Body Map<String, String> email);

}
