package com.example.pasteleria.main.collections;

public class Anuncio {
    private final int imageRes;
    private final String texto;

    public Anuncio(int imageRes, String texto) {
        this.imageRes = imageRes;
        this.texto = texto;
    }

    public int getImageRes() {
        return imageRes;
    }

    public String getTexto() {
        return texto;
    }
}
