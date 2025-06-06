package com.example.pasteleria.main.misc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pasteleria.R;
import com.example.pasteleria.main.collections.Anuncio;

import java.util.List;

public class AnuncioAdapter extends RecyclerView.Adapter<AnuncioAdapter.AnuncioViewHolder> {

    private final List<Anuncio> anuncios;
    private final OnAnuncioClickListener listener;

    public interface OnAnuncioClickListener {
        void onClick(Anuncio anuncio);
    }

    public AnuncioAdapter(List<Anuncio> anuncios, OnAnuncioClickListener listener) {
        this.anuncios = anuncios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AnuncioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_anuncio, parent, false);
        return new AnuncioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnuncioViewHolder holder, int position) {
        Anuncio anuncio = anuncios.get(position);
        holder.bind(anuncio);
        holder.itemView.setOnClickListener(v -> listener.onClick(anuncio));
    }

    @Override
    public int getItemCount() {
        return anuncios.size();
    }

    static class AnuncioViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView text;

        AnuncioViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgAnuncio);
            text = itemView.findViewById(R.id.textAnuncio);
        }

        void bind(Anuncio anuncio) {
            img.setImageResource(anuncio.getImageRes());
            text.setText(anuncio.getTexto());
        }
    }
}