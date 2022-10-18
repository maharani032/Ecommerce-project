package com.group4.ecommerce.admin;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.group4.ecommerce.R;
import com.group4.ecommerce.WelcomeActivity;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddStaffActivity extends AppCompatActivity {

    ActivityResultLauncher<Intent> activityResultLauncherForUploadImage;
    Button addStaff;
    ImageView goback;
    CircleImageView staffPicture;
    EditText inputEmail,inputPassword,inputFullname;
    ProgressBar pb;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    boolean imageControl=false;
    Uri selectedImage;
    String imagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff);
        RegisterActivityForUploadImage();


        auth= FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();
        reference=database.getReference();
        firebaseStorage= FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();

        goback=findViewById(R.id.goback);
        staffPicture=findViewById(R.id.staffpicture);
        inputEmail=findViewById(R.id.input_email);
        inputPassword=findViewById(R.id.input_password);
        inputFullname=findViewById(R.id.input_fullname);
        addStaff=findViewById(R.id.btnAddStaff);
        pb=findViewById(R.id.pb);

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                }
                else {
                    AddStaffActivity.super.onBackPressed();
                }
            }
        });
        addStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=inputEmail.getText().toString();
                String password=inputPassword.getText().toString();
                String fullname=inputFullname.getText().toString();
                    if(!email.isEmpty() && !password.isEmpty()) {
                        RegisterStaff(email,password,fullname);
                        auth.signOut();
                        Intent i=new Intent(AddStaffActivity.this, WelcomeActivity.class);
                        startActivity(i);
                    }
                else if(password.length()<=5) Toast.makeText(AddStaffActivity.this,"Minimum length of password",Toast.LENGTH_SHORT).show();
                else Toast.makeText(AddStaffActivity.this,"Please Input Your Email,Fullname and Password",Toast.LENGTH_SHORT).show();
            }
        });
        staffPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePhoto();
            }
        });

    }

    private void choosePhoto() {
        if (ContextCompat.checkSelfPermission(AddStaffActivity.this
                , Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(AddStaffActivity.this
                    ,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else{

            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncherForUploadImage.launch(i);
        }
    }
    public void selesai(){
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
//        FirebaseAuth.getInstance().signOut();
//        Intent i= new Intent(AddStaffActivity.this, WelcomeActivity.class);
//        startActivity(i);

    }
    public void RegisterStaff(String email, String password, String fullname){
        pb.setVisibility(View.VISIBLE);
        auth.signOut();
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    reference.child("Users").child(auth.getUid()).child("email").setValue(email);
//                    reference.child("Users").child(auth.getUid()).child("fullname").setValue(fullname);
//                        role=> user,staff,admin
                    reference.child("Users").child(auth.getUid()).child("admin").setValue(false);
                    reference.child("Users").child(auth.getUid()).child("staff").setValue(true);


                    if(imageControl){
                        UUID randomId= UUID.randomUUID();
                        String imageName = "StaffPicture/"+ randomId + " - "+fullname+".jpg";
                        storageReference.child(imageName).putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                StorageReference myStorageRef=firebaseStorage.getReference(imageName);
                                myStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        imagePath=uri.toString();
                                        reference.child("Staffs").child(auth.getUid()).child("image").setValue(imagePath).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(AddStaffActivity.this,"success kirim gambar",Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddStaffActivity.this,"upload gambar gagal",Toast.LENGTH_LONG).show();

                            }
                        });

                    }
                    reference.child("Staffs").child(auth.getUid()).child("fullname").setValue(fullname);
                    reference.child("Staffs").child(auth.getUid()).child("email").setValue(email);
                    reference.child("Staffs").child(auth.getUid()).child("image").setValue("null");


                }

                else if(!task.isSuccessful()){
                    pb.setVisibility(View.GONE);
                    Toast.makeText(AddStaffActivity.this,"cek email dan password",Toast.LENGTH_LONG).show();
                }
            }
        });
//        ? bug dimana create user tapi masih login
//        ? double login krn buat create user baru akan selalu login dengan akun baru

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
                    Picasso.get().load(selectedImage).into(staffPicture);
                    imageControl=true;
                }else{
                    imageControl=false;
                }

            }
        });
    }
}