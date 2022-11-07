package com.group4.ecommerce.user.ui;

import static java.text.DateFormat.getDateTimeInstance;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.group4.ecommerce.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

public class DetailProductActivity extends AppCompatActivity {
    TextView nameProduct,jumlahProduct,HargaProduct,kategoriItem,kategori,filter,description,date;
    ImageView img,goback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);
        img=findViewById(R.id.img);
        nameProduct=findViewById(R.id.namaProduct);
        jumlahProduct=findViewById(R.id.JumlahProduct);
        HargaProduct=findViewById(R.id.price);
        kategoriItem=findViewById(R.id.kategoriItem);
        kategori=findViewById(R.id.kategori);
        filter=findViewById(R.id.filter);
        description=findViewById(R.id.description);
        date=findViewById(R.id.date);
        goback=findViewById(R.id.goback);

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Intent i=getIntent();
        String getName=i.getStringExtra("name");
        String getImage=i.getStringExtra("image");
        String getJumlah=i.getStringExtra("jumlah");
        String getHarga=i.getStringExtra("harga");
        String getFilter=i.getStringExtra("filter");
        String getKategori=i.getStringExtra("kategori");
        String getTimestamp =i.getStringExtra("timestamp");
        String getKategoriItem=i.getStringExtra("kategoriItem");
        String getDescription=i.getStringExtra("description");
        if(getImage!=null&&
                getName!=null&&
                getDescription!=null&&
                getFilter!=null&&
                getHarga!=null&&
                getJumlah!=null&&
                getKategoriItem!=null&&
                getKategori!=null
                &&getTimestamp!=null){
            nameProduct.setText(getName);
            jumlahProduct.setText("Stok product "+getJumlah+" pcs");
            filter.setText(getFilter.equals("-")?"Semua orang":getFilter);
            kategori.setText(getKategori);
            kategoriItem.setText(getKategoriItem);
            description.setText(getDescription);
//            img
            Picasso.get().load(getImage).into(img);

//            date
            long time=Long.parseLong(getTimestamp);
            DateFormat dateFormat = getDateTimeInstance();
            Date tgl=new Date(time);
            date.setText(dateFormat.format(tgl));

//            harga
            double harga=Double.parseDouble(getHarga);
            DecimalFormat formatter = new DecimalFormat("#,###.00");
            HargaProduct.setText("Rp "+formatter.format(harga));
        }

    }
}