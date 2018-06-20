package resources;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import entities.Topic;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;

import static com.mongodb.client.model.Filters.eq;

@Path("/topic")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TopicResource {
    private final MongoCollection<Document> topicCollection;

    public TopicResource(MongoDatabase database) {
        this.topicCollection = database.getCollection("topicCollection");
    }

    @GET
    public Response getAllTopics() {
        ArrayList<Topic> topics = topicCollection.find().map(this::docToTopic).into(new ArrayList<>());
        return Response.status(Response.Status.ACCEPTED).entity(topics).build();
    }

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") String id) {
        try {
            Topic topic = getTopicById(id);
            if(topic == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.status(Response.Status.ACCEPTED).entity(topic).build();

        } catch (Exception e) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("message", "Exeception catched :" + e.toString());
            JSONObject responseBody = new JSONObject(map);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(responseBody).build();
        }
    }

    private Topic docToTopic(Document doc) {
        Topic topic = new Topic();
        topic.setId(doc.get("_id").toString());
        topic.setDescription((doc.getString("description")));
        topic.setName(doc.getString("name"));
        return topic;
    }

    private Topic getTopicById(String id) throws Exception {
        ArrayList<Topic> topics = topicCollection.find(eq("_id", new ObjectId(id))).map(this::docToTopic).into(new ArrayList<>());
        int size = topics.size();
        if (size > 1) {
           throw new Exception("[TOPIC] Database corruption !! There is " + topics.size() + " topic with this id " + id + "!");
        } else if (size == 1) {
            return topics.get(0);
        }
        return null;
    }
}
