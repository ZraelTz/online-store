package com.ztech.store.domain;

public class CreatePaymentResponse {
    private String clientSecret;
    public CreatePaymentResponse(String clientSecret) {
      this.clientSecret = clientSecret; 
    }
}
