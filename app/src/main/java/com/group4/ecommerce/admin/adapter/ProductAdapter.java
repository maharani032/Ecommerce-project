package com.group4.ecommerce.admin.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.group4.ecommerce.model.Product;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder>{
    List<Product> productList;
    Context mContext;
    private StaffAdapter.OnItemClickListener listenerUpdate;

    FirebaseDatabase database;
    DatabaseReference reference;

    public ProductAdapter(List<Product> productList, Context mContext) {
        this.productList = productList;
        this.mContext = mContext;
        database=FirebaseDatabase.getInstance();
        reference=database.getReference();
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ProductHolder extends RecyclerView.ViewHolder{

        TextView fname,email;
        CircleImageView staffPicture;
        public ProductHolder(@NonNull View itemView) {
            super(itemView);
        }

    }
}

