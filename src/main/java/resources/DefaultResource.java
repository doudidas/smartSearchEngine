package resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONObject;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DefaultResource {
  public DefaultResource() {
  }
  @GET
  public Response sayHi() {
      JSONObject responseBody = new JSONObject();
      responseBody.put("message", "hello");
      return Response.status(Response.Status.ACCEPTED).entity(responseBody).build();
  }
}
