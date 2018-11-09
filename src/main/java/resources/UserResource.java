package resources;

import static com.mongodb.client.model.Filters.eq;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import entities.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    private final MongoCollection<Document> userCollection;
    // private final MongoCollection<Document> citiesCollection;
    private List<User> users = new ArrayList<>();

    public UserResource(MongoDatabase database) {
        this.userCollection = database.getCollection("userCollection");
        // this.citiesCollection = database.getCollection("ourCitiesCollection");
    }

    @GET
    public List<User> getAllUsers() {
        this.users = userCollection.find().map(UserResource::docToUser).into(new ArrayList<User>());
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
        HashMap<String, String> map = new HashMap<String, String>();
        responseBody = new JSONObject();
        User user = getUserById(id);
        if (user == null) {
            map.put("message", "No user found for this id");
            responseBody = new JSONObject(map);
            return Response.status(Response.Status.NOT_FOUND).entity(responseBody).build();
        } else
            try {
                boolean result = removeUser(userToDoc(user));
                map.put("message", String.valueOf(result));
                responseBody = new JSONObject(map);
                return Response.status(Response.Status.ACCEPTED).entity(responseBody).build();
            } catch (Exception e) {
                e.printStackTrace();
                map.put("message", "Exception : " + e.toString());
                responseBody = new JSONObject(map);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseBody).build();
            }
    }

    @POST
    public Response postUser(User user) {
        JSONObject responseBody;
        HashMap<String, String> map = new HashMap<String, String>();
        try {
            if (!userExist(user.getEmail())) {
                saveToDB(user);
                user = getUserByEmail(user.getEmail());
                map.put("token", user.getId());
                map.put("message", "user created");
                responseBody = new JSONObject(map);
                return Response.status(Response.Status.CREATED).entity(responseBody).build();
            } else {
                map.put("message", "user already exist, please do a PUT Request");
                responseBody = new JSONObject(map);
                return Response.status(Response.Status.FORBIDDEN).entity(responseBody).build();
            }
        } catch (Exception e) {
            map.put("message", "Exeception catched :" + e.toString());
            responseBody = new JSONObject(map);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseBody).build();
        }
    }

    @PUT
    public Response updateUserById(User user) throws Exception {
        Document local = userToDoc(getUserById(user.getId()));
        boolean userRemoved = removeUser(local);
        if (userRemoved) {
            saveToDB(user);
            return Response.status(Response.Status.ACCEPTED).build();
        } else {
            JSONObject responseBody;
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("message", "this user doesn't exist on base !");
            responseBody = new JSONObject(map);
            return Response.status(Response.Status.FORBIDDEN).entity(responseBody).build();
        }

    }

    private void saveToDB(User user) throws NullPointerException {
        userCollection.insertOne(userToDoc(user));
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
        user.setHashcode(doc.getString("hashcode"));
        user.setUsername(doc.getString("username"));
        return user;
    }

    private Document userToDoc(User user) {
        return new Document().append("firstName", user.getFirstName()).append("lastName", user.getLastName())
                .append("topics", user.getTopics()).append("email", user.getEmail()).append("gender", user.getGender())
                .append("status", user.getStatus()).append("departure", user.getDeparture())
                .append("username", user.getUsername()).append("hashcode", user.getHashcode());
    }

    private Boolean userExist(String email) {
        User userByEmail = getUserByEmail(email);
        return userByEmail != null;
    }

    private User getUserByEmail(String email) {
        users = userCollection.find(eq("email", email)).map(UserResource::docToUser).into(new ArrayList<>());
        return (users.size() == 1) ? users.get(0) : null;
    }

    private User getUserById(String id) {
        users = userCollection.find(eq("_id", new ObjectId(id))).map(UserResource::docToUser).into(new ArrayList<>());
        return (users.size() == 1) ? users.get(0) : null;
    }

    private boolean removeUser(Document user) throws Exception {
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
