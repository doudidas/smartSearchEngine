package resources;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import entities.Topic;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.bson.Document;
import org.json.simple.JSONArray;

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
        JSONArray topics = topicCollection.find().map(this::docToTopic).into(new JSONArray());
        return Response.status(Response.Status.ACCEPTED).entity(topics).build();
    }

    private Topic docToTopic(Document doc) {
        Topic topic = new Topic();
        topic.setId(doc.get("_id").toString());
        topic.setName(doc.getString("name"));
        return topic;
    }
}
