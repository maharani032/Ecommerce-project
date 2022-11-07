package com.group4.ecommerce.adapter;

import static java.text.DateFormat.getDateTimeInstance;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.group4.ecommerce.R;
import com.group4.ecommerce.model.Product;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

public class ProductUserAdapter extends RecyclerView.Adapter<ProductUserAdapter.ph>{
    List<Product> productList;
    Context mContext;
    private OnItemClickListener listenerClick;
    FirebaseDatabase database;
    DatabaseReference reference;

    public ProductUserAdapter(List<Product> productList, Context mContext) {
        this.productList = productList;
        this.mContext = mContext;

        database=FirebaseDatabase.getInstance();
        reference=database.getReference();
    }

    @NonNull
    @Override
    public ph onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_product_user,parent,false);
        return new ph(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ph holder, int position) {
        ProductUserAdapter.ph productHolder=(ProductUserAdapter.ph) holder;
        Product product=productList.get(position);
        holder.namaProduct.setText(product.getName());
        double harga=Double.parseDouble(product.getHarga());
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        holder.hargaProduct.setText("Rp "+formatter.format(harga));
        if(product.getImage()!=null&&!product.getImage().equals("String")){
            Picasso.get().load(product.getImage()).into(holder.imageProduct);
        }
        long time=product.getTimestamp();
        DateFormat dateFormat = getDateTimeInstance();
        Date date=new Date(time);
        holder.dateProduct.setText(dateFormat.format(date));
//        holder.dateProduct
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ph extends RecyclerView.ViewHolder{
        ImageView imageProduct;
        TextView namaProduct,hargaProduct,dateProduct;
        public ph(@NonNull View itemView) {

            super(itemView);
            imageProduct=itemView.findViewById(R.id.imageProduct);
            namaProduct=itemView.findViewById(R.id.namaProduct);
            hargaProduct=itemView.findViewById(R.id.hargaProduct);
            dateProduct=itemView.findViewById(R.id.dateProduct);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position=getAdapterPosition();

                    if(listenerClick!=null &&position!=RecyclerView.NO_POSITION){
                        listenerClick.onItemClick(productList.get(position));
                    }
                }
            });
        }

    } public interface OnItemClickListener{
        void onItemClick(Product product);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listenerClick=listener;
    }
}
