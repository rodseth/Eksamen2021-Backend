package facades;

import dto.CommentDTO;
import dto.ReportDTO;
import entities.*;
import dto.MemeDTO;
import java.util.ArrayList;

import errorhandling.MissingInput;
import errorhandling.NotFoundException;
import utils.EMF_Creator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Disabled;
import security.errorhandling.AuthenticationException;

//Uncomment the line below, to temporarily disable this test
//@Disabled
public class MemeFacadeTest {

    private static EntityManagerFactory emf;
    private static MemeFacade facade;
    private static User user, admin, banned;
    private static Meme meme1, meme2;
    private static Comment comment1, comment2, comment3;
    private static MemeStatus status1, status2, status3;


    public MemeFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = MemeFacade.getMemeFacade(emf);
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        setupTestData(em);
    }

    @AfterEach
    public void tearDown() {
    }
    @Test
    public void testAddComment(){
        Comment comment = new Comment("Dette er en test", user);
        comment.setMeme(meme1);
        CommentDTO commentDTO = facade.addComment(new CommentDTO(comment));
        assertTrue(commentDTO.getComment().equals("Dette er en test"));

    }

    @Test
    public void testGetComments(){
        List<CommentDTO> commentDTOList = facade.getAllCommentsById(meme1.getId());
        assertTrue(commentDTOList.size() == 2);
    }
    
    @Test
    public void testUpvoteMeme() {
        MemeDTO upvotedMeme = facade.upvoteMeme(user.getUsername(), new MemeDTO(meme2));
        assertEquals(2, upvotedMeme.getUpvotes());
    }
    
    @Test
    public void testDownvoteMeme() {
        MemeDTO downvotedMeme = facade.downvoteMeme(admin.getUsername(), new MemeDTO(meme2));
        assertEquals(1, downvotedMeme.getDownvotes());
    }
    
    @Test
    public void testUndoUpvote() {
        MemeDTO undoneUpvote = facade.upvoteMeme(user.getUsername(), new MemeDTO(meme1));
        assertTrue(undoneUpvote.getUpvotes() == 0);
    }
    
    @Test
    public void testUndoDownvote() {
        MemeDTO downvotedMeme = facade.downvoteMeme(admin.getUsername(), new MemeDTO(meme1));
        assertTrue(downvotedMeme.getDownvotes() == 0);
    }
    
    @Test
    public void testGetAllDownvotedMemes() {
        List<MemeDTO> memeDTOsList = facade.getAllDownvotedMemes();
        assertTrue(memeDTOsList.size() == 1);
    }
    
    @Test
    public void testGetAllUpvotedMemes() {
        List<MemeDTO> memeDTOsList = facade.getAllUpvotedMemes();
        assertTrue(memeDTOsList.size() == 2);
    }
    
    @Test
    public void testGetFavoriteMemes() {
        List<MemeDTO> memeDTOsList = facade.getFavoriteMemes("user");
        assertTrue(memeDTOsList.size() == 1);
        
    }
    
    @Test
    public void testGetMemeById() {
        MemeDTO memeDTO = facade.getMemeById(meme1.getId());
        assertTrue(memeDTO.getImageUrl().equals(meme1.getImageUrl()));
    }
    @Test
    public void testReportMeme() throws MissingInput {
        ReportDTO reportDTO = new ReportDTO("Test", meme1.getId(), user.getUsername());
        MemeDTO memeDTO = facade.reportMeme(reportDTO);
        assertTrue(memeDTO.getStatus().equals("Reported"));
        assertTrue((memeDTO.getReports().get(0).getDescription().equals("Test")));

    }
    
    @Test
    public void testAddUserMeme() throws AuthenticationException {
        Meme meme = new Meme("tester.png", "");
        meme.setPostedBy(user.getUsername());
        meme.setMemeStatus(status1);
        MemeDTO memeDTO = new MemeDTO(meme);
        MemeDTO addedDTO = facade.addUserMeme(memeDTO);
        assertTrue(addedDTO.getTitle().equals("UserSubmission"));
    }
    
    @Test
    public void testGetUserMemes() throws AuthenticationException {
        Meme meme = new Meme("tester.png", "");
        meme.setPostedBy(admin.getUsername());
        meme.setMemeStatus(status1);
        MemeDTO memeDTO = new MemeDTO(meme);
        facade.addUserMeme(memeDTO);
        assertTrue(facade.getUserMemes().size() == 1);
    }
    
    @Test
    public void testGetReportedMemes() throws MissingInput {
        List<MemeDTO> reportedMemesList = new ArrayList<>();
        ReportDTO reportDTO = new ReportDTO("Voldeligt indhold", meme2.getId(), user.getUsername());
        MemeDTO memeDTO = facade.reportMeme(reportDTO);
        reportedMemesList.add(memeDTO);
        reportedMemesList = facade.getReportedMemes();
        assertTrue(reportedMemesList.size() == 1);
    }
    
    @Test 
    public void testBlackListMeme() {
        MemeDTO memeDTO = facade.blackListMeme(meme1.getId());
        assertTrue(memeDTO.getStatus().equals("Blacklisted"));
    }
    
    public void setupTestData(EntityManager em) {
        user = new User("user", "test123");
        admin = new User("admin", "test123");
        banned = new User("banned", "test123");
        meme1 = new Meme("fatcat.jpg", "Random cat");
        meme2 = new Meme("yomama.jpg", "Offensive joke");
        comment1 = new Comment("Jeg synes den er sjov", user);
        comment2 = new Comment("Jeg synes den er fed", user);
        comment3 = new Comment("Jeg synes den er nederen", admin);
        try {
            em.getTransaction().begin();
            em.createNamedQuery("Roles.deleteAllRows").executeUpdate();
            em.createNamedQuery("Comment.deleteAllRows").executeUpdate();
            em.createNamedQuery("Report.deleteAllRows").executeUpdate();
            em.createNamedQuery("Meme.deleteAllRows").executeUpdate();
            em.createNamedQuery("User.deleteAllRows").executeUpdate();
            em.createNamedQuery("MemeStatus.deleteAllRows").executeUpdate();
            
            
            Role userRole = new Role("user");
            Role adminRole = new Role("admin");
            Role bannedRole = new Role("banned");
            status1 = new MemeStatus("OK");
            status2 = new MemeStatus("Reported");
            status3 = new MemeStatus("Blacklisted");
            user.addRole(userRole);
            admin.addRole(adminRole);
            banned.addRole(bannedRole);
            meme1.getComments().add(comment1);
            meme1.getComments().add(comment2);
            meme2.getComments().add(comment3);
            meme1.getUpvoters().add(user);
            meme1.getDownvoters().add(admin);
            meme2.getUpvoters().add(admin);
            comment1.setMeme(meme1);
            comment2.setMeme(meme1);
            comment3.setMeme(meme2);
            meme1.setMemeStatus(status1);
            meme2.setMemeStatus(status1);
            user.getUpvotedMemes().add(meme1);
            admin.getUpvotedMemes().add(meme2);
            admin.getDownvotedMemes().add(meme1);
            em.persist(userRole);
            em.persist(adminRole);
            em.persist(status1);
            em.persist(status2);
            em.persist(status3);
            em.persist(user);
            em.persist(admin);
            em.persist(banned);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }


}