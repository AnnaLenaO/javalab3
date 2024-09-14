package service;

import entities.InputProductData;
import entities.ProductList;
import entities.ProductRecord;
import entities.Category;

import java.time.LocalDate;
import java.time.Month;
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
////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Map<UUID, List<ProductRecord>> getProductsPerId() {
        Map<UUID, List<ProductRecord>> productsPerId = productList.products().stream()
                .collect(Collectors.groupingBy(
                        ProductRecord::id));

        return Collections.unmodifiableMap(productsPerId);
    }

    public List<ProductRecord> getAProductForItsId(UUID id) {
        Map<UUID, List<ProductRecord>> productsPerId = getProductsPerId();
        return productsPerId.getOrDefault(id, List.of());
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Map<LocalDate, List<ProductRecord>> getProductsPerCreatedAt() {
        Map<LocalDate, List<ProductRecord>> productsPerCreatedAt = productList.products().stream()
                .collect(Collectors.groupingBy(
                        ProductRecord::createdAt));

        return Collections.unmodifiableMap(productsPerCreatedAt);
    }

    public record ProductDatesToCompareRecord(LocalDate dateOne, LocalDate dateTwo) {

        public static boolean isDateAfter(LocalDate dateOne, LocalDate dateTwo) {
            return dateOne.isAfter(dateTwo);
        }
    }

    public List<ProductRecord> getAllProductsCreatedAfterADate(LocalDate createdAt, LocalDate dateToCompare) {
        Map<LocalDate, List<ProductRecord>> allProductsForCreatedAt = getProductsPerCreatedAt();

        return allProductsForCreatedAt.entrySet().stream()
                .filter(_ -> ProductDatesToCompareRecord.isDateAfter(createdAt, dateToCompare))
                .flatMap(entry -> entry.getValue().stream())
                .toList();
    }

    public List<ProductRecord> getAllUpdatedProducts(LocalDate updatedAt, LocalDate createdAt) {
        Map<LocalDate, List<ProductRecord>> allProductsForCreatedAt = getProductsPerCreatedAt();

        return allProductsForCreatedAt.entrySet().stream()
                .filter(_ -> ProductDatesToCompareRecord.isDateAfter(updatedAt, createdAt))
                .flatMap(entry -> entry.getValue().stream())
                .toList();
    }
  ////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Map<Category, List<ProductRecord>> getProductsPerCategory() {
        Map<Category, List<ProductRecord>> productsPerCategory = productList.products().stream()
                .collect(Collectors.groupingBy(
                        ProductRecord::category));

        return Collections.unmodifiableMap(productsPerCategory);
    }

    record ProductsPerProductRecord(List<ProductRecord> productRecord) {
        public ProductsPerProductRecord(List<ProductRecord> productRecord) {
            this.productRecord = List.copyOf(productRecord);
        }

        public static boolean productHasFirstLetter(ProductRecord productRecord) {
            char firstLetter = getCharAt(productRecord);

            return Character.isLetter(firstLetter) && (firstLetter >= 'a' && firstLetter <= 'z');
        }

        private static char getCharAt(ProductRecord productRecord) {
            return productRecord.name().charAt(0);
        }

        public static Comparator<ProductRecord> comparingByNameOfProducts() {
            return Comparator.comparing(ProductRecord::name);
        }
    }

    private ProductsPerProductRecord getSortedProductsForACategory(Category category) {
        List<ProductRecord> productsForACategory = getProductsPerCategory().get(category);

        return productsForACategory.stream()
                .filter(ProductsPerProductRecord::productHasFirstLetter)
                .sorted(ProductsPerProductRecord.comparingByNameOfProducts())
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(), ProductsPerProductRecord::new)
                );
    }
 ////////////////////////////////////////////////////////////////////////////////////////////////////
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

        public static boolean categoryHasProducts(Map.Entry<Category, Long> entry) {
            return entry.getValue() > 0;
        }

        public static Comparator<NumberOfProductsPerCategoryRecord> comparingByNumberOfProducts() {
            return Comparator.comparing(NumberOfProductsPerCategoryRecord::numberOfProducts);
        }
    }

    private List<NumberOfProductsPerCategoryRecord> getAllCategoriesWithProducts() {
        Map<Category, Long> numberOfProductsPerCategory = getNumberOfProductsPerCategory();

        return numberOfProductsPerCategory.entrySet().stream()
                .filter(NumberOfProductsPerCategoryRecord::categoryHasProducts)
                .map(NumberOfProductsPerCategoryRecord::new)
                .toList();
    }
///////////////////////////////////////////////////////////////////////////////////////////////////////
    public Map<Character, List<ProductRecord>> getProductsPerFirstLetter() {
        Map<Character, List<ProductRecord>> productsPerFirstLetter = productList.products().stream()
                .filter(ProductsPerProductRecord::productHasFirstLetter)
                .collect(Collectors.groupingBy(
                        ProductsPerProductRecord::getCharAt));

        return Collections.unmodifiableMap(productsPerFirstLetter);
    }

    record ProductsPerFirstLetterRecord(char firstLetter, List<ProductRecord> productRecord) {
        public ProductsPerFirstLetterRecord(Map.Entry<Character, List<ProductRecord>> entry) {
            this(entry.getKey(), List.copyOf(entry.getValue()));
        }
    }

    private List<ProductsPerFirstLetterRecord> getProductsPerUniqueFirstLetter() {
        Map<Character, List<ProductRecord>> productsPerFirstLetter = getProductsPerFirstLetter();

        return productsPerFirstLetter.entrySet().stream()
                .map(ProductsPerFirstLetterRecord::new)
                .toList();
    }
 ////////////////////////////////////////////////////////////////////////////////////////////////////
    public Map<Month, List<ProductRecord>> getProductsPerMonth() {
        Map<Month, List<ProductRecord>> productsPerMonth = productList.products().stream()
                .collect(Collectors.groupingBy(
                        product -> product.createdAt().getMonth()));
        return Collections.unmodifiableMap(productsPerMonth);
    }

    public List<ProductRecord> getProductsForAMonth(Month month) {
        Map<Month, List<ProductRecord>> productsPerMonth = getProductsPerMonth();
        return productsPerMonth.getOrDefault(month, List.of());
    }

    record ProductsPerMonthRecord(Month month, List<ProductRecord> productRecord) {

        public static boolean productHasMaxRating(ProductRecord productRecord, double maxRating) {
            return productRecord.rating() == maxRating;
        }

        public static Comparator<ProductRecord> comparingByLocalDate() {
            return Comparator.comparing(ProductRecord::createdAt);
        }
    }

    private List<ProductRecord> getSortedProductsWithMaxRatingForThisMonth() {
        Month currentMonth = LocalDate.now().getMonth();
        List<ProductRecord> productsForAMonth = getProductsForAMonth(currentMonth);

        double maxRating = productsForAMonth.stream()
                .map(ProductRecord::rating)
                .max(Double::compareTo)
                .orElseThrow();

        return productsForAMonth.stream()
                .filter(product -> ProductsPerMonthRecord.productHasMaxRating(product, maxRating))
                .sorted(ProductsPerMonthRecord.comparingByLocalDate().reversed())
                .toList();
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

