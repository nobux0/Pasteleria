package com.example.pasteleria.main.misc;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pasteleria.R;
import com.example.pasteleria.databinding.FragmentItemsBinding;
import com.example.pasteleria.databinding.ViewholderCategoriaBinding;
import com.example.pasteleria.databinding.ViewholderProductoBinding;
import com.example.pasteleria.main.collections.Producto;
import com.example.pasteleria.main.viewmodel.CarritoViewModel;
import com.example.pasteleria.main.viewmodel.ProductoViewModel;
import com.example.pasteleria.main.viewmodel.ReciboViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_CATEGORIA = 0;
    private static final int TYPE_PRODUCTO = 1;
    private boolean busqueda = false;
    private boolean precio = false;
    private boolean stock = false;
    private LifecycleOwner lifecycleOwner;
    private List<Object> items;
    private Map<String, Boolean> categoriasExpandibles;
    List<Producto> listaProductos;
    CarritoViewModel carritoViewModel;
    ReciboViewModel reciboViewModel;
    ProductoViewModel productoViewModel;
    NavController navController;
    private SharedPreferencesHelper helper;
    private FirebaseAuth mAuth;

    public ProductosAdapter(Context context, NavController navController, ProductoViewModel productoViewModel,ReciboViewModel reciboViewModel, CarritoViewModel carritoViewModel, LifecycleOwner lifecycleOwner) {
        this.navController = navController;
        this.carritoViewModel = carritoViewModel;
        this.productoViewModel = productoViewModel;
        this.reciboViewModel = reciboViewModel;
        this.helper = new SharedPreferencesHelper(context);
        this.items = new ArrayList<>();
        this.categoriasExpandibles = new HashMap<>();
        this.lifecycleOwner = lifecycleOwner;

        productoViewModel.getStock().observe(lifecycleOwner, isChecked -> {
            stock = isChecked;
            if (busqueda) {
                establecerListaProductos(listaProductos);
            }
        });

        productoViewModel.getBusqueda().observe(lifecycleOwner, isChecked -> {
            busqueda = isChecked;
            establecerListaProductos(listaProductos);
        });

        productoViewModel.getPrecio().observe(lifecycleOwner, isChecked -> {
            precio = isChecked;
            if (busqueda) {
                establecerListaProductos(listaProductos);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return (items.get(position) instanceof String) ? TYPE_CATEGORIA : TYPE_PRODUCTO;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_CATEGORIA) {
            ViewholderCategoriaBinding binding = ViewholderCategoriaBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new CategoriaViewHolder(binding);
        } else {
            ViewholderProductoBinding binding = ViewholderProductoBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new ProductoViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CategoriaViewHolder) {
            String categoria = (String) items.get(position);
            CategoriaViewHolder categoriaHolder = (CategoriaViewHolder) holder;
            categoriaHolder.bind(categoria);
        } else {
            Producto producto = (Producto) items.get(position);
            ProductoViewHolder productoHolder = (ProductoViewHolder) holder;
            productoHolder.bind(producto);
        }
    }

    private void toggleCategoria(String categoria) {
        int index = items.indexOf(categoria);
        int count = 0;
        if (categoriasExpandibles.get(categoria)) {
            while (index + 1 < items.size() && items.get(index + 1) instanceof Producto) {
                items.remove(index + 1);
                count ++;
            }
            notifyItemRangeRemoved(index + 1,count);
        } else {
            productoViewModel.obtenerProductosPorCategoria(categoria).observe(lifecycleOwner, productos -> {
                items.addAll(index + 1, productos);
                notifyItemRangeInserted(index + 1, productos.size());
            });
        }
        categoriasExpandibles.put(categoria, !categoriasExpandibles.get(categoria));
    }

    private void navegarPantallaDetalle(Producto producto, int currentFragmentId) {
        Bundle args = new Bundle();
        args.putParcelable("producto",producto);
        if (currentFragmentId == R.id.itemsFragment) {
            navController.navigate(R.id.action_itemsFragment_to_detailsFragment, args);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public Producto obtenerProducto(int posicion) {
        return listaProductos.get(posicion);
    }

    public void establecerListaProductos(List<Producto> listaProductos) {

        if (listaProductos != null && !listaProductos.isEmpty()) {
            this.listaProductos = listaProductos;
            items.clear();
            categoriasExpandibles.clear();

            List<Producto> productosFiltrados = new ArrayList<>();

            for (Producto producto : listaProductos) {
                if (stock) {
                    if (producto.getStock() > 0) {
                        productosFiltrados.add(producto);
                    }
                }else {
                productosFiltrados.add(producto);
                }
            }
            if (busqueda) {
                //burbuja
                for (int i = 0; i < productosFiltrados.size(); i++) {
                    for (int j = 0; j < productosFiltrados.size() - i - 1; j++) {
                        Producto p1 = productosFiltrados.get(j);
                        Producto p2 = productosFiltrados.get(j + 1);
                        if (precio) {
                            if (p1.getPrecio() > p2.getPrecio()) {
                                productosFiltrados.set(j, p2);
                                productosFiltrados.set(j + 1, p1);
                            }
                        } else {
                            if (p1.getPrecio() < p2.getPrecio()) {
                                productosFiltrados.set(j, p2);
                                productosFiltrados.set(j + 1, p1);
                            }
                        }
                    }
                }
                items.addAll(productosFiltrados);
            } else {
                Map<String, List<Producto>> productosPorCategoria = new HashMap<>();
                for (Producto producto : productosFiltrados) {
                    productosPorCategoria
                            //esta funcion revisa si la clave (categoria en este caso) ya existe en el mapa,
                            //en caso de no existir crea una nueva lista para esa categoria, si ya existe
                            //simplemente la devuelve
                            .computeIfAbsent(producto.getCategoria(), k -> new ArrayList<>()).add(producto);
                }
                for (Map.Entry<String, List<Producto>> entry : productosPorCategoria.entrySet()) {
                    items.add(entry.getKey());
                    categoriasExpandibles.put(entry.getKey(), false);
                }
            }
            //mejora visual solo
            notifyItemRangeInserted(0, items.size());
            notifyDataSetChanged();

        }
    }


    class ProductoViewHolder extends RecyclerView.ViewHolder {
        private final ViewholderProductoBinding binding;
        public ProductoViewHolder(ViewholderProductoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        void bind(Producto producto) {
            binding.nombretv.setText(producto.getNombre());
            binding.categoriatv.setText(producto.getCategoria());
            Log.d("productoid", "idProducto: " + producto.getId());
            binding.aAdir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    carritoViewModel.obtenerProductoYAgregarAlCarrito(producto.getId());
                    reciboViewModel.obtenerProductoYAgregarAlRecibo(producto.getId());
                }
            });
            int currentFragmentId = navController.getCurrentDestination().getId();
            binding.getRoot().setOnClickListener(view -> navegarPantallaDetalle(producto, currentFragmentId));
        }
    }

    class CategoriaViewHolder extends RecyclerView.ViewHolder {
        private final ViewholderCategoriaBinding binding;

        public CategoriaViewHolder(ViewholderCategoriaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(String categoria) {
            binding.categoriaTv.setText(categoria);
            binding.getRoot().setOnClickListener(v -> {
                binding.getRoot().setEnabled(false);
                toggleCategoria(categoria);
                //tengo el switch del click izquierdo roto
                v.postDelayed(() -> binding.getRoot().setEnabled(true), 500);
            });
        }
    }
}
