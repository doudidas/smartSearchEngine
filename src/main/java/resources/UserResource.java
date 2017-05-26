package resources;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import entities.User;
import org.bson.Document;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private final MongoCollection<Document> collection;
    private List<User> users = new ArrayList<>();

    public UserResource(MongoDatabase database) {

        this.collection = database.getCollection("userCollection");
    }

    @GET
    public List<User> getAllUsers() {
        this.users = collection.find().map(this::docToUser).into(new ArrayList<>());
        return users;
    }

    @GET
    @Path("{email}")
    public List<User> getByEmail(@PathParam("email") String email) {
        return getUsersByEmail(email);
    }

    @POST
    public Response postUser(User user) {
        try {
            if (! userExist(user.getEmail())) {
                saveToDB(user);
            } else {
                return Response.status(Response.Status.FORBIDDEN).entity("user exist !").build();
            }
            return Response.status(Response.Status.CREATED).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Exeception catched :" + e.toString()).build();
        }
    }

    private void saveToDB(User user) throws NullPointerException {

        collection.insertOne(userToDoc(user));
    }

    private final Document userToDoc(User user) {

        return new Document()
                .append("firstName", user.getFirstName())
                .append("lastName", user.getLastName())
                .append("topics",user.getTopics())
                .append("email", user.getEmail());
    }

    private User docToUser(Document doc) {
        User user = new User();
        user.setId(doc.get("_id").toString());
        user.setFirstName(doc.getString("firstName"));
        user.setLastName(doc.getString("lastName"));
        user.setEmail(doc.getString("email"));
        user.setTopics((List<String>) doc.get("topics"));
        return user;
    }

    private Boolean userExist(String email) {
        List<User> userByEmail = getUsersByEmail(email);
        return !userByEmail.isEmpty();
    }

    private List<User> getUsersByEmail(String email) {
        return collection.find(eq("email", email)).map(this::docToUser).into(new ArrayList<>());
    }
    private List<User> getUsersById(String id) {
        return collection.find(eq("_id", id)).map(this::docToUser).into(new ArrayList<>());
    }
}