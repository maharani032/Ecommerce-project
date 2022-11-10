package com.group4.ecommerce;

import static java.lang.String.valueOf;

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
                bSignUp.setEnabled(false);
                String mail=email.getText().toString().replaceAll("\\s+","").toLowerCase();
                String pw=password.getText().toString();
                String ph=phone.getText().toString();
                if(password.length()<=5){
                    Toast.makeText(SignUpActivity.this,"Minimum length of password",Toast.LENGTH_SHORT).show();
                    bSignUp.setEnabled(true);
                }
                else if(mail.isEmpty()||pw.isEmpty()||ph.isEmpty()) {
                    Toast.makeText(SignUpActivity.this,"Please Input Your Email and Password",Toast.LENGTH_SHORT).show();
                    bSignUp.setEnabled(true);
                }
                if(!mail.isEmpty() && !pw.isEmpty()&& !ph.isEmpty()){
                    signup(mail,pw,ph);

                }
            }
        });
    }

    public void signup(String email,String password, String phone){
        UUID randomId= UUID.randomUUID();
        reference.child("Auth").orderByChild("email").equalTo(email)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Auth auth=snapshot.getValue(Auth.class);
                if(!snapshot.exists()){
                    lemail.clear();
                    createUser(randomId.toString(),email,password,phone);
                }else if(snapshot.exists()){
                    Log.i("size lama",valueOf(lemail.size()));

                    lemail.add(auth.getEmail());
                    Log.i("size",valueOf(lemail.size()));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                bSignUp.setEnabled(true);
            }
        });
        int size=lemail.size();
        if(size>0){
            Toast.makeText(SignUpActivity.this, "email telah dipakai", Toast.LENGTH_SHORT).show();
            bSignUp.setEnabled(true);
        }



    }
    public void createUser(String randomId,String email,String password,String phone){
        reference.child("Auth").child(randomId).child("id").setValue(randomId);
        reference.child("Auth").child(randomId).child("email").setValue(email);
        reference.child("Auth").child(randomId).child("password").setValue(password);
        reference.child("Auth").child(randomId).child("phone").setValue(phone);
        reference.child("Auth").child(randomId).child("role").setValue("user");
        Intent i= new Intent(SignUpActivity.this,WelcomeActivity.class);
        startActivity(i);

    }
}