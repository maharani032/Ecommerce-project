package com.group4.ecommerce.admin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.group4.ecommerce.R;
import com.group4.ecommerce.model.Auth;
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
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    boolean imageControl=false,checkEmailStaff=false;
    Uri selectedImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_staff);
        RegisterActivityForUploadImage();
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
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        addStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pb.setVisibility(View.VISIBLE);

                String email=inputEmail.getText().toString().replaceAll("\\s+","");;
                String password=inputPassword.getText().toString();
                String fullname=inputFullname.getText().toString();
                UUID randomId= UUID.randomUUID();
                if(!email.isEmpty() && !password.isEmpty()) {
                    reference.child("Auth").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot keys:snapshot.getChildren()){
                                Auth auth=keys.getValue(Auth.class);
                                if(String.valueOf(keys.child("email")).equals(email)){
                                    checkEmailStaff=true;
                                }
                            }
                            if(checkEmailStaff){;
                                pb.setVisibility(View.INVISIBLE); return;}
                            else if (!checkEmailStaff)
                            {
                                RegisterStaff(email,password,fullname, randomId.toString());
                                Log.i("imageControl",String.valueOf(imageControl));

                            }
//                            position upload gambar tapi keluar dari apk

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    uploadFoto(fullname, email, randomId.toString());
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
    public void RegisterStaff(String email, String password, String fullname,String id){

        pb.setVisibility(View.VISIBLE);
        reference.child("Auth").child(id).child("id").setValue(id);
        reference.child("Auth").child(id).child("email").setValue(email);
        reference.child("Auth").child(id).child("password").setValue(password);
        reference.child("Auth").child(id).child("role").setValue("staff");



    }

public void uploadFoto(String fullname,String email,String id) {
    if (imageControl) {
        UUID randomId = UUID.randomUUID();
        String imageName = "StaffPicture/" + randomId.toString() + " - " + fullname + ".jpg";
        storageReference.child(imageName).putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                StorageReference myStorageRef = firebaseStorage.getReference(imageName);
                myStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Intent i = new Intent();
                        i.putExtra("email", email);
                        i.putExtra("fullname", fullname);
                        i.putExtra("id", id);
                        i.putExtra("image", uri.toString());
                        setResult(RESULT_OK, i);
                        finish();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddStaffActivity.this, "upload gambar gagal", Toast.LENGTH_LONG).show();
            }
        });
    }else{
        Log.i("di imageControl false",String.valueOf(imageControl));
        Intent i = new Intent();
        i.putExtra("email",email);
        i.putExtra("fullname",fullname);
        i.putExtra("id",id);
        i.putExtra("image","String");
        setResult(RESULT_OK,i);
        finish();
    }
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