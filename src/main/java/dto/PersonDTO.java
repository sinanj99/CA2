package dto;

import entities.Address;
import entities.Hobby;
import entities.Person;
import entities.Phone;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

@Schema(name = "Person")
public class PersonDTO {

    private Long id;
    @Schema(required = true, example = "lars@lars.dk")
    private String email;
    @Schema(required = true, example = "Lars")
    private String firstName;
    @Schema(required = true, example = "Mortensen")
    private String lastName;
    @Schema(required = true)
    private Set<Phone> phone;
    @Schema(required = true)
    private Address address;
    @Schema(required = true)
    private Set<Hobby> hobbies;

    public PersonDTO(Person p) {
        this.email = p.getEmail();
        this.firstName = p.getFirstName();
        this.lastName = p.getLastName();
        this.phone = p.getPhone();
        this.address = p.getAddress();
        this.hobbies = p.getHobbies();
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

    public Set<Phone> getPhone() {
        return phone;
    }

    public void setPhone(Set<Phone> phone) {
        this.phone = phone;
    }

}
