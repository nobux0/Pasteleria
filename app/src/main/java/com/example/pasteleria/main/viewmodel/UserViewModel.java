package com.example.pasteleria.main.viewmodel;

import android.app.Application;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.pasteleria.main.misc.ImageUtils;
import com.example.pasteleria.main.misc.SharedPreferencesHelper;
import com.example.pasteleria.main.model.UserRepository;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class UserViewModel extends AndroidViewModel {
    private SharedPreferencesHelper helper;
    private UserRepository userRepository;

    public MutableLiveData<String> nombre = new MutableLiveData<>();
    public MutableLiveData<String> telefono = new MutableLiveData<>();
    public MutableLiveData<Date> cumple = new MutableLiveData<>();
    MutableLiveData<Boolean> resultadoLiveData = new MutableLiveData<>();

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
    }
    public LiveData<String> getImageUrl(String userId) {
        return userRepository.getImageUrl(userId);
    }
    public LiveData<Boolean> agregarImagen( Uri imageFileUri) {
        try {
            File imageFile = ImageUtils.getFileFromUri(getApplication().getApplicationContext(), imageFileUri);

            return Transformations.switchMap(userRepository.uploadImage(imageFile), fileUrl -> {
                if (fileUrl != null) {
                    return userRepository.agregarImagenCliente(fileUrl);
                } else {
                    resultadoLiveData.postValue(false);
                    return resultadoLiveData;
                }
            });

        } catch (IOException e) {
            resultadoLiveData.postValue(false);
        }

        return resultadoLiveData;
    }
//    public void actualizarNombre(String nombreUser){
//        nombre.setValue(nombreUser);
//    }

    public void cargarNombreUsuario() {
        userRepository.obtenerNombreCliente().observeForever(nombre1 -> {
            nombre.setValue(nombre1);
        });
    }
    public void cargarTelefono() {
        userRepository.obtenerTelefonoCliente().observeForever(telefono1 -> {
            telefono.setValue(telefono1);
        });
    }
    public void cargarCumple() {
        userRepository.obtenerCumpleCliente().observeForever(cumple1 -> {
            cumple.setValue(cumple1);
        });
    }

}
