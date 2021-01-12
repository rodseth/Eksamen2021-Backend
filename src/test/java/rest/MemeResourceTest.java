package rest;

import dto.CommentDTO;
import dto.MemeDTO;
import entities.Comment;
import entities.Meme;
import entities.MemeStatus;
import entities.Role;
import entities.User;
import utils.EMF_Creator;
import io.restassured.RestAssured;

import static io.restassured.RestAssured.given;

import io.restassured.parsing.Parser;

import java.net.URI;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//Uncomment the line below, to temporarily disable this test
//@Disabled
public class MemeResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static User user, admin;
    private static Meme meme1, meme2, meme3;
    private static Comment comment1, comment2, comment3;
    private static MemeStatus status1, status2, status3;

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        //System.in.read();

        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        setupTestData(em);
    }
    
    private static String securityToken;

    private static void login(String role, String password) {
        String json = String.format("{username: \"%s\", password: \"%s\"}", role, password);
        securityToken = given()
                .contentType("application/json")
                .body(json)
                .when().post("/login")
                .then()
                .extract().path("token");
    }

    @Test
    public void testServerIsUp() {
        given().when().get("memes/funny").then().statusCode(200);
    }

    @Test
    public void testGetFunny() {
        given()
                .contentType("application/json")
                .get("memes/funny")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("size()", is(5));
    }

    @Test
    public void testGetCat() {
        given()
                .contentType("application/json")
                .get("memes/cat")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("size()", is(5));
    }

    @Test
    public void testGetYesOrNo() {
        given()
                .contentType("application/json")
                .get("memes/yesorno")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("size()", is(5));
    }

    @Test
    public void testGetDog() {
        given()
                .contentType("application/json")
                .get("memes/dog")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("size()", is(5));
    }

    @Test
    public void testUpvoteMeme() {
        login("user", "test123");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(new MemeDTO(meme2))
                .post("/memes/upvote/{username}", user.getUsername())
                .then().assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("upvotes", equalTo(2));
    }
    
    @Test
    public void testDownvoteMeme() {
        login("admin", "test123");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(new MemeDTO(meme2))
                .post("/memes/downvote/{username}", admin.getUsername())
                .then().assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("downvotes", equalTo(1));
    }
    
    @Test
    public void testUndoUpvote() {
        login("user", "test123");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(new MemeDTO(meme1))
                .post("/memes/upvote/{username}", user.getUsername())
                .then().assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("upvotes", equalTo(0));
    }
    
    @Test
    public void testUndoDownvote() {
        login("admin", "test123");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(new MemeDTO(meme1))
                .post("/memes/downvote/{username}", admin.getUsername())
                .then().assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("downvotes", equalTo(0));
    }
    
    @Test
    public void testGetColdList() {
        given()
                .contentType("application/json")
                .get("memes/cold")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("size()", is(1));
    }

    @Test
    public void testGetHotList() {
        given()
                .contentType("application/json")
                .get("memes/hot")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("size()", is(2));
    }
    
    @Test
    public void testGetFavoriteList() {
        login("user", "test123");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .get("/memes/favorite/{username}", user.getUsername())
                .then().assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("size()", is(1));
        
    }

    @Test
    public void testAddComment(){
        Comment comment = new Comment("Test", user);
        comment.setMeme(meme1);

        login("user", "test123");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(new CommentDTO(comment))
                .post("/memes/comment")
                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("comment", equalTo("Test"));


    }

    @Test
    public void testGetCommentsById (){
        List<CommentDTO> commentDTOList;

        login("user", "test123");
        commentDTOList =  given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .get("/memes/comment/{id}", meme1.getId())
                .then()
                .extract().body().jsonPath().getList("", CommentDTO.class);

                assertThat(commentDTOList.size(), equalTo(2));

    }
    
    @Test
    public void testGetMemeById() {
        int meme_id = meme2.getId();
        login("user", "test123");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .get("/memes/{id}", meme_id)
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("imageUrl", equalTo(meme2.getImageUrl()));
    }
    
    @Test
    public void testAddUserMeme() {
        Meme meme = new Meme("tester.png", "");
        meme.setMemeStatus(status1);
        meme.setPostedBy(user.getUsername());
        MemeDTO memeDTO = new MemeDTO(meme);
        
        login("user", "test123");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(memeDTO)
                .post("/memes/post")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("title", equalTo("UserSubmission"));
    }
    
    @Test
    public void testGetUserMemes() {
        List<MemeDTO> memeDTOs;
        
        login("user", "test123");
        memeDTOs = given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .get("/memes/submissions")
                .then()
                .extract().body().jsonPath().getList("", MemeDTO.class);
        
        assertThat(memeDTOs.size(), equalTo(0));
    }
    
    @Test
    public void testGetReportedMemes() {
        login("admin", "test123");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .get("/memes/reports")
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("size()", is(1));
    }
    
    @Test
    public void testBlackListMeme() {
        login("admin", "test123");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .put("/memes/blacklist/{id}", meme1.getId())
                .then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("status", equalTo("Blacklisted"));
    }

  public void setupTestData(EntityManager em) {
        user = new User("user", "test123");
        admin = new User("admin", "test123");
        meme1 = new Meme("fatcat.jpg", "Random cat");
        meme2 = new Meme("yomama.jpg", "Offensive joke");
        comment1 = new Comment("Jeg synes den er sjov", user);
        comment2 = new Comment("Jeg synes den er fed", user);
        comment3 = new Comment("Jeg synes den er nederen", admin);
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Roles.deleteAllRows").executeUpdate();
            em.createNamedQuery("Comment.deleteAllRows").executeUpdate();
            em.createNamedQuery("Report.deleteAllRows").executeUpdate();
            em.createNamedQuery("Meme.deleteAllRows").executeUpdate();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.createNamedQuery("MemeStatus.deleteAllRows").executeUpdate();
            
            
            Role userRole = new Role("user");
            Role adminRole = new Role("admin");
            status1 = new MemeStatus("OK");
            status2 = new MemeStatus("Reported");
            status3 = new MemeStatus("Blacklisted");
            user.addRole(userRole);
            admin.addRole(adminRole);
            meme1.getComments().add(comment1);
            meme1.getComments().add(comment2);
            meme2.getComments().add(comment3);
            meme1.getUpvoters().add(user);
            meme1.getDownvoters().add(admin);
            meme2.getUpvoters().add(admin);
            comment1.setMeme(meme1);
            comment2.setMeme(meme1);
            comment3.setMeme(meme2);
            meme1.setMemeStatus(status1);
            meme2.setMemeStatus(status1);
            meme1.setMemeStatus(status2);
            user.getUpvotedMemes().add(meme1);
            admin.getUpvotedMemes().add(meme2);
            admin.getDownvotedMemes().add(meme1);
            em.persist(userRole);
            em.persist(adminRole);
            em.persist(status1);
            em.persist(status2);
            em.persist(status3);
            em.persist(user);
            em.persist(admin);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
}
