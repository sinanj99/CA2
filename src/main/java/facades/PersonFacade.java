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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.WebApplicationException;

/**
 *
 * @author Kasper Jeppesen
 */
public class PersonFacade implements IPersonFacade {

    private static PersonFacade instance;
    private static EntityManagerFactory emf;

    //Private Constructor to ensure Singleton
    private PersonFacade() {
    }

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
    public long getPersonCount() {
        EntityManager em = emf.createEntityManager();
        try {
            long PersonCount = (long) em.createQuery("SELECT COUNT(person) FROM Person person").getSingleResult();
            return PersonCount;
        } finally {
            em.close();
        }

    }

    @Override
    public PersonDTO addPerson(Person person) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            //for each phone, check if already exists, so we know if it needs to be persisted
            checkPhone(person, em);
            //check if address already exists, so we know if it needs to be persisted
            checkAddress(person, em);
            //for each hobby, check if already exists, so we know if it needs to be persisted
            List<Hobby> toAdd = new ArrayList();
            List<Hobby> toRemove = new ArrayList();
            checkHobby(person, toAdd, toRemove, em);

            person.getHobbies().removeAll(toRemove);
            person.getHobbies().addAll(toAdd);
            em.persist(person);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new PersonDTO(person);
    }

    @Override
    public Person deletePerson(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Person p = em.find(Person.class, Long.valueOf(id));
            em.remove(p);
            em.getTransaction().commit();
            return p;
        } finally {
            em.close();
        }
    }

    @Override
    public Person editPerson(Person person) {
        EntityManager em = emf.createEntityManager();
        try {
            if (em.find(Person.class, person.getId()) == null) {
                throw new NullPointerException();
            }
            em.getTransaction().begin();
            em.merge(person);
            em.getTransaction().commit();
            return person;
        } finally {
            em.close();
        }
    }

    @Override
    public List<PersonDTO> getAllPerson() {

        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<Person> query
                    = em.createQuery("Select person from Person person", Person.class);

            List<Person> allPersons = query.getResultList();

            List<PersonDTO> allPersonsDTO = new LinkedList<>();

            for (Person person : allPersons) {
                allPersonsDTO.add(new PersonDTO(person));
            }

            return allPersonsDTO;
        } finally {
            em.close();
        }
    }

    @Override
    public List<PersonDTO> getPersonsByHobby(Hobby hobby) {
        EntityManager em = emf.createEntityManager();
        List<PersonDTO> withHobby = new ArrayList();
        try {
            em.getTransaction().begin();
            hobby = findHobby(hobby, em);
            System.out.println(hobby.getName());
            List<PersonDTO> allPersons = getAllPerson();
            System.out.println(allPersons.get(0).getFirstName());
            for (PersonDTO p : allPersons) {
                if (p.getHobbies().contains("name:"+hobby.getName()+",description:"+hobby.getDescription())) {
                    withHobby.add(p);
                }
            }
        } finally {
            em.close();
        }
        return withHobby;
    }

    @Override
    public List<PersonDTO> getPersonByCity(Address address) {
        EntityManager em = emf.createEntityManager();

        try {
            TypedQuery<Person> query
                    = em.createQuery("Select person from Person person where person.address.cityInfo.city = :address", Person.class);
            query.setParameter("address", address.getCityInfo().getCity());

            List<Person> allPersonsInTheCity = query.getResultList();
            List<PersonDTO> allPersonsInTheCityDTO = new LinkedList<>();

            for (Person person : allPersonsInTheCity) {
                allPersonsInTheCityDTO.add(new PersonDTO(person));
            }

            return allPersonsInTheCityDTO;
        } finally {
            em.close();
        }
    }

    @Override
    public PersonDTO getPersonByPhone(Phone phone) {
//        EntityManager em = emf.createEntityManager();
//        
//        try{
//            TypedQuery<Person> query = 
//                       em.createQuery("Select person from Person person where person.phone.number = :number",Person.class);
//            query.setParameter("number", phone.getNumber());
//            
//            return new PersonDTO(query.getSingleResult());
//        }
//        finally{
//            em.close();
//        }

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PersonDTO getPersonById(int id) {
        EntityManager em = emf.createEntityManager();
        try {
            return new PersonDTO(em.find(Person.class, Long.valueOf(id)));
        } finally {
            em.close();
        }
    }

    private void checkPhone(Person person, EntityManager em) {
        //for each phone, check if already exists, so we know if it needs to be persisted
        TypedQuery<Phone> searchPhone;
        for (Phone phone : person.getPhone()) {
            searchPhone = em.createQuery("SELECT p.person FROM Phone p WHERE p.number = :number", Phone.class);
            searchPhone.setParameter("number", phone.getNumber());
            if (!searchPhone.getResultList().isEmpty()) {
                throw new WebApplicationException("Person already exists", 409);
            }
        }
    }

    private void checkAddress(Person person, EntityManager em) {
        TypedQuery<Address> searchAddress
                = em.createQuery("SELECT a FROM Address a WHERE a.street = :street", Address.class);
        searchAddress.setParameter("street", person.getAddress().getStreet());
        if (!searchAddress.getResultList().isEmpty()) {
            Address a = searchAddress.getSingleResult();
            person.setAddress(a);
        }
    }

    private void checkHobby(Person person, List<Hobby> toAdd, List<Hobby> toRemove, EntityManager em) {
        TypedQuery<Hobby> searchHobby;
        for (Hobby hobby : person.getHobbies()) {
            searchHobby = em.createQuery("SELECT h FROM Hobby h WHERE h.name = :name", Hobby.class);
            searchHobby.setParameter("name", hobby.getName());
            if (!searchHobby.getResultList().isEmpty()) {
                toRemove.add(hobby);
                hobby = searchHobby.getSingleResult();
                toAdd.add(hobby);
            }
        }
    }

    private Hobby findHobby(Hobby h, EntityManager em) {
        return em.createQuery("SELECT h FROM Hobby h WHERE h.name = :name", Hobby.class)
                .setParameter("name", h.getName()).getSingleResult();
    }

}
