package service;

import entities.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class WarehouseTest {

    @ParameterizedTest(name = "Create new product to ProductRecord from input arguments")
    @MethodSource("inputProductDataProvider")
    void testCreateNewProduct(ArgumentsAccessor arguments) {
        Warehouse warehouse = new Warehouse();
//        UUID uuidForTests = UUID.randomUUID();
//
//        ProductRecord newExpectedProduct = new ProductRecord(
//                uuidForTests,
//                arguments.getString(0),
//                arguments.get(1, Category.class),
//                arguments.getDouble(2),
//                LocalDate.now(),
//                LocalDate.now()
//        );

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

    static Stream<Arguments> inputProductDataProvider() {
        return Stream.of(
                arguments("Hippolyte", Category.GALLICA, 10.0),
                arguments("Wasagaming", Category.RUGOSA, 6.3)
        );
    }
}
