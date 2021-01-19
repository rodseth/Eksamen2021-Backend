package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.ActivityDTO;
import facades.ActivityFacade;
import java.io.IOException;
import java.util.concurrent.*;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import fetchers.ExampleFetcher;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import utils.EMF_Creator;

@Path("activity")
public class ActivityResource {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static ExecutorService es = Executors.newCachedThreadPool();
    private static final EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();
    private static final ActivityFacade ACTIVITY_FACADE = ActivityFacade.getActivityFacade(emf);
   
   @POST
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public String addActivity(String activity) throws NotFoundException {
       ActivityDTO activityDTO = gson.fromJson(activity, ActivityDTO.class);
       ActivityDTO newActivity = ACTIVITY_FACADE.addActivity(activityDTO);
       
       return gson.toJson(newActivity);
              
   }
   
   
   
   
   
   
    
   
}
