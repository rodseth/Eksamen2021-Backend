
package entities;

import java.io.Serializable;
import java.util.ArrayList;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;


@Entity
public class Activity implements Serializable {

    

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String exerciseDate;
    @ManyToOne (cascade = CascadeType.PERSIST)
    private ExerciseType exerciseType;
    private int durationInMinutes;
    private int distanceInKm;
    private String comment;
    
    @ManyToOne
    private User user;

    public Activity(String exerciseDate, int durationInMinutes, int distanceInKm, String comment) {
        this.exerciseDate = exerciseDate;
        this.durationInMinutes = durationInMinutes;
        this.distanceInKm = distanceInKm;
        this.comment = comment;
    }
    
    
    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getExerciseDate() {
        return exerciseDate;
    }

    public void setExerciseDate(String excerciseDate) {
        this.exerciseDate = excerciseDate;
    }

    public ExerciseType getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(ExerciseType excerciseType) {
        this.exerciseType = exerciseType;
        
    }
        


    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(int durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public int getDistanceInKm() {
        return distanceInKm;
    }

    public void setDistanceInKm(int distanceInKm) {
        this.distanceInKm = distanceInKm;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    

    
}
