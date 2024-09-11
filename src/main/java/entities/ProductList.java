package entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductList {
    private final List<ProductRecord> products;

    public ProductList(List<ProductRecord> products) {
        this.products = new ArrayList<>(products);
    }

    public void addProduct(ProductRecord newProduct) {
//        List<ProductRecord> newProductList = new ArrayList<>(this.products);
//        newProductList.add(newProduct);
        products.add(newProduct);
    }

    public List<ProductRecord> products() {
        return Collections.unmodifiableList(products);
    }
}
