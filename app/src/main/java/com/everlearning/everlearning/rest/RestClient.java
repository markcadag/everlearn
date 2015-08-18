package com.everlearning.everlearning.rest;

import com.everlearning.everlearning.rest.service.ApiService;

import retrofit.RestAdapter;

/**
 * Created by mark on 8/8/15.
 */
public class RestClient
{
    //TODO change Base url
    private static final String BASE_URL = "https://api.github.com";
    private ApiService apiService;

    private static RestClient restClient = new RestClient();
    private static RestAdapter restAdapter;

    public static RestClient getInstance() {
        return restClient;
    }

    private RestClient() {
        restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(BASE_URL)
                .build();
        apiService = restAdapter.create(ApiService.class);

    }

    public ApiService getApiService()
    {
        return apiService;
    }
}
