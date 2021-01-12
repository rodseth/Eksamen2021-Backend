package dto;

public class ReportDTO {

    private String description;
    private int meme_id;
    private String username;

    public ReportDTO() {
    }

    public ReportDTO(String description, int meme_id, String username) {
        this.description = description;
        this.meme_id = meme_id;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMeme_id() {
        return meme_id;
    }

    public void setMeme_id(int meme_id) {
        this.meme_id = meme_id;
    }
}
