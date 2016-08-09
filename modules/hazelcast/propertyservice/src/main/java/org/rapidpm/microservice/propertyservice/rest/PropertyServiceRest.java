package org.rapidpm.microservice.propertyservice.rest;


import com.google.gson.Gson;
import org.rapidpm.microservice.propertyservice.api.PropertyService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;

@Path("/propertyservice")
public class PropertyServiceRest {

  @Inject
  private PropertyService propertyService;
  private final Gson gson = new Gson();

  @GET()
  @Path("loadProperties")
  @Produces(MediaType.TEXT_PLAIN)
  public String loadProperties(@QueryParam("scope") String scope) {
    return propertyService.loadProperties(scope);
  }


  @GET()
  @Path("getSingleProperty")
  @Produces(MediaType.TEXT_PLAIN)
  public String getSingleProperty(@QueryParam("property") String property) {
    return propertyService.getSingleProperty(property);
  }

  @GET()
  @Path("getIndexOfLoadedProperties")
  @Produces(MediaType.APPLICATION_JSON)
  public String getIndexOfLoadedProperties() {
    return gson.toJson(propertyService.getIndexOfLoadedProperties());
  }

  @GET()
  @Path("getIndexOfScope")
  @Produces(MediaType.APPLICATION_JSON)
  public String getIndexOfScope(@QueryParam("scope") String scope) {
    return gson.toJson(propertyService.getIndexOfScope(scope));
  }

  @GET()
  @Path("getPropertiesOfScope")
  @Produces(MediaType.APPLICATION_JSON)
  public String getPropertiesOfScope(@QueryParam("scope") String scope) {
    return gson.toJson(propertyService.getPropertiesOfScope(scope));
  }

  @GET
  @Path("getConfigurationFile")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public Response getConfigurationFile(@QueryParam("filename") String filename) {
    try {
      File file = propertyService.getConfigurationFile(filename);
      return Response.ok(file, MediaType.APPLICATION_OCTET_STREAM)
          .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
          .build();
    } catch (IOException e) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
  }

}
