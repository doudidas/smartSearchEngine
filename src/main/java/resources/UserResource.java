package resources;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import entities.User;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@Path("/user")
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
    public User getById(@PathParam("id") String id) {
        return getUserById(id);
    }

    @DELETE
    @Path("{id}")
    public Response deleteUserById(@PathParam("id") String id) {
        JSONObject responseBody;
        responseBody = new JSONObject();
        User user = getUserById(id);
        if (user == null) {
            responseBody.put("message", "No user found for this id");
            return Response.status(Response.Status.NOT_FOUND).entity(responseBody).build();
        } else try {
            boolean result = removeUser(userToDoc(user));
            responseBody.put("message", result);
            return Response.status(Response.Status.ACCEPTED).entity(responseBody).build();
        } catch (Exception e) {
            e.printStackTrace();
            responseBody.put("message", "Exception : " + e.toString());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseBody).build();
        }
    }

    @POST
    public Response postUser(User user) {
        JSONObject responseBody;
        responseBody = new JSONObject();
        try {
            if (!userExist(user.getEmail())) {
                saveToDB(user);
                user = getUserByEmail(user.getEmail());
                responseBody.put("token", user.getId());
                responseBody.put("message", "user created");
                return Response.status(Response.Status.CREATED).entity(responseBody).build();
            } else {
                responseBody.put("message", "user already exist");
                return Response.status(Response.Status.FORBIDDEN).entity(responseBody).build();
            }
        } catch (Exception e) {
            responseBody.put("message", "Exeception catched :" + e.toString());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseBody).build();
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
        User userByEmail = getUserByEmail(email);
        return userByEmail != null;
    }

    User getUserByEmail(String email) {
        users = userCollection.find(eq("email", email)).map(UserResource::docToUser).into(new ArrayList<>());
        return (users.size() == 1) ? users.get(0) : null;
    }

    User getUserById(String id) {
        users = userCollection.find(eq("_id", new ObjectId(id))).map(UserResource::docToUser).into(new ArrayList<>());
        return (users.size() == 1) ? users.get(0) : null;
    }

    boolean removeUser(Document user) throws Exception {
        BsonDocument userToRemove;
        DeleteResult result;
        userToRemove = user.toBsonDocument(User.class, MongoClient.getDefaultCodecRegistry());
        try {
            result = userCollection.deleteOne(userToRemove);
        } catch (Exception e) {
            throw new Exception("Failed to delete user on database" + e);
        }
        if (result.getDeletedCount() != 1) {
            throw new Exception("Error while deleting user in database ! " + result.toString());
        }
        return true;
    }

}
