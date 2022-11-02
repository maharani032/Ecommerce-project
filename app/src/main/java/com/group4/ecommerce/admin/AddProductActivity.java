package com.group4.ecommerce.admin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.group4.ecommerce.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddProductActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> activityResultLauncherForUploadImage;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    DatabaseReference reference;
    FirebaseDatabase database;
    TextView titlenavbar;
    Spinner kategoriProduct,kategoriItem,filter;
    ProgressBar pb;
    CircleImageView cl;
    Button btn;
    ImageView goback;
    EditText namaProduct,idProduct,kuantitas,harga,deskripsi;
    RelativeLayout fieldKategoriItem,fieldFilter;

    boolean imageControl=false;
    Uri selectedImage;

    @Override
    protected void onStart() {
        super.onStart();
        Intent i=getIntent();
        String id=i.getStringExtra("id");

        if(id!=null){
            getData();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        RegisterActivityForUploadImage();
        kategoriProduct=findViewById(R.id.input_kategori_product);
        kategoriItem=findViewById(R.id.input_kategori_item);
        filter=findViewById(R.id.input_filter);
        titlenavbar=findViewById(R.id.title_navbar);
        fieldKategoriItem=findViewById(R.id.field_kategori_item);
        fieldFilter=findViewById(R.id.field_filter);

        namaProduct=findViewById(R.id.input_name);
        idProduct=findViewById(R.id.input_productId);
        kuantitas=findViewById(R.id.input_kuantitas);
        harga=findViewById(R.id.input_harga);
        deskripsi=findViewById(R.id.input_deskripsi);
        btn=findViewById(R.id.btnaddProduct);
        cl=findViewById(R.id.productImage);
        pb=findViewById(R.id.pb);
        goback=findViewById(R.id.goback);
        ProductKategori();

        firebaseStorage= FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();
        database= FirebaseDatabase.getInstance();
        reference= database.getReference();

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
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
        cl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePhoto();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pb.setVisibility(View.VISIBLE);
                String id=idProduct.getText().toString();
                Intent i=getIntent();
                String OldId=i.getStringExtra("id");

                String nameProduct=namaProduct.getText().toString();
                String inputCategori=kategoriProduct.getSelectedItem().toString();
                String inputDeskripsi=deskripsi.getText().toString();
                String kuantitasProduct=kuantitas.getText().toString();
                String hargaProduct=harga.getText().toString();
                String inputFilter=(inputCategori.equals("Book")||
                        inputCategori.equals("Other")?
                        "-"
                        :filter.getSelectedItem().toString());
                String inputCategoriItem=(
                        inputCategori.equals("Book")|| inputCategori.equals("Other")
                                ? "-"
                                :kategoriItem.getSelectedItem().toString());

                if(imageControl==false ||
                        selectedImage==null||
                        id==null||
                        nameProduct==null||
                        inputCategori==null||
                        hargaProduct==null||
                        kuantitasProduct==null||
                        inputDeskripsi==null){
                    pb.setVisibility(View.INVISIBLE);
                    Toast.makeText(AddProductActivity.this
                            ,"harus ada gambar, nama product,kategori,kuantitas,harga,dekripsi barang"
                            ,Toast.LENGTH_SHORT).show();
                    return;
                }

                if(OldId==null){
                    reference.child("Products").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                Toast.makeText(AddProductActivity.this, "id telah digunakan", Toast.LENGTH_SHORT).show();
                                pb.setVisibility(View.INVISIBLE);
                                return;
                            }else {
                                uploadFoto(id,nameProduct,inputCategori,kuantitasProduct,hargaProduct,inputFilter
                                        ,inputCategoriItem,inputDeskripsi);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else if(OldId!=null){
                    uploadFoto(id,nameProduct,inputCategori,kuantitasProduct,hargaProduct,inputFilter
                            ,inputCategoriItem,inputDeskripsi);
                }
                }


            });
    }
    private void choosePhoto() {
        if (ContextCompat.checkSelfPermission(AddProductActivity.this
                , Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(AddProductActivity.this
                    ,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else{

            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncherForUploadImage.launch(i);
        }
    }
    public void uploadFoto(String id,String nama,String kategori, String jumlah,
                           String hargabarang,String filter,String itemkategori,String deskripsi
    ) {
            Log.i("update",selectedImage.toString());
//            kalau sudah upload file ke firebase
            if(selectedImage.toString().contains("https://firebasestorage.googleapis.com"))
            {
                    Log.i("update","gambar tidak upload ulang");
                    HashMap<String, Map<String, String>> hashMap = new HashMap<>();
                    hashMap.put("timestamp", ServerValue.TIMESTAMP);
                    Intent i=getIntent();
                    i.putExtra("id", id);
                    i.putExtra("image", selectedImage.toString());
                    i.putExtra("name",nama);
                    i.putExtra("harga",hargabarang);
                    i.putExtra("kategori",kategori);
                    i.putExtra("kategoriItem",itemkategori);
                    i.putExtra("filter",filter);
                    i.putExtra("jumlah",jumlah);
                    i.putExtra("description",deskripsi);
                    i.putExtra("timestamp", hashMap);

                    setResult(RESULT_OK, i);
                    finish();

            }
            else {
                Log.i("update","upload foto");
                String imageName = "product/" + "Product" + " - " + id + ".jpg";
                storageReference.child(imageName).putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        StorageReference myStorageRef = firebaseStorage.getReference(imageName);
                        myStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                HashMap<String, Map<String, String>> hashMap = new HashMap<>();
                                hashMap.put("timestamp", ServerValue.TIMESTAMP);
                                Intent i = new Intent();
                                i.putExtra("id", id);
                                i.putExtra("image", uri.toString());
                                i.putExtra("name",nama);
                                i.putExtra("harga",hargabarang);
                                i.putExtra("kategori",kategori);
                                i.putExtra("kategoriItem",itemkategori);
                                i.putExtra("filter",filter);
                                i.putExtra("jumlah",jumlah);
                                i.putExtra("description",deskripsi);
                                i.putExtra("timestamp", hashMap);
                                setResult(RESULT_OK, i);
                                finish();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pb.setVisibility(View.INVISIBLE);
                        Toast.makeText(AddProductActivity.this, "upload gambar gagal", Toast.LENGTH_LONG).show();
                    }
                });
            }
    }
    public void getData(){
        Intent intent=getIntent();
        String id=intent.getStringExtra("id");
        String image=intent.getStringExtra("image");
        String nProduct=intent.getStringExtra("name");
        String hProduct=intent.getStringExtra("harga");
        String kProduct=intent.getStringExtra("kategori");
        String kItem=intent.getStringExtra("kategoriItem");
        String filter=intent.getStringExtra("filter");
        String jumlah=intent.getStringExtra("jumlah");
        String dProduct=intent.getStringExtra("description");
        namaProduct.setText(nProduct);
        idProduct.setText(id);
        kuantitas.setText(jumlah);
        harga.setText(hProduct);
        deskripsi.setText(dProduct);
        Picasso.get().load(image).into(cl);
        selectedImage=Uri.parse(image);
        idProduct.setFocusable(false);
        imageControl=true;
        titlenavbar.setText("Update Product");
        btn.setText("Update Product");
        getProductKategori(kProduct);
        if(kProduct.equals("Clothing")){
            ItemKategori("clothing");
            getItemKategori("clothing",kItem);
            getFilterItemKategori(filter);
            fieldFilter.setVisibility(View.VISIBLE);
            fieldKategoriItem.setVisibility(View.VISIBLE);
        }
        else if(kProduct.equals("Electronic")){
            getItemKategori("electronic",kItem);
            fieldKategoriItem.setVisibility(View.VISIBLE);
            fieldFilter.setVisibility(View.GONE);
        }
        else{
            fieldKategoriItem.setVisibility(View.GONE);
            fieldFilter.setVisibility(View.GONE);
        }

    }
    public void ProductKategori(){

        ArrayAdapter<CharSequence> kategoriProductAdapter = ArrayAdapter
                .createFromResource(this, R.array.kategori_item,
                        android.R.layout.simple_spinner_item);

        kategoriProductAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kategoriProduct.setAdapter(kategoriProductAdapter);
    }
    public void getProductKategori(String x){
        ArrayAdapter<CharSequence> kategoriProductAdapter = ArrayAdapter
                .createFromResource(this, R.array.kategori_item,
                        android.R.layout.simple_spinner_item);
        kategoriProductAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kategoriProduct.setAdapter(kategoriProductAdapter);
        int spinnerPosition = kategoriProductAdapter.getPosition(x);
        kategoriProduct.setSelection(spinnerPosition);
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
    public void getItemKategori(String item,String value){
        if(item.equals("clothing")){
            ArrayAdapter<CharSequence> kategoriProductAdapter = ArrayAdapter
                    .createFromResource(this, R.array.kategori_clothing,
                            android.R.layout.simple_spinner_item);

            kategoriProductAdapter
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            kategoriItem.setAdapter(kategoriProductAdapter);
            int spinnerPosition = kategoriProductAdapter.getPosition(value);
            kategoriItem.setSelection(spinnerPosition);
        }
        else if(item.equals("electronic")){
            ArrayAdapter<CharSequence> kategoriProductAdapter = ArrayAdapter
                    .createFromResource(this, R.array.kategori_electonic,
                            android.R.layout.simple_spinner_item);

            kategoriProductAdapter
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            kategoriItem.setAdapter(kategoriProductAdapter);
            int spinnerPosition = kategoriProductAdapter.getPosition(value);
            kategoriItem.setSelection(spinnerPosition);
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
    public void getFilterItemKategori(String value){
        ArrayAdapter<CharSequence> kategoriProductAdapter = ArrayAdapter
                .createFromResource(this, R.array.filter_clothing,
                        android.R.layout.simple_spinner_item);

        kategoriProductAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filter.setAdapter(kategoriProductAdapter);
        int spinnerPosition = kategoriProductAdapter.getPosition(value);
        filter.setSelection(spinnerPosition);
    }
    public void RegisterActivityForUploadImage(){
        activityResultLauncherForUploadImage= registerForActivityResult(new ActivityResultContracts
                .StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                int resultCode=result.getResultCode();
                Intent data=result.getData();

                if (resultCode==RESULT_OK && data!=null){
                    selectedImage=data.getData();
                    Picasso.get().load(selectedImage).into(cl);
                    imageControl=true;
                }else{
                    imageControl=false;
                }

            }
        });
    }

}