package com.group4.ecommerce.admin.ui.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group4.ecommerce.admin.AddProductActivity;
import com.group4.ecommerce.adapter.ProductAdapter;
import com.group4.ecommerce.databinding.FragmentStockBinding;
import com.group4.ecommerce.model.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockFragment extends Fragment {
    ActivityResultLauncher<Intent> activityResultLauncherForAddProduct;
    ActivityResultLauncher<Intent> activityResultLauncherForUpdateProduct;
    private FragmentStockBinding binding;
    RecyclerView rv;

    DatabaseReference reference;
    FirebaseDatabase database;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    List<Product> list=new ArrayList<>();
    ProductAdapter productAdapter;

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
        RegisterActivityForUpdateProduct();

        database= FirebaseDatabase.getInstance();
        reference= database.getReference();
        firebaseStorage= FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();

        rv= binding.rvStock;
        rv.setLayoutManager(new LinearLayoutManager(requireActivity()));
        list=new ArrayList<>();
        productAdapter=new ProductAdapter(list,getContext());
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position=viewHolder.getAdapterPosition();
                reference.child("Products").child(list.get(position).getId()).removeValue();
                if(list.get(position)!=null&&!list.get(position).getImage().equals("String")){
                    String imageName = "product/Product"+ " - " + list.get(position).getId() + ".jpg";
                    storageReference.child(imageName).delete();
                }
                Toast.makeText(getContext(), "data has been delete", Toast.LENGTH_SHORT).show();

            }
        }).attachToRecyclerView(rv);
        rv.setAdapter(productAdapter);
        reference.child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot sp:snapshot.getChildren()){
                    Product product=sp.getValue(Product.class);
                    list.add(product);
                }
                productAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        productAdapter.setOnItemClickListenerUpdate(new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                reference.child(product.getId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Intent intent= new Intent(getActivity(), AddProductActivity.class);
                        intent.putExtra("id", product.getId());
                        intent.putExtra("image", product.getImage());
                        intent.putExtra("name",product.getName());
                        intent.putExtra("harga",product.getHarga());
                        intent.putExtra("kategori",product.getKategori());
                        intent.putExtra("kategoriItem",product.getKategoriItem());
                        intent.putExtra("filter",product.getFilter());
                        intent.putExtra("jumlah",product.getJumlah());
                        intent.putExtra("description",product.getDescription());
                        activityResultLauncherForUpdateProduct.launch(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

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
                            String nama=data.getStringExtra("name");
                            String harga=data.getStringExtra("harga");
                            String kbarang=data.getStringExtra("kategori");
                            String kitem=data.getStringExtra("kategoriItem");
                            String filter=data.getStringExtra("filter");
                            String kuantitas=data.getStringExtra("jumlah");
                            String deskripsi=data.getStringExtra("description");
//                            String timestamp=data.getStringExtra("timestamp");

                            HashMap<String, String> hashMap = (HashMap<String, String>)data.getSerializableExtra("timestamp");
                            Log.i("hashmap",hashMap.toString());
                            if(id!=null&&image!=null&&nama!=null&&harga!=null&&kbarang!=null&&
                                    kitem!=null&&filter!=null&&kuantitas!=null&&deskripsi!=null){
                                reference.child("Products").child(id).child("id").setValue(id);
                                reference.child("Products").child(id).child("name").setValue(nama);
                                reference.child("Products").child(id).child("kategori").setValue(kbarang);
                                reference.child("Products").child(id).child("kategoriItem").setValue(kitem);
                                reference.child("Products").child(id).child("harga").setValue(harga);
                                reference.child("Products").child(id).child("jumlah").setValue(kuantitas);
                                reference.child("Products").child(id).child("filter").setValue(filter);
                                reference.child("Products").child(id).child("image").setValue(image);
                                reference.child("Products").child(id).child("description").setValue(deskripsi);
                                reference.child("Products").child(id).child("timestamp").setValue(hashMap.get("timestamp"));
                            }
                        }
                    }
                });
    }
    public void RegisterActivityForUpdateProduct(){
        activityResultLauncherForUpdateProduct= registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        int ResultCode=result.getResultCode();
                        Intent data=result.getData();
                        if(ResultCode==RESULT_OK&&data!=null){
                            String id=data.getStringExtra("id");
                            String image=data.getStringExtra("image");
                            String nama=data.getStringExtra("name");
                            String harga=data.getStringExtra("harga");
                            String kbarang=data.getStringExtra("kategori");
                            String kitem=data.getStringExtra("kategoriItem");
                            String filter=data.getStringExtra("filter");
                            String kuantitas=data.getStringExtra("jumlah");
                            String deskripsi=data.getStringExtra("description");
                            String timestamp=data.getStringExtra("timestamp");

                            if(id!=null&&image!=null&&nama!=null&&harga!=null&&kbarang!=null&&
                                    kitem!=null&&filter!=null&&kuantitas!=null&&deskripsi!=null){
                                Map<String, Object> hasMap = new HashMap<>();
                                hasMap.put("id",id);
                                hasMap.put("name",nama);
                                hasMap.put("kategori",kbarang);
                                hasMap.put("kategoriItem",kitem);
                                hasMap.put("harga",harga);
                                hasMap.put("jumlah",kuantitas);
                                hasMap.put("filter",filter);
                                hasMap.put("image",image);
                                hasMap.put("description",deskripsi);
                                hasMap.put("timestamp",timestamp);

                                reference.child("Products").child(id).updateChildren(hasMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getContext(),"product telah diupdate",Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }

                    }
                }
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}