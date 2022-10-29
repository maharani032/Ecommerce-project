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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.group4.ecommerce.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddProductActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> activityResultLauncherForUploadImage;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;


    Spinner kategoriProduct,kategoriItem,filter;
    ProgressBar pb;
    CircleImageView cl;
    Button btn;
    EditText namaProduct,idProduct,kuantitas,harga;
    RelativeLayout fieldKategoriItem,fieldFilter;

    boolean imageControl=false,checkProduct=false;
    Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        RegisterActivityForUploadImage();
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
        cl=findViewById(R.id.productImage);
        pb=findViewById(R.id.pb);
        ProductKategori();

        firebaseStorage= FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();
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
                String nameProduct=namaProduct.getText().toString();
                String inputCategori=kategoriProduct.getSelectedItem().toString();
                String kuantitasProduct=kuantitas.getText().toString();
                String hargaProduct=harga.getText().toString();
                if(imageControl==false ||
                        selectedImage==null||
                        id==null||
                        nameProduct==null||
                        inputCategori==null||
                        hargaProduct==null||
                        kuantitasProduct==null){
                    pb.setVisibility(View.INVISIBLE);
                    Toast.makeText(AddProductActivity.this
                            ,"harus ada gambar, nama product,kategori,kuantitas barang"
                            ,Toast.LENGTH_SHORT).show();
                    return;
                }
                String inputFilter=(inputCategori.equals("Book")||
                        inputCategori.equals("Other")?
                        "-"
                        :filter.getSelectedItem().toString()
                        );
                String inputCategoriItem=(
                        inputCategori.equals("Book")||
                                inputCategori.equals("Other")?
                                "-"
                                :kategoriItem.getSelectedItem().toString()
                        );

                if(checkProduct){
                    pb.setVisibility(View.INVISIBLE);
                    Toast.makeText(AddProductActivity.this,"ada id yg sama",Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    uploadFoto(id,nameProduct,inputCategori,kuantitasProduct,hargaProduct,inputFilter,inputCategoriItem);
                }

            }
        });


    }
//    public void AddProduct(String id,String nama,String kategori,
//                           String jumlah,String hargabarang,String filter,String itemkategori){
//        pb.setVisibility(View.VISIBLE);
//        reference.child("Products").child(id).child("id").setValue(id);
//        reference.child("Products").child(id).child("nama").setValue(nama);
//        reference.child("Products").child(id).child("kategori barang").setValue(kategori);
//        reference.child("Products").child(id).child("kategori item").setValue(itemkategori);
//        reference.child("Products").child(id).child("harga barang").setValue(hargabarang);
//        reference.child("Products").child(id).child("kuantitas barang").setValue(jumlah);
//        reference.child("Products").child(id).child("filter barang").setValue(filter);
//
//    }
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
                           String hargabarang,String filter,String itemkategori
    ) {
            Log.i("update",selectedImage.toString());
            if(selectedImage.toString().contains("https://firebasestorage.googleapis.com"))
            {
                Log.i("update","gambar tidak upload ulang");
                Intent i = new Intent();
                i.putExtra("id", id);
                i.putExtra("image", selectedImage.toString());
                i.putExtra("nama",nama);
                i.putExtra("harga",hargabarang);
                i.putExtra("kategori barang",kategori);
                i.putExtra("kategori item",itemkategori);
                i.putExtra("filter",filter);
                i.putExtra("kuantitas",jumlah);

                setResult(RESULT_OK, i);
                finish();
            }
            else {
                Log.i("update","upload foto");
                String imageName = "product/" + id+ " - " + nama + ".jpg";
                storageReference.child(imageName).putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        StorageReference myStorageRef = firebaseStorage.getReference(imageName);
                        myStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Intent i = new Intent();
                                i.putExtra("id", id);
                                i.putExtra("image", uri.toString());
                                i.putExtra("nama",nama);
                                i.putExtra("harga",hargabarang);
                                i.putExtra("kategori barang",kategori);
                                i.putExtra("kategori item",itemkategori);
                                i.putExtra("filter",filter);
                                i.putExtra("kuantitas",jumlah);
                                setResult(RESULT_OK, i);
                                finish();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddProductActivity.this, "upload gambar gagal", Toast.LENGTH_LONG).show();
                    }
                });
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