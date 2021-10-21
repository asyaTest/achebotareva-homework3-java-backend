package ru.annachemic.tests;

import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Base64;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class ImageTests extends BaseTest {
    private final String PATH_TO_IMAGE = "src/test/images.jpeg";
    static String encodedFile;
    static String[] encodedFiles;
    String uploadedImageId;

    @BeforeEach
    void beforeTest() {
        byte[] byteArray = getFileContent();
        encodedFile = Base64.getEncoder().encodeToString(byteArray);
        encodedFiles = new String[]{encodedFile, encodedFile};
    }

    @Test
    void uploadFileTest() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", encodedFile)
                .formParam("title", "ImageTitle")
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Test
    void uploadFileImageTest() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", new File(PATH_TO_IMAGE))
                .expect()
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Test
    void uploadNewFileImageTest() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", new File(PATH_TO_IMAGE))
                .expect()
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/upload")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Test
    void uploadFileWithoutImageTest() {
        given()
                .headers("Authorization", token)
                .expect()
                .statusCode(400)
                .when()
                .post("https://api.imgur.com/3/upload");
    }
    @Test
    void DeleteFileWithoutImageTest() {
        given()
                .headers("Authorization", token)
                .expect()
                .statusCode(400)
                .when()
                .post("https://api.imgur.com/3/upload");
    }


    @Test
    void FavoriteImageTest() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", encodedFile)
                .formParam("title", "ImageTitle")
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");

        given()
                .header("Authorization", token)
                .multiPart("image", new File(PATH_TO_IMAGE))
                .expect()
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/image/{imageHash}/favorite", uploadedImageId)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath();
    }

    @Test
    void UnfavoriteImageTest() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", encodedFile)
                .formParam("title", "ImageTitle")
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");

        given()
                .header("Authorization", token)
                .multiPart("image", new File(PATH_TO_IMAGE))
                .expect()
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/image/{imageHash}/favorite", uploadedImageId)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data");

        given()
                .header("Authorization", token)
                .multiPart("image", new File(PATH_TO_IMAGE))
                .expect()
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/image/{imageHash}/favorite", uploadedImageId)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data");
    }
    @Test
    void uploadMultipleFilesTest() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", encodedFiles)
                .formParam("title", "ImageTitle")
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");
    }

    @Test
    void ChangeDescriptionFileNameTest() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", encodedFile)
                .formParam("title", "ImageTitle")
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");

        given()
                .header("Authorization", token)
                .formParam("title", "ChangeTitle")
                .formParam("description", "ChangeDescription")
                .expect()
                .statusCode(200)
                .when()
                .post("https://api.imgur.com/3/image/{imageHash}", uploadedImageId)
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data");
    }

    @Test
    void DeleteImageTest() {
        uploadedImageId = given()
                .headers("Authorization", token)
                .multiPart("image", encodedFile)
                .formParam("title", "ImageTitle")
                .expect()
                .body("success", is(true))
                .body("data.id", is(notNullValue()))
                .when()
                .post("https://api.imgur.com/3/image")
                .prettyPeek()
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("data.deletehash");

        given()
                .headers("Authorization", token)
                .multiPart("image", new File(PATH_TO_IMAGE))
                .expect()
                .statusCode(200)
                .when()
                .delete("https://api.imgur.com/3/account/{username}/image/{deleteHash}", username, uploadedImageId)
                .prettyPeek()
                .then()
                .statusCode(200);

    }

    @AfterEach
    void tearDown() {
        if (uploadedImageId == null) {
        return;
        }

        given()
                .headers("Authorization", token)
                .when()
                .delete("https://api.imgur.com/3/account/{username}/image/{deleteHash}", username, uploadedImageId)
                .prettyPeek()
                .then()
                .statusCode(200);

    }

    private byte[] getFileContent() {
        byte[] byteArray = new byte[0];
        try {
            byteArray = FileUtils.readFileToByteArray(new File(PATH_TO_IMAGE));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArray;
    }
}