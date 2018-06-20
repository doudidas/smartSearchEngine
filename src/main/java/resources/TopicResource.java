package resources;

import java.util.ArrayList;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import entities.Topic;
import org.bson.types.ObjectId;

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
        ArrayList<Topic> topics = topicCollection.find().map(this::docToTopic).into(new ArrayList<Topic>());
        return Response.status(Response.Status.ACCEPTED).entity(topics).build();
    }

    private Topic docToTopic(Document doc) {
        Topic topic = new Topic();
        topic.setId(doc.get("_id").toString());
        topic.setName(doc.getString("name"));
        return topic;
    }

    @GET
    @Path("{id}")
    public Topic getById(@PathParam("id") String id) {
        return getTopicById(id);

    }

    Topic getTopicById(String id) {
        ArrayList<Topic> topics = topicCollection.find(eq("_id", new ObjectId(id))).map(this::docToTopic).into(new ArrayList<Topic>());
        return (topics.size() == 1) ? topics.get(0) : null;
    }
}
