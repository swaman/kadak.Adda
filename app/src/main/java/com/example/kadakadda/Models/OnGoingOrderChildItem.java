package com.example.kadakadda.Models;

public class OnGoingOrderChildItem  {

    private String imagePath;
    private String price;
    private String title;
    private String deliveredBy;

    public OnGoingOrderChildItem(String imagePath, String price, String title, String deliveredBy) {
        this.imagePath = imagePath;
        this.price = price;
        this.title = title;
        this.deliveredBy = deliveredBy;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getPrice() {
        return price;
    }

    public String getTitle() {
        return title;
    }

    public String getDeliveredBy() {
        return deliveredBy;
    }
}
