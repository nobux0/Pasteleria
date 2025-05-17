package com.example.pasteleria.main.misc;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pasteleria.databinding.ViewholderReciboBinding;
import com.example.pasteleria.main.collections.ProductoPedido;
import com.example.pasteleria.main.viewmodel.ReciboViewModel;

import java.util.List;

public class ReciboAdapter extends RecyclerView.Adapter<ReciboAdapter.ReciboViewHolder> {
    private List<ProductoPedido> productos;
    private ReciboViewModel reciboViewModel;

    public ReciboAdapter(List<ProductoPedido> productos, ReciboViewModel reciboViewModel) {
        this.productos = productos;
        this.reciboViewModel = reciboViewModel;
    }

    public void setProductos(List<ProductoPedido> nuevosProductos) {
        this.productos = nuevosProductos;
    }

    @NonNull
    public ReciboViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderReciboBinding binding = ViewholderReciboBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ReciboViewHolder(binding);
    }
    @Override
    public void onBindViewHolder(@NonNull ReciboViewHolder holder, int position) {
        ProductoPedido producto = productos.get(position);
        holder.binding.nombretv.setText(producto.getNombre());
        holder.binding.cantidadtv.setText(String.valueOf(producto.getCantidad())+" X");
        holder.binding.preciotv.setText(producto.getPrecioUnitario()+"€");
    }

    public int getItemCount() {
        return productos.size();
    }

    public class ReciboViewHolder extends RecyclerView.ViewHolder {
        private final ViewholderReciboBinding binding;

        public ReciboViewHolder(ViewholderReciboBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }
}


