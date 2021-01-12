package dto;

import entities.Meme;
import entities.Report;
import entities.User;
import java.util.ArrayList;
import java.util.List;

public class MemeDTO {

    private String title;
    private String imageUrl;
    private int upvotes;
    private int downvotes;
    private int meme_id;
    private String postedBy;
    private String status;
    private List<ReportDTO> reports = new ArrayList<>();

    public MemeDTO() {
    }
    
    public MemeDTO(Meme meme) {
        this.title = meme.getTitle();
        this.imageUrl = meme.getImageUrl();
        this.upvotes = meme.getUpvoters().size();
        this.downvotes = meme.getDownvoters().size();
        this.meme_id = meme.getId();
        this.postedBy = meme.getPostedBy();
        this.status = meme.getMemeStatus().getStatusName();
        addReportDTOList(meme.getReportList());
    }

    public final void addReportDTOList(List<Report> reportList) {
        for (Report report : reportList) {
            this.reports.add(new ReportDTO(report.getDescription(), report.getMeme().getId(), report.getUser().getUsername()));
        }
    }
    
    public List<ReportDTO> getReports() {
        return reports;
    }

    public void setReports(List<ReportDTO> reports) {
        this.reports = reports;
    }

    public MemeDTO(FunnyDTO funny) {
        this.title = funny.getTitle();
        this.imageUrl = funny.getUrl();
    }

    public MemeDTO(CatDTO cat) {
        this.title = cat.getId();
        this.imageUrl = cat.getUrl();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public MemeDTO(YesOrNoDTO yesOrNo){
        this.imageUrl = yesOrNo.getImage();
    }

    public MemeDTO(DogDTO dog) {
        this.imageUrl = dog.getMessage();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(int upvotes) {
        this.upvotes = upvotes;
    }

    public int getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(int downvotes) {
        this.downvotes = downvotes;
    }

    public int getMeme_id() {
        return meme_id;
    }

    public void setMeme_id(int meme_id) {
        this.meme_id = meme_id;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }
}
