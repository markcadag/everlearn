package com.everlearning.everlearning;

import android.app.Application;

import com.everlearning.everlearning.db.DatabaseHelper;
import com.everlearning.everlearning.db.DatabaseManager;

/**
 * Created by mark on 8/9/15.
 */
public class Everlearning extends Application {

    public Everlearning() {
        DatabaseManager.initializeInstance(new DatabaseHelper(this));
    }

}
