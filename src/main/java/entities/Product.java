package entities;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public final class Product {
    private final String id;
    //    private final String name;
    private String name;
    //    private final Category category;
    private Category category;
    //    private final double rating;
    private double rating;
    private final LocalDate created_at;
    //    private final LocalDate updated_at;
    private LocalDate updated_at;

    public Product(String name, Category category, double rating) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.category = category;
        this.rating = rating;
        this.created_at = LocalDate.now();
        this.updated_at = LocalDate.now();
    }

//    public String id() {
//        return id;
//    }
//
//    public String name() {
//        return name;
//    }
//
    public void setName(String name) {
        this.name = name;
    }
//
//    public Category category() {
//        return category;
//    }
//
    public void setCategory(Category category) {
        this.category = category;
    }
//
//    public double rating() {
//        return rating;
//    }
//
    public void setRating(double rating) {
        this.rating = rating;
    }
//
//    public LocalDate created_at() {
//        return created_at;
//    }
//
//    public LocalDate updated_at() {
//        return updated_at;
//    }
//
    public void setUpdated_at(LocalDate updated_at) {
        this.updated_at = LocalDate.now();
    }

//    @Override
//    public boolean equals(Object obj) {
//        if (obj == this) return true;
//        if (obj == null || obj.getClass() != this.getClass()) return false;
//        var that = (Product) obj;
//        return this.id == that.id &&
//                Objects.equals(this.name, that.name) &&
//                Objects.equals(this.category, that.category) &&
//                Double.doubleToLongBits(this.rating) == Double.doubleToLongBits(that.rating) &&
//                Objects.equals(this.created_at, that.created_at) &&
//                Objects.equals(this.updated_at, that.updated_at);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id, name, category, rating, created_at, updated_at);
//    }

    @Override
    public String toString() {
        return "Product[" +
                "id=" + id + ", " +
                "name=" + name + ", " +
                "category=" + category + ", " +
                "rating=" + rating + ", " +
                "created_at=" + created_at + ", " +
                "updated_at=" + updated_at + ']';
    }
}
