package com.group4.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SignUpActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth= FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        reference=database.getReference();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();
//create data manual
//        signup("admin1@gmail.com","123456","admin1");
    }

    public void signup(String email,String password, String fullname){
//        register untuk user
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            reference.child("Users").child(auth.getUid()).child("email").setValue(email);

//                        role=> user,staff,admin
                            reference.child("Users").child(auth.getUid()).child("admin").setValue(false);
                            reference.child("Users").child(auth.getUid()).child("staff").setValue(false);

                            reference.child("User").child(auth.getUid()).child("Fullname").setValue(fullname);
                            reference.child("User").child(auth.getUid()).child("email").setValue(email);

                            Intent welcome=new Intent(SignUpActivity.this,WelcomeActivity.class);
                            startActivity(welcome);
                        }else if(!task.isSuccessful()){
                            Toast.makeText(SignUpActivity.this,"cek email dan password",Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }
}