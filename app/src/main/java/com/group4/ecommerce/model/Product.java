package com.group4.ecommerce.model;

public class Product {
    String id,image,name,jumlah,harga,filter,kategori,description,kategoriItem;
    Long timestamp;
    public Product(){

    }
    public Product(String id, String image, String name, String jumlah, String harga, String filter
            ,String kategoriItem, String kategori, String description, Long timestamp
    ) {
        this.id = id;
        this.image = image;
        this.name = name;
        this.jumlah = jumlah;
        this.harga = harga;
        this.filter = filter;
        this.kategori = kategori;
        this.description = description;
        this.kategoriItem=kategoriItem;
        this.timestamp=timestamp;
    }

    public String getKategoriItem() {
        return kategoriItem;
    }

    public void setKategoriItem(String kategoriItem) {
        this.kategoriItem = kategoriItem;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public String getHarga() {
        return harga;
    }

    public void setHarga(String harga) {
        this.harga = harga;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
