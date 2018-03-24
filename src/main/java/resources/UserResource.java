package resources;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import entities.User;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import org.bson.Document;
import org.json.simple.JSONObject;

import static com.mongodb.client.model.Filters.eq;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private final MongoCollection<Document> userCollection;
    private final MongoCollection<Document> citiesCollection;
    private List<User> users = new ArrayList<>();

    public UserResource(MongoDatabase database) {
        this.userCollection = database.getCollection("userCollection");
        this.citiesCollection = database.getCollection("ourCitiesCollection");
    }

    @GET
    public List<User> getAllUsers() {
        this.users = userCollection.find().map(UserResource::docToUser).into(new ArrayList<>());
        return users;
    }

    @GET
    @Path("{id}")
    public List<User> getById(@PathParam("id") String id) {
        return getUsersById(id);
    }

    @POST
    public Response postUser(User user) {
        try {
            JSONObject responseBody;
            responseBody = new JSONObject();
            if (!userExist(user.getEmail())) {
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
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Exeception catched :" + e.toString())
                    .build();
        }
    }

    private void saveToDB(User user) throws NullPointerException {
        userCollection.insertOne(userToDoc(user));
    }

    Document userToDoc(User user) {
        return new Document().append("firstName", user.getFirstName()).append("lastName", user.getLastName())
                .append("topics", user.getTopics()).append("email", user.getEmail()).append("gender", user.getGender())
                .append("status", user.getStatus()).append("departure", user.getDeparture());
    }

    static User docToUser(Document doc) {
        User user = new User();
        user.setId(doc.get("_id").toString());
        user.setGender(doc.getString("gender"));
        user.setStatus(doc.getString("status"));
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

    List<User> getUsersByEmail(String email) {
        return userCollection.find(eq("email", email)).map(UserResource::docToUser).into(new ArrayList<>());
    }

    List<User> getUsersById(String id) {
        return userCollection.find(eq("_id", id)).map(UserResource::docToUser).into(new ArrayList<>());
    }

}
