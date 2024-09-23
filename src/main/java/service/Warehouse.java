package service;

import entities.InputProductData;
import entities.ProductList;
import entities.Product;
import entities.Category;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Warehouse {
    private final ProductList productList;

    public Warehouse() {
        this.productList = new ProductList(List.of());
    }

    public Warehouse(ProductList productList) {
        this.productList = new ProductList(productList.products());
    }

    public Product createNewProduct(InputProductData inputProductData) {
        return new Product(
                UUID.randomUUID(),
                inputProductData.name(),
                inputProductData.category(),
                inputProductData.rating(),
                LocalDate.now(),
                LocalDate.now()
        );
    }

    public void addNewProduct(InputProductData inputProductData) {
        productList.addProduct(createNewProduct(inputProductData));
    }

    public List<Product> getProductListRecord() {
        return Collections.unmodifiableList(productList.products());
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public <K, V> Map<K, List<V>> groupingProducts(Function<V, K> function, List<V> listItems) {
        return Collections.unmodifiableMap(
                listItems.stream()
                        .collect(Collectors.groupingBy(function)));
    }

    public <K, V> Map<K, Long> numberOfGroupedProducts(Function<V, K> function, List<V> listItems) {
        return Collections.unmodifiableMap(
                listItems.stream()
                        .collect(Collectors.groupingBy(function, Collectors.counting())));
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Map<UUID, List<Product>> getProductsPerId() {
        return groupingProducts(Product::id, productList.products());
    }

    public Optional<Product> getAProductForItsId(UUID id) {
        Map<UUID, List<Product>> productsPerId = getProductsPerId();
        return productsPerId.entrySet().stream()
                .filter(entry -> entry.getKey().equals(id))
                .flatMap(entry -> entry.getValue().stream())
                .findFirst();
    }

    public Product changeProductNameCategoryRating(UUID id, InputProductData inputProductData) {
        Product product = getAProductForItsId(id).orElseThrow();

        return new Product(
                product.id(),
                inputProductData.name(),
                inputProductData.category(),
                inputProductData.rating(),
                product.createdAt(),
                LocalDate.now()
        );
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Map<LocalDate, List<Product>> getProductsPerCreatedAt() {
        return groupingProducts(Product::createdAt, productList.products());
    }

    public Map<LocalDate, List<Product>> getProductsPerUpdatedAt() {
        return groupingProducts(Product::updatedAt, productList.products());
    }

    public record ProductDatesToCompareRecord(LocalDate dateOne, LocalDate dateTwo) {
        public static boolean isDateAfter(LocalDate dateOne, LocalDate dateTwo) {
            return dateOne.isAfter(dateTwo);
        }
    }

    public List<Product> getFilteredProductsByDate(LocalDate dateTwo) {
        Map<LocalDate, List<Product>> allProductsCreatedAfter = getProductsPerCreatedAt();
        return allProductsCreatedAfter.entrySet().stream()
                .filter(entry -> ProductDatesToCompareRecord.isDateAfter(entry.getKey(), dateTwo))
                .flatMap(entry -> entry.getValue().stream())
                .toList();
    }

    public List<Product> getAllUpdatedProducts() {
        Map<LocalDate, List<Product>> allUpdatedProducts = getProductsPerUpdatedAt();
        return allUpdatedProducts.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .filter(product -> ProductDatesToCompareRecord.isDateAfter(product.updatedAt(), product.createdAt()))
                .toList();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Map<Category, List<Product>> getProductsPerCategory() {
        return groupingProducts(Product::category, productList.products());
    }

    public record ProductsPerProductRecord(List<Product> productRecord) {
        public ProductsPerProductRecord(List<Product> productRecord) {
            this.productRecord = List.copyOf(productRecord);
        }

        public static boolean productHasFirstLetter(Product productRecord) {
            char firstLetter = Character.toLowerCase(getCharAt(productRecord));

            return Character.isLetter(firstLetter) && (firstLetter >= 'a' && firstLetter <= 'z');
        }

        private static char getCharAt(Product productRecord) {
            return productRecord.name().charAt(0);
        }

        public static Comparator<Product> comparingByNameOfProducts() {
            return Comparator.comparing(Product::name);
        }
    }

    public ProductsPerProductRecord getSortedProductsForACategory(Category category) {
        List<Product> productsForACategory = getProductsPerCategory().get(category);

        return productsForACategory.stream()
                .filter(ProductsPerProductRecord::productHasFirstLetter)
                .sorted(ProductsPerProductRecord.comparingByNameOfProducts())
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(), ProductsPerProductRecord::new)
                );
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Map<Category, Long> getNumberOfProductsPerCategory() {
        return numberOfGroupedProducts(Product::category, productList.products());
    }

    public long getNumberOfProductsForACategory(Category category) {
        Map<Category, Long> numberOfProductsPerCategory = getNumberOfProductsPerCategory();
        return numberOfProductsPerCategory.getOrDefault(category, 0L);
    }

    public record NumberOfProductsPerCategoryRecord(Category category, long numberOfProducts) {
        public static boolean categoryHasProducts(Map.Entry<Category, Long> entry) {
            return entry.getValue() >= 1;
        }
    }

    public List<Category> getAllCategoriesWithProducts() {
        Map<Category, Long> numberOfProductsPerCategory = getNumberOfProductsPerCategory();

        return numberOfProductsPerCategory.entrySet().stream()
                .filter(NumberOfProductsPerCategoryRecord::categoryHasProducts)
                .map(Map.Entry::getKey)
                .toList();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Map<Character, List<Product>> getProductsPerFirstLetter() {
        return groupingProducts(ProductsPerProductRecord::getCharAt,
                productList.products().stream()
                        .filter(ProductsPerProductRecord::productHasFirstLetter)
                        .collect(Collectors.toList()));
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Map<Month, List<Product>> getProductsPerCreatedAtMonth() {
        return groupingProducts(product -> product.createdAt().getMonth(), productList.products());
    }

    public List<Product> getProductsForAMonth(Month month) {
        Map<Month, List<Product>> productsPerMonth = getProductsPerCreatedAtMonth();
        return productsPerMonth.getOrDefault(month, List.of());
    }

    record ProductsPerMonthRecord(Month month, List<Product> productRecord) {
        public static boolean productHasMaxRating(Product productRecord, double maxRating) {
            return productRecord.rating() == maxRating;
        }

        public static Comparator<Product> comparingByLocalDate() {
            return Comparator.comparing(Product::createdAt);
        }
    }

    public List<Product> getSortedProductsWithMaxRatingForThisMonthByCreatedAt(CurrentMonthRecord currentMonthRecord) {
        Month currentMonth = currentMonthRecord.currentMonth();
        List<Product> productsForAMonth = getProductsForAMonth(currentMonth);

        final double maxRating = getMaxRating(productsForAMonth);

        return productsForAMonth.stream()
                .filter(product -> ProductsPerMonthRecord.productHasMaxRating(product, maxRating))
                .sorted(ProductsPerMonthRecord.comparingByLocalDate().reversed())
                .toList();
    }

    public record CurrentMonthRecord(Month currentMonth) {
        public CurrentMonthRecord() {
            this(LocalDate.now().getMonth());
        }
    }

    private static double getMaxRating(List<Product> productsForAMonth) {
        return productsForAMonth.stream()
                .map(Product::rating)
                .max(Double::compareTo)
                .orElseThrow();
    }
}
