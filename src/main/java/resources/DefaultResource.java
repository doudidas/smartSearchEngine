package resources;

import java.util.HashMap;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.json.simple.JSONObject;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DefaultResource {
    HashMap<String, Object> description;
    HashMap<String, String[]> destinationDescription, topicDescription, userDescription;
    String userEndpoints[] = { "api/users", "api/users/{id}" };
    String topicEndpoints[] = { "api/topic" };
    String destinationEndpoints[] = { "api/destination", "api/destination/random", "/api/destination/user/{id}",
            "/api/destination/{id}" };

    public DefaultResource() {
        this.userDescription.put("endpoints", userEndpoints);
        this.topicDescription.put("endpoints", topicEndpoints);
        this.destinationDescription.put("endpoints", destinationEndpoints);

        this.description.put("user", this.userDescription);
        this.description.put("topic", this.topicDescription);
        this.description.put("destination", this.destinationDescription);

    }

    @GET
    public Response getDescriptions() {
        return Response.status(Response.Status.ACCEPTED).entity(new JSONObject(description)).build();
    }
}
