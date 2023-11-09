package org.example;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductsTest {

    public static final String ENDPOINT = "/products";
    public static final String URL = "https://api-v2.practicesoftwaretesting.com" + ENDPOINT;

    // TESTING GET

    @Test
    public void testRetrieveAllProducts() {
        // Make the request and get the response
        Response response = RestAssured.get(URL);

        // Assertions
        assertEquals(200, response.getStatusCode());
        // Add more assertions based on your API response structure
    }

    @Test
    public void testFilterProductsByBrand() {
        // Make the request with the by_brand parameter
        Response response = RestAssured.given().param("by_brand", 1).get(URL);

        // Assertions
        assertEquals(200, response.getStatusCode());
        // Add more assertions based on your API response structure and the filter
    }

    @Test
    public void testFilterProductsByCategory() {
        // Make the request with the by_category parameter
        Response response = RestAssured.given().param("by_category", 2).get(URL);

        // Assertions
        assertEquals(200, response.getStatusCode());
        // Add more assertions based on your API response structure and the filter
    }

    @Test
    public void testRetrieveRentalProducts() {
        // Make the request with the is_rental parameter
        Response response = RestAssured.given().param("is_rental", "true").get(URL);

        // Assertions
        assertEquals(200, response.getStatusCode());
        // Add more assertions based on your API response structure and the filter
    }

    // TESTING POST

    //    Test a successful product creation (status code 201)
    @Test
    public void testCreateProduct() {
        // Define the request body
        String requestBody = "{\n" +
                "  \"name\": \"New Product\",\n" +
                "  \"description\": \"Product description\",\n" +
                "  \"price\": 19.99,\n" +
                "  \"category_id\": 1,\n" +
                "  \"brand_id\": 1,\n" +
                "  \"product_image_id\": 1\n" +
                "}";

        // Make the request and get the response
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .post(URL);

        // Assertions
        assertEquals(201, response.getStatusCode());
        // Add more assertions based on your API response structure
    }

    //    Test for a 404 response when the requested item is not found
    @Test
    public void testProductNotFound() {
        // Make the request with an invalid item ID (assuming ID 999 does not exist)
        Response response = RestAssured.given().post(URL + "/999");

        // Assertions
        assertEquals(404, response.getStatusCode());
        // Add more assertions based on your API response structure
    }

    //    Test for a 405 response when the method is not allowed
    @Test
    public void testMethodNotAllowed() {
        // Make a GET request to the /products endpoint (assuming it only allows POST)
        Response response = RestAssured.get(URL);

        // Assertions
        assertEquals(405, response.getStatusCode());
        // Add more assertions based on your API response structure
    }

    //   Test for a 423 response when the server was not able to process the content
    @Test
    public void testUnprocessableEntity() {
        // Make the request with an invalid request body (assuming missing required fields)
        String invalidRequestBody = "{\n" +
                "  \"description\": \"Invalid Product\"\n" +
                "}";

        Response response = RestAssured.given()
                .contentType("application/json")
                .body(invalidRequestBody)
                .post(URL);

        // Assertions
        assertEquals(422, response.getStatusCode());
        // Add more assertions based on your API response structure
    }

    // TESTING DELETE

    // Test a successful product deletion (status code 204)
    @Test
    public void testDeleteProduct() {
        // Assume productId 1 exists
        int productIdToDelete = 1;

        // Make the DELETE request and get the response
        Response response = RestAssured.delete(URL + "/" + productIdToDelete);

        // Assertions
        assertEquals(204, response.getStatusCode());
        // Add more assertions based on your API response structure
    }

    // Test for a 404 response when the resource is not found
    @Test
    public void testDeleteProductNotFound() {
        // Assume productId 999 does not exist
        int nonExistentProductId = 999;

        // Make the DELETE request and get the response
        Response response = RestAssured.delete(URL + "/" + nonExistentProductId);

        // Assertions
        assertEquals(404, response.getStatusCode());
        // Add more assertions based on your API response structure
    }

    // Test for a 405 response when the method is not allowed
    @Test
    public void testDeleteMethodNotAllowed() {
        // Assume productId 1 exists
        int productIdToDelete = 1;

        // Make a GET request to the DELETE endpoint
        Response response = RestAssured.get(URL + "/" + productIdToDelete);

        // Assertions
        assertEquals(405, response.getStatusCode());
        // Add more assertions based on your API response structure
    }

    // Test for a 422 response when the server was not able to process the content
    @Test
    public void testDeleteUnprocessableEntity() {
        // Assume productId is not provided in the path
        int invalidProductId = 0; // Assuming 0 is an invalid productId

        // Make the DELETE request without providing a productId
        Response response = RestAssured.delete(URL + "/" + invalidProductId);

        // Assertions
        assertEquals(422, response.getStatusCode());
        // Add more assertions based on your API response structure
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
        // Add more assertions based on your API response structure
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
        // Add more assertions based on your API response structure
    }

    // Test for a 405 response when the method is not allowed
    @Test
    public void testUpdateMethodNotAllowed() {
        int productIdToUpdate = 1;
        Response response = RestAssured.get(URL + "/" + productIdToUpdate);
        assertEquals(405, response.getStatusCode());
        // Add more assertions based on your API response structure
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
        // Add more assertions based on your API response structure
    }
}
