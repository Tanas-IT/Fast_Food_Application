package com.group4.net.fastfoodapp.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface PaymentAPI {
    @POST("api/Payment/pay")
    Call<PaymentResponse> makePayment(@Body PaymentRequest request);

}
