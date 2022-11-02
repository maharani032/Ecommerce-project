package com.group4.ecommerce.admin.ui.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group4.ecommerce.admin.AddStaffActivity;
import com.group4.ecommerce.adapter.StaffAdapter;
import com.group4.ecommerce.databinding.FragmentStaffAdminBinding;
import com.group4.ecommerce.model.Staff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaffFragment extends Fragment {
    ActivityResultLauncher<Intent> activityResultLauncherForAddStaff;
    ActivityResultLauncher<Intent> activityResultLauncherForUpdateStaff;
    RecyclerView rv;

    DatabaseReference reference;
    FirebaseDatabase database;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    FloatingActionButton fabStaff;
    private FragmentStaffAdminBinding binding;

    List<Staff> list=new ArrayList<>();
    StaffAdapter staffAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentStaffAdminBinding.inflate(inflater, container, false);
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
        RegisterActivityForUpdateStaff();

        rv= binding.rvStaff;
        rv.setLayoutManager(new LinearLayoutManager(requireActivity()));
//        binding.rvStaff.setAdapter(staffAdapter);
        database= FirebaseDatabase.getInstance();
        reference= database.getReference();
        firebaseStorage= FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();

        list=new ArrayList<>();
        staffAdapter=new StaffAdapter(list,getContext());
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position=viewHolder.getAdapterPosition();
                reference.child("Staffs").child(list.get(position).getId()).removeValue();
                reference.child("Auth").child(list.get(position).getId()).removeValue();
                if(list.get(position)!=null&&!list.get(position).getImage().equals("String")){
                    String imageName = "StaffPicture/" + list.get(position).getId()+ " - " + list.get(position).getFullname() + ".jpg";
                    storageReference.child(imageName).delete();
                }
                Toast.makeText(getContext(), "data has been delete", Toast.LENGTH_SHORT).show();

            }
        }).attachToRecyclerView(rv);
        rv.setAdapter(staffAdapter);

        reference.child("Staffs").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot sp:snapshot.getChildren()){
                    Staff staff=sp.getValue(Staff.class);
                    list.add(staff);
                }
                staffAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        staffAdapter.setOnItemClickListenerUpdate(new StaffAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Staff staff) {
                String id=staff.getId();
                reference.child("Auth").child(id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Intent intent= new Intent(getActivity(),AddStaffActivity.class);
                        intent.putExtra("id",staff.getId());
                        intent.putExtra("email",staff.getEmail());
                        intent.putExtra("password",snapshot.child("password")!=null
                                ?snapshot.child("password").getValue().toString():
                                "test");
                        intent.putExtra("fullname",staff.getFullname());
                        intent.putExtra("image",staff.getImage());
                        activityResultLauncherForUpdateStaff.launch(intent);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });



    }
    public void RegisterActivityForAddStaff(){
        activityResultLauncherForAddStaff=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        int ResultCode=result.getResultCode();
                        Intent data=result.getData();

                        if(ResultCode==RESULT_OK && data !=null){
                            Log.i("data staff fragment",data.toString());
                            String id=data.getStringExtra("id");
                            String fullname=data.getStringExtra("fullname");
                            String image=data.getStringExtra("image");
                            String email=data.getStringExtra("email");
                            if(id!=null && fullname!=null && image!=null && email!=null){
                                reference.child("Staffs").child(id).child("id").setValue(id);
                                reference.child("Staffs").child(id).child("fullname").setValue(fullname);
                                reference.child("Staffs").child(id).child("image").setValue(image);
                                reference.child("Staffs").child(id).child("email").setValue(email);
                            }
                        }
                    }
                });
    }
    public void RegisterActivityForUpdateStaff(){
        activityResultLauncherForUpdateStaff=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult()
                , new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        int ResultCode=result.getResultCode();
                        Intent data=result.getData();

                        if(ResultCode==RESULT_OK && data !=null){
                            Log.i("update data staff fragment",data.toString());
                            String id=data.getStringExtra("id");
                            String fullname=data.getStringExtra("fullname");
                            String image=data.getStringExtra("image");
                            String email=data.getStringExtra("email");
                            Log.i("update data",id+""+fullname+""+image+""+email);
                            if(id!=null && fullname!=null && image!=null && email!=null){
                                Log.i("update","data tidak null");
                                Map<String, Object> hasMap = new HashMap<>();
                                hasMap.put("fullname",fullname);
                                hasMap.put("email",email);
                                hasMap.put("image",image);
                                hasMap.put("id",id);
                                reference.child("Staffs").child(id)
                                        .updateChildren(hasMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.i("update","harus ke update");
                                    }
                                });
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