package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@NamedQuery (name = "Meme.deleteAllRows", query = "DELETE FROM Meme")
@Table(name = "meme")
public class Meme implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column (name = "meme_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "title")
    private String title;
    
    @ManyToMany
    @JoinTable(name = "upvotes")
    private List<User> upvoters = new ArrayList<>();
    
    @ManyToMany
    @JoinTable(name = "downvotes")
    private List<User> downvoters = new ArrayList<>();
    
    @OneToMany(mappedBy = "meme", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne(cascade = CascadeType.ALL)
    private MemeStatus memeStatus;

    @OneToMany(mappedBy = "meme", cascade = CascadeType.ALL)
    private List<Report> reportList = new ArrayList<>();

    @Column(name = "posted_by")
    private String postedBy;
    
    public Meme() {
    }


    public Meme(String image, String title) {
        this.imageUrl = image;
        this.title = title;
        this.postedBy = "none";
    }

    public List<Report> getReportList() {
        return reportList;
    }

    public void setReportList(List<Report> reportList) {
        this.reportList = reportList;
    }
    
    public MemeStatus getMemeStatus() {
        return memeStatus;
    }

    public void setMemeStatus(MemeStatus memeStatus) {
        this.memeStatus = memeStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<User> getUpvoters() {
        return upvoters;
    }

    public void setUpvoters(List<User> upvoters) {
        this.upvoters = upvoters;
    }

    public List<User> getDownvoters() {
        return downvoters;
    }

    public void setDownvoters(List<User> downvoters) {
        this.downvoters = downvoters;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }
    
}
