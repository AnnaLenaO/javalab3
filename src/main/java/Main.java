import entities.*;
import service.Warehouse;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        var warehouse = new Warehouse();

        InputProductData inputProductData = new InputProductData("Hippolyte", Category.GALLICA, 10);
        warehouse.addNewProduct(inputProductData);
        List<Product> products = warehouse.getProductList();
        products.forEach(System.out::println);
    }
}
