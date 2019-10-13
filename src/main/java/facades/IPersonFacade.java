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
import java.util.List;

/**
 *
 * @author Kasper Jeppesen
 */
public interface IPersonFacade {
    
    public PersonDTO addPerson(PersonDTO personDTO);
    
    public Person deletePerson(int id);
    
    public Person editPerson(PersonDTO personDTO);
    
    public List<PersonDTO> getAllPerson();
    
    public PersonDTO getPersonById(int id);
    
    public List<PersonDTO> getPersonsByHobby(String hobbyName);
    
    public List<PersonDTO> getPersonsByCity(String city);
    
    public PersonDTO getPersonByPhone(String phoneNr);
    
}
