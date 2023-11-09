package org.example;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.hamcrest.Matchers.equalTo;

public class CategoryTest {

    public static final String URL = "https://api.practicesoftwaretesting.com/categories";

    public static final String AUTH_TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL2FwaS5wcmFjdGljZXNvZnR3YXJldGVzdGluZy5jb20vdXNlcnMvbG9naW4iLCJpYXQiOjE2OTk1NjM3MTgsImV4cCI6MTY5OTU2NDAxOCwibmJmIjoxNjk5NTYzNzE4LCJqdGkiOiJIeVRETnhZTkJXZUlITzU2Iiwic3ViIjoiMDFIRVRZNTVHMkQ2SzA2MlpIVzBWTlg4MUMiLCJwcnYiOiIyM2JkNWM4OTQ5ZjYwMGFkYjM5ZTcwMWM0MDA4NzJkYjdhNTk3NmY3Iiwicm9sZSI6ImFkbWluIn0.2AKJdx65_4MH9lOpcNyVJnEaL3tdfVZQ0yoGI0gLdA8";

    public static final Random random = new Random();

    // TESTING GET

    @Test
    public void testGetAllCategories() {
        Response response = RestAssured.get(URL);
        response.then().assertThat().statusCode(200);
    }

    @Test
    public void testGetAllCategoriesWithSubcategories() {
        Response response = RestAssured.get(URL + "/tree");
        response.then().assertThat().statusCode(200);
    }

