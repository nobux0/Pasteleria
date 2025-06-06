package com.example.pasteleria.main.model;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private final FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private SupabaseStorageApi storageApi;
    MutableLiveData<String> liveDataUrl = new MutableLiveData<>();
    private final CollectionReference clientesCollection;
    FirebaseUser user;
    private static final String API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6IndwanhoeGl1Y3dpeWJ0aHV3d3dtIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDEwOTc0OTAsImV4cCI6MjA1NjY3MzQ5MH0.yz3FdJAaFXPwLGzUkBrJ_-Ui8iOvAIht8g30EAwiZ9k";

    public UserRepository(Application application) {
        db = FirestoreClient.getInstance().getDatabase();
        clientesCollection = db.collection("clientes");
        mAuth = FirebaseAuth.getInstance();
        storageApi = SupabaseClient.getClient().create(SupabaseStorageApi.class);
        user = mAuth.getCurrentUser();

    }
    public LiveData<String> getImage(){

        String filename = mAuth.getCurrentUser().getUid()+".jpg";
        String fileUrl = "https://wpjxhxiucwiybthuwwwm.supabase.co/storage/v1/object/public/pasteles/" + filename;

        liveDataUrl.postValue(fileUrl);

        return liveDataUrl;
    }
    public LiveData<String> obtenerNombreCliente() {
        MutableLiveData<String> nombreLiveData = new MutableLiveData<>();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            db.collection("clientes").document(userId).get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            nombreLiveData.setValue(document.getString("nombre"));
                        }
                    });
        }
        return nombreLiveData;
    }
    public LiveData<String> obtenerTelefonoCliente() {
        MutableLiveData<String> telefonoLiveData = new MutableLiveData<>();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            db.collection("clientes").document(userId).get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            telefonoLiveData.setValue(document.getString("telefono"));
                        }
                    });
        }
        return telefonoLiveData;
    }
    public LiveData<Date> obtenerCumpleCliente() {
        MutableLiveData<Date> cumpleLiveData = new MutableLiveData<>();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            db.collection("clientes").document(userId).get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            cumpleLiveData.setValue(document.getDate("cumpleanos"));
                        }
                    });
        }
        return cumpleLiveData;
    }
    public LiveData<Boolean> agregarImagenCliente(String fileUrl) {
        Log.d("UserRepository", "Actualizando Firestore con URL: " + fileUrl);
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("clientes").document(userId)
                .update("imageUrl", fileUrl)
                .addOnSuccessListener(aVoid -> {
                    result.postValue(true);
                })
                .addOnFailureListener(e -> {
                    result.postValue(false);
                });

        return result;
    }
    public LiveData<String> uploadImage(File imageFile) {
        MutableLiveData<String> liveDataUrl = new MutableLiveData<>();
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file",user.getUid()+".jpg", requestFile);
        Call<Void> deleteCall = storageApi.deleteImage("Bearer " + API_KEY, "pasteles", user.getUid()+".jpg");
        deleteCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Call<Void> uploadCall = storageApi.uploadImage("Bearer " + API_KEY, "pasteles", user.getUid()+".jpg", body);
                uploadCall.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            String fileUrl = "https://wpjxhxiucwiybthuwwwm.supabase.co/storage/v1/object/public/pasteles/" + user.getUid()+".jpg";
                            Log.d("UserRepository", "URL de la imagen subida: " + fileUrl);
                            liveDataUrl.postValue(fileUrl);
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "sin error";
                                Log.e("UserRepository", "Error en uploadCall: " + response.code() + " - " + errorBody);
                            } catch (Exception e) {
                                Log.e("UserRepository", "Error leyendo errorBody: " + e.getMessage());
                            }
                            liveDataUrl.postValue(null);
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e("UserRepository", "Fallo en uploadCall: " + t.getMessage());
                        liveDataUrl.postValue(null);
                    }
                });
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                liveDataUrl.postValue(null);
            }
        });
        return liveDataUrl;
    }

    public LiveData<String> getImageUrl(String userId) {
        MutableLiveData<String> liveDataUrl = new MutableLiveData<>();
        db.collection("clientes")
                .document(userId)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        Log.e("Firestore", "Error al escuchar los cambios", e);
                        return;
                    }
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        String url = documentSnapshot.getString("imageUrl");
                        liveDataUrl.postValue(url);
                    }
                });
        return liveDataUrl;
    }
}
