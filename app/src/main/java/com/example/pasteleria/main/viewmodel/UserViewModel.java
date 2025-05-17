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

public class UserViewModel extends AndroidViewModel {
    private SharedPreferencesHelper helper;
    private UserRepository userRepository;
    public MutableLiveData<String> nombre = new MutableLiveData<>();
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
            // Convertir el Uri en un File
            File imageFile = ImageUtils.getFileFromUri(getApplication().getApplicationContext(), imageFileUri);

            // Usamos switchMap para esperar a que la imagen se suba antes de guardar el anuncio
            return Transformations.switchMap(userRepository.uploadImage(imageFile), fileUrl -> {
                if (fileUrl != null) {
                    // Si obtenemos una URL válida de la imagen, actualizamos el Firestore
                    return userRepository.agregarImagenCliente(fileUrl);
                } else {
                    // Si la subida falla, devolvemos false
                    resultadoLiveData.postValue(false);
                    return resultadoLiveData;
                }
            });

        } catch (IOException e) {
            // Capturamos posibles errores en la conversión del archivo
            resultadoLiveData.postValue(false);
        }

        return resultadoLiveData;
    }
    public void actualizarNombre(String nombreUser){
        nombre.setValue(nombreUser);
    }

    public void cargarNombreUsuario() {
        userRepository.obtenerNombreCliente().observeForever(nombre1 -> {
            nombre.setValue(nombre1);
        });
    }

}
