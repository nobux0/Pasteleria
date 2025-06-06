package com.example.pasteleria.main.misc;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    private SharedPreferences sharedPreferences;

    private static final String PREFS_NAME = "prefs";

    private static final String NOMBRE_BOOL = "nombre";

    private static final String IDIOMA = "idioma";
    private static final String KEY_TEMA_OSCURO = "tema_oscuro";

    public void guardarTemaOscuro(boolean esOscuro) {
        sharedPreferences.edit().putBoolean(KEY_TEMA_OSCURO, esOscuro).apply();
    }

    public boolean obtenerTemaOscuro() {
        return sharedPreferences.getBoolean(KEY_TEMA_OSCURO, false);
    }

    public void guardarIdioma(String idioma) {
        sharedPreferences.edit().putString(IDIOMA, idioma).apply();
    }
    public String obtenerIdioma() {
        return sharedPreferences.getString(IDIOMA, "es");
    }

    public SharedPreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
    public boolean obtenerNombreBool(){
        return sharedPreferences.getBoolean(NOMBRE_BOOL,false);
    }

}