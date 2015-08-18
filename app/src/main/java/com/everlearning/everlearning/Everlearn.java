package com.everlearning.everlearning;

import android.app.Application;

import com.everlearning.everlearning.communicator.ApiErrorEvent;
import com.everlearning.everlearning.db.DatabaseHelper;
import com.everlearning.everlearning.db.DatabaseManager;
import com.everlearning.everlearning.rest.RestClient;
import com.everlearning.everlearning.rest.SubjectListService;

import de.greenrobot.event.EventBus;

/**
 * Created by mark on 8/9/15.
 */
public class Everlearn extends Application {

    private SubjectListService subjectListService;
    private RestClient client = RestClient.getInstance();
    private EventBus eventBus = EventBus.getDefault();

    public Everlearn() {
        DatabaseManager.initializeInstance(new DatabaseHelper(this));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        subjectListService = new SubjectListService(client.getApiService(),eventBus,this);
        EventBus.getDefault().register(subjectListService);
        eventBus.register(this);
    }

    public void onEvent(ApiErrorEvent apiErrorEvent){

    }
}
