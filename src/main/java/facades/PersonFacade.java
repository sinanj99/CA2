/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import dto.PersonDTO;
import entities.Address;
import entities.Hobby;
import entities.Person;
import entities.Phone;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

/**
 *
 * @author Kasper Jeppesen
 */
public class PersonFacade implements IPersonFacade {
    
    private static PersonFacade instance;
    private static EntityManagerFactory emf;
    
    //Private Constructor to ensure Singleton
    private PersonFacade() {}
    
    
   /**
     * 
     * @param _emf
     * @return an instance of this facade class.
     */
    public static PersonFacade getPersonFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    //TODO Remove/Change this before use
    public long getPersonount(){
        EntityManager em = emf.createEntityManager();
        try{
            long PersonCount = (long)em.createQuery("SELECT COUNT(person) FROM Person person").getSingleResult();
            return PersonCount;
        }finally{  
            em.close();
        }
        
    }

    @Override
    public Person addPerson(Person person) {
        EntityManager em = emf.createEntityManager();
        
        try{
            em.getTransaction().begin();
            em.persist(person);
            em.getTransaction().commit();
        }
        finally{
            em.close();
        }
        return person;
    }

    @Override
    public Person deletePerson(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Person editperson(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<PersonDTO> getAllPerson() {
        
        EntityManager em = emf.createEntityManager();
        
        try{
            TypedQuery<Person> query = 
                       em.createQuery("Select person from Person person",Person.class);
            
            List<Person> allPersons = query.getResultList();
            
            List<PersonDTO> allPersonsDTO = new LinkedList<>();
            
            for(Person person : allPersons){
                allPersonsDTO.add(new PersonDTO(person));
            }
            
            return allPersonsDTO;
        }
        finally{
            em.close();
        }
    }

    @Override
    public List<PersonDTO> getPersonsByHobby(Hobby hobby) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<PersonDTO> getPersonByCity(Address address) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PersonDTO getPersonByPhone(Phone phone) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
