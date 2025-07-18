package com.example.gadgetguard;

public class GadgetUsageEntry {
    private String app;
    private String time;
    private String date;

    public GadgetUsageEntry() {} // Required for Firestore

    public GadgetUsageEntry(String app, String time, String date) {
        this.app = app;
        this.time = time;
        this.date = date;
    }

    public String getApp() { return app; }
    public String getTime() { return time; }
    public String getDate() { return date; }
}
