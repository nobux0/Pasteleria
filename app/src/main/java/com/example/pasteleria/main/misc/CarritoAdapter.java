package com.example.pasteleria.main.misc;
import android.app.Application;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pasteleria.R;
import com.example.pasteleria.databinding.ViewholderCarritoBinding;
import com.example.pasteleria.databinding.ViewholderCategoriaBinding;
import com.example.pasteleria.main.collections.Producto;
import com.example.pasteleria.main.collections.ProductoPedido;
import com.example.pasteleria.main.model.ProductoRepository;
import com.example.pasteleria.main.viewmodel.CarritoViewModel;
import com.example.pasteleria.main.viewmodel.ProductoViewModel;
import com.example.pasteleria.main.viewmodel.ReciboViewModel;
import java.util.List;

public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder> {
    private List<ProductoPedido> productos;
    private List<Producto> productosP;
    private CarritoViewModel carritoViewModel;
    private ReciboViewModel reciboViewModel;

    public CarritoAdapter(List<ProductoPedido> productos,List<Producto> productosP, CarritoViewModel carritoViewModel, ReciboViewModel reciboViewModel) {
        this.productos = productos;
        this.productosP = productosP;
        this.carritoViewModel = carritoViewModel;
        this.reciboViewModel =  reciboViewModel;
    }
    public void setProductosP(List<Producto> nuevosProductosP) {
        this.productosP = nuevosProductosP;
        notifyDataSetChanged();
    }
    public void setProductos(List<ProductoPedido> nuevosProductos) {
        this.productos = nuevosProductos;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CarritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewholderCarritoBinding binding = ViewholderCarritoBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CarritoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CarritoViewHolder holder, int position) {
        Producto productoP = productosP.get(position);
        ProductoPedido producto = productos.get(position);
        holder.binding.nombretv.setText(producto.getNombre());
        holder.binding.cantidadTv.setText(String.valueOf(producto.getCantidad()));
        String total = String.valueOf(carritoViewModel.obtenerTotalProducto(producto))+"€";
        holder.binding.precioTextView.setText(total);
        if(productoP.getImagenUrl() != null){
            Glide.with(holder.itemView.getContext())
                    .load(productoP.getImagenUrl())
                    .into(holder.binding.productoCarritoTV);
        }

        holder.binding.papeleraButton.setOnClickListener(v-> {reciboViewModel.eliminarProducto(producto);
            carritoViewModel.eliminarProducto(producto);
        });
        holder.binding.anadirButton.setOnClickListener(v -> {carritoViewModel.agregarProducto(producto);
            reciboViewModel.agregarProducto(producto);
        });
        holder.binding.restarButton.setOnClickListener(v -> {carritoViewModel.restarProducto(producto);
            reciboViewModel.restarProducto(producto);
        });
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    public class CarritoViewHolder extends RecyclerView.ViewHolder {
        private final ViewholderCarritoBinding binding;

        public CarritoViewHolder(ViewholderCarritoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

    }
}