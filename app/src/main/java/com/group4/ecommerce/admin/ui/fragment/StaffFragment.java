package com.group4.ecommerce.admin.ui.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group4.ecommerce.admin.AddStaffActivity;
import com.group4.ecommerce.admin.adapter.StaffAdapter;
import com.group4.ecommerce.admin.model.Staff;
import com.group4.ecommerce.databinding.FragmentStaffBinding;

import java.util.ArrayList;
import java.util.List;

public class StaffFragment extends Fragment {
    ActivityResultLauncher<Intent> activityResultLauncherForAddStaff;
    String fullname,email,staffPicture;
    Staff staff;
    RecyclerView rv;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference;
    FirebaseDatabase database;

    FloatingActionButton fabStaff;
    private FragmentStaffBinding binding;

    List<Staff> listStaff= new ArrayList<>();
    List<String> listKEY=new ArrayList<>();

    List<String> list;
    StaffAdapter staffAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentStaffBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        fabStaff=binding.fabStaff;

        fabStaff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addStaff=new Intent(getActivity(), AddStaffActivity.class);
//                startActivity(addStaff);
                activityResultLauncherForAddStaff.launch(addStaff);
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RegisterActivityForAddStaff();

        rv= binding.rvStaff;
        rv.setLayoutManager(new LinearLayoutManager(requireActivity()));
//        binding.rvStaff.setAdapter(staffAdapter);
        auth= FirebaseAuth.getInstance();
        user= auth.getCurrentUser();
        database= FirebaseDatabase.getInstance();
        reference= database.getReference();

        reference.child("Staffs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot keys:snapshot.getChildren()){
                    Staff staff=keys.getValue(Staff.class);
                    String key= keys.getKey();
                    listKEY.add(key);
                    Log.d("staff", "User name: " + staff.getFullname().toString() + ", email " + staff.getEmail());
                    staffAdapter=new StaffAdapter(listKEY,staff.getFullname(),staff.getEmail(),staff.getImage(),getContext());

                    rv.setAdapter(staffAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    public void getStaffs(){
    reference.child("Staffs").addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            String key= snapshot.getKey();
            list.add(key);
            staffAdapter.notifyDataSetChanged();

        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

        }
    });
}
    public void RegisterActivityForAddStaff(){
        activityResultLauncherForAddStaff= registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                int ResultCode=result.getResultCode();
                Intent data=result.getData();
//                resultCode == Result_ok // berhasil creat user
//                resultCode == 2 // tidak berhasil create user
                if(ResultCode==RESULT_OK && data !=null){
                    Log.i("result data",data.toString());
                    String id=data.getStringExtra("id");
                    String fullname=data.getStringExtra("fullname");
                    String image=data.getStringExtra("image");
                    String email=data.getStringExtra("email");
                    if(id!=null && fullname!=null && image!=null && email!=null){
                    reference.child("Staffs").child(id).child("fullname").setValue(fullname);
                    reference.child("Staffs").child(id).child("image").setValue(image);
                    reference.child("Staffs").child(id).child("email").setValue(email);
                    notify();
                    }
                }
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}