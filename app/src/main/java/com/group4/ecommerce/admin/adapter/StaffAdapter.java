package com.group4.ecommerce.admin.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.group4.ecommerce.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.StaffHolder> {
    List<String> staffList;
    String fullname,email,pictureStaff;
    Context mContext;

    FirebaseDatabase database;
    DatabaseReference reference;

    public StaffAdapter(List<String> staffList, String fullname,String email,String pictureStaff, Context mContext) {
        this.staffList = staffList;
        this.fullname = fullname;
        this.email = email;
        this.pictureStaff=pictureStaff;

        this.mContext = mContext;

        database=FirebaseDatabase.getInstance();
        reference=database.getReference();
        Log.i("database","di construtor"+database.toString());
    }

    @NonNull
    @Override
    public StaffHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_staff,parent,false);
        return new StaffHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StaffHolder holder, int position) {
        reference.child("Staffs").child(staffList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String fname=snapshot.child("fullname").getValue().toString();
                String email=snapshot.child("email").getValue().toString();
                String imageURL=snapshot.child("image").getValue().toString();

                holder.email.setText(email);
                holder.fname.setText(fname);

                if(imageURL.equals("null")) holder.staffPicture.setImageResource(R.drawable.ic_baseline_person_24);
                else if(!imageURL.equals("null")) Picasso.get().load(imageURL).into(holder.staffPicture);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {

        return staffList.size();
    }

    public class StaffHolder extends RecyclerView.ViewHolder{

        TextView fname,email;
        CircleImageView staffPicture;
        public StaffHolder(@NonNull View itemView) {
            super(itemView);
            fname=itemView.findViewById(R.id.fname);
            email=itemView.findViewById(R.id.email);
            staffPicture=itemView.findViewById(R.id.staffpicture);
        }
    }
}
