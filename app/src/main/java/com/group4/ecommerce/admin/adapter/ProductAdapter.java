package com.group4.ecommerce.admin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.group4.ecommerce.R;
import com.group4.ecommerce.model.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductHolder>{
    List<Product> productList;
    Context mContext;
    private OnItemClickListener listenerUpdate;


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
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_product,parent,false);
        return new ProductHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ProductHolder holder, int position) {
        ProductHolder productHolder=(ProductHolder) holder;
        Product product=productList.get(position);
        holder.productName.setText(product.getName());
        holder.productId.setText(product.getId());
        holder.productKategori.setText(product.getKategori());
        holder.productKuantitas.setText(product.getJumlah());
        if(product.getImage()!=null&&!product.getImage().equals("String")){
            Picasso.get().load(product.getImage()).into(holder.productImage);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductHolder extends RecyclerView.ViewHolder{

        TextView productId,productName,productKuantitas,productKategori;
        CircleImageView productImage;
        public ProductHolder(@NonNull View itemView) {

            super(itemView);
            productId=itemView.findViewById(R.id.productId);
            productName=itemView.findViewById(R.id.productname);
            productKuantitas=itemView.findViewById(R.id.productKuantitas);
            productKategori=itemView.findViewById(R.id.productkategori);
            productImage=itemView.findViewById(R.id.productImage);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position=getAdapterPosition();

                    if(listenerUpdate!=null &&position!=RecyclerView.NO_POSITION){
                        listenerUpdate.onItemClick(productList.get(position));
                    }
                }
            });
        }

    }
    public interface OnItemClickListener{
        void onItemClick(Product product);
    }
    public void setOnItemClickListenerUpdate(ProductAdapter.OnItemClickListener listener){
        this.listenerUpdate=listener;
    }
}

