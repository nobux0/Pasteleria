package com.example.pasteleria.main.misc;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pasteleria.databinding.ViewholderDireccionBinding;
import com.example.pasteleria.main.collections.Direccion;

import java.util.List;

public class DireccionAdapter extends RecyclerView.Adapter<DireccionAdapter.ViewHolder> {
    private List<Direccion> direcciones;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Direccion direccion);
    }

    public DireccionAdapter(List<Direccion> direcciones, OnItemClickListener listener) {
        this.direcciones = direcciones;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewholderDireccionBinding binding;

        public ViewHolder(ViewholderDireccionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Direccion direccion, OnItemClickListener listener) {
            binding.tvNombre.setText(direccion.getNombre());
            binding.tvDireccion.setText(direccion.getCalle());
            binding.tvTelefono.setText(direccion.getTelefono());

            binding.getRoot().setOnClickListener(v -> listener.onItemClick(direccion));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewholderDireccionBinding binding = ViewholderDireccionBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(direcciones.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return direcciones.size();
    }
}
