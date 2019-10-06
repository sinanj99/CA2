package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.PersonDTO;
import entities.Address;
import entities.CityInfo;
import entities.Hobby;
import entities.Person;
import entities.Phone;
import entities.RenameMe;
import utils.EMF_Creator;
import facades.FacadeExample;
import facades.PersonFacade;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@OpenAPIDefinition(
        info = @Info(
                title = "Person API",
                version = "0.4",
                description = "API to get info about persons.",
                contact = @Contact(name = "Lars Mortensen", email = "lam@cphbusiness.dk")
        ),
        tags = {
            @Tag(name = "movie", description = "API related to Movie Info")

        },
        servers = {
            @Server(
                    description = "For Local host testing",
                    url = "http://localhost:8080/startcodeoas"
            ),
            @Server(
                    description = "Server API",
                    url = "https://sinanjasar"
            )

        }
)
@Path("person")
public class PersonResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(
            "pu",
            "jdbc:mysql://localhost:3307/startcode",
            "dev",
            "ax2",
            EMF_Creator.Strategy.CREATE);
    private static final PersonFacade FACADE = PersonFacade.getPersonFacade(EMF);

    @Operation(summary = "Get Person by ID",
            tags = {"person"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "The Requested Person"),
                @ApiResponse(responseCode = "404", description = "Person not found")})

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public PersonDTO getPerson(@PathParam("id") int id) {
        Set<Phone> phones = new HashSet();
        Phone phone = new Phone("30232376", "private");
        phones.add(phone);
        CityInfo ci = new CityInfo("2800", "lyngby");
        Address address = new Address(ci, "Lyngbyvej", "home address");
        Set<Hobby> hobbies = new HashSet();
        Hobby hobby = new Hobby("coding", "writing code");
        hobbies.add(hobby);
        return new PersonDTO(new Person("email", "fname", "lname", phones, address, hobbies));
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)

    @Operation(summary = "Create new person", tags = {"person"},
            responses = {
                @ApiResponse(responseCode = "200", description = "The newly created person"),
                @ApiResponse(responseCode = "400", description = "Not all arguments provided with the body")
            })
    public PersonDTO createPerson(PersonDTO personDTO) {
        CityInfo cityInfo = new CityInfo(personDTO.getZip(), personDTO.getCity());
        Address address = new Address(cityInfo, personDTO.getStreet(), personDTO.getStreetInfo());
        Set<Hobby> hobbies = new HashSet();
        Set<Phone> phones = new HashSet();
        for (Map.Entry<String, String> hobby : personDTO.getHobbies().entrySet()) {
            hobbies.add(new Hobby(hobby.getKey(), hobby.getValue()));
        }
        for (Map.Entry<String, String> phone : personDTO.getPhones().entrySet()) {
            phones.add(new Phone(phone.getKey(), phone.getValue()));
        }
        Person person = new Person(personDTO.getEmail(), personDTO.getFirstName(),
                personDTO.getLastName(), phones, address, hobbies);
        personDTO = FACADE.addPerson(person);
        return personDTO;
    }
}
