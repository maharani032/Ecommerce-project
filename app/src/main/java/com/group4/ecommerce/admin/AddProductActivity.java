package com.group4.ecommerce.admin;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group4.ecommerce.R;

public class AddProductActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;


    Spinner kategoriProduct,kategoriItem,filter;
    Button btn;
    EditText namaProduct,idProduct,kuantitas,harga;
    RelativeLayout fieldKategoriItem,fieldFilter;

    boolean imageControl=false,checkEmailStaff=false;
    Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        kategoriProduct=findViewById(R.id.input_kategori_product);
        kategoriItem=findViewById(R.id.input_kategori_item);
        filter=findViewById(R.id.input_filter);

        fieldKategoriItem=findViewById(R.id.field_kategori_item);
        fieldFilter=findViewById(R.id.field_filter);

        namaProduct=findViewById(R.id.input_name);
        idProduct=findViewById(R.id.input_productId);
        kuantitas=findViewById(R.id.input_kuantitas);
        harga=findViewById(R.id.input_harga);
        btn=findViewById(R.id.btnaddProduct);
        ProductKategori();
        kategoriProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.v("item", (String) adapterView.getItemAtPosition(i));
                if(adapterView.getItemAtPosition(i).equals("Clothing")){
                    ItemKategori("clothing");
                    FilterItemKategori();
                    fieldFilter.setVisibility(View.VISIBLE);
                    fieldKategoriItem.setVisibility(View.VISIBLE);
                }
                else if(adapterView.getItemAtPosition(i).equals("Electronic")){
                    ItemKategori("electronic");
                    fieldKategoriItem.setVisibility(View.VISIBLE);
                    fieldFilter.setVisibility(View.GONE);
                }
                else{
                    fieldKategoriItem.setVisibility(View.GONE);
                    fieldFilter.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(AddProductActivity.this, "harus dipilih", Toast.LENGTH_SHORT).show();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id=idProduct.getText().toString();
                String nameProduct=namaProduct.getText().toString();
                String inputCategori=kategoriProduct.getSelectedItem().toString();
                String inputFilter=(inputCategori.equals("Book")||
                        inputCategori.equals("Other")?
                        "null"
                        :filter.getSelectedItem().toString()
                        );
                String inputCategoriItem=(
                        inputCategori.equals("Book")||
                                kategoriProduct.getSelectedItem().toString().equals("Other")?
                                "null"
                                :kategoriItem.getSelectedItem().toString()
                        );


            }
        });




    }
    public void ProductKategori(){

        ArrayAdapter<CharSequence> kategoriProductAdapter = ArrayAdapter
                .createFromResource(this, R.array.kategori_item,
                        android.R.layout.simple_spinner_item);

        kategoriProductAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kategoriProduct.setAdapter(kategoriProductAdapter);
    }
    public void ItemKategori(String item){
        if(item.equals("clothing")){
            ArrayAdapter<CharSequence> kategoriProductAdapter = ArrayAdapter
                    .createFromResource(this, R.array.kategori_clothing,
                            android.R.layout.simple_spinner_item);

            kategoriProductAdapter
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            kategoriItem.setAdapter(kategoriProductAdapter);
        }
        else if(item.equals("electronic")){
            ArrayAdapter<CharSequence> kategoriProductAdapter = ArrayAdapter
                    .createFromResource(this, R.array.kategori_electonic,
                            android.R.layout.simple_spinner_item);

            kategoriProductAdapter
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            kategoriItem.setAdapter(kategoriProductAdapter);
        }

    }
    public void FilterItemKategori(){
        ArrayAdapter<CharSequence> kategoriProductAdapter = ArrayAdapter
                .createFromResource(this, R.array.filter_clothing,
                        android.R.layout.simple_spinner_item);

        kategoriProductAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filter.setAdapter(kategoriProductAdapter);
    }


}