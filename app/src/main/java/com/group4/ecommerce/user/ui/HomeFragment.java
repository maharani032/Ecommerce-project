package com.group4.ecommerce.user.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group4.ecommerce.adapter.ProductUserAdapter;
import com.group4.ecommerce.databinding.FragmentHomeBinding;
import com.group4.ecommerce.model.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    RecyclerView rvProduct;
    List<Product> list=new ArrayList<>();
    ProductUserAdapter adapter;

    DatabaseReference reference;
    FirebaseDatabase database;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database= FirebaseDatabase.getInstance();
        reference= database.getReference();
        firebaseStorage= FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();

        rvProduct=binding.rvProduct;
        rvProduct.setLayoutManager(new GridLayoutManager(requireActivity(),2));
        list=new ArrayList<>();
        adapter=new ProductUserAdapter(list,getContext());
        rvProduct.setAdapter(adapter);

        reference.child("Products").orderByChild("timestamp").limitToLast(4).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot sp:snapshot.getChildren()){
                    Product product=sp.getValue(Product.class);
                    list.add(product);
                }
//                reverse dari terbaru
                Collections.reverse(list);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}