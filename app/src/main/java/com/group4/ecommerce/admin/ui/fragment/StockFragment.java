package com.group4.ecommerce.admin.ui.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group4.ecommerce.admin.AddProductActivity;
import com.group4.ecommerce.databinding.FragmentStockBinding;

public class StockFragment extends Fragment {
    ActivityResultLauncher<Intent> activityResultLauncherForAddProduct;
    private FragmentStockBinding binding;
    RecyclerView rv;

    DatabaseReference reference;
    FirebaseDatabase database;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    FloatingActionButton fabProduct;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentStockBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        fabProduct=binding.fabProduct;
        fabProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addProduct=new Intent(getActivity(), AddProductActivity.class);
                activityResultLauncherForAddProduct.launch(addProduct);
            }
        });
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RegisterActivityForAddProduct();

        database= FirebaseDatabase.getInstance();
        reference= database.getReference();
        firebaseStorage= FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();

    }

    public void RegisterActivityForAddProduct() {
        activityResultLauncherForAddProduct= registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
                , new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        int ResultCode=result.getResultCode();
                        Intent data=result.getData();
                        Log.i("product","tambah product");
                        if(ResultCode==RESULT_OK && data !=null){
                            String id=data.getStringExtra("id");
                            String image=data.getStringExtra("image");
                            String nama=data.getStringExtra("nama");
                            String harga=data.getStringExtra("harga");
                            String kbarang=data.getStringExtra("kategori barang");
                            String kitem=data.getStringExtra("kategori item");
                            String filter=data.getStringExtra("filter");
                            String kuantitas=data.getStringExtra("kuantitas");

                            if(id!=null||image!=null||nama!=null||harga!=null||kbarang!=null||kitem!=null||filter!=null||kuantitas!=null){
                                reference.child("Products").child(id).child("id").setValue(id);
                                reference.child("Products").child(id).child("nama product").setValue(nama);
                                reference.child("Products").child(id).child("kategori barang").setValue(kbarang);
                                reference.child("Products").child(id).child("kategori item").setValue(kitem);
                                reference.child("Products").child(id).child("harga barang").setValue(harga);
                                reference.child("Products").child(id).child("kuantitas barang").setValue(kuantitas);
                                reference.child("Products").child(id).child("filter barang").setValue(filter);
                            }
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}