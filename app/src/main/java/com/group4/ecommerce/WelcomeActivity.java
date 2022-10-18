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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group4.ecommerce.admin.AdminActivity;

public class WelcomeActivity extends AppCompatActivity {
    private EditText inputEmail,inputPassword ;
    private Button buttonSignIn;
    private TextView daftar;
    private ProgressBar pb;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
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

        auth=FirebaseAuth.getInstance();

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
                String email=inputEmail.getText().toString();
                String password=inputPassword.getText().toString();
                if(!email.isEmpty() && !password.isEmpty()) signIn(email,password);
                else if(password.length()<=5) Toast.makeText(WelcomeActivity.this,"Minimum length of password",Toast.LENGTH_SHORT).show();
                else Toast.makeText(WelcomeActivity.this,"Please Input Your Email and Password",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void signIn(String email,String password){
        pb.setVisibility(View.VISIBLE);
//       auth sign in
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.i("login","login sucess");
                    checkRole(auth.getUid());

                }else{
                    pb.setVisibility(View.GONE);
                }
            }
        });
    }
    public void checkRole(String id){
        pb.setVisibility(View.VISIBLE);

        reference.child("Users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.child("admin").exists()){
                    FirebaseAuth.getInstance().signOut();
                }
                Boolean admin= (Boolean) snapshot.child("admin").getValue();
                Boolean staff= (Boolean) snapshot.child("staff").getValue();

                Log.i("login", String.valueOf(admin));
                if(admin==true && staff==false){
//                                pindah halaman admin
                    Intent adminPage= new Intent(WelcomeActivity.this, AdminActivity.class);
                    startActivity(adminPage);

                }
                else if(admin==false && staff==false){
//                                pindah ke halaman user
                    Log.i("login", "halaman user");
                    pb.setVisibility(View.INVISIBLE);



                }
                else if(staff==true && admin==false){
//                                pindah ke halaman staff
                    Log.i("login", "halaman staff");
                    pb.setVisibility(View.INVISIBLE);


                }
                else{
                    FirebaseAuth.getInstance().signOut();
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
        Log.i("login","on start");
        FirebaseUser user=auth.getCurrentUser();
        if(user!= null){
            checkRole(user.getUid());
        }
    }
}