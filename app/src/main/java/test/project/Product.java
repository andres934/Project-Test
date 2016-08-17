package test.project;

/**
 * Created by andre_000 on 8/14/2016.
 */
public class Product {

    public int id;
    public String name;
    public String description;
    public int price;
    public int user_id;
    public int image_id;
    public int location_id;
    public int barcode_id;
    public String signature_state;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }

    public int getLocation_id() {
        return location_id;
    }

    public void setLocation_id(int location_id) {
        this.location_id = location_id;
    }

    public int getBarcode_id() {
        return barcode_id;
    }

    public void setBarcode_id(int barcode_id) {
        this.barcode_id = barcode_id;
    }

    public String getSignature_state() {
        return signature_state;
    }

    public void setSignature_state(String signature_state) {
        this.signature_state = signature_state;
    }

    public Product(int id, String name, String description, int price, int user_id, int image_id, int location_id, int barcode_id, String signature_state) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.user_id = user_id;
        this.image_id = image_id;
        this.location_id = location_id;
        this.barcode_id = barcode_id;
        this.signature_state = signature_state;
    }

    public Product() {
    }
}
