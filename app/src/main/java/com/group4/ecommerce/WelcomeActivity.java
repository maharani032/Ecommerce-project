package com.group4.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.group4.ecommerce.admin.AdminActivity;
import com.group4.ecommerce.model.Auth;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity {
    private EditText inputEmail,inputPassword ;
    private Button buttonSignIn;
    private TextView daftar;
    private ProgressBar pb;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    List<String> lEmail= new ArrayList<>();
    List<String> lId= new ArrayList<>();
    String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputEmail=findViewById(R.id.input_email);
        inputPassword=findViewById(R.id.input_password);
        buttonSignIn=findViewById(R.id.buttonlogin);
        daftar=findViewById(R.id.daftar);
        pb=findViewById(R.id.pb);
        database= FirebaseDatabase.getInstance();
        reference=database.getReference();
        firebaseStorage= FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();


        daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent SignUp=new Intent(WelcomeActivity.this,SignUpActivity.class);
                startActivity(SignUp);
            }
        });
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=inputEmail.getText().toString().replaceAll("\\s+","");
                String password=inputPassword.getText().toString();
                if(!email.isEmpty() && !password.isEmpty()) signIn(email,password);
                else if(password.length()<=5) Toast.makeText(WelcomeActivity.this,"Minimum length of password",Toast.LENGTH_SHORT).show();
                else Toast.makeText(WelcomeActivity.this,"Please Input Your Email and Password",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void signIn(String email,String password){
        pb.setVisibility(View.VISIBLE);
        reference.child("Auth").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot keys:snapshot.getChildren()){
                    Auth auth=keys.getValue(Auth.class);
                    if(keys.child("email").getValue().toString().equals(email)){
                        id=keys.child("id").getValue().toString();
                        CheckRole(id,email,password);
                    }
                }
                Log.i("id",id);

                pb.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void CheckRole(String id,String email,String password){
        reference.child("Auth").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Auth auth=snapshot.getValue(Auth.class);

                if(auth.getEmail().equals(email) && auth.getPassword().equals(password)){
                    if(auth.getRole().equals("user")){
                        preferences.setDataLogin(WelcomeActivity.this, true);
                        preferences.setDataAs(WelcomeActivity.this, "user");
                        preferences.setDataUid(WelcomeActivity.this,auth.getId());


                    }
                    else if(auth.getRole().equals("admin")){

                        preferences.setDataLogin(WelcomeActivity.this, true);
                        preferences.setDataAs(WelcomeActivity.this, "admin");
                        preferences.setDataUid(WelcomeActivity.this,auth.getId());
//                                pindah halaman admin
                        Intent adminPage= new Intent(WelcomeActivity.this, AdminActivity.class);
                        startActivity(adminPage);
                    }
                    else if(auth.getRole().equals("staff")){
                        preferences.setDataLogin(WelcomeActivity.this, true);
                        preferences.setDataAs(WelcomeActivity.this, "staff");
                        preferences.setDataUid(WelcomeActivity.this,auth.getId());
                        Log.i("login", "halaman staff");
                    }
                }
                else {
                    Toast.makeText(WelcomeActivity.this,"check password & email",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (preferences.getDataLogin(this)) {
            if (preferences.getDataAs(this).equals("admin")) {
                startActivity(new Intent(this, AdminActivity.class));
                finish();
            }
        }
    }
}