package com.example.pasteleria.main.model;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirestoreClient {
    private static FirestoreClient instance;
    private final FirebaseFirestore db;

    private FirestoreClient() {
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized FirestoreClient getInstance() {
        if (instance == null) {
            instance = new FirestoreClient();
        }
        return instance;
    }

    public FirebaseFirestore getDatabase() {
        return db;
    }
}
