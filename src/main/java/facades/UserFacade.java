package facades;

import dto.UserDTO;
import entities.MemeStatus;
import entities.Role;
import entities.User;
import errorhandling.MissingInput;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import security.errorhandling.AuthenticationException;
import java.util.ArrayList;
import java.util.List;

public class UserFacade {

    private static EntityManagerFactory emf;
    private static UserFacade instance;

    private UserFacade() {
    }

    public static UserFacade getUserFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserFacade();
        }
        return instance;
    }

    public UserDTO deleteUser(String username) {

        EntityManager em = emf.createEntityManager();
        Query q = em.createQuery("SELECT c FROM Comment c WHERE c.author.username = :name");
        q.setParameter("name", username);
        
        if (q.getResultList().size() > 0) {
            em.getTransaction().begin();
            Query del = em.createQuery("DELETE FROM Comment c WHERE c.author.username = :name");
            del.setParameter("name", username).executeUpdate();
            em.getTransaction().commit();
        }

        try{
            em.getTransaction().begin();
            User user = em.find(User.class, username);
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
            if(user.getRoleList().get(0).getRoleName().equals("banned")){
                throw new AuthenticationException("You are banned!");
            }
        } finally {
            em.close();
        }
        return user;
    }

    public List<UserDTO> getAllUsers() {

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

    public UserDTO addUser(UserDTO userDTO) throws  AuthenticationException {

        if(userDTO.getPassword().length() < 4){
            throw new AuthenticationException("Your password has to be 4 characters or more!");
        }
        if(userDTO.getUsername().length() < 2){
            throw new AuthenticationException("Your username has to be 2 characters or more!");
        }

        EntityManager em = emf.createEntityManager();
        User user = new User(userDTO.getUsername(), userDTO.getPassword());
        addInitialRoles(em);
        checkRole(user, em);
        checkIfExists(userDTO, em);
        try{
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            return new UserDTO(user);

        }finally {
            em.close();
        }
    }
    
    public UserDTO editUser(UserDTO userDTO) throws MissingInput, AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user = em.find(User.class, userDTO.getUsername());     
        checkInput(userDTO);
        user.setProfilePicture(userDTO.getProfilePicture());
        
        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            return new UserDTO(user);
        } finally {
            em.close();
        }
    }
    
    public void changePassword(UserDTO currentDTO, String newPassword) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User currentUser = em.find(User.class, currentDTO.getUsername());
        
        if (currentUser.verifyPassword(currentDTO.getPassword())) {
            currentUser.setUserPass(newPassword);
            
            try {
                em.getTransaction().begin();
                em.persist(currentUser);
                em.getTransaction().commit();
            } finally {
                em.close();
            }
        } else {
            throw new AuthenticationException("The entered current password is invalid.");
        }
    }

    private void checkIfExists(UserDTO userDTO, EntityManager em) throws AuthenticationException {

        Query query = em.createQuery("SELECT u FROM User u WHERE u.username =:username ");
        query.setParameter("username", userDTO.getUsername());

       List<User> result = query.getResultList();
        if(result.size() > 0){
            throw new AuthenticationException("A user with this username already exists!");
        }
    } 
    
    private void checkInput(UserDTO userDTO) throws MissingInput {
        if (userDTO.getProfilePicture().isEmpty()) {
            throw new MissingInput("All fields must be filled out.");
        } 
    }

    public void checkRole(User user, EntityManager em){
        String param;
        if (user.getUsername().equals("admin")) {
            param = "admin";
        } else {
            param = "user";
        }
        Query query = em.createQuery("SELECT r FROM Role r WHERE r.roleName =:role ");
        query.setParameter("role", param);
        user.addRole((Role) query.getSingleResult());
    }
    
    public void addInitialRoles(EntityManager em) {
        Query query = em.createQuery("SELECT r FROM Role r");
        if (query.getResultList().isEmpty()) {
            em.getTransaction().begin();
            em.persist(new Role("user"));
            em.persist(new Role("admin"));
            em.persist(new Role("banned"));
            em.persist(new MemeStatus("OK"));
            em.persist(new MemeStatus("Reported"));
            em.persist(new MemeStatus("Blacklisted"));
            em.getTransaction().commit();
        }
    }

    public UserDTO banUser(String username) {

        EntityManager em = emf.createEntityManager();

        User user = em.find(User.class, username);

        Query query = em.createQuery("SELECT r from Role r where r.roleName = 'banned'");
        Role role = (Role) query.getSingleResult();

        user.getRoleList().clear();
        user.getRoleList().add(role);

        try{
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
            }finally {
            em.close();
        }
        return new UserDTO(user);
    }

    public UserDTO unbanUser(String username) {

        EntityManager em = emf.createEntityManager();

        User user = em.find(User.class, username);

        Query query = em.createQuery("SELECT r from Role r where r.roleName = 'user'");
        Role role = (Role) query.getSingleResult();

        user.getRoleList().clear();
        user.getRoleList().add(role);

        try{
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        }finally {

            em.close();
        }
        return new UserDTO(user);
    }
}
