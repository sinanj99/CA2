package rest;

import entities.Address;
import entities.CityInfo;
import entities.Hobby;
import entities.Person;
import entities.Phone;
import entities.RenameMe;
import facades.PersonFacade;
import utils.EMF_Creator;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.parsing.Parser;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator.DbSelector;
import utils.EMF_Creator.Strategy;

//Uncomment the line below, to temporarily disable this test
public class PersonResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    
    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;
    
    // Person 1 info:
    Address p_Address = new Address(new CityInfo("2200", "København Ø"), "Tagensvej", "Hjem");
    List<Phone> p_Phones = new ArrayList();
    List<Hobby> p_hobbies = new ArrayList(); 
    {
        p_Phones.add(new Phone("12345678", "Hjem"));
        p_hobbies.add(new Hobby("Eating", "I love food!"));
    }
    Person person = new Person("michael@cphbusiness.dk", "Michael", "Pedersen", p_Phones, p_Address, p_hobbies);
    
    // Person 2 info:
    Address p2_Address = new Address(new CityInfo("2100", "København Ø"), "Victor Bendix Gade", "Hjem");
    List<Phone> p2_Phones = new ArrayList();
    List<Hobby> p2_hobbies = new ArrayList(); 
    {
        p2_Phones.add(new Phone("87654321", "Hjem"));
        p2_hobbies.add(new Hobby("Eating", "I love food!"));
    }
    Person person2 = new Person("mads@cphbusiness.dk", "Mads", "Andersen", p2_Phones, p2_Address, p2_hobbies);

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactory(DbSelector.TEST, Strategy.DROP_AND_CREATE);
        
        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }
    
    @AfterAll
    public static void closeTestServer(){
        //System.in.read();
         //Don't forget this, if you called its counterpart in @BeforeAll
         EMF_Creator.endREST_TestWithDB();
         httpServer.shutdownNow();
    }
    
    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.persist(person);
            em.persist(person2);
            em.getTransaction().commit();
        } finally { 
            em.close();
        }
    }
    
    @Test
    public void testGetPersonCount(){
        given()
        .contentType("application/json")
        .get("/person/count").then()
        .assertThat()
        .statusCode(HttpStatus.OK_200.getStatusCode())
        .log().body()
        .body("count", equalTo(2));
    }
    
    @Test
    public void testGetById(){
        System.out.println("--------------------- Get person by id test -------------------------");
        System.out.println("--------------------- Person 1 --------------------------------------");
        //Person 1
        given()
        .contentType("application/json")
        .get("/person/" + person.getId()).then()
        .assertThat()
        .statusCode(HttpStatus.OK_200.getStatusCode())
        .log().body()
        .body("firstName", equalTo(person.getFirstName()))
        .body("lastName", equalTo(person.getLastName())) 
        .body("email", equalTo(person.getEmail()));
        
        System.out.println("--------------------- Person 2 --------------------------------------");
        //Person 2
        given()
        .contentType("application/json")
        .get("/person/" + person2.getId()).then()
        .assertThat()
        .statusCode(HttpStatus.OK_200.getStatusCode())
        .log().body()
        .body("firstName", equalTo(person2.getFirstName()))
        .body("lastName", equalTo(person2.getLastName())) 
        .body("email", equalTo(person2.getEmail()));
    }
    
    @Test
    public void testAddPerson(){
        System.out.println("--------------------- Add person test -------------------------");
        String json =   "{\n" +
                        "  \"id\": 0,\n" +
                        "  \"email\": \"mathias@cphbusiness.dk\",\n" +
                        "  \"firstName\": \"Mathias\",\n" +
                        "  \"lastName\": \"Stenberg\",\n" +
                        "  \"phones\": [\n" +
                        "    \"number:30232376,description:private\",\n" +
                        "    \"number:40455045,description:work\"\n" +
                        "  ],\n" +
                        "  \"street\": \"Lyngbyvej 21\",\n" +
                        "  \"streetInfo\": \"Home address\",\n" +
                        "  \"zip\": \"2800\",\n" +
                        "  \"city\": \"Lyngby\",\n" +
                        "  \"hobbies\": [\n" +
                        "    \"name:coding,description:writing code\",\n" +
                        "    \"name:beer,description:drinking beer\"\n" +
                        "  ]\n" +
                        "}";
        
        given()
        .contentType("application/json")
        .accept("application/json")
        .body(json)
        .post("/person/").then()      
        .log().body()
        .assertThat()
        .statusCode(HttpStatus.OK_200.getStatusCode())
        .body("firstName", equalTo("Mathias"))
        .body("lastName", equalTo("Stenberg")) 
        .body("email", equalTo("mathias@cphbusiness.dk"));
        
        given()
        .contentType("application/json")
        .get("/person/count").then()
        .assertThat()
        .statusCode(HttpStatus.OK_200.getStatusCode())
        .log().body()
        .body("count", equalTo(3));
        
    }
    
    @Test
    public void testEditPerson(){
        System.out.println("--------------------- Edit person test -------------------------");
        String json =   "{\n" +
                        "\"id\": "+ person2.getId() +" ," +
                        "    \"city\": \"Frederiksberg\",\n" +
                        "    \"email\": \"rasmus@cphbusiness.dk\",\n" +
                        "    \"firstName\": \"Rasmus\",\n" +
                        "    \"hobbies\": [\n" +
                        "        \"name:running,description:Do you even cardio bro?\"\n" +
                        "    ],\n" +
                        "    \"lastName\": \"Ejlers\",\n" +
                        "    \"phones\": [\n" +
                        "         \"number:30232376,description:private\"\n" +
                        "    ],\n" +
                        "    \"street\": \"Roskildevej 32\",\n" +
                        "    \"streetInfo\": \"Hjem\",\n" +
                        "    \"zip\": \"2000\"\n" +
                        "}";
        
        given()
        .contentType("application/json")
        .accept("application/json")
        .body(json)
        .put("/person/").then()      
        .log().body()
        .assertThat()
        .statusCode(HttpStatus.OK_200.getStatusCode())
        .body("firstName", equalTo("Rasmus"))
        .body("lastName", equalTo("Ejlers")) 
        .body("email", equalTo("rasmus@cphbusiness.dk"));
    }
    
    @Test
    public void testDeletePerson(){
        System.out.println("--------------------- Delete person test -------------------------");
        given()
        .contentType("application/json")
        .delete("/person/" + person.getId()).then()
        .assertThat()
        .statusCode(HttpStatus.OK_200.getStatusCode())
        .log().body()
        .body("status", equalTo("deleted"));
        
        given()
        .contentType("application/json")
        .get("/person/count").then()
        .assertThat()
        .statusCode(HttpStatus.OK_200.getStatusCode())
        .log().body()
        .body("count", equalTo(1));
    }
    // ------------------------- FAIL TESTS ----------------------------
    
    @Test
    public void testGetByIdFail(){
        System.out.println("--------------------- (FAIL) Get person by id test -------------------------");
        given()
        .contentType("application/json").when()
        .get("/person/2321").then().log().body()
        .assertThat()
        .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode())
        .body("code", equalTo(404))    
        .body("message", equalTo("Person not found"));
    }
    
    @Test
    public void testAddPersonFail(){
        System.out.println("--------------------- (FAIL) Add person test -------------------------");
        String json =   "{\n" +
                        "  \"email\": \"lars@lars.dk\",\n" +
                        "  \"firstName\": \"Lars\"\n" +
                        "}";
        
        given()
        .contentType("application/json")
        .accept("application/json")
        .body(json)
        .post("/person/").then()      
        .log().body()
        .assertThat()
        .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode())
        .body("code", equalTo(400))
        .body("message", equalTo("Not all arguments provided with the body"));
    }
    @Test
    public void testAddPersonFail2(){
        System.out.println("--------------------- (FAIL2) Add person test -------------------------");
        String json =   "{\n" +
                        "  \"email\": \"mathias@cphbusiness.dk\",\n" +
                        "  \"firstName\": \"Mathias\",\n" +
                        "  \"lastName\": \"Stenberg\",\n" +
                        "  \"phones\": [\n" +
                        "    \"number:12345678,description:private\",\n" +
                        "    \"number:40455045,description:work\"\n" +
                        "  ],\n" +
                        "  \"street\": \"Lyngbyvej 21\",\n" +
                        "  \"streetInfo\": \"Home address\",\n" +
                        "  \"zip\": \"2800\",\n" +
                        "  \"city\": \"Lyngby\",\n" +
                        "  \"hobbies\": [\n" +
                        "    \"name:coding,description:writing code\",\n" +
                        "    \"name:beer,description:drinking beer\"\n" +
                        "  ]\n" +
                        "}";
        
        given()
        .contentType("application/json")
        .accept("application/json")
        .body(json)
        .post("/person/").then()      
        .log().body()
        .assertThat()
        .statusCode(HttpStatus.CONFLICT_409.getStatusCode())
        .body("code", equalTo(409))
        .body("message", equalTo("Person already exists"));
    }
    
    @Test
    public void testEditPersonFail(){
        System.out.println("--------------------- (FAIL) Edit person test -------------------------");
        
        System.out.println("----------- 400 No id provided ---------------------------------");
        given()
        .contentType("application/json")
        .accept("application/json")
        .body("{\"blabla\":\"blabla\"}")
        .put("/person/").then()      
        .log().body()
        .assertThat()
        .statusCode(HttpStatus.BAD_REQUEST_400.getStatusCode())
        .body("code", equalTo(400))    
        .body("message", equalTo("No id provided"));
        
        System.out.println("----------- 404 Person not found ---------------------------------");
        given()
        .contentType("application/json")
        .accept("application/json")
        .body("{\"id\":\"985632156\"}")
        .put("/person/").then()      
        .log().body()
        .assertThat()
        .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode())
        .body("code", equalTo(404))    
        .body("message", equalTo("Person not found"));
    }
    
    @Test
    public void testDeletePersonFail(){
        System.out.println("--------------------- (FAIL) Delete person test -------------------------");
        given()
        .contentType("application/json")
        .delete("/person/" + 4365746).then()
        .assertThat()
        .statusCode(HttpStatus.NOT_FOUND_404.getStatusCode())
        .log().body()
        .body("code", equalTo(404))    
        .body("message", equalTo("Person not found"));
    }
}
