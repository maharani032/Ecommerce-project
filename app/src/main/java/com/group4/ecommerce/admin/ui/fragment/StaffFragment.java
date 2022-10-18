package com.group4.ecommerce.admin.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.group4.ecommerce.admin.AddStaffActivity;
import com.group4.ecommerce.databinding.FragmentStaffBinding;

public class StaffFragment extends Fragment {
    FloatingActionButton fabStaff;
    RecyclerView rvStaff;
    private FragmentStaffBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentStaffBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        fabStaff=binding.fabStaff;
        rvStaff=binding.rvStaff;

        fabStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addStaff=new Intent(getActivity(), AddStaffActivity.class);
                startActivity(addStaff);
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}