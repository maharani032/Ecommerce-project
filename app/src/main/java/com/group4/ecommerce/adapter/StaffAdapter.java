package com.group4.ecommerce.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group4.ecommerce.R;
import com.group4.ecommerce.model.Staff;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.StaffHolder> {
    List<Staff> staffList;
    Context mContext;
    private OnItemClickListener listenerUpdate;

    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    public StaffAdapter(List<Staff> staffList, Context mContext) {
        this.staffList = staffList;
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
        StaffHolder staffHolder=(StaffHolder)holder;
        Staff staff=staffList.get(position);
        holder.email.setText(staff.getEmail());
        holder.fname.setText(staff.getFullname());
        if(staff.getImage()!=null&&!staff.getImage().equals("String")){
            Picasso.get().load(staff.getImage()).into(holder.staffPicture);

        } else{
            holder.staffPicture.setImageResource(R.drawable.ic_baseline_person_24);
        }

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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position=getAdapterPosition();

                    if(listenerUpdate!=null &&position!=RecyclerView.NO_POSITION){
                        listenerUpdate.onItemClick(staffList.get(position));
                    }
                }
            });
        }

    }
    public interface OnItemClickListener{
        void onItemClick(Staff staff);
    }
    public void setOnItemClickListenerUpdate(OnItemClickListener listener){
        this.listenerUpdate=listener;
    }
}
