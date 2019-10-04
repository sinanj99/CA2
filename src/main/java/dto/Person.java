package dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

@Schema(name = "Person")  //Because of this we could have called the class MovieInfoDTO
public class Person {

    private Long id;
    @Schema(required = true, example = "lars@lars.dk")
    private String email;
    @Schema(required = true, example = "Lars")
    private String firstName;
    @Schema(required = true, example = "Mortensen")
    private String lastName;
    @Schema(example = "[\"Work\",\"23213444\"]")
    private Set<String> phone;

    public Person(String email, String firstName, String lastName, Set<String> phone) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public Person() {
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

    public Set<String> getPhone() {
        return phone;
    }

    public void setPhone(Set<String> phone) {
        this.phone = phone;
    }

}
