package org.example.Brands;
import io.github.cdimascio.dotenv.Dotenv;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class UsersTest {

    static Dotenv dotenv = Dotenv.configure()
            .directory("src/test/assets")
            .filename("users_env")
            .load();

    //-------------------------------------

    private static String userToken;
    private static String secondUserToken;
    private static String adminToken;
    public static final String ENDPOINT = "/users";
    public static final String URL = dotenv.get("API_URL");

    //-------------------------------------

    @BeforeClass
    public static void tokenSetUp() {

        RestAssured.baseURI = URL;

        Response resUser = given()
                .contentType("application/json")
                .body("{\"email\":\"" + dotenv.get("USER_EMAIL") + "\",\"password\":\"" + dotenv.get("USER_PASSWORD") + "\"}")
                .when()
                .post("/users/login");

        Response resSecUser = given()
                .contentType("application/json")
                .body("{\"email\":\"" + dotenv.get("SECOND_USER_EMAIL") + "\",\"password\":\"" + dotenv.get("SECOND_USER_PASSWORD") + "\"}")
                .when()
                .post("/users/login");

        Response resAdmin = given()
                .contentType("application/json")
                .body("{\"email\":\"" + dotenv.get("ADMIN_EMAIL") + "\",\"password\":\"" + dotenv.get("ADMIN_PASSWORD") + "\"}")
                .when()
                .post("/users/login");


        userToken = resUser.jsonPath().getString("access_token");
        secondUserToken = resSecUser.jsonPath().getString("access_token");
        adminToken = resAdmin.jsonPath().getString("access_token");

        }



    // -------------------
    //   GET USER
    // -------------------

    // test of GET all users as an unlogged user
    @Test
    public void testGetUsersAsUnlogged() {
        given()
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(401)
                .body("message", equalTo("Unauthorized"));
    }

    // test of GET all users as a logged user
    @Test
    public void testGetUsersAsUser() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(403)
                .body("message", equalTo("Forbidden"));
    }

    // test of GET all users as an admin user
    @Test
    public void testGetUsersAsAdmin() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(200)
                .body("data", not(equalTo(null)));
    }

    // test of GET all users from page
    @Test
    public void testGetAllUsersFromPage() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .get(ENDPOINT + "?page=2")
                .then()
                .statusCode(200)
                .body("current_page", equalTo(2));
    }

    // test of GET user details as an unlogged user
    @Test
    public void testGetUserDetailsAsUnlogged() {
        given()
                .when()
                .get(ENDPOINT + "/me")
                .then()
                .statusCode(401)
                .body("message", equalTo("Unauthorized"));
    }

    // test of GET user details as a logged user
    @Test
    public void testGetUserDetailsAsUser() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .get(ENDPOINT + "/me")
                .then()
                .statusCode(200)
                .body("email", equalTo(dotenv.get("USER_EMAIL")));

    }



    // -----------------------
    //   DELETE USER
    // -----------------------

    // test of DELETE user as an unlogged user
    @Test
    public void testDeleteUserAsUnlogged() {
        given()
                .when()
                .delete(ENDPOINT+"/" + dotenv.get("USER_TO_DELETE"))
                .then()
                .statusCode(401)
                .body("message", equalTo("Unauthorized"));
    }

    // test of DELETE user as a logged user
    @Test
    public void testDeleteUserAsUser() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .when()
                .delete(ENDPOINT+"/" + dotenv.get("USER_TO_DELETE"))
                .then()
                .statusCode(403);
    }

    // test of DELETE user as an admin user
    @Test
    public void testDeleteUserAsAdmin() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .delete(ENDPOINT+"/" + dotenv.get("USER_TO_DELETE"))
                .then()
                .statusCode(204);
    }

    // test of DELETE user with wrong userId
    @Test
    public void testDeleteUserWrongUserId() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .when()
                .delete(ENDPOINT+"/30")
                .then()
                .statusCode(422);
    }



    // -----------------
    //   PUT USER
    // -----------------

    // test of UPDATE other account details as an unlogged user
    @Test
    public void testUpdateAccountAsUnlogged() {
        given()
                .formParams(
                        "first_name", "Janni",
                        "last_name", "Deski",
                        "address", "Test street 98",
                        "city", "Vienna",
                        "country", "Austria",
                        "email", "customer2@practicesoftwaretesting.com"
                )
                .when()
                .put(ENDPOINT+ "/" + dotenv.get("SECOND_USER_ID"))
                .then()
                .statusCode(401)
                .body("message", equalTo("Unauthorized"));
    }

    // test of UPDATE own account details as an admin user
    @Test
    public void testUpdateOwnAccountAsAdmin() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .formParams(
                        "first_name", "Adminoo",
                        "last_name", "admin",
                        "address", "Test street 98",
                        "city", "Vienna",
                        "country", "Austria",
                        "email", "admin@practicesoftwaretesting.com"
                )
                .put(ENDPOINT+ "/" + dotenv.get("ADMIN_ID"))
                .then()
                .statusCode(200)
                .body("success", is(true));
    }

    // test of UPDATE other account details as an admin user
    @Test
    public void testUpdateOtherAccountAsAdmin() {
        given()
                .header("Authorization", "Bearer " + adminToken)
                .formParams(
                        "first_name", "Johnnyy",
                        "last_name", "Doe",
                        "address", "Test street 1",
                        "city", "Vienna",
                        "country", "Austria",
                        "email", "customer5@practicesoftwaretesting.com"
                )
                .when()
                .put(ENDPOINT+ "/" + dotenv.get("USER_TO_CHANGE"))
                .then()
                .statusCode(200)
                .body("success", is(true));
    }

    // test of UPDATE user with wrong body request
    @Test
    public void testUpdateUserWithWrongBody() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .formParams("state", "state_test")
                .when()
                .put(ENDPOINT+ "/" + dotenv.get("USER_ID"))
                .then()
                .statusCode(422)
                .body("first_name", contains("The first name field is required."))
                .body("last_name", contains("The last name field is required."))
                .body("address", contains("The address field is required."))
                .body("city", contains("The city field is required."))
                .body("country", contains("The country field is required."))
                .body("email", contains("The email field is required."));
    }

    // test of UPDATE own account details as a logged user
    @Test
    public void testUpdateOwnAccountAsUser() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .formParams(
                        "first_name", "Janea",
                        "last_name", "Doesky",
                        "address", "Test street 98",
                        "city", "Vienna",
                        "country", "Austria",
                        "email", "customer@practicesoftwaretesting.com"
                )
                .when()
                .put(ENDPOINT+ "/" + dotenv.get("USER_ID"))
                .then()
                .statusCode(200)
                .body("success", is(true));
    }

    // test of UPDATE other account details as a logged user
    @Test
    public void testUpdateOtherAccountAsUser() {
        given()
                .header("Authorization", "Bearer " + userToken)
                .formParams(
                        "first_name", "Janni",
                        "last_name", "Deski",
                        "address", "Test street 98",
                        "city", "Vienna",
                        "country", "Austria",
                        "email", "customer2@practicesoftwaretesting.com"
                )
                .when()
                .put(ENDPOINT+ "/" + dotenv.get("SECOND_USER_ID"))
                .then()
                .statusCode(403)
                .body("error", is("You can only update your own data."));
    }




    // -------------------
    //   POST USER
    // -------------------

    // test of POST login user
    @Test
    public void testPostUserLogin() {

        given()
                .contentType(ContentType.JSON)
                .body("{\"email\":\"" + dotenv.get("USER_EMAIL") + "\",\"password\":\"" + dotenv.get("USER_PASSWORD") + "\"}")
                .when()
                .post(ENDPOINT+"/login")
                .then()
                .statusCode(200)
                .body("access_token", not(isEmptyOrNullString()))
                .body("expires_in", not(isEmptyOrNullString()))
                .body("token_type", equalTo("bearer"));
    }

    // test of POST login user with wrong creds
    @Test
    public void testPostUserLoginWrongCredentials() {

        given()
                .contentType(ContentType.JSON)
                .body("{\"email\":\"" + dotenv.get("WRONG_EMAIL") + "\",\"password\":\"" + dotenv.get("WRONG_PASSWORD") + "\"}")
                .when()
                .post(ENDPOINT+"/login")
                .then()
                .statusCode(401)
                .body("error", equalTo("Unauthorized"));
    }


    // test of POST change password as an unlogged user
    @Test
    public void testPostChangePasswordAsUnlogged() {
        given()
                .contentType(ContentType.JSON)
                .body("{\"current_password\":\"abc\",\"new_password\":\"aaa\",\"new_password_confirmation\":\"aaa\"}")
                .when()
                .post(ENDPOINT+"/change-password")
                .then()
                .statusCode(401)
                .body("message", equalTo("Unauthorized"));
    }


    // test of POST change password as a logged user
    @Test
    public void testPostChangePasswordAsUser() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + secondUserToken)
                .body("{\"current_password\":\""+ dotenv.get("SECOND_USER_PASSWORD") +"\",\"new_password\":\""+ dotenv.get("NEW_SECOND_USER_PASSWORD") +"\",\"new_password_confirmation\":\""+dotenv.get("NEW_SECOND_USER_PASSWORD")+"\"}")
                .when()
                .post(ENDPOINT+"/change-password")
                .then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    // test of POST change password to the same as now
    @Test
    public void testPostChangePasswordSame() {
        given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + userToken)
                .body("{\"current_password\":\""+ dotenv.get("USER_PASSWORD") +"\",\"new_password\":\""+ dotenv.get("USER_PASSWORD") +"\",\"new_password_confirmation\":\""+dotenv.get("USER_PASSWORD")+"\"}")
                .when()
                .post(ENDPOINT+"/change-password")
                .then()
                .statusCode(400)
                .body("success", equalTo(false))
                .body("message", equalTo("New Password cannot be same as your current password."));
    }
}



