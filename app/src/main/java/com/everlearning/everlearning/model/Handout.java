package com.everlearning.everlearning.model;

import android.widget.ProgressBar;

/**
 * Created by mark on 8/8/15.
 */
public class Handout {

    private long id;
    private String name;
    private long subject_id;
    private String urlPath;
    private String updatedAt;
    private String encryptedPath;
    private ProgressBar progressBar;
    private int progress;
    private long thinDLid;
    private long dlState;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(long subject_id) {
        this.subject_id = subject_id;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public void setUrlPath(String urlPath) {
        this.urlPath = urlPath;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getEncryptedPath() {
        return encryptedPath;
    }

    public void setEncryptedPath(String encryptedPath) {
        this.encryptedPath = encryptedPath;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public long getThinDLid() {
        return thinDLid;
    }

    public void setThinDLid(long thinDLid) {
        this.thinDLid = thinDLid;
    }

    public long getDlState() {
        return dlState;
    }

    public void setDlState(long dlState) {
        this.dlState = dlState;
    }
}
