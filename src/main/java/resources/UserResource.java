package resources;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import entities.User;
import org.bson.Document;
import org.json.simple.JSONObject;

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
    @Path("{id}")
    public List<User> getByEmail(@PathParam("id") String id) {
        return getUsersById(id);
    }

    @POST
    public Response postUser(User user) {
        try {
            JSONObject responseBody;
            responseBody = new JSONObject();
            if (! userExist(user.getEmail())) {
                saveToDB(user);
                user = getUsersByEmail(user.getEmail()).get(0);
                responseBody.put("token", user.getId());
                responseBody.put("message", "user created");
                return Response.status(Response.Status.CREATED).entity(responseBody).build();
            } else {
                responseBody.put("message", "user already exist");
                return Response.status(Response.Status.FORBIDDEN).entity(responseBody).build();
            }
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
                .append("email", user.getEmail())
                .append("departure", user.getDeparture());
    }

    private User docToUser(Document doc) {
        User user = new User();
        user.setId(doc.get("_id").toString());
        user.setFirstName(doc.getString("firstName"));
        user.setLastName(doc.getString("lastName"));
        user.setEmail(doc.getString("email"));
        user.setDeparture((doc.getString("departure")));
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