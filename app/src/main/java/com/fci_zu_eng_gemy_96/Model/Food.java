package com.fci_zu_eng_gemy_96.Model;

public class Food {
    public String name , price ,discount , image , description , menuId ;

    public Food() {
    }

    public Food(String name, String price, String discount, String image, String description, String menuId) {
        this.name = name;
        this.price = price;
        this.discount = discount;
        this.image = image;
        this.description = description;
        this.menuId = menuId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }
}
