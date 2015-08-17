package com.everlearning.everlearning.rest.service;

import com.everlearning.everlearning.model.Handout;
import com.everlearning.everlearning.model.Repo;
import com.everlearning.everlearning.model.Subject;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

/**
 * Created by mark on 8/8/15.
 */
public interface ApiService
{

    @GET("/users/{users}/repos")
    void listSubjects(@Path("users") String user, Callback<List<Subject>> callback);

    @GET("/users/{id}/repos")
    void listHandouts(@Path("id") String subjectId, Callback<List<Handout>> callback);

    @POST("/users")
    void registerUser(@Body Repo repo, Callback<Repo> callback);

    @PUT("/users/{id}")
    void updateUser(@Body Repo repo, Callback<Repo> callback);


}
