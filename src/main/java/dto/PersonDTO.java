package dto;

import entities.Hobby;
import entities.Person;
import entities.Phone;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Schema(name = "Person")
public class PersonDTO {

    private Long id;
    @Schema(example = "lars@lars.dk")
    private String email;
    @Schema(required = true, example = "Lars")
    private String firstName;
    @Schema(required = true, example = "Mortensen")
    private String lastName;
    @Schema(required = true, example = "[\"number:30232376,description:private\",\"number:12121212,description:work\"]")
    private List<String> phones = new ArrayList();
    @Schema(required = true, example = "Lyngbyvej 21")
    private String street;
    @Schema(example = "Home address")
    private String streetInfo;
    @Schema(required = true, example = "2800")
    private String zip;
    @Schema(required = true, example = "Lyngby")
    private String city; 
    @Schema(required = true, example = "[\"name:coding,description:writing code\",\"name:beer,description:drinking beer\"]")
    private List<String> hobbies = new ArrayList();

    public PersonDTO(Person p) {
        this.email = p.getEmail();
        this.firstName = p.getFirstName();
        this.lastName = p.getLastName();
        for(Phone phone : p.getPhone()) {
            phones.add("\"number:"+phone.getNumber()+",description:"+phone.getDescription()+"\"");
        }
        this.street = p.getAddress().getStreet();
        this.streetInfo = p.getAddress().getInfo();
        this.zip = p.getAddress().getCityInfo().getZip();
        this.city = p.getAddress().getCityInfo().getCity();
        for(Hobby hobby : p.getHobbies()) {
            phones.add("\"name:"+hobby.getName()+",description:"+hobby.getDescription()+"\"");
        }
    }

    public PersonDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetInfo() {
        return streetInfo;
    }

    public void setStreetInfo(String streetInfo) {
        this.streetInfo = streetInfo;
    }

    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
    }

    public List<String> getHobbies() {
        return hobbies;
    }

    public void setHobbies(List<String> hobbies) {
        this.hobbies = hobbies;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


}
