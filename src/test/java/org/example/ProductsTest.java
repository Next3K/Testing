package org.example;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class ProductsTest {

    public static final String ENDPOINT = "/products";
    public static final String URL = "https://api-v2.practicesoftwaretesting.com" + ENDPOINT;

    // TESTING GET

    @Test
    public void testRetrieveAllProducts() {
        // Make the request and get the response
        Response response = RestAssured.get(URL);

        assertEquals(200, response.getStatusCode());
        JsonPath jsonPath = response.jsonPath();
        int numberOfElements = jsonPath.getMap("").size();
        assertTrue(numberOfElements > 0, "Number of products was not greater than 0");
    }

    @Test
    public void testFilterProductsByBrand() {
        Response response = RestAssured.given().param("by_brand", 1).get(URL);

        assertEquals(200, response.getStatusCode());

        JsonPath jsonPath = response.jsonPath();

        List<Integer> brandIds = jsonPath.getList("data.brand.id");

        brandIds.forEach(brandId -> assertEquals(1, brandId, "Not all products have brand id equal to 1"));
    }

    @Test
    public void testFilterProductsByCategory() {

        Response response = RestAssured.given().param("by_category", 1).get(URL);

        assertEquals(200, response.getStatusCode());

        JsonPath jsonPath = response.jsonPath();

        List<Integer> brandIds = jsonPath.getList("data.category.id");

        brandIds.forEach(brandId -> assertEquals(1, brandId, "Not all products have category id equal to 1"));
    }

    @Test
    public void testRetrieveRentalProducts() {
        Response response = RestAssured.given().param("is_rental", "true").get(URL);

        assertEquals(200, response.getStatusCode());

        JsonPath jsonPath = response.jsonPath();

        List<Boolean> isRentalValues = jsonPath.getList("data.is_rental");

        isRentalValues.stream().filter(Objects::nonNull).forEach(isRental -> assertTrue(isRental, "Not all products are marked as rental"));
    }

    // TESTING POST

    //    Test a successful product creation (status code 201)
    @Test
    public void testCreateProduct() {
        String requestBody = "{\n" +
                "  \"name\": \"New Product\",\n" +
                "  \"description\": \"Product description\",\n" +
                "  \"price\": 19.99,\n" +
                "  \"category_id\": 1,\n" +
                "  \"brand_id\": 1,\n" +
                "  \"product_image_id\": 1\n" +
                "}";

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .post(URL);

        assertEquals(201, response.getStatusCode());
    }

    //    Test for a 404 response when the requested item is not found
    @Test
    public void testProductNotFound() {
        Response response = RestAssured.given().post(URL + "/999");

        assertEquals(404, response.getStatusCode());
    }

    //    Test for a 405 response when the method is not allowed
    @Test
    public void testMethodNotAllowed() {
        Response response = RestAssured.get(URL);

        assertEquals(405, response.getStatusCode());
    }

    //   Test for a 423 response when the server was not able to process the content
    @Test
    public void testUnprocessableEntity() {
        String invalidRequestBody = "{\n" +
                "  \"description\": \"Invalid Product\"\n" +
                "}";

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(invalidRequestBody)
                .post(URL);

        assertEquals(422, response.getStatusCode());
    }

    // TESTING DELETE

    // Test a successful product deletion (status code 204)
    @Test
    public void testDeleteProduct() {
        int productIdToDelete = 1;

        Response response = RestAssured.delete(URL + "/" + productIdToDelete);

        assertEquals(204, response.getStatusCode());
    }

    // Test for a 404 response when the resource is not found
    @Test
    public void testDeleteProductNotFound() {
        int nonExistentProductId = 999;

        Response response = RestAssured.delete(URL + "/" + nonExistentProductId);

        assertEquals(404, response.getStatusCode());
    }

    // Test for a 405 response when the method is not allowed
    @Test
    public void testDeleteMethodNotAllowed() {
        int productIdToDelete = 1;

        Response response = RestAssured.get(URL + "/" + productIdToDelete);

        assertEquals(405, response.getStatusCode());
    }

    // Test for a 422 response when the server was not able to process the content
    @Test
    public void testDeleteUnprocessableEntity() {
        int invalidProductId = 0;

        Response response = RestAssured.delete(URL + "/" + invalidProductId);

        assertEquals(422, response.getStatusCode());
    }

    // TESTING PUT

    // Test a successful product update (status code 200)
    @Test
    public void testUpdateProduct() {
        int productIdToUpdate = 1;
        String requestBody = "{\n" +
                "  \"name\": \"Updated Product\",\n" +
                "  \"description\": \"Updated description\",\n" +
                "  \"price\": 29.99,\n" +
                "  \"category_id\": 2,\n" +
                "  \"brand_id\": 2,\n" +
                "  \"product_image_id\": 2\n" +
                "}";
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .put(URL + "/" + productIdToUpdate);
        assertEquals(200, response.getStatusCode());
    }

    // Test for a 404 response when the resource is not found
    @Test
    public void testUpdateProductNotFound() {
        int nonExistentProductId = 999;
        String requestBody = "{\n" +
                "  \"name\": \"Updated Product\",\n" +
                "  \"description\": \"Updated description\",\n" +
                "  \"price\": 29.99,\n" +
                "  \"category_id\": 2,\n" +
                "  \"brand_id\": 2,\n" +
                "  \"product_image_id\": 2\n" +
                "}";
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .put(URL + "/" + nonExistentProductId);
        assertEquals(404, response.getStatusCode());
    }

    // Test for a 405 response when the method is not allowed
    @Test
    public void testUpdateMethodNotAllowed() {
        int productIdToUpdate = 1;
        Response response = RestAssured.get(URL + "/" + productIdToUpdate);
        assertEquals(405, response.getStatusCode());
    }

    // Test for a 422 response when the server was not able to process the content
    @Test
    public void testUpdateUnprocessableEntity() {
        int productIdToUpdate = 1;
        String invalidRequestBody = "{\n" +
                "  \"description\": \"Updated description\"\n" +
                "}";
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(invalidRequestBody)
                .put(URL + "/" + productIdToUpdate);
        assertEquals(422, response.getStatusCode());
    }
}
