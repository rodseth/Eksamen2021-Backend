package facades;

import dto.ActivityDTO;
import dto.UserDTO;
import entities.Activity;
import entities.ExerciseType;
import entities.Role;
import entities.User;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import security.errorhandling.AuthenticationException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.NotFoundException;

public class ActivityFacade {

    private static EntityManagerFactory emf;
    private static ActivityFacade instance;

    private ActivityFacade() {
    }

    public static ActivityFacade getActivityFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new ActivityFacade();
        }
        return instance;
    }
    
    public ActivityDTO addActivity(ActivityDTO a) throws NotFoundException {
        EntityManager em = emf.createEntityManager();
        
        
      
        Activity activityToAdd = new Activity(a.getExerciseDate(), a.getDurationInMinutes(), a.getDistanceInKm(), a.getComment());
        ExerciseType exerciseTypeToAdd = new ExerciseType(a.getExerciseType());
        User userToAdd = new User(a.getUsername());
        
        exerciseTypeToAdd.setActivitys(activityToAdd);
        userToAdd.setActivitys(activityToAdd);
        
        
        ExerciseType foundExerciseType = em.find(ExerciseType.class, exerciseTypeToAdd.getTypeName());
        
        if (foundExerciseType != null) {
            if (foundExerciseType.getTypeName().equals(activityToAdd.getExerciseType().getTypeName())) {
                activityToAdd.getExerciseType().setTypeName(foundExerciseType.getTypeName());
            } else {
                throw new NotFoundException("feil i adActivityMetoden");
            }
            
        }
        
       
        
        Query query = em.createQuery("SELECT a FROM Activity a WHERE  a.typeName =:name ");
        query.setParameter("name", activityToAdd.getExerciseType().getTypeName());
        
           em.getTransaction().begin();
           em.persist(activityToAdd);
           em.getTransaction().commit();
           em.close();
       
        
        
        
        return new ActivityDTO(activityToAdd);
        
    }

    public UserDTO deleteUser(String userName) {

        EntityManager em = emf.createEntityManager();

        try{
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            em.remove(user);
            em.getTransaction().commit();

            return new UserDTO(user);

        }finally {
            em.close();
        }


    }

    public User getVerifiedUser(String username, String password) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.find(User.class, username);
            if (user == null || !user.verifyPassword(password)) {
                throw new AuthenticationException("Invalid user name or password");
            }
        } finally {
            em.close();
        }
        return user;
    }

    public List<UserDTO> getAllUsers (){

        EntityManager em = emf.createEntityManager();

        try{
           TypedQuery query = em.createQuery("SELECT u from User u", User.class);
            List<User> userList = query.getResultList();
            List<UserDTO> userDTOlist = new ArrayList<>();

            for (User user: userList){
                userDTOlist.add(new UserDTO(user));
            }

            return userDTOlist;

        }finally {
            em.close();
        }

    }

    

    private void checkIfExists(ExerciseType exerciseType, EntityManager em) throws AuthenticationException {

        Query query = em.createQuery("SELECT e FROM ExerciseType e WHERE e.typeName =:typeName ");
        query.setParameter("typeName", exerciseType.getTypeName());

       List<ExerciseType> result = query.getResultList();
        if(result.size() > 0){
            throw new AuthenticationException("An exercise of this type already exists!");
        }
    }

    
}
