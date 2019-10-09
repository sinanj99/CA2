package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.PersonDTO;
import entities.Address;
import entities.CityInfo;
import entities.Hobby;
import entities.Person;
import entities.Phone;
import utils.EMF_Creator;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

@OpenAPIDefinition(
        info = @Info(
                title = "Person API",
                version = "0.4",
                description = "API to get info about persons.",
                contact = @Contact(name = "Lars Mortensen", email = "lam@cphbusiness.dk")
        ),
        tags = {
            @Tag(name = "person", description = "API related to person Info")

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
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Operation(summary = "Get person by id",
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
        PersonDTO p;
        try {
            p = FACADE.getPersonById(id);
        } catch (NullPointerException e) {
            throw new WebApplicationException("Person not found", 404);
        }
        return p;
    }
    
    
    @Operation(summary = "Get person count",
            tags = {"person"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json", schema = @Schema(implementation = PersonDTO.class))),
                @ApiResponse(responseCode = "200", description = "The person count has succesfully been fetched")})
    @GET
    @Path("count")
    @Produces(MediaType.APPLICATION_JSON)
    public String getPersonCount() {
        return "{\"count\""+":"+FACADE.getPersonCount()+"}";
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)

    @Operation(summary = "Create new person", tags = {"person"},
            responses = {
                @ApiResponse(responseCode = "200", description = "The newly created person"),
                @ApiResponse(responseCode = "400", description = "Not all arguments provided with the body"),
                @ApiResponse(responseCode = "409", description = "Person already exists")
            })
    public PersonDTO createPerson(PersonDTO personDTO) {

        //check for missing input
        if (personDTO.getFirstName() == null || personDTO.getLastName() == null
                || personDTO.getHobbies() == null || personDTO.getPhones() == null
                || personDTO.getStreet() == null || personDTO.getStreet() == null
                || personDTO.getZip() == null) {
            throw new WebApplicationException("Not all arguments provided with the body", 400);
        }
        Person person = DTOMapper(personDTO);
        personDTO = FACADE.addPerson(person);
        
        return personDTO;
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)

    @Operation(summary = "Edit a person", tags = {"person"},
            responses = {
                @ApiResponse(responseCode = "200", description = "The person has succesfully been edited"),
                @ApiResponse(responseCode = "400", description = "No id provided"),
                @ApiResponse(responseCode = "404", description = "Person not found")
            })
    public PersonDTO editPerson(PersonDTO personDTO) {

        if (personDTO.getId() == null) {
            throw new WebApplicationException("No id provided", 400);
        } 
        Person person = DTOMapper(personDTO);
        person.setId(personDTO.getId());

        try {
            personDTO = new PersonDTO(FACADE.editPerson(person));
        } catch (NullPointerException e) {
            throw new WebApplicationException("Person not found", 404);
        }

        return personDTO;
    }

    @DELETE
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.APPLICATION_JSON})
    @Operation(summary = "Delete a person", tags = {"person"},
            responses = {
                @ApiResponse(responseCode = "200", description = "The person has succesfully been deleted"),
                @ApiResponse(responseCode = "404", description = "Person not found")
            })
    public String deletePerson(@PathParam("id") int id) {
        try {
            FACADE.deletePerson(id);
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException("Person not found", 404);
        }
        return "{\"status\": \"deleted\"}";
    }
    private Person DTOMapper(PersonDTO personDTO) {
        
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
