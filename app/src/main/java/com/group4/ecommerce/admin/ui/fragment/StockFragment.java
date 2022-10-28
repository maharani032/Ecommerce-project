package com.group4.ecommerce.admin.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
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
import com.group4.ecommerce.admin.AddProductActivity;
import com.group4.ecommerce.databinding.FragmentStockBinding;

public class StockFragment extends Fragment {
    ActivityResultLauncher<Intent> activityResultLauncherForAddProduct;
    private FragmentStockBinding binding;
    RecyclerView rv;

    DatabaseReference reference;
    FirebaseDatabase database;

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

    }

    public void RegisterActivityForAddProduct() {
        activityResultLauncherForAddProduct= registerForActivityResult(new ActivityResultContracts.StartActivityForResult()
                , new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        int ResultCode=result.getResultCode();
                        Intent data=result.getData();
//                        if(ResultCode==RESULT_OK && data !=null){
//                            Log.i("data product fragment",data.toString());
//                            String id=data.getStringExtra("id");
//                            String fullname=data.getStringExtra("fullname");
//                            String image=data.getStringExtra("image");
//                            String email=data.getStringExtra("email");
//                            if(id!=null && fullname!=null && image!=null && email!=null){
//                                reference.child("Staffs").child(id).child("fullname").setValue(fullname);
//                                reference.child("Staffs").child(id).child("image").setValue(image);
//                                reference.child("Staffs").child(id).child("email").setValue(email);
//                                notify();
//                            }
//                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}