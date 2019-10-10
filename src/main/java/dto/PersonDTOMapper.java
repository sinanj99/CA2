/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import entities.Address;
import entities.CityInfo;
import entities.Hobby;
import entities.Person;
import entities.Phone;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kasper Jeppesen
 */
public class PersonDTOMapper {
    
    public Person DTOMapper(PersonDTO personDTO) {
        
        CityInfo cityInfo = new CityInfo(personDTO.getZip(), personDTO.getCity());
        Address address = new Address(cityInfo, personDTO.getStreet(), personDTO.getStreetInfo());
        List<Hobby> hobbies = new ArrayList();
        List<Phone> phones = new ArrayList();
        //syntax for hobbies is: "name:value,description:value"
        for (String hobby : personDTO.getHobbies()) {
            hobbies.add(new Hobby(hobby.split(",")[0].split(":")[1],
                    hobby.split(",")[1].split(":")[1]));
        }
        //syntax for phones is: "number:value,description:value"
        for (String phone : personDTO.getPhones()) {
            phones.add(new Phone(phone.split(",")[0].split(":")[1],
                    phone.split(",")[1].split(":")[1]));
        }
        Person p = new Person(personDTO.getEmail(), personDTO.getFirstName(),
                personDTO.getLastName(), phones, address, hobbies);
        
        for(Phone phone : phones) {
            phone.setPerson(p);
        }
        return p;
    }
    
}
