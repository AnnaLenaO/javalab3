package entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductList {
    private final List<Product> products;

    public ProductList(List<Product> products) {
        this.products = new ArrayList<>(products);
    }

    public void addProduct(Product newProduct) {
        products.add(newProduct);
    }

    public List<Product> products() {
        return Collections.unmodifiableList(products);
    }
}
