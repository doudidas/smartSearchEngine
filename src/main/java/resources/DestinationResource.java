package resources;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import entities.City;
import entities.User;
import org.bson.BsonDocument;
import org.bson.BsonJavaScript;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

@Path("/destination")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DestinationResource {
    private final MongoCollection<Document> userCollection;
    private final MongoCollection<Document> citiesCollection;
    private final MongoDatabase database;
    private List<User> users = new ArrayList<>();

    public DestinationResource(MongoDatabase database) {
        this.database =  database;
        this.userCollection = database.getCollection("userCollection");
        this.citiesCollection = database.getCollection("ourCitiesCollection");
    }


    @GET
    public Response getAllDestination() {
        JSONArray cities = citiesCollection.find().map(this::docToCity).into(new JSONArray());
        return  Response.status(Response.Status.ACCEPTED).entity(cities).build();
    }
    @GET
    @Path("random")
    public Response getRandomDestination() {
       BsonDocument getRandom = new BsonDocument("value",
                new BsonJavaScript("function(){return db.getCollection('ourCitiesCollection').aggregate([{$sample : { size : 8 }}]);}"));
       Document doc1 = database.runCommand(new Document("$eval", "getRandom()"));

       return  Response.status(Response.Status.ACCEPTED).entity(doc1.toJson()).build();
    }
    @GET
    @Path("{id}")
    public Response getDestinationById(@PathParam("id") String id) {
        JSONArray result = citiesCollection.find(eq("_id",  new ObjectId(id))).map(this::docToCity).into(new JSONArray());
        if (result.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            return Response.status(Response.Status.ACCEPTED).entity(result.get(0)).build();

        }
    }

    @GET
    @Path("user/{id}")
    public Response getDestinationByUser(@PathParam("id") String id) {
    try {
        JSONArray result = new JSONArray();

        User user = userCollection.find(eq("_id", new ObjectId(id))).map(UserResource::docToUser).into(new ArrayList<>()).get(0);
        result.add(user);

        BasicDBObject inQuery = new BasicDBObject();
        List<Integer> list = new ArrayList<>();

        for (String elem : user.getTopics()) {
            list.add(Integer.parseInt(elem));
        }

        inQuery.put("topics", new BasicDBObject("$in", list));
        JSONArray cities = citiesCollection.find(inQuery).map(this::docToCity).into(new JSONArray());
        if (cities.isEmpty()){
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        result.add(cities);
        return Response.status(Response.Status.ACCEPTED).entity(result).build();

    } catch (Exception e) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getStackTrace()).build();
    }
    }

    private final Document userToDoc(City city) {
        return new Document()
                .append("id", city.getId())
                .append("name", city.getName())
                .append("topics",city.getTopics())
                .append("description", city.getDescription());
    }

    private final City docToCity(Document doc ) {
        City city = new City();
        city.setId(doc.get("_id").toString());
        city.setName(doc.getString("name"));
        city.setDescription(doc.getString("description"));
        city.setTopics((List<Integer>) doc.get("topics"));
        return city;
    }
}
