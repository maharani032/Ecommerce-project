package com.group4.ecommerce.admin;

import android.Manifest;
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
                Intent i=new Intent();
                setResult(RESULT_CANCELED);
                finish();
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

    public void RegisterStaff(String email, String password, String fullname){
        //        TODO: double login krn buat create user ketika masih login

        pb.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    reference.child("Users").child(auth.getUid()).child("email").setValue(email);
                    reference.child("Users").child(auth.getUid()).child("role").setValue("staff");
                    reference.child("Users").child(auth.getUid()).child("fullname").setValue(fullname);
//                        role=> user,staff,admin
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
                                        Intent i =new Intent();
                                        i.putExtra("email",email);
                                        i.putExtra("fullname",fullname);
                                        i.putExtra("id",auth.getUid());
                                        i.putExtra("image",uri.toString());
                                        setResult(RESULT_OK,i);
                                        finish();

//                                        reference.child("Staffs").child(auth.getUid()).child("image").setValue("null");

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
                    else if(!imageControl){
                        Intent i =new Intent();
                        i.putExtra("email",email);
                        i.putExtra("fullname",fullname);
                        i.putExtra("id",auth.getUid());
                        i.putExtra("image","null");
                        setResult(RESULT_OK,i);
                        auth.signOut();
                        finish();


                    }


//                    reference.child("Staffs").child(auth.getUid()).child("fullname").setValue(fullname);
//                    reference.child("Staffs").child(auth.getUid()).child("email").setValue(email);
//                    if(!imageControl){
//                        reference.child("Staffs").child(auth.getUid()).child("image").setValue("null");
//                        imagePath="null";
//                    }



                }

                else if(!task.isSuccessful()){
                    pb.setVisibility(View.GONE);
                    Toast.makeText(AddStaffActivity.this,"cek email dan password",Toast.LENGTH_LONG).show();
                }
            }
        });

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