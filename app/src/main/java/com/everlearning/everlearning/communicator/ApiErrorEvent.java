package com.everlearning.everlearning.communicator;

import retrofit.RetrofitError;

/**
 * Created by mark on 8/18/15.
 */
public class ApiErrorEvent {

    private RetrofitError retrofitError;

    public ApiErrorEvent(RetrofitError error) {
        this.retrofitError = error;
    }

    public RetrofitError getRetrofitError() {
        return retrofitError;
    }

}
