
package dto;

import entities.Activity;
import entities.ExerciseType;
import entities.User;

public class ActivityDTO {
    private int id;
    private String exerciseDate;
    private String exerciseType;
    private int durationInMinutes;
    private int distanceInKm;
    private String comment;
    private String username;

    public ActivityDTO() {
    }

    public ActivityDTO(Activity activity) {
        this.exerciseDate = activity.getExerciseDate();
        this.exerciseType = activity.getExerciseType().getTypeName();
        this.durationInMinutes = activity.getDurationInMinutes();
        this.distanceInKm = activity.getDistanceInKm();
        this.comment = activity.getComment();
        this.username = activity.getUser().getUsername();
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

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    
    
    
    
    
    
}
