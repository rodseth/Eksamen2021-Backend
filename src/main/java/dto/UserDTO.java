package dto;

import entities.Activity;
import entities.User;

import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class UserDTO {

    private String username;
    private List<String> roles;
    private String password;
    private String firstName;
    private String lastName;
    private int age;
    private double weight;
    private List<Activity> activities;


    public UserDTO(User user) {
        this.username = user.getUsername();
        this.roles = user.getRolesAsStrings();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.age = user.getAge();
        this.weight = user.getWeight();
        this.activities = user.getActivitys();
      
    }
    
    

    public UserDTO(){}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
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

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }
    
    
    
    
}
