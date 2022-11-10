package com.group4.ecommerce.staff;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.group4.ecommerce.R;
import com.group4.ecommerce.WelcomeActivity;
import com.group4.ecommerce.preferences;

public class StaffActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Dashboard Staff");
        setContentView(R.layout.activity_staff);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.staff_menu,menu);
//        return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.nav_signout){
            Intent i= new Intent(StaffActivity.this, WelcomeActivity.class);
            preferences.clearData(StaffActivity.this);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}