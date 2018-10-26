package resources;

import static com.mongodb.client.model.Filters.eq;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import entities.City;
import entities.User;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.bson.BsonDocument;
import org.bson.BsonJavaScript;
import org.bson.Document;
import org.bson.types.ObjectId;

@Path("/destination")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DestinationResource {
    private final MongoCollection<Document> userCollection;
    private final MongoCollection<Document> citiesCollection;
    private final MongoDatabase database;

    public DestinationResource(MongoDatabase database) {
        this.database = database;
        this.userCollection = database.getCollection("userCollection");
        this.citiesCollection = database.getCollection("ourCitiesCollection");
    }

    @GET
    public Response getAllDestination() {
        ArrayList<City> cities = citiesCollection.find().map(this::docToCity).into(new ArrayList<>());
        // JSONArray cities = citiesCollection.find().map(this::docToCity).into(new
        // JSONArray());
        return Response.status(Response.Status.ACCEPTED).entity(cities).build();
    }

    @GET
    @Path("random")
    public Response getRandomDestination() {
        MongoDatabase mdb = database;
        /* run this <code snippet> in bootstrap */
        BsonDocument randomFunction = new BsonDocument("value", new BsonJavaScript(
                "function(){return db.getCollection('ourCitiesCollection').aggregate([{$sample : { size : 8 }}]);}"));

        mdb.getCollection("system.js").updateOne(new Document("_id", "random"), new Document("$set", randomFunction),
                new UpdateOptions().upsert(true));

        mdb.runCommand(new Document("$eval", "db.loadServerScripts()"));
        /* end </code snippet> */

        Document doc1 = mdb.runCommand(new Document("$eval", "random()"));
        System.out.println(doc1);
        return Response.status(Response.Status.ACCEPTED).entity(doc1.toJson()).build();
    }

    @GET
    @Path("{id}")
    public Response getDestinationById(@PathParam("id") String id) {
        List<City> result = citiesCollection.find(eq("_id", new ObjectId(id))).map(this::docToCity)
                .into(new ArrayList<>());
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
            Object[] result = new Object[2];
            User user = userCollection.find(eq("_id", new ObjectId(id))).map(UserResource::docToUser)
                    .into(new ArrayList<>()).get(0);
            result[0] = user;
            BasicDBObject inQuery = new BasicDBObject();
            List<Integer> list = new ArrayList<>();

            for (String elem : user.getTopics()) {
                list.add(Integer.parseInt(elem));
            }

            inQuery.put("topics", new BasicDBObject("$in", list));
            ArrayList<City> cities = citiesCollection.find(inQuery).map(this::docToCity).into(new ArrayList<>());
            if (cities.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            result[1] = cities;
            return Response.status(Response.Status.ACCEPTED).entity(result).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getStackTrace()).build();
        }
    }

    private City docToCity(Document doc) {
        AtomicReference<City> city = new AtomicReference<>(new City());
        city.get().setId(doc.get("_id").toString());
        city.get().setName(doc.getString("name"));
        city.get().setDescription(doc.getString("description"));
        city.get().setTopics((List<Integer>) doc.getOrDefault("topics", null));
        return city.get();
    }
}
