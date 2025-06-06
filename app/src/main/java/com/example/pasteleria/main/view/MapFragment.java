package com.example.pasteleria.main.view;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.pasteleria.R;
import com.example.pasteleria.databinding.FragmentLoginBinding;
import com.example.pasteleria.databinding.FragmentMapBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.net.URISyntaxException;

public class MapFragment extends Fragment {
    private WebView webView;
    private FragmentMapBinding binding;
    private FirebaseAuth mAuth;
    private NavController navController;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return (binding = FragmentMapBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        webView = binding.webView;
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("intent://")) {
                    try {
                        Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        if (intent != null) {
                            startActivity(intent);
                            return true;
                        }
                    } catch (URISyntaxException | ActivityNotFoundException e) {
                        // Fallback: abre Play Store o página web
                        Intent fallback = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps"));
                        startActivity(fallback);
                        return true;
                    }
                } else if (url.startsWith("http") || url.startsWith("https")) {
                    view.loadUrl(url);
                    return true;
                }
                return false;
            }
        });
        webView.loadUrl("file:///android_asset/map.html");
        webView.setBackgroundColor(Color.WHITE);

    }

}