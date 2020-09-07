package com.example.kadakadda.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    public String id, description, imgPath;
    public Boolean availability;
    public Long price;
    public String title, type;

    public Product(String id, Boolean availability, String description, String imgPath, Long price, String title, String type) {
        this.id = id;
        this.availability = availability;
        this.description = description;
        this.imgPath = imgPath;
        this.price = price;
        this.title = title;
        this.type = type;
    }

    protected Product(Parcel in) {
        id = in.readString();
        description = in.readString();
        imgPath = in.readString();
        byte tmpAvailability = in.readByte();
        availability = tmpAvailability == 0 ? null : tmpAvailability == 1;
        if (in.readByte() == 0) {
            price = null;
        } else {
            price = in.readLong();
        }
        title = in.readString();
        type = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getImgPath() {
        return imgPath;
    }

    public Boolean getAvailability() {
        return availability;
    }

    public Long getPrice() {
        return price;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(description);
        dest.writeString(imgPath);
        dest.writeByte((byte) (availability == null ? 0 : availability ? 1 : 2));
        if (price == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(price);
        }
        dest.writeString(title);
        dest.writeString(type);
    }
}
