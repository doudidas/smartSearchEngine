package resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
<<<<<<< HEAD
=======

>>>>>>> 5f7f252250c40cb6dfdd2ee4f3d16ecdce339be2

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DefaultResource {
  public DefaultResource() {
  }
<<<<<<< HEAD
  @GET
  public Response sayHi() {
      JSONObject responseBody = new JSONObject();
      responseBody.put("message", "hello");
      return Response.status(Response.Status.ACCEPTED).entity(responseBody).build();
  }
=======
    @GET
    public Response sayHi() {
        return Response.status(Response.Status.ACCEPTED).build();
    }
>>>>>>> 5f7f252250c40cb6dfdd2ee4f3d16ecdce339be2
}
