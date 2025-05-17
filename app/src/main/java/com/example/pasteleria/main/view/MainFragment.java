package com.example.pasteleria.main.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pasteleria.R;
import com.example.pasteleria.databinding.FragmentLoginBinding;
import com.example.pasteleria.databinding.FragmentMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainFragment extends Fragment {
    private FragmentMainBinding binding;
    private FirebaseAuth mAuth;
    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }
}