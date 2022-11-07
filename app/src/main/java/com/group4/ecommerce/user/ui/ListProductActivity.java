package com.group4.ecommerce.user.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group4.ecommerce.R;
import com.group4.ecommerce.adapter.ProductUserAdapter;
import com.group4.ecommerce.model.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListProductActivity extends AppCompatActivity {
    TextView navbar;
    ImageView goback;
    RecyclerView rv;
    FrameLayout fabFilter;
    RelativeLayout fieldKategoriItem,fieldFilter;
    TextView save,cancel;
    Spinner kategoriItem,filter;
    List<Product> list=new ArrayList<>();
    ProductUserAdapter adapter;


    DatabaseReference reference;
    FirebaseDatabase database;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    @Override
    protected void onStart() {
        super.onStart();
        Intent i=getIntent();
        if(i.getStringExtra("navbarName")!=null){
            navbar.setText(i.getStringExtra("navbarName"));
        }
        if (i.getStringExtra("type").equals("Book")||i.getStringExtra("type").equals("other")
                ||i.getStringExtra("type").equals("all")){
            fabFilter.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product);
        database= FirebaseDatabase.getInstance();
        reference= database.getReference();
        firebaseStorage= FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();


        navbar=findViewById(R.id.navbarText);
        goback=findViewById(R.id.goback);
        fabFilter=findViewById(R.id.fabFilter);

        fabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popUpFilter(view);
            }
        });
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        rv=findViewById(R.id.rvProduct);
        rv.setLayoutManager(new GridLayoutManager(ListProductActivity.this,2));
        list=new ArrayList<>();
        adapter=new ProductUserAdapter(list,ListProductActivity.this);
        adapter.setOnItemClickListener(new ProductUserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                Intent i=new Intent(ListProductActivity.this,DetailProductActivity.class);
                i.putExtra("name",product.getName());
                i.putExtra("image",product.getImage());
                i.putExtra("jumlah",product.getJumlah());
                i.putExtra("harga",product.getHarga());
                i.putExtra("filter",product.getFilter());
                i.putExtra("kategori",product.getKategori());
                i.putExtra("id",product.getId());
                i.putExtra("timestamp",product.getTimestamp().toString());
                i.putExtra("kategoriItem",product.getKategoriItem());
                i.putExtra("description",product.getDescription());
                startActivity(i);
            }
        });
        rv.setAdapter(adapter);
        Intent i=getIntent();
        if(i.getStringExtra("type")!=null &&i.getStringExtra("type").equals("all")){
            reference.child("Products").orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
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
        else if(i.getStringExtra("type")!=null){
            reference.child("Products").orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    for (DataSnapshot sp:snapshot.getChildren()){
                       Product product=sp.getValue(Product.class);
                       if(product.getKategori().equals(i.getStringExtra("type"))){
                           list.add(product);
                       }
                    }
//                    reverse dari terbaru
                    Collections.reverse(list);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
    void popUpFilter(View view){
        LayoutInflater inflater=(LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View FilterPopUp=inflater.inflate(R.layout.filter_kategori,null);
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(FilterPopUp, width, height, focusable);
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        Intent i=getIntent();
        String kategori=i.getStringExtra("type");

        kategoriItem=FilterPopUp.findViewById(R.id.input_kategori_item); //spinner kategoriITEM
        filter=FilterPopUp.findViewById(R.id.input_filter); // spinner filter
        fieldFilter=FilterPopUp.findViewById(R.id.field_filter); //field filter
        save=FilterPopUp.findViewById(R.id.save_button);
        cancel=FilterPopUp.findViewById(R.id.cancel_button);
        fieldFilter.setVisibility(View.INVISIBLE);

        if(kategori!=null && kategori.equals("Clothing")){
            fieldFilter.setVisibility(View.VISIBLE);
            ArrayAdapter<CharSequence> kategoriProductAdapter = ArrayAdapter
                    .createFromResource(this, R.array.kategori_clothing,
                            android.R.layout.simple_spinner_item);

            kategoriProductAdapter
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            kategoriItem.setAdapter(kategoriProductAdapter);

            ArrayAdapter<CharSequence> filterArrayAdapter = ArrayAdapter
                    .createFromResource(this, R.array.filter_clothing,
                            android.R.layout.simple_spinner_item);

            filterArrayAdapter
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            filter.setAdapter(filterArrayAdapter);
        }
        else  if(kategori!=null && kategori.equals("Electronic")){
            fieldFilter.setVisibility(View.INVISIBLE);
            ArrayAdapter<CharSequence> kategoriProductAdapter = ArrayAdapter
                    .createFromResource(this, R.array.kategori_electonic,
                            android.R.layout.simple_spinner_item);

            kategoriProductAdapter
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            kategoriItem.setAdapter(kategoriProductAdapter);
        }
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Kitem=kategoriItem.getSelectedItem().toString();
                String kFilter=filter.getSelectedItem().toString();
                reference.child("Products").orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot sp:snapshot.getChildren()){
                            Product product=sp.getValue(Product.class);
                            if(product.getKategori().equals(kategori)){
                                if(Kitem!=null && product.getKategoriItem().equals(Kitem)
                                    && kFilter==null
                                ){
                                    list.add(product);
                                }
                                else if(Kitem!=null && product.getKategoriItem().equals(Kitem)
                                        && kFilter!=null &&product.getFilter().equals(kFilter)){
                                    list.add(product);

                                }
                            }

                        }
//                reverse dari terbaru
                        Collections.reverse(list);
                        adapter.notifyDataSetChanged();
                        popupWindow.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
//        adapter spinner
    }
    //        adapter spinner

}