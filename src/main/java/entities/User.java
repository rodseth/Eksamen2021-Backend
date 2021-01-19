package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.mindrot.jbcrypt.BCrypt;

@Entity
@NamedQuery (name = "User.deleteAllRows", query = "DELETE FROM User")
@Table(name = "user")
public class User implements Serializable {


  private static final long serialVersionUID = 1L;
  @Id
  @Basic(optional = false)
  @NotNull
  @Column(name = "user_name", length = 25)
  private String username;

  @Basic(optional = false)
  @NotNull
  @Size(min = 1, max = 255)
  @Column(name = "user_pass")
  private String userPass;
  @JoinTable(name = "user_roles", joinColumns = {
    @JoinColumn(name = "user_name", referencedColumnName = "user_name")}, inverseJoinColumns = {
    @JoinColumn(name = "role_name", referencedColumnName = "role_name")})
  @ManyToMany (cascade = CascadeType.PERSIST) 
  private List<Role> roleList = new ArrayList<>();
  
  private String firstName;
  private String lastName;
  private int age;
  private double weight;
    @OneToMany(mappedBy = "user")
  private List<Activity> activitys;


  
         

  public List<String> getRolesAsStrings() {
    if (roleList.isEmpty()) {
      return null;
    }
    List<String> rolesAsStrings = new ArrayList<>();
    roleList.forEach((role) -> {
        rolesAsStrings.add(role.getRoleName());
      });
    return rolesAsStrings;
  }

  public User() {}
  
  public User(String username) {
        this.username = username;
    }
  

   public boolean verifyPassword(String pw){
       boolean matches = BCrypt.checkpw(pw, this.userPass);
       return(matches);
    }

  public User(String username, String userPass) {
    this.username = username;
    this.userPass = BCrypt.hashpw(userPass, BCrypt.gensalt(12));
  }

    public User(String username, String userPass, String firstName, String lastName, int age, double weight) {
        this.username = username;
        this.userPass = BCrypt.hashpw(userPass, BCrypt.gensalt(12));
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.weight = weight;
    }
  
  


  public String getUsername() {
    return username;
  }

  public void setUsername(String userName) {
    this.username = userName;
  }

  public String getUserPass() {
    return this.userPass;
  }

  public void setUserPass(String userPass) {
    this.userPass = BCrypt.hashpw(userPass, BCrypt.gensalt(12));
  }

  public List<Role> getRoleList() {

    return roleList;
  }

  public void setRoleList(List<Role> roleList) {
    this.roleList = roleList;
  }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public List<Activity> getActivitys() {
        return activitys;
    }

    public void setActivitys(List<Activity> activitys) {
        this.activitys = activitys;
    }
    
    public void setActivitys(Activity a) {
        if(this.activitys == null){
            
         this.activitys = new ArrayList<>();
    }
        
    this.activitys.add(a);
    a.setUser(this);
    }
    
  
  

  public void addRole(Role userRole) {
    roleList.add(userRole);
  }
  
  public void addActivity (Activity activity) {
      activitys.add(activity);
  }

}
