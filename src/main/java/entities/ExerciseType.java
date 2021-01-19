/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 *
 * @author MariHaugen
 */
@Entity
public class ExerciseType implements Serializable {

    @OneToMany(mappedBy = "exerciseType")
    private List<Activity> activitys;

    private static final long serialVersionUID = 1L;
    @Id
    private int id;
    private String typeName;

    public ExerciseType(String typeName) {
        this.id = id;
        this.typeName = typeName;
    }

    public ExerciseType() {
    }

    
    

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
    a.setExerciseType(this);
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    
    

    
}
