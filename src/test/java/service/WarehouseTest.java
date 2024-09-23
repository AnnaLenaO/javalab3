package service;

import entities.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static entities.Category.*;
import static java.time.Month.SEPTEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class WarehouseTest {

    @ParameterizedTest(name = "Create new product to ProductRecord from input arguments")
    @MethodSource("inputProductDataProvider")
    void testCreateNewProduct(ArgumentsAccessor arguments) {

        Warehouse warehouse = new Warehouse();

        Product newProduct = warehouse.createNewProduct(new InputProductData(
                arguments.getString(0),
                arguments.get(1, Category.class),
                arguments.getDouble(2)
        ));

        assertThat(newProduct).isNotNull();
        assertThat(newProduct.id()).isNotNull();
        assertThat(newProduct.name()).isEqualTo(arguments.getString(0));
        assertThat(newProduct.category()).isEqualTo(arguments.get(1, Category.class));
        assertThat(newProduct.rating()).isEqualTo(arguments.getDouble(2));
        assertThat(newProduct.createdAt()).isNotNull();
        assertThat(newProduct.updatedAt()).isNotNull();
    }

    @ParameterizedTest(name = "Add new ProductRecord to ProductList from input arguments")
    @MethodSource("inputProductDataProvider")
    void testAddAndGetNewProduct(ArgumentsAccessor arguments) {

        Warehouse warehouse = new Warehouse();

        InputProductData inputProductData = new InputProductData(
                arguments.getString(0),
                arguments.get(1, Category.class),
                arguments.getDouble(2)
        );

        warehouse.addNewProduct(inputProductData);
        Product createdProductRecord = warehouse.createNewProduct(inputProductData);
        List<Product> returnedProductRecord = warehouse.getProductList();

        assertThat(warehouse.getProductList().size()).isGreaterThanOrEqualTo(1);
        assertThat(returnedProductRecord.getFirst().id()).isNotNull();
        assertThat(returnedProductRecord.getFirst().name()).isEqualTo(inputProductData.name());
        assertThat(returnedProductRecord.getFirst().category()).isEqualTo(inputProductData.category());
        assertThat(returnedProductRecord.getFirst().rating()).isEqualTo(inputProductData.rating());
        assertThat(returnedProductRecord.getFirst().createdAt()).isEqualTo(createdProductRecord.createdAt());
        assertThat(returnedProductRecord.getFirst().updatedAt()).isEqualTo(createdProductRecord.updatedAt());
    }

    @ParameterizedTest(name = "{index} - Group products per attribute")
    @MethodSource("productDataProvider")
    void testGenericGroupingProducts(Function<Product, ?> function, List<Product> productList,
                                     Map<?, List<Product>> groupedByProductDataProvider) {

        Warehouse warehouse = new Warehouse();

        Map<?, List<Product>> groupedTestResult =
                ReflectionTestUtils.invokeMethod(warehouse, "groupingProducts", function, productList);

        assertThat(groupedTestResult).isNotNull();
        assertThat(groupedTestResult.size()).isGreaterThanOrEqualTo(1);
        assertThat(groupedTestResult).isEqualTo(groupedByProductDataProvider);
    }

    @ParameterizedTest(name = "{index} - Group and count products per attribute")
    @MethodSource("productDataCountingProvider")
    void testGenericGroupingAndCountingProducts(Function<Product, ?> function, List<Product> productList,
                                                Map<?, Long> countedByProductDataProvider) {

        Warehouse warehouse = new Warehouse();

        Map<?, Long> groupedTestResult =
                ReflectionTestUtils.invokeMethod(warehouse, "numberOfGroupedProducts", function, productList);

        assertThat(groupedTestResult).isNotNull();
        assertThat(groupedTestResult.size()).isGreaterThanOrEqualTo(1);
        assertThat(groupedTestResult).isEqualTo(countedByProductDataProvider);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @ParameterizedTest(name = "Group products per id")
    @MethodSource("productListProvider")
    void testGetProductsPerId(List<Product> productListRecord,
                              Map<UUID, List<Product>> groupedByIdProviderExpected,
                              Map<String, List<Product>> groupedByNameProviderExpected,
                              Map<Category, List<Product>> groupedByCategoryProviderExpected,
                              Map<LocalDate, List<Product>> groupedByCreatedAtProviderExpected,
                              Map<LocalDate, List<Product>> groupedByUpdatedAtProviderExpected) {

        ProductList productList = new ProductList(productListRecord);
        Warehouse warehouse = new Warehouse(productList);

        Map<UUID, List<Product>> groupedTestResult = warehouse.getProductsPerId();

        assertThat(groupedTestResult).isNotNull();
        assertThat(groupedTestResult.size()).isEqualTo(8);
        assertThat(groupedTestResult).containsExactlyInAnyOrderEntriesOf(groupedByIdProviderExpected);
    }

    @ParameterizedTest(name = "Group products per created at")
    @MethodSource("productListProvider")
    void testGetProductsPerCreatedAt(List<Product> productListRecord,
                                     Map<UUID, List<Product>> groupedByIdProviderExpected,
                                     Map<String, List<Product>> groupedByNameProviderExpected,
                                     Map<Category, List<Product>> groupedByCategoryProviderExpected,
                                     Map<LocalDate, List<Product>> groupedByCreatedAtProviderExpected,
                                     Map<LocalDate, List<Product>> groupedByUpdatedAtProviderExpected) {

        ProductList productList = new ProductList(productListRecord);
        Warehouse warehouse = new Warehouse(productList);

        Map<LocalDate, List<Product>> groupedTestResult = warehouse.getProductsPerCreatedAt();

        assertThat(groupedTestResult).isNotNull();
        assertThat(groupedTestResult.size()).isEqualTo(5);
        assertThat(groupedTestResult).containsExactlyInAnyOrderEntriesOf(groupedByCreatedAtProviderExpected);
    }

    @ParameterizedTest(name = "Group products per category")
    @MethodSource("productListProvider")
    void testGetProductsPerCategory(List<Product> productListRecord,
                                    Map<UUID, List<Product>> groupedByIdProviderExpected,
                                    Map<String, List<Product>> groupedByNameProviderExpected,
                                    Map<Category, List<Product>> groupedByCategoryProviderExpected,
                                    Map<LocalDate, List<Product>> groupedByCreatedAtProviderExpected,
                                    Map<LocalDate, List<Product>> groupedByUpdatedAtProviderExpected) {

        ProductList productList = new ProductList(productListRecord);
        Warehouse warehouse = new Warehouse(productList);

        Map<Category, List<Product>> groupedTestResult = warehouse.getProductsPerCategory();

        assertThat(groupedTestResult).isNotNull();
        assertThat(groupedTestResult.size()).isEqualTo(5);
        assertThat(groupedTestResult).containsExactlyInAnyOrderEntriesOf(groupedByCategoryProviderExpected);
    }

    @ParameterizedTest(name = "Product for an id")
    @MethodSource("productListProvider")
    void testGetProductForItsId(List<Product> productListRecord) {

        ProductList productList = new ProductList(productListRecord);
        Warehouse warehouse = new Warehouse(productList);
        UUID productId = UUID.fromString("e1c63601-999c-48d8-8900-cbc4b870db2e");

        Optional<Product> singleTestResult = warehouse.getAProductForItsId(productId);

        assertThat(singleTestResult).isNotNull();
        assertThat(singleTestResult.isPresent()).isTrue();
        assertThat((singleTestResult.get()).id()).isEqualTo(productId);
        assertThat((singleTestResult.get()).name()).isEqualTo("Wasagaming");
        assertThat((singleTestResult.get()).name()).containsOnlyOnce("Wasagaming");
    }

    @ParameterizedTest(name = "Change product name, category & rating")
    @MethodSource("productListProvider")
    void testChangeProductNameCategoryRating(List<Product> productListRecord) {

        ProductList productList = new ProductList(productListRecord);
        Warehouse warehouse = new Warehouse(productList);
        UUID productId = UUID.fromString("e1c63601-999c-48d8-8900-cbc4b870db2e");

        InputProductData inputProductData = new InputProductData(
                "Aimable Amie",
                GALLICA,
                10.0
        );

        Product singleTestResult =
                warehouse.changeProductNameCategoryRating(productId, inputProductData);

        assertThat(singleTestResult).isNotNull();
        assertThat(singleTestResult)
                .extracting("id").isEqualTo(UUID.fromString("e1c63601-999c-48d8-8900-cbc4b870db2e"));
        assertThat(singleTestResult)
                .extracting("name").isEqualTo("Aimable Amie");
        assertThat(singleTestResult)
                .extracting("category").isEqualTo(GALLICA);
        assertThat(singleTestResult)
                .extracting("rating").isEqualTo(10.0);
        assertThat(singleTestResult)
                .extracting("createdAt").isEqualTo(LocalDate.parse("2024-09-15"));
        assertThat(singleTestResult)
                .extracting("updatedAt").isEqualTo(LocalDate.now());
    }

    @ParameterizedTest(name = "Record compare two dates")
    @MethodSource("dateProvider")
    void testProductDatesToCompareRecord(LocalDate dateOne, LocalDate dateTwo, boolean resultExpected) {

        Warehouse.ProductDatesToCompare compareRecordContent =
                new Warehouse.ProductDatesToCompare(dateOne, dateTwo);

        boolean testResultCompareRecord =
                Warehouse.ProductDatesToCompare.isDateAfter(dateOne, dateTwo);

        assertThat(compareRecordContent).isNotNull();
        assertThat(testResultCompareRecord).isEqualTo(resultExpected);
    }

    @ParameterizedTest(name = "Filter products created after a date")
    @MethodSource("productListProvider")
    void testGetFilteredProductsByDate(List<Product> productListRecord) {

        ProductList productList = new ProductList(productListRecord);
        Warehouse warehouse = new Warehouse(productList);
        LocalDate dateTwo = LocalDate.parse("2024-02-10");
        List<Product> filteredTestResult = warehouse.getFilteredProductsByDate(dateTwo);

        assertThat(filteredTestResult).isNotNull();
        assertThat(filteredTestResult.size()).isEqualTo(5);
        assertThat(filteredTestResult)
                .extracting("name")
                .containsOnlyOnce("New Dawn");
        assertThat(filteredTestResult)
                .extracting("category")
                .containsOnlyOnce(RAMBLER);
        assertThat(filteredTestResult)
                .extracting("name")
                .doesNotContain("Duchesse De Montebello");
    }

    @ParameterizedTest(name = "Filter all updated products")
    @MethodSource("productListProvider")
    void testGetAllUpdatedProducts(List<Product> productListRecord) {

        ProductList productList = new ProductList(productListRecord);
        Warehouse warehouse = new Warehouse(productList);
        List<Product> filteredTestResult = warehouse.getAllUpdatedProducts();

        assertThat(filteredTestResult).isNotNull();
        assertThat(filteredTestResult.size()).isEqualTo(5);
        assertThat(filteredTestResult)
                .extracting("name")
                .containsOnlyOnce("Ispahan");
        assertThat(filteredTestResult)
                .extracting("category")
                .containsOnlyOnce(RUGOSA);
        assertThat(filteredTestResult)
                .extracting("name")
                .doesNotContain("Hippolyte");
        assertThat(filteredTestResult)
                .extracting("category")
                .containsSequence(RAMBLER, RAMBLER);
    }

    @ParameterizedTest(name = "Sort products for a category")
    @MethodSource("productListProvider")
    void testGetSortedProductsForACategory(List<Product> productListRecord) {

        ProductList productList = new ProductList(productListRecord);
        Warehouse warehouse = new Warehouse(productList);

        List<Product> sortedTestResult = warehouse.getSortedProductsForACategory(RAMBLER).product();

        assertThat(sortedTestResult).isNotNull();
        assertThat(sortedTestResult.size()).isEqualTo(3);
        assertThat(sortedTestResult)
                .extracting("name")
                .isSorted();
        assertThat(sortedTestResult)
                .extracting("name")
                .containsSequence("Helenae Hybrida", "Lyckefund", "New Dawn");
    }

    @ParameterizedTest(name = "Number of products per category")
    @MethodSource("productListProvider")
    void testGetNumberOfProductsPerCategory(List<Product> productListRecord) {

        ProductList productList = new ProductList(productListRecord);
        Warehouse warehouse = new Warehouse(productList);

        Map<Category, Long> numberOfProductsResult = warehouse.getNumberOfProductsPerCategory();
        System.out.println(numberOfProductsResult);

        assertThat(numberOfProductsResult).isNotNull();
        assertThat(numberOfProductsResult.size()).isEqualTo(5);
        assertThat(numberOfProductsResult.get(GALLICA)).isEqualTo(2);
        assertThat(numberOfProductsResult.get(RAMBLER)).isEqualTo(3);
        assertThat(numberOfProductsResult.get(RUGOSA)).isEqualTo(1);
        assertThat(numberOfProductsResult.get(CANADIAN)).isEqualTo(1);
        assertThat(numberOfProductsResult.get(DAMASCENE)).isEqualTo(1);
    }

    @ParameterizedTest(name = "Number of products for a category")
    @MethodSource("productListProvider")
    void testGetNumberOfProductsForACategory(List<Product> productListRecord) {

        ProductList productList = new ProductList(productListRecord);
        Warehouse warehouse = new Warehouse(productList);

        long numberOfProductsResultA = warehouse.getNumberOfProductsForACategory(RAMBLER);
        long numberOfProductsResultB = warehouse.getNumberOfProductsForACategory(CANADIAN);
        long numberOfProductsResultC = warehouse.getNumberOfProductsForACategory(GALLICA);
        long numberOfProductsResultD = warehouse.getNumberOfProductsForACategory(DAMASCENE);
        long numberOfProductsResultE = warehouse.getNumberOfProductsForACategory(RUGOSA);

        assertThat(numberOfProductsResultA).isEqualTo(3);
        assertThat(numberOfProductsResultB).isEqualTo(1);
        assertThat(numberOfProductsResultC).isEqualTo(2);
        assertThat(numberOfProductsResultD).isEqualTo(1);
        assertThat(numberOfProductsResultE).isEqualTo(1);
    }


    @ParameterizedTest(name = "All categories with products")
    @MethodSource("productListProvider")
    void testGetAllCategoriesWithProducts(List<Product> productListRecord) {

        ProductList productList = new ProductList(productListRecord);
        Warehouse warehouse = new Warehouse(productList);

        List<Category> categoriesWithProductsResult = warehouse.getAllCategoriesWithProducts();

        assertThat(categoriesWithProductsResult).isNotNull();
        assertThat(categoriesWithProductsResult.size()).isEqualTo(5);
        assertThat(categoriesWithProductsResult)
                .containsOnlyOnce(RAMBLER, RUGOSA, DAMASCENE, GALLICA, CANADIAN);
    }

    @ParameterizedTest(name = "Grouped products per unique first letter")
    @MethodSource("productListProvider")
    void testGetProductsPerFirstLetter(List<Product> productListRecord) {

        ProductList productList = new ProductList(productListRecord);
        Warehouse warehouse = new Warehouse(productList);

        Map<Character, List<Product>> productsPerFirstLetterResult =
                warehouse.getProductsPerFirstLetter();

        assertThat(productsPerFirstLetterResult).isNotNull();
        assertThat(productsPerFirstLetterResult.size()).isEqualTo(6);
        assertThat(productsPerFirstLetterResult).containsOnlyKeys('D', 'H', 'L', 'N', 'I', 'W');
        assertThat(productsPerFirstLetterResult.get('D')).isNotNull().hasSize(1);
        assertThat(productsPerFirstLetterResult.get('H')).isNotNull().hasSize(2);
        assertThat(productsPerFirstLetterResult.get('L')).isNotNull().hasSize(2);
        assertThat(productsPerFirstLetterResult.get('N')).isNotNull().hasSize(1);
        assertThat(productsPerFirstLetterResult.get('I')).isNotNull().hasSize(1);
        assertThat(productsPerFirstLetterResult.get('W')).isNotNull().hasSize(1);
        assertThat(productsPerFirstLetterResult.values().stream()
                .mapToInt(List::size).summaryStatistics().getSum()).isEqualTo(8);
    }

    @ParameterizedTest(name = "Grouped products per month by createdAt")
    @MethodSource("productListProvider")
    void testGetProductsPerCreatedAtMonth(List<Product> productListRecord) {

        ProductList productList = new ProductList(productListRecord);
        Warehouse warehouse = new Warehouse(productList);

        Map<Month, List<Product>> productsPerMonthResult =
                warehouse.getProductsPerCreatedAtMonth();

        assertThat(productsPerMonthResult).isNotNull();
        assertThat(productsPerMonthResult.size()).isEqualTo(3);
        assertThat(productsPerMonthResult.get(Month.FEBRUARY)).hasSize(3);
        assertThat(productsPerMonthResult.get(Month.JUNE)).hasSize(1);
        assertThat(productsPerMonthResult.get(SEPTEMBER)).hasSize(4);
        assertThat(productsPerMonthResult.values().stream()
                .mapToInt(List::size).summaryStatistics().getSum()).isEqualTo(8);

        assertThat(productsPerMonthResult.get(Month.FEBRUARY))
                .extracting("name").doesNotContain(
                        "New Dawn", "Hippolyte", "Wasagaming", "Louise Bugnet", "Ispahan");

        assertThat(productsPerMonthResult.get(Month.JUNE))
                .extracting("name").containsOnly(
                        "New Dawn");

        assertThat(productsPerMonthResult.get(SEPTEMBER))
                .extracting("name").containsOnly(
                        "Hippolyte", "Wasagaming", "Louise Bugnet", "Ispahan");
    }

    @ParameterizedTest(name = "Grouped products for a month by createdAt")
    @MethodSource("productListProvider")
    void testGetProductsForAMonth(List<Product> productListRecord) {

        ProductList productList = new ProductList(productListRecord);
        Warehouse warehouse = new Warehouse(productList);

        List<Product> productForAMonthResultA = warehouse.getProductsForAMonth(Month.FEBRUARY);
        List<Product> productForAMonthResultB = warehouse.getProductsForAMonth(Month.JUNE);
        List<Product> productForAMonthResultC = warehouse.getProductsForAMonth(SEPTEMBER);
        List<Product> productForAMonthResultD = warehouse.getProductsForAMonth(Month.DECEMBER);

        assertThat(productForAMonthResultA).isNotNull().hasSize(3);
        assertThat(productForAMonthResultB).isNotNull().hasSize(1);
        assertThat(productForAMonthResultC).isNotNull().hasSize(4);
        assertThat(productForAMonthResultD).isNotNull().hasSize(0);

        assertThat(productForAMonthResultA).extracting("name")
                .containsOnly("Lyckefund", "Helenae Hybrida", "Duchesse De Montebello");

        assertThat(productForAMonthResultB).extracting("name")
                .containsOnly("New Dawn");

        assertThat(productForAMonthResultC).extracting("name")
                .doesNotContain("New Dawn", "Lyckefund", "Helenae Hybrida", "Duchesse De Montebello");
    }

    @ParameterizedTest(name = "Sort products with max rating for a month by createdAt")
    @MethodSource("productListProvider")
    void testGetSortedProductsWithMaxRatingForThisMonth(List<Product> productListRecord) {

        ProductList productList = new ProductList(productListRecord);
        Warehouse warehouse = new Warehouse(productList);
        Warehouse.ThisMonth currentMonthRecord = new Warehouse.ThisMonth(SEPTEMBER);

        List<Product> sortedTestResult =
                warehouse.getSortedProductsWithMaxRatingForThisMonthByCreatedAt(currentMonthRecord);

        assertThat(sortedTestResult).isNotNull();
        assertThat(sortedTestResult.size()).isEqualTo(2);
        assertThat(sortedTestResult)
                .extracting("name")
                .containsOnly("Louise Bugnet", "Hippolyte");
        assertThat(sortedTestResult)
                .extracting("name")
                .containsSequence("Hippolyte", "Louise Bugnet");
    }

    static Stream<Arguments> inputProductDataProvider() {
        return Stream.of(
                arguments("Hippolyte", GALLICA, 10.0),
                arguments("Wasagaming", RUGOSA, 6.3)
        );
    }

    static Stream<Arguments> dateProvider() {
        return Stream.of(
                arguments(LocalDate.parse("2024-02-10"), LocalDate.parse("2024-02-10"), false),
                arguments(LocalDate.parse("2024-02-10"), LocalDate.parse("2024-02-11"), false),
                arguments(LocalDate.parse("2024-02-10"), LocalDate.parse("2024-02-09"), true)
        );
    }

    static Stream<Arguments> productListProvider() {
        List<Product> productListRecord = Arrays.asList(
                new Product(UUID.fromString("bc108fc2-6785-40c4-9392-b0e93358b26e"), "Hippolyte",
                        GALLICA, 10.0, LocalDate.parse("2024-09-16"), LocalDate.parse("2024-09-16")),
                new Product(UUID.fromString("51feaf5d-2972-44ec-a78a-5d8a8e1be1e9"), "Lyckefund",
                        RAMBLER, 8.7, LocalDate.parse("2024-02-10"), LocalDate.parse("2024-05-19")),
                new Product(UUID.fromString("e1c63601-999c-48d8-8900-cbc4b870db2e"), "Wasagaming",
                        RUGOSA, 6.3, LocalDate.parse("2024-09-15"), LocalDate.parse("2024-09-16")),
                new Product(UUID.fromString("4ae21842-25d3-43f2-b502-626a25ac8f96"), "New Dawn",
                        RAMBLER, 9.1, LocalDate.parse("2024-06-10"), LocalDate.parse("2024-05-19")),
                new Product(UUID.fromString("25c20fc8-7d2c-4a16-94e3-9e797ea8472c"), "Helenae Hybrida",
                        RAMBLER, 7.3, LocalDate.parse("2024-02-10"), LocalDate.parse("2024-07-19")),
                new Product(UUID.fromString("1a39744d-fe31-46d4-bb2f-3a62353581dc"), "Duchesse De Montebello",
                        GALLICA, 9.8, LocalDate.parse("2024-02-10"), LocalDate.parse("2024-06-04")),
                new Product(UUID.fromString("4c00b99d-5106-4ebb-be9b-14c69b58b3a5"), "Louise Bugnet",
                        CANADIAN, 10.0, LocalDate.parse("2024-09-01"), LocalDate.parse("2024-09-01")),
                new Product(UUID.fromString("aad34fe5-9994-42e6-baa9-e6d42340627c"), "Ispahan",
                        DAMASCENE, 9.9, LocalDate.parse("2024-09-01"), LocalDate.parse("2024-09-04"))
        );

        Map<UUID, List<Product>> groupedByIdProviderExpected = new HashMap<>();
        Map<String, List<Product>> groupedByNameProviderExpected = new HashMap<>();
        Map<Category, List<Product>> groupedByCategoryProviderExpected = new HashMap<>();
        Map<LocalDate, List<Product>> groupedByCreatedAtProviderExpected = new HashMap<>();
        Map<LocalDate, List<Product>> groupedByUpdatedAtProviderExpected = new HashMap<>();

        for (Product productRecord : productListRecord) {
            groupedByIdProviderExpected.computeIfAbsent(productRecord.id(), k -> new ArrayList<>()).add(productRecord);
            groupedByNameProviderExpected.computeIfAbsent(productRecord.name(), k -> new ArrayList<>()).add(productRecord);
            groupedByCategoryProviderExpected.computeIfAbsent(productRecord.category(), k -> new ArrayList<>()).add(productRecord);
            groupedByCreatedAtProviderExpected.computeIfAbsent(productRecord.createdAt(), k -> new ArrayList<>()).add(productRecord);
            groupedByUpdatedAtProviderExpected.computeIfAbsent(productRecord.updatedAt(), k -> new ArrayList<>()).add(productRecord);
        }

        return Stream.of(
                arguments(productListRecord, groupedByIdProviderExpected, groupedByNameProviderExpected,
                        groupedByCategoryProviderExpected, groupedByCreatedAtProviderExpected,
                        groupedByUpdatedAtProviderExpected)
        );
    }

    static Stream<Arguments> productDataProvider() {
        List<Product> productListRecord = Arrays.asList(
                new Product(UUID.fromString("bc108fc2-6785-40c4-9392-b0e93358b26e"), "Hippolyte",
                        GALLICA, 10.0, LocalDate.parse("2024-09-16"), LocalDate.parse("2024-09-16")),
                new Product(UUID.fromString("51feaf5d-2972-44ec-a78a-5d8a8e1be1e9"), "Lyckefund",
                        RAMBLER, 8.7, LocalDate.parse("2024-02-10"), LocalDate.parse("2024-05-19")),
                new Product(UUID.fromString("e1c63601-999c-48d8-8900-cbc4b870db2e"), "Wasagaming",
                        RUGOSA, 6.3, LocalDate.parse("2024-09-15"), LocalDate.parse("2024-09-16")),
                new Product(UUID.fromString("4ae21842-25d3-43f2-b502-626a25ac8f96"), "New Dawn",
                        RAMBLER, 9.1, LocalDate.parse("2024-06-10"), LocalDate.parse("2024-05-19")),
                new Product(UUID.fromString("25c20fc8-7d2c-4a16-94e3-9e797ea8472c"), "Helenae Hybrida",
                        RAMBLER, 7.3, LocalDate.parse("2024-02-10"), LocalDate.parse("2024-07-19")),
                new Product(UUID.fromString("1a39744d-fe31-46d4-bb2f-3a62353581dc"), "Duchesse De Montebello",
                        GALLICA, 9.8, LocalDate.parse("2024-02-10"), LocalDate.parse("2024-06-04")),
                new Product(UUID.fromString("4c00b99d-5106-4ebb-be9b-14c69b58b3a5"), "Louise Bugnet",
                        CANADIAN, 10.0, LocalDate.parse("2024-09-01"), LocalDate.parse("2024-09-01")),
                new Product(UUID.fromString("aad34fe5-9994-42e6-baa9-e6d42340627c"), "Ispahan",
                        DAMASCENE, 9.9, LocalDate.parse("2024-09-01"), LocalDate.parse("2024-09-04"))
        );

        Map<UUID, List<Product>> groupedByIdProviderExpected = new HashMap<>();
        Map<String, List<Product>> groupedByNameProviderExpected = new HashMap<>();
        Map<Category, List<Product>> groupedByCategoryProviderExpected = new HashMap<>();
        Map<LocalDate, List<Product>> groupedByCreatedAtProviderExpected = new HashMap<>();
        Map<LocalDate, List<Product>> groupedByUpdatedAtProviderExpected = new HashMap<>();

        for (Product productRecord : productListRecord) {
            groupedByIdProviderExpected.computeIfAbsent(productRecord.id(), k -> new ArrayList<>()).add(productRecord);
            groupedByNameProviderExpected.computeIfAbsent(productRecord.name(), k -> new ArrayList<>()).add(productRecord);
            groupedByCategoryProviderExpected.computeIfAbsent(productRecord.category(), k -> new ArrayList<>()).add(productRecord);
            groupedByCreatedAtProviderExpected.computeIfAbsent(productRecord.createdAt(), k -> new ArrayList<>()).add(productRecord);
            groupedByUpdatedAtProviderExpected.computeIfAbsent(productRecord.updatedAt(), k -> new ArrayList<>()).add(productRecord);
        }

        return Stream.of(
                arguments((Function<Product, UUID>) Product::id, productListRecord,
                        groupedByIdProviderExpected),
                arguments((Function<Product, String>) Product::name, productListRecord,
                        groupedByNameProviderExpected),
                arguments((Function<Product, Category>) Product::category, productListRecord,
                        groupedByCategoryProviderExpected),
                arguments((Function<Product, LocalDate>) Product::createdAt, productListRecord,
                        groupedByCreatedAtProviderExpected),
                arguments((Function<Product, LocalDate>) Product::updatedAt, productListRecord,
                        groupedByUpdatedAtProviderExpected)
        );
    }

    static Stream<Arguments> productDataCountingProvider() {
        List<Product> productListRecord = Arrays.asList(
                new Product(UUID.fromString("bc108fc2-6785-40c4-9392-b0e93358b26e"), "Hippolyte",
                        GALLICA, 10.0, LocalDate.parse("2024-09-16"), LocalDate.parse("2024-09-16")),
                new Product(UUID.fromString("51feaf5d-2972-44ec-a78a-5d8a8e1be1e9"), "Lyckefund",
                        RAMBLER, 8.7, LocalDate.parse("2024-02-10"), LocalDate.parse("2024-05-19")),
                new Product(UUID.fromString("e1c63601-999c-48d8-8900-cbc4b870db2e"), "Wasagaming",
                        RUGOSA, 6.3, LocalDate.parse("2024-09-15"), LocalDate.parse("2024-09-16")),
                new Product(UUID.fromString("4ae21842-25d3-43f2-b502-626a25ac8f96"), "New Dawn",
                        RAMBLER, 9.1, LocalDate.parse("2024-06-10"), LocalDate.parse("2024-05-19")),
                new Product(UUID.fromString("25c20fc8-7d2c-4a16-94e3-9e797ea8472c"), "Helenae Hybrida",
                        RAMBLER, 7.3, LocalDate.parse("2024-02-10"), LocalDate.parse("2024-07-19")),
                new Product(UUID.fromString("1a39744d-fe31-46d4-bb2f-3a62353581dc"), "Duchesse De Montebello",
                        GALLICA, 9.8, LocalDate.parse("2024-02-10"), LocalDate.parse("2024-06-04")),
                new Product(UUID.fromString("4c00b99d-5106-4ebb-be9b-14c69b58b3a5"), "Louise Bugnet",
                        CANADIAN, 10.0, LocalDate.parse("2024-09-01"), LocalDate.parse("2024-09-01")),
                new Product(UUID.fromString("aad34fe5-9994-42e6-baa9-e6d42340627c"), "Ispahan",
                        DAMASCENE, 9.9, LocalDate.parse("2024-09-01"), LocalDate.parse("2024-09-04"))
        );

        Map<UUID, Long> countedByIdProviderExpected = productListRecord.stream()
                .collect(Collectors.groupingBy(Product::id, Collectors.counting()));
        Map<String, Long> countedByNameProviderExpected = productListRecord.stream()
                .collect(Collectors.groupingBy(Product::name, Collectors.counting()));
        Map<Category, Long> countedByCategoryProviderExpected = productListRecord.stream()
                .collect(Collectors.groupingBy(Product::category, Collectors.counting()));
        Map<LocalDate, Long> countedByCreatedAtProviderExpected = productListRecord.stream()
                .collect(Collectors.groupingBy(Product::createdAt, Collectors.counting()));
        Map<LocalDate, Long> countedByUpdatedAtProviderExpected = productListRecord.stream()
                .collect(Collectors.groupingBy(Product::updatedAt, Collectors.counting()));

        return Stream.of(
                arguments((Function<Product, UUID>) Product::id, productListRecord,
                        countedByIdProviderExpected),
                arguments((Function<Product, String>) Product::name, productListRecord,
                        countedByNameProviderExpected),
                arguments((Function<Product, Category>) Product::category, productListRecord,
                        countedByCategoryProviderExpected),
                arguments((Function<Product, LocalDate>) Product::createdAt, productListRecord,
                        countedByCreatedAtProviderExpected),
                arguments((Function<Product, LocalDate>) Product::updatedAt, productListRecord,
                        countedByUpdatedAtProviderExpected)
        );
    }
}
