
package entities;

import java.io.Serializable;
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
    private String excerciseDate;
    private String excerciseType;
    private int durationInMinutes;
    private int distanceInKm;
    private String comment;
    
    //@ManyToOne
    //private User user;
    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    
}