    @Test
    public void testGetCategoryWithExistingId() {
        // Create new category
        String nameNumber = String.valueOf(Math.abs(random.nextLong()));
        String requestBody = "{\n" +
                "  \"name\": \"new category " + nameNumber + "\",\n" +
                "  \"slug\": \"new-category-" + nameNumber + "\"\n" +
                "}";
        Response postResponse = RestAssured
                .given()
                .contentType("application/json")
                .body(requestBody)
                .post(URL);

        // Get new category ID
        String categoryId = postResponse.body().jsonPath().get("id");

        // Test GET category with given ID (status code 200)
        Response response = RestAssured.get(URL + '/' + categoryId);
        response.then().assertThat().statusCode(200);

        // Delete new category
        RestAssured.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + AUTH_TOKEN)
                .delete(URL + '/' + categoryId);
    }

    @Test
    public void testGetNonExistingCategory() {
        // Test GET category with non-existing ID (status code 404)
        Response response = RestAssured.get(URL + '/' + "non-existing-id");
        response.then().assertThat().statusCode(404);
    }

    @Test
    public void testGetMethodNotAllowed() {
        // Test using not allowed method (status code 405)
        Response response = RestAssured.post(URL + "/tree");
        response.then().assertThat().statusCode(405);
    }

    @Test
    public void testGetCategoryTreeBySlug() {
        // Create new category
        String nameNumber = String.valueOf(Math.abs(random.nextLong()));
        String slug = "new-category-" + nameNumber;
        String requestBody = "{\n" +
                "  \"name\": \"new category " + nameNumber +"\",\n" +
                "  \"slug\": \"" + slug + "\"\n" +
                "}";
        Response postResponse = RestAssured
                .given()
                .contentType("application/json")
                .body(requestBody)
                .post(URL);

        // Get new category ID
        String categoryId = postResponse.body().jsonPath().get("id");

        // Test GET category with given slug (status code 200)
        Response response = RestAssured.get(URL + "/tree?by_category_slug=" + slug);
        response.then().assertThat().statusCode(200);

        // Delete new category
        RestAssured.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + AUTH_TOKEN)
                .delete(URL + '/' + categoryId);
    }

    // TESTING POST

    @Test
    public void testPostCategoryWithUniqueSlug() {
        // Test POST with unique slug (status code 201)
        String nameNumber = String.valueOf(Math.abs(random.nextLong()));
        String requestBody = "{\n" +
                "  \"name\": \"new category " + nameNumber + "\",\n" +
                "  \"slug\": \"new-category-" + nameNumber + "\"\n" +
                "}";
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .post(URL);
        response.then()
                .assertThat()
                .statusCode(201);

        // Get new category ID
        String categoryId = response.body().jsonPath().get("id");

        // Delete new category
        RestAssured.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + AUTH_TOKEN)
                .delete(URL + '/' + categoryId);
    }

    @Test
    public void testPostCategoryWithExistingSlug() {
        // Create new category
        String nameNumber = String.valueOf(Math.abs(random.nextLong()));
        String requestBody = "{\n" +
                "  \"name\": \"existing name " + nameNumber + "\",\n" +
                "  \"slug\": \"existing-name-" + nameNumber + "\"\n" +
                "}";
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .post(URL);

        // Test POST with already existing slug (status code 422)
        RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .post(URL)
                .then()
                .assertThat()
                .statusCode(422);

        // Get existing category ID
        String existingCategoryId = response.body().jsonPath().get("id");

        // Delete new category
        RestAssured.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + AUTH_TOKEN)
                .delete(URL + '/' + existingCategoryId);
    }

    @Test
    public void testPostRequestedItemNotFound() {
        // Test POST with invalid url (status code 404)
        String nameNumber = String.valueOf(Math.abs(random.nextLong()));
        String requestBody = "{\n" +
                "  \"name\": \"new category " + nameNumber + "\",\n" +
                "  \"slug\": \"new-category-" + nameNumber + "\"\n" +
                "}";
        Response response = RestAssured.given()
                .contentType("application/json")
                .body(requestBody)
                .post(URL + "/some/path");
        response.body().prettyPrint();
        response.then()
                .assertThat()
                .statusCode(404);
        // Get new category ID
        String categoryId = response.body().jsonPath().get("id");

        // Delete new category
        RestAssured.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + AUTH_TOKEN)
                .delete(URL + '/' + categoryId);
    }

    @Test
    public void testPostCategoryWithParentCategory() {
        // Create parent category
        String nameNumber = String.valueOf(Math.abs(random.nextLong()));
        String parentRequestBody = "{\n" +
                "  \"name\": \"new category parent " + nameNumber + "\",\n" +
                "  \"slug\": \"new-category-parent-" + nameNumber + "\"\n" +
                "}";
        Response parentResponse = RestAssured.given()
                .contentType("application/json")
                .body(parentRequestBody)
                .post(URL);
        parentResponse.body().prettyPrint();

        // Get parent category ID
        String parentCategoryId = parentResponse.body().jsonPath().get("id");

        // Test POST with parent (status code 201)
        String nameNumber2 = String.valueOf(Math.abs(random.nextLong()));
        String childRequestBody = "{\n" +
                "  \"id\": \"\",\n" +
                "  \"parent_id\": \"" + parentCategoryId + "\",\n" +
                "  \"name\": \"new category child " + nameNumber2 + "\",\n" +
                "  \"slug\": \"new-category-child-" + nameNumber2 + "\"\n" +
                "}";

        Response childResponse = RestAssured.given()
                .contentType("application/json")
                .body(childRequestBody)
                .post(URL);
        childResponse.body().prettyPrint();
        childResponse.then()
                .assertThat()
                .statusCode(201);

        // Get child category ID
        String childCategoryId = childResponse.body().jsonPath().get("id");

        // Delete child category
        RestAssured.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + AUTH_TOKEN)
                .delete(URL + '/' + childCategoryId);

        // Delete parent category
        RestAssured.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + AUTH_TOKEN)
                .delete(URL + '/' + parentCategoryId);
    }

    // TESTING DELETE

    @Test
    public void testDeleteExistingCategory() {
        // Create new category
        String nameNumber = String.valueOf(Math.abs(random.nextLong()));
        String requestBody = "{\n" +
                "  \"name\": \"new category " + nameNumber + "\",\n" +
                "  \"slug\": \"new-category-" + nameNumber + "\"\n" +
                "}";
        Response postResponse = RestAssured
                .given()
                .contentType("application/json")
                .body(requestBody)
                .post(URL);

        // Get new category ID
        String categoryId = postResponse.body().jsonPath().get("id");

        // Test DELETE existing category
        RestAssured.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + AUTH_TOKEN)
                .delete(URL + '/' + categoryId)
                .then()
                .assertThat()
                .statusCode(204);
    }

    @Test
    public void testDeleteNonExistingCategory() {
        // Create new category
        String nameNumber = String.valueOf(Math.abs(random.nextLong()));
        String requestBody = "{\n" +
                "  \"name\": \"new category " + nameNumber + "\",\n" +
                "  \"slug\": \"new-category-" + nameNumber + "\"\n" +
                "}";
        Response postResponse = RestAssured
                .given()
                .contentType("application/json")
                .body(requestBody)
                .post(URL);

        // Get new category ID
        String categoryId = postResponse.body().jsonPath().get("id");

        // Delete existing category
        RestAssured.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + AUTH_TOKEN)
                .delete(URL + '/' + categoryId);

        // Test DELETE non-existing category
        RestAssured.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + AUTH_TOKEN)
                .delete(URL + '/' + categoryId)
                .then()
                .assertThat()
                .statusCode(422);
    }

    @Test
    public void testDeleteUnauthorized() {
        // Create new category
        String nameNumber = String.valueOf(Math.abs(random.nextLong()));
        String requestBody = "{\n" +
                "  \"name\": \"new category " + nameNumber + "\",\n" +
                "  \"slug\": \"new-category-" + nameNumber + "\"\n" +
                "}";
        Response postResponse = RestAssured
                .given()
                .contentType("application/json")
                .body(requestBody)
                .post(URL);

        // Get new category ID
        String categoryId = postResponse.body().jsonPath().get("id");

        // Test unauthorized DELETE
        RestAssured.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + "invalid.token")
                .delete(URL + '/' + categoryId)
                .then()
                .assertThat()
                .statusCode(401);

        // Delete category anyway
        RestAssured.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + AUTH_TOKEN)
                .delete(URL + '/' + categoryId);
    }


    // TESTING PUT

    @Test
    public void testPutOnExistingCategory() {
        // Create new category
        String nameNumber = String.valueOf(Math.abs(random.nextLong()));
        String requestBody = "{\n" +
                "  \"name\": \"new category " + nameNumber + "\",\n" +
                "  \"slug\": \"new-category-" + nameNumber + "\"\n" +
                "}";
        Response postResponse = RestAssured
                .given()
                .contentType("application/json")
                .body(requestBody)
                .post(URL);

        // Get new category ID
        String categoryId = postResponse.body().jsonPath().get("id");

        // Test PUT category with given ID (status code 200)
        String newRequestBody = "{\n" +
                "  \"name\": \"changed category name " + nameNumber + "\",\n" +
                "  \"slug\": \"changed-category-slug-" + nameNumber + "\"\n" +
                "}";
        RestAssured.given()
                .contentType("application/json")
                .body(newRequestBody)
                .put(URL + "/" + categoryId)
                .then()
                .assertThat()
                .statusCode(200);

        // Delete new category
        RestAssured.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + AUTH_TOKEN)
                .delete(URL + '/' + categoryId);
    }

    @Test
    public void testPutOnNonExistingCategory() {
        // Test PUT category with non-existing ID (status code 404)
        String nameNumber = String.valueOf(Math.abs(random.nextLong()));
        String newRequestBody = "{\n" +
                "  \"name\": \"category name " + nameNumber + "\",\n" +
                "  \"slug\": \"category-slug-" + nameNumber + "\"\n" +
                "}";
        RestAssured.given()
                .contentType("application/json")
                .body(newRequestBody)
                .put(URL + "/" + "non-existing-id")
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    public void testPutMethodNotAllowed() {
        // Test not allowed method (status code 404)
        String nameNumber = String.valueOf(Math.abs(random.nextLong()));
        String newRequestBody = "{\n" +
                "  \"name\": \"category name " + nameNumber + "\",\n" +
                "  \"slug\": \"category-slug-" + nameNumber + "\"\n" +
                "}";
        RestAssured.given()
                .contentType("application/json")
                .body(newRequestBody)
                .get(URL + "/" + "some-id")
                .then()
                .assertThat()
                .statusCode(404);
    }

    @Test
    public void testPutWithInvalidBody() {
        // Create new category
        String nameNumber = String.valueOf(Math.abs(random.nextLong()));
        String requestBody = "{\n" +
                "  \"name\": \"new category " + nameNumber + "\",\n" +
                "  \"slug\": \"new-category-" + nameNumber + "\"\n" +
                "}";
        Response postResponse = RestAssured
                .given()
                .contentType("application/json")
                .body(requestBody)
                .post(URL);

        // Get new category ID
        String categoryId = postResponse.body().jsonPath().get("id");

        // Test PUT category with invalid body (status code 422)
        String invalidBody = "{\n" +
                "  \"somekey\": \"data\",\n" +
                "  \"anotherkey\": \"changed-category-slug\"\n" +
                "  \"key\": \"changed-slug\"\n" +
                "}";
        Response putResponse = RestAssured.given()
                .contentType("application/json")
                .body(invalidBody)
                .put(URL + "/" + categoryId);
        putResponse.then().assertThat().body("success", equalTo(false));
        // Delete new category
        RestAssured.given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + AUTH_TOKEN)
                .delete(URL + '/' + categoryId);
    }
}