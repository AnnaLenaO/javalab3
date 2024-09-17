package service;

import entities.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static entities.Category.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class WarehouseTest {

    @ParameterizedTest(name = "Create new product to ProductRecord from input arguments")
    @MethodSource("inputProductDataProvider")
    void testCreateNewProduct(ArgumentsAccessor arguments) {

        Warehouse warehouse = new Warehouse();

        ProductRecord newProduct = warehouse.createNewProduct(new InputProductData(
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
        ProductRecord createdProductRecord = warehouse.createNewProduct(inputProductData);
        List<ProductRecord> returnedProductRecord = warehouse.getProductListRecord();

        assertThat(warehouse.getProductListRecord().size()).isGreaterThanOrEqualTo(1);
        assertThat(returnedProductRecord.getFirst().id()).isNotNull();
        assertThat(returnedProductRecord.getFirst().name()).isEqualTo(inputProductData.name());
        assertThat(returnedProductRecord.getFirst().category()).isEqualTo(inputProductData.category());
        assertThat(returnedProductRecord.getFirst().rating()).isEqualTo(inputProductData.rating());
        assertThat(returnedProductRecord.getFirst().createdAt()).isEqualTo(createdProductRecord.createdAt());
        assertThat(returnedProductRecord.getFirst().updatedAt()).isEqualTo(createdProductRecord.updatedAt());
    }

    @ParameterizedTest(name = "{index} - Group products per attribute")
    @MethodSource("productDataProvider")
    void testGenericGroupingProducts(Function<ProductRecord, ?> function, List<ProductRecord> productList,
                                     Map<?, List<ProductRecord>> groupedByProductDataProvider) {

        Warehouse warehouse = new Warehouse();

        Map<?, List<ProductRecord>> groupedTestResult = warehouse.groupingProducts(function, productList);

        assertThat(groupedTestResult).isNotNull();
        assertThat(groupedTestResult.size()).isGreaterThanOrEqualTo(1);
        assertThat(groupedTestResult).isEqualTo(groupedByProductDataProvider);
    }

    @ParameterizedTest(name = "{index} - Group and count products per attribute")
    @MethodSource("productDataCountingProvider")
    void testGenericGroupingAndCountingProducts(Function<ProductRecord, ?> function, List<ProductRecord> productList,
                                                Map<?, Long> countedByProductDataProvider) {

        Warehouse warehouse = new Warehouse();

        Map<?, Long> groupedTestResult = warehouse.numberOfGroupedProducts(function, productList);

        assertThat(groupedTestResult).isNotNull();
        assertThat(groupedTestResult.size()).isGreaterThanOrEqualTo(1);
        assertThat(groupedTestResult).isEqualTo(countedByProductDataProvider);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @ParameterizedTest(name = "Group products per id")
    @MethodSource("productListProvider")
    void testGetProductsPerId(List<ProductRecord> productListRecord,
                              Map<UUID, List<ProductRecord>> groupedByIdProviderExpected,
                              Map<String, List<ProductRecord>> groupedByNameProviderExpected,
                              Map<Category, List<ProductRecord>> groupedByCategoryProviderExpected,
                              Map<LocalDate, List<ProductRecord>> groupedByCreatedAtProviderExpected,
                              Map<LocalDate, List<ProductRecord>> groupedByUpdatedAtProviderExpected) {

        ProductList productList = new ProductList(productListRecord);
        Warehouse warehouse = new Warehouse(productList);

        Map<UUID, List<ProductRecord>> groupedTestResult = warehouse.getProductsPerId();

        assertThat(groupedTestResult).isNotNull();
        assertThat(groupedTestResult.size()).isEqualTo(8);
        assertThat(groupedTestResult).containsExactlyInAnyOrderEntriesOf(groupedByIdProviderExpected);
    }

    @ParameterizedTest(name = "Group products per id")
    @MethodSource("productListProvider")
    void testGetProductsPerCreatedAt(List<ProductRecord> productListRecord,
                                     Map<UUID, List<ProductRecord>> groupedByIdProviderExpected,
                                     Map<String, List<ProductRecord>> groupedByNameProviderExpected,
                                     Map<Category, List<ProductRecord>> groupedByCategoryProviderExpected,
                                     Map<LocalDate, List<ProductRecord>> groupedByCreatedAtProviderExpected,
                                     Map<LocalDate, List<ProductRecord>> groupedByUpdatedAtProviderExpected) {

        ProductList productList = new ProductList(productListRecord);
        Warehouse warehouse = new Warehouse(productList);

        Map<LocalDate, List<ProductRecord>> groupedTestResult = warehouse.getProductsPerCreatedAt();

        assertThat(groupedTestResult).isNotNull();
        assertThat(groupedTestResult.size()).isEqualTo(5);
        assertThat(groupedTestResult).containsExactlyInAnyOrderEntriesOf(groupedByCreatedAtProviderExpected);
    }

    @ParameterizedTest(name = "Group products per id")
    @MethodSource("productListProvider")
    void testGetProductsPerCategory(List<ProductRecord> productListRecord,
                                    Map<UUID, List<ProductRecord>> groupedByIdProviderExpected,
                                    Map<String, List<ProductRecord>> groupedByNameProviderExpected,
                                    Map<Category, List<ProductRecord>> groupedByCategoryProviderExpected,
                                    Map<LocalDate, List<ProductRecord>> groupedByCreatedAtProviderExpected,
                                    Map<LocalDate, List<ProductRecord>> groupedByUpdatedAtProviderExpected) {

        ProductList productList = new ProductList(productListRecord);
        Warehouse warehouse = new Warehouse(productList);

        Map<Category, List<ProductRecord>> groupedTestResult = warehouse.getProductsPerCategory();

        assertThat(groupedTestResult).isNotNull();
        assertThat(groupedTestResult.size()).isEqualTo(5);
        assertThat(groupedTestResult).containsExactlyInAnyOrderEntriesOf(groupedByCategoryProviderExpected);
    }

    @ParameterizedTest(name = "Group products per id")
    @MethodSource("productListProvider")
    void testGetProductForItsId(List<ProductRecord> productListRecord) {

        ProductList productList = new ProductList(productListRecord);
        Warehouse warehouse = new Warehouse(productList);
        UUID productId = UUID.fromString("e1c63601-999c-48d8-8900-cbc4b870db2e");

        List<ProductRecord> singleTestResult = warehouse.getAProductForItsId(productId);

        assertThat(singleTestResult).isNotNull();
        assertThat(singleTestResult.size()).isEqualTo(1);
        assertThat(singleTestResult)
                .extracting("id")
                .singleElement()
                .isEqualTo(productId);
        assertThat(singleTestResult)
                .extracting("name")
                .singleElement()
                .isEqualTo("Wasagaming");
        assertThat(singleTestResult)
                .extracting("name")
                .containsOnlyOnce("Wasagaming");
    }

    @ParameterizedTest(name = "Record compare two dates")
    @MethodSource("dateProvider")
    void testProductDatesToCompareRecord(LocalDate dateOne, LocalDate dateTwo, boolean resultExpected) {

        Warehouse.ProductDatesToCompareRecord compareRecordContent =
                new Warehouse.ProductDatesToCompareRecord(dateOne, dateTwo);

        boolean testResultCompareRecord =
                Warehouse.ProductDatesToCompareRecord.isDateAfter(dateOne, dateTwo);

        System.out.println(compareRecordContent);

        assertThat(compareRecordContent).isNotNull();
        assertThat(testResultCompareRecord).isEqualTo(resultExpected);
    }

    @ParameterizedTest(name = "Filter products by date")
    @MethodSource("productListProvider")
    void testGetFilteredProductsByDate(List<ProductRecord> productListRecord) {

        ProductList productList = new ProductList(productListRecord);
        Warehouse warehouse = new Warehouse(productList);
        LocalDate dateTwo = LocalDate.parse("2024-02-10");
        List<ProductRecord> filteredTestResult = warehouse.getFilteredProductsByDate(dateTwo);

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
    void testGetAllUpdatedProducts(List<ProductRecord> productListRecord) {

        ProductList productList = new ProductList(productListRecord);
        Warehouse warehouse = new Warehouse(productList);
        List<ProductRecord> filteredTestResult = warehouse.getAllUpdatedProducts();
        System.out.println(filteredTestResult);

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

    @ParameterizedTest(name = "Filter all updated products")
    @MethodSource("productListProvider")
    void testGetSortedProductsForACategory(List<ProductRecord> productListRecord) {

        ProductList productList = new ProductList(productListRecord);
        Warehouse warehouse = new Warehouse(productList);

        List<ProductRecord> sortedTestResult = warehouse.getSortedProductsForACategory(RAMBLER).productRecord();
        System.out.println(sortedTestResult);

        assertThat(sortedTestResult).isNotNull();
        assertThat(sortedTestResult.size()).isEqualTo(3);
        assertThat(sortedTestResult)
                .extracting("name")
                .isSorted();
        assertThat(sortedTestResult)
                .extracting("name")
                .containsSequence("Helenae Hybrida", "Lyckefund", "New Dawn");
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
        List<ProductRecord> productListRecord = Arrays.asList(
                new ProductRecord(UUID.fromString("bc108fc2-6785-40c4-9392-b0e93358b26e"), "Hippolyte", GALLICA, 10.0, LocalDate.parse("2024-09-16"), LocalDate.parse("2024-09-16")),
                new ProductRecord(UUID.fromString("51feaf5d-2972-44ec-a78a-5d8a8e1be1e9"), "Lyckefund", RAMBLER, 8.7, LocalDate.parse("2024-02-10"), LocalDate.parse("2024-05-19")),
                new ProductRecord(UUID.fromString("e1c63601-999c-48d8-8900-cbc4b870db2e"), "Wasagaming", RUGOSA, 6.3, LocalDate.parse("2024-09-15"), LocalDate.parse("2024-09-16")),
                new ProductRecord(UUID.fromString("4ae21842-25d3-43f2-b502-626a25ac8f96"), "New Dawn", RAMBLER, 9.1, LocalDate.parse("2024-06-10"), LocalDate.parse("2024-05-19")),
                new ProductRecord(UUID.fromString("25c20fc8-7d2c-4a16-94e3-9e797ea8472c"), "Helenae Hybrida", RAMBLER, 7.3, LocalDate.parse("2024-02-10"), LocalDate.parse("2024-07-19")),
                new ProductRecord(UUID.fromString("1a39744d-fe31-46d4-bb2f-3a62353581dc"), "Duchesse De Montebello", GALLICA, 9.8, LocalDate.parse("2024-02-10"), LocalDate.parse("2024-06-04")),
                new ProductRecord(UUID.fromString("4c00b99d-5106-4ebb-be9b-14c69b58b3a5"), "Louise Bugnet", CANADIAN, 10.0, LocalDate.parse("2024-09-01"), LocalDate.parse("2024-09-01")),
                new ProductRecord(UUID.fromString("aad34fe5-9994-42e6-baa9-e6d42340627c"), "Ispahan", DAMASCENE, 9.9, LocalDate.parse("2024-09-01"), LocalDate.parse("2024-09-04"))
        );

        Map<UUID, List<ProductRecord>> groupedByIdProviderExpected = new HashMap<>();
        Map<String, List<ProductRecord>> groupedByNameProviderExpected = new HashMap<>();
        Map<Category, List<ProductRecord>> groupedByCategoryProviderExpected = new HashMap<>();
        Map<LocalDate, List<ProductRecord>> groupedByCreatedAtProviderExpected = new HashMap<>();
        Map<LocalDate, List<ProductRecord>> groupedByUpdatedAtProviderExpected = new HashMap<>();

        for (ProductRecord productRecord : productListRecord) {
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
        List<ProductRecord> productListRecord = Arrays.asList(
                new ProductRecord(UUID.fromString("bc108fc2-6785-40c4-9392-b0e93358b26e"), "Hippolyte", GALLICA, 10.0, LocalDate.parse("2024-09-16"), LocalDate.parse("2024-09-16")),
                new ProductRecord(UUID.fromString("51feaf5d-2972-44ec-a78a-5d8a8e1be1e9"), "Lyckefund", RAMBLER, 8.7, LocalDate.parse("2024-02-10"), LocalDate.parse("2024-05-19")),
                new ProductRecord(UUID.fromString("e1c63601-999c-48d8-8900-cbc4b870db2e"), "Wasagaming", RUGOSA, 6.3, LocalDate.parse("2024-09-15"), LocalDate.parse("2024-09-16")),
                new ProductRecord(UUID.fromString("4ae21842-25d3-43f2-b502-626a25ac8f96"), "New Dawn", RAMBLER, 9.1, LocalDate.parse("2024-06-10"), LocalDate.parse("2024-05-19")),
                new ProductRecord(UUID.fromString("25c20fc8-7d2c-4a16-94e3-9e797ea8472c"), "Helenae Hybrida", RAMBLER, 7.3, LocalDate.parse("2024-02-10"), LocalDate.parse("2024-07-19")),
                new ProductRecord(UUID.fromString("1a39744d-fe31-46d4-bb2f-3a62353581dc"), "Duchesse De Montebello", GALLICA, 9.8, LocalDate.parse("2024-02-10"), LocalDate.parse("2024-06-04")),
                new ProductRecord(UUID.fromString("4c00b99d-5106-4ebb-be9b-14c69b58b3a5"), "Louise Bugnet", CANADIAN, 10.0, LocalDate.parse("2024-09-01"), LocalDate.parse("2024-09-01")),
                new ProductRecord(UUID.fromString("aad34fe5-9994-42e6-baa9-e6d42340627c"), "Ispahan", DAMASCENE, 9.9, LocalDate.parse("2024-09-01"), LocalDate.parse("2024-09-04"))
        );

        Map<UUID, List<ProductRecord>> groupedByIdProviderExpected = new HashMap<>();
        Map<String, List<ProductRecord>> groupedByNameProviderExpected = new HashMap<>();
        Map<Category, List<ProductRecord>> groupedByCategoryProviderExpected = new HashMap<>();
        Map<LocalDate, List<ProductRecord>> groupedByCreatedAtProviderExpected = new HashMap<>();
        Map<LocalDate, List<ProductRecord>> groupedByUpdatedAtProviderExpected = new HashMap<>();

        for (ProductRecord productRecord : productListRecord) {
            groupedByIdProviderExpected.computeIfAbsent(productRecord.id(), k -> new ArrayList<>()).add(productRecord);
            groupedByNameProviderExpected.computeIfAbsent(productRecord.name(), k -> new ArrayList<>()).add(productRecord);
            groupedByCategoryProviderExpected.computeIfAbsent(productRecord.category(), k -> new ArrayList<>()).add(productRecord);
            groupedByCreatedAtProviderExpected.computeIfAbsent(productRecord.createdAt(), k -> new ArrayList<>()).add(productRecord);
            groupedByUpdatedAtProviderExpected.computeIfAbsent(productRecord.updatedAt(), k -> new ArrayList<>()).add(productRecord);
        }

        return Stream.of(
                arguments((Function<ProductRecord, UUID>) ProductRecord::id, productListRecord, groupedByIdProviderExpected),
                arguments((Function<ProductRecord, String>) ProductRecord::name, productListRecord, groupedByNameProviderExpected),
                arguments((Function<ProductRecord, Category>) ProductRecord::category, productListRecord, groupedByCategoryProviderExpected),
                arguments((Function<ProductRecord, LocalDate>) ProductRecord::createdAt, productListRecord, groupedByCreatedAtProviderExpected),
                arguments((Function<ProductRecord, LocalDate>) ProductRecord::updatedAt, productListRecord, groupedByUpdatedAtProviderExpected)
        );
    }

    static Stream<Arguments> productDataCountingProvider() {
        List<ProductRecord> productListRecord = Arrays.asList(
                new ProductRecord(UUID.fromString("bc108fc2-6785-40c4-9392-b0e93358b26e"), "Hippolyte", GALLICA, 10.0, LocalDate.parse("2024-09-16"), LocalDate.parse("2024-09-16")),
                new ProductRecord(UUID.fromString("51feaf5d-2972-44ec-a78a-5d8a8e1be1e9"), "Lyckefund", RAMBLER, 8.7, LocalDate.parse("2024-02-10"), LocalDate.parse("2024-05-19")),
                new ProductRecord(UUID.fromString("e1c63601-999c-48d8-8900-cbc4b870db2e"), "Wasagaming", RUGOSA, 6.3, LocalDate.parse("2024-09-15"), LocalDate.parse("2024-09-16")),
                new ProductRecord(UUID.fromString("4ae21842-25d3-43f2-b502-626a25ac8f96"), "New Dawn", RAMBLER, 9.1, LocalDate.parse("2024-06-10"), LocalDate.parse("2024-05-19")),
                new ProductRecord(UUID.fromString("25c20fc8-7d2c-4a16-94e3-9e797ea8472c"), "Helenae Hybrida", RAMBLER, 7.3, LocalDate.parse("2024-02-10"), LocalDate.parse("2024-07-19")),
                new ProductRecord(UUID.fromString("1a39744d-fe31-46d4-bb2f-3a62353581dc"), "Duchesse De Montebello", GALLICA, 9.8, LocalDate.parse("2024-02-10"), LocalDate.parse("2024-06-04")),
                new ProductRecord(UUID.fromString("4c00b99d-5106-4ebb-be9b-14c69b58b3a5"), "Louise Bugnet", CANADIAN, 10.0, LocalDate.parse("2024-09-01"), LocalDate.parse("2024-09-01")),
                new ProductRecord(UUID.fromString("aad34fe5-9994-42e6-baa9-e6d42340627c"), "Ispahan", DAMASCENE, 9.9, LocalDate.parse("2024-09-01"), LocalDate.parse("2024-09-04"))
        );

        Map<UUID, Long> countedByIdProviderExpected = productListRecord.stream().collect(Collectors.groupingBy(ProductRecord::id, Collectors.counting()));
        Map<String, Long> countedByNameProviderExpected = productListRecord.stream().collect(Collectors.groupingBy(ProductRecord::name, Collectors.counting()));
        Map<Category, Long> countedByCategoryProviderExpected = productListRecord.stream().collect(Collectors.groupingBy(ProductRecord::category, Collectors.counting()));
        Map<LocalDate, Long> countedByCreatedAtProviderExpected = productListRecord.stream().collect(Collectors.groupingBy(ProductRecord::createdAt, Collectors.counting()));
        Map<LocalDate, Long> countedByUpdatedAtProviderExpected = productListRecord.stream().collect(Collectors.groupingBy(ProductRecord::updatedAt, Collectors.counting()));

        return Stream.of(
                arguments((Function<ProductRecord, UUID>) ProductRecord::id, productListRecord, countedByIdProviderExpected),
                arguments((Function<ProductRecord, String>) ProductRecord::name, productListRecord, countedByNameProviderExpected),
                arguments((Function<ProductRecord, Category>) ProductRecord::category, productListRecord, countedByCategoryProviderExpected),
                arguments((Function<ProductRecord, LocalDate>) ProductRecord::createdAt, productListRecord, countedByCreatedAtProviderExpected),
                arguments((Function<ProductRecord, LocalDate>) ProductRecord::updatedAt, productListRecord, countedByUpdatedAtProviderExpected)
        );
    }
}
