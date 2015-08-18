package com.everlearning.everlearning.rest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.everlearning.everlearning.communicator.ApiErrorEvent;
import com.everlearning.everlearning.communicator.LoadSubjectEvent;
import com.everlearning.everlearning.communicator.SubjectsLoadedEvent;
import com.everlearning.everlearning.db.DatabaseManager;
import com.everlearning.everlearning.db.QueryExecutor;
import com.everlearning.everlearning.db.dao.SubjectDAO;
import com.everlearning.everlearning.model.Subject;
import com.everlearning.everlearning.rest.service.ApiService;

import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by mark on 8/17/15.
 */
public class SubjectListService {

    private ApiService apiService;
    private EventBus eventBus;
    private Context context;
    private final String TAG = SubjectListService.class.getSimpleName();

    public SubjectListService(ApiService apiService,EventBus eventBus,Context context){
        this.apiService = apiService;
        this.eventBus = eventBus;
        this.context = context;
    }

    public void insertDataSubjects(final List<Subject> subjectsData) {
        DatabaseManager.getInstance().executeQuery(new QueryExecutor() {
            @Override
            public void run(SQLiteDatabase database) {
                SubjectDAO subjectDAO = new SubjectDAO(database, context);
                subjectDAO.insertOrUpdate(subjectsData);
                eventBus.post(new SubjectsLoadedEvent());
            }
        });
    }

    public void onEvent(LoadSubjectEvent event){
        apiService.listSubjects("mark",
                new Callback<List<Subject>>() {
                    @Override
                    public void success(List<Subject> subjects, Response response) {
                        insertDataSubjects(subjects);
                    }
                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(TAG, error.toString());
                        eventBus.post(new ApiErrorEvent(error));
                    }
                });
        Log.i(TAG, "getting subjects");
    }
}
