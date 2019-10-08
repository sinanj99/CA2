package rest;

import entities.Address;
import entities.CityInfo;
import entities.Hobby;
import entities.Person;
import entities.Phone;
import entities.RenameMe;
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
    Address p2_Address = new Address(new CityInfo("2200", "København Ø"), "Tagensvej", "Hjem");
    List<Phone> p2_Phones = new ArrayList();
    List<Hobby> p2_hobbies = new ArrayList(); 
    {
        p2_Phones.add(new Phone("12345678", "Hjem"));
        p2_hobbies.add(new Hobby("Eating", "I love food!"));
    }
    Person person2 = new Person("michael@cphbusiness.dk", "Michael", "Pedersen", p2_Phones, p2_Address, p2_hobbies);

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactory(DbSelector.TEST, Strategy.CREATE);
        
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
    public void testGetById(){
        //Person 1
        given()
        .contentType("application/json")
        .get("/person/" + person.getId()).then()
        .assertThat()
        .statusCode(HttpStatus.OK_200.getStatusCode())
        .log().body()
        .body("firstName", equalTo(person.getFirstName()))
        .and()
        .body("lastName", equalTo(person.getLastName()))
        .and()  
        .body("email", equalTo(person.getEmail()));
        
        //Person 2
        given()
        .contentType("application/json")
        .get("/person/" + person2.getId()).then()
        .assertThat()
        .statusCode(HttpStatus.OK_200.getStatusCode())
        .log().body()
        .body("firstName", equalTo(person2.getFirstName()))
        .and()
        .body("lastName", equalTo(person2.getLastName()))
        .and()  
        .body("email", equalTo(person2.getEmail()));
    }
}
