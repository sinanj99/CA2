/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import entities.Address;
import entities.CityInfo;
import entities.Hobby;
import entities.Person;
import entities.Phone;
import entities.RenameMe;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

/**
 *
 * @author Kasper Jeppesen
 */

public class PersonFacadeTest {
    
    private static EntityManagerFactory emf;
    private static PersonFacade facade;

    public PersonFacadeTest() {
    }

    //@BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactory(
                "pu",
                "jdbc:mysql://localhost:3307/startcode_test",
                "dev",
                "ax2",
                EMF_Creator.Strategy.CREATE);
        facade = PersonFacade.getPersonFacade(emf);
    }

    /*   **** HINT **** 
        A better way to handle configuration values, compared to the UNUSED example above, is to store those values
        ONE COMMON place accessible from anywhere.
        The file config.properties and the corresponding helper class utils.Settings is added just to do that. 
        See below for how to use these files. This is our RECOMENDED strategy
     */
    @BeforeAll
    public static void setUpClassV2() {
       emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.TEST,EMF_Creator.Strategy.DROP_AND_CREATE);
       facade = PersonFacade.getPersonFacade(emf);
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the script below to use YOUR OWN entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            
            Set<Phone> phone1 = new HashSet<>();
            phone1.add(new Phone("22334455", "Hjemme nummer"));
            Address address1 = new Address(new CityInfo("3000", "Helsingør"), "Kongevejen", "Hjemme");
            Set<Hobby> hobbies1 = new HashSet<>();
            hobbies1.add(new Hobby("Cykling", "Sport på 2 hjul"));
            
            Set<Phone> phone2 = new HashSet<>();
            phone1.add(new Phone("99887766", "Mobil"));
            Address address2 = new Address(new CityInfo("3070", "Snekkersten"), "Klyveren", "Ude");
            Set<Hobby> hobbies2 = new HashSet<>();
            hobbies1.add(new Hobby("Sejlads", "Sport til havs"));
            
            
            em.getTransaction().begin();
            em.createNamedQuery("Person.deleteAllRows").executeUpdate();
            em.persist(new Person("Tom@cphbusiness.dk", "Tom", "Tomsen", phone1, address1, hobbies1));
            em.persist(new Person("Kim@cphbusiness.dk", "Kim", "Kimsen", phone2, address2, hobbies2));

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterEach
    public void tearDown() {
//        Remove any data after each test was run
    }

    // TODO: Delete or change this method 
    @Test
    public void testGerPersonCount() {
        assertEquals(2, facade.getPersonount(), "Expects two rows in the database");
    }
    
    @Test
    public void testAddPerson(){
        Long count;
        
        Set<Phone> phone1 = new HashSet<>();
        phone1.add(new Phone("5555555", "Hjemme nummer"));
        Address address1 = new Address(new CityInfo("3000", "Helsingør"), "Rosenkildevej", "Hjemme");
        Set<Hobby> hobbies1 = new HashSet<>();
        hobbies1.add(new Hobby("100m Løb", "Det skal gå hurtigt"));
        
        Person person = new Person("Ida@cphbusiness.dk", "Ida", "Larsen", phone1, address1, hobbies1);
        
        count = facade.getPersonount();
        facade.addPerson(person);
        
        //if the person above got persisted, the person count should be equal to the count before it got persisted +1 
        assertEquals(count+1, facade.getPersonount());
    }
    
}
