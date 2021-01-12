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
    
  private static final String defaultProfilePic = "https://m2bob-forum.net/wcf/images/avatars/3e/2720-3e546be0b0701e0cb670fa2f4fcb053d4f7e1ba5.jpg";

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
  
  @Column(name = "profile_pic")
  private String profilePicture;
  
  @ManyToMany(mappedBy = "upvoters", cascade = CascadeType.PERSIST)
  private List<Meme> upvotedMemes = new ArrayList<>();
  
  @ManyToMany(mappedBy = "downvoters", cascade = CascadeType.PERSIST)
  private List<Meme> downvotedMemes = new ArrayList<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private List<Report> reportList = new ArrayList<>();





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

   public boolean verifyPassword(String pw){
       boolean matches = BCrypt.checkpw(pw, this.userPass);
       return(matches);
    }

  public User(String username, String userPass) {
    this.username = username.toLowerCase();
    this.userPass = BCrypt.hashpw(userPass, BCrypt.gensalt(12));
    this.profilePicture = defaultProfilePic;
  }

  public List<Report> getReportList() {
    return reportList;
  }

  public void setReportList(List<Report> reportList) {
    this.reportList = reportList;
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

  public void addRole(Role userRole) {
    roleList.add(userRole);
  }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public List<Meme> getUpvotedMemes() {
        return upvotedMemes;
    }

    public void setUpvotedMemes(List<Meme> upvotedMemes) {
        this.upvotedMemes = upvotedMemes;
    }

    public List<Meme> getDownvotedMemes() {
        return downvotedMemes;
    }

    public void setDownvotedMemes(List<Meme> downvotedMemes) {
        this.downvotedMemes = downvotedMemes;
    }
    
    
}
