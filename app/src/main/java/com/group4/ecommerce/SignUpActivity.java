package com.group4.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group4.ecommerce.model.Auth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    Button bSignUp;
    EditText email,password,phone;
    boolean checkEmail;
    List<String> lemail= new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        database=FirebaseDatabase.getInstance();
        reference=database.getReference();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();

        bSignUp=findViewById(R.id.bSignUp);
        email=findViewById(R.id.input_email);
        password=findViewById(R.id.input_password);
        phone=findViewById(R.id.input_phone);

        bSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mail=email.getText().toString().replaceAll("\\s+","");;
                String pw=password.getText().toString();
                String ph=phone.getText().toString();

                if(!mail.isEmpty() && !pw.isEmpty()&& !ph.isEmpty()) signup(mail,pw,ph);
                else if(password.length()<=5) Toast.makeText(SignUpActivity.this,"Minimum length of password",Toast.LENGTH_SHORT).show();
                else Toast.makeText(SignUpActivity.this,"Please Input Your Email and Password",Toast.LENGTH_SHORT).show();


            }
        });
    }

    public void signup(String email,String password, String phone){

//        register untuk user
        reference.child("Auth").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot keys:snapshot.getChildren()){

                    Auth auth=keys.getValue(Auth.class);
                    lemail.add(auth.getEmail().toString());
                }
                Log.i("auth",lemail.toString());
                if(!lemail.contains(email)){
                    UUID randomId= UUID.randomUUID();
                    reference.child("Auth").child(randomId.toString()).child("id").setValue(randomId.toString());
                    reference.child("Auth").child(randomId.toString()).child("email").setValue(email);
                    reference.child("Auth").child(randomId.toString()).child("password").setValue(password);
                    reference.child("Auth").child(randomId.toString()).child("phone").setValue(phone);
                    reference.child("Auth").child(randomId.toString()).child("role").setValue("user");
                    Intent i= new Intent(SignUpActivity.this,WelcomeActivity.class);
                    startActivity(i);
                }else{
                    Toast.makeText(SignUpActivity.this,"email sudah terpakai.",Toast.LENGTH_LONG).show();
                    return;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}