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

    public List<Product> getProductList() {
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

    public record ProductDatesToCompare(LocalDate dateOne, LocalDate dateTwo) {
        public static boolean isDateAfter(LocalDate dateOne, LocalDate dateTwo) {
            return dateOne.isAfter(dateTwo);
        }
    }

    public List<Product> getFilteredProductsByDate(LocalDate dateTwo) {
        Map<LocalDate, List<Product>> allProductsCreatedAfter = getProductsPerCreatedAt();
        return allProductsCreatedAfter.entrySet().stream()
                .filter(entry -> ProductDatesToCompare.isDateAfter(entry.getKey(), dateTwo))
                .flatMap(entry -> entry.getValue().stream())
                .toList();
    }

    public List<Product> getAllUpdatedProducts() {
        Map<LocalDate, List<Product>> allUpdatedProducts = getProductsPerUpdatedAt();
        return allUpdatedProducts.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                .filter(product -> ProductDatesToCompare.isDateAfter(product.updatedAt(), product.createdAt()))
                .toList();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Map<Category, List<Product>> getProductsPerCategory() {
        return groupingProducts(Product::category, productList.products());
    }

    public record SortedProducts(List<Product> product) {
        public SortedProducts(List<Product> product) {
            this.product = List.copyOf(product);
        }

        public static boolean productHasFirstLetter(Product product) {
            char firstLetter = Character.toLowerCase(getCharAt(product));

            return Character.isLetter(firstLetter) && (firstLetter >= 'a' && firstLetter <= 'z');
        }

        private static char getCharAt(Product product) {
            return product.name().charAt(0);
        }

        public static Comparator<Product> comparingByNameOfProducts() {
            return Comparator.comparing(Product::name);
        }
    }

    public SortedProducts getSortedProductsForACategory(Category category) {
        List<Product> productsForACategory = getProductsPerCategory().get(category);

        return productsForACategory.stream()
                .filter(SortedProducts::productHasFirstLetter)
                .sorted(SortedProducts.comparingByNameOfProducts())
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(), SortedProducts::new)
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

    public record NumberOfProductsPerCategory(Category category, long numberOfProducts) {
        public static boolean categoryHasProducts(Map.Entry<Category, Long> entry) {
            return entry.getValue() >= 1;
        }
    }

    public List<Category> getAllCategoriesWithProducts() {
        Map<Category, Long> numberOfProductsPerCategory = getNumberOfProductsPerCategory();

        return numberOfProductsPerCategory.entrySet().stream()
                .filter(NumberOfProductsPerCategory::categoryHasProducts)
                .map(Map.Entry::getKey)
                .toList();
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Map<Character, List<Product>> getProductsPerFirstLetter() {
        return groupingProducts(SortedProducts::getCharAt,
                productList.products().stream()
                        .filter(SortedProducts::productHasFirstLetter)
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

    record ProductsPerMonth(Month month, List<Product> products) {
        public static boolean productHasMaxRating(Product product, double maxRating) {
            return product.rating() == maxRating;
        }

        public static Comparator<Product> comparingByLocalDate() {
            return Comparator.comparing(Product::createdAt);
        }
    }

    public List<Product> getSortedProductsWithMaxRatingForThisMonthByCreatedAt(ThisMonth thisMonth) {
        Month currentMonth = thisMonth.currentMonth();
        List<Product> productsForAMonth = getProductsForAMonth(currentMonth);

        final double maxRating = getMaxRating(productsForAMonth);

        return productsForAMonth.stream()
                .filter(product -> ProductsPerMonth.productHasMaxRating(product, maxRating))
                .sorted(ProductsPerMonth.comparingByLocalDate().reversed())
                .toList();
    }

    public record ThisMonth(Month currentMonth) {
        public ThisMonth() {
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
