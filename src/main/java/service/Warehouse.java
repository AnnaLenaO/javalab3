package service;

import entities.InputProductData;
import entities.ProductList;
import entities.ProductRecord;
import entities.Category;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Warehouse {
    private final ProductList productList;

    public Warehouse(ProductList productList) {
        this.productList = productList;
    }

    public Warehouse() {
        this.productList = new ProductList(List.of());
    }

    public void addNewProduct(InputProductData inputProductData) {
        ProductRecord newProduct = new ProductRecord(
                UUID.randomUUID(),
                inputProductData.name(),
                inputProductData.category(),
                inputProductData.rating(),
                LocalDate.now(),
                LocalDate.now()
        );

        productList.addProduct(newProduct);
    }

    public List<ProductRecord> getProductListRecord() {
        return Collections.unmodifiableList(productList.products());
    }

    ///////////////////////////////////////////////////////////////////////////
//    public Map<Category, Long> getNumberOfProductsPerCategory() {
//        return productList.products().stream()
//                .collect(Collectors.groupingBy(
//                        ProductRecord::category, Collectors.counting()
//                )).
//    }
    public Map<Category, Long> getNumberOfProductsPerCategory() {
        Map<Category, Long> productsPerCategory = productList.products().stream()
                .collect(Collectors.groupingBy(
                        ProductRecord::category, Collectors.counting()
                ));

        return Collections.unmodifiableMap(productsPerCategory);
    }

    public long getNumberOfProductsForACategory(Category category) {
        Map<Category, Long> numberOfProductsPerCategory = getNumberOfProductsPerCategory();
        return numberOfProductsPerCategory.getOrDefault(category, 0L);
    }

    record NumberOfProductsPerCategoryRecord(Category category, long numberOfProducts) {
        public NumberOfProductsPerCategoryRecord(Map.Entry<Category, Long> entry) {
            this(entry.getKey(), entry.getValue());
        }

        public static Comparator<NumberOfProductsPerCategoryRecord> comparingByNumberOfProducts() {
            return Comparator.comparing(NumberOfProductsPerCategoryRecord::numberOfProducts);
        }

        public static boolean categoryHasProducts(Map.Entry<Category, Long> entry) {
            return entry.getValue() > 0;
        }
    }

    private List<NumberOfProductsPerCategoryRecord> allCategoriesWithProducts() {
        Map<Category, Long> numberOfProductsPerCategory = getNumberOfProductsPerCategory();

        return numberOfProductsPerCategory.entrySet().stream()
                .filter(NumberOfProductsPerCategoryRecord::categoryHasProducts)
                .map(NumberOfProductsPerCategoryRecord::new).toList();
    }

    //////////////////////////////////////////////////////////////////////////////////
    private NumberOfProductsPerCategoryRecord CategoryWithTheMostProducts() {
        Map<Category, Long> numberOfProductsPerCategory = getNumberOfProductsPerCategory();

        return numberOfProductsPerCategory.entrySet().stream()
                .map(NumberOfProductsPerCategoryRecord::new)
                .max(NumberOfProductsPerCategoryRecord.comparingByNumberOfProducts())
                .orElseThrow();
    }
}

