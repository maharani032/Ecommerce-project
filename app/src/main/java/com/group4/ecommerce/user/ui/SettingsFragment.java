package com.group4.ecommerce.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.group4.ecommerce.WelcomeActivity;
import com.group4.ecommerce.databinding.FragmentSettingsBinding;
import com.group4.ecommerce.preferences;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    LinearLayout SignOutLayout;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        SignOutLayout=binding.SignOutLayout;
        SignOutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(getContext(), WelcomeActivity.class);
                preferences.clearData(getContext());
                startActivity(i);
            }
        });
        return root;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}