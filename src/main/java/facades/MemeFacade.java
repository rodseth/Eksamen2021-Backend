package facades;

import dto.CommentDTO;
import entities.Comment;
import dto.ReportDTO;
import entities.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import dto.MemeDTO;
import entities.Meme;
import entities.User;
import errorhandling.MissingInput;
import errorhandling.NotFoundException;

import javax.persistence.Query;
import security.errorhandling.AuthenticationException;

public class MemeFacade {

    private static EntityManagerFactory emf;
    private static MemeFacade instance;

    public static MemeFacade getMemeFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new MemeFacade();
        }
        return instance;
    }

    public CommentDTO addComment(CommentDTO commentDTO) {

        EntityManager em = emf.createEntityManager();

        Meme meme = em.find(Meme.class, commentDTO.getMeme_id());
        User user = em.find(User.class, commentDTO.getUsername());
        Comment comment = new Comment(commentDTO.getComment(), user);
        meme.getComments().add(comment);
        comment.setMeme(meme);

        try {
            em.getTransaction().begin();
            em.persist(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new CommentDTO(comment);
    }

    public List<CommentDTO> getAllCommentsById(int id) {

        EntityManager em = emf.createEntityManager();

        TypedQuery<Comment> query = em.createQuery("SELECT c From Comment c join c.meme m where m.id = :id", Comment.class);
        query.setParameter("id", id);

        List<Comment> commentList = query.getResultList();
        List<CommentDTO> commentDTOList = new ArrayList<>();

        for (Comment comment : commentList) {
            commentDTOList.add(new CommentDTO(comment));
        }
        return commentDTOList;
    }

    public MemeDTO getMemeById(int id) {
        EntityManager em = emf.createEntityManager();
        Meme meme = em.find(Meme.class, id);
        return new MemeDTO(meme);
    }

    public MemeDTO upvoteMeme(String username, MemeDTO memeDTO) {
        EntityManager em = emf.createEntityManager();
        User user = em.find(User.class, username);
        Meme meme = checkIfMemeExists(memeDTO, em);
        addDefaultStatus(meme, em);

        if (!checkHasUpvoted(meme, user, em)) {
            try {
                em.getTransaction().begin();
                if (user.getDownvotedMemes().contains(meme)) {
                    meme.getDownvoters().remove(user);
                    user.getDownvotedMemes().remove(meme);
                }
                meme.getUpvoters().add(user);
                user.getUpvotedMemes().add(meme);
                em.getTransaction().commit();
                return new MemeDTO(meme);
            } finally {
                em.close();
            }
        }
        return new MemeDTO(meme);
    }

    public MemeDTO downvoteMeme(String username, MemeDTO memeDTO) {
        EntityManager em = emf.createEntityManager();
        User user = em.find(User.class, username);
        Meme meme = checkIfMemeExists(memeDTO, em);
        addDefaultStatus(meme, em);

        if (!checkHasDownvoted(meme, user, em)) {
            try {
                em.getTransaction().begin();
                if (user.getUpvotedMemes().contains(meme)) {
                    meme.getUpvoters().remove(user);
                    user.getUpvotedMemes().remove(meme);
                }
                meme.getDownvoters().add(user);
                user.getDownvotedMemes().add(meme);
                em.getTransaction().commit();
                return new MemeDTO(meme);
            } finally {
                em.close();
            }
        }
        return new MemeDTO(meme);
    }

    public MemeDTO addUserMeme(MemeDTO memeDTO) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        Query q = em.createQuery("SELECT m FROM Meme m WHERE m.imageUrl = :url");
        q.setParameter("url", memeDTO.getImageUrl());
        if (q.getResultList().size() > 0) {
            throw new AuthenticationException("A meme with this URL already exists.");
        }
        Meme meme = new Meme(memeDTO.getImageUrl(), "UserSubmission");
        meme.setPostedBy(memeDTO.getPostedBy());
        addDefaultStatus(meme, em);

        try {
            em.getTransaction().begin();
            em.persist(meme);
            em.getTransaction().commit();
            return new MemeDTO(meme);
        } finally {
            em.close();
        }
    }

    public List<MemeDTO> getUserMemes() {
        EntityManager em = emf.createEntityManager();
        List<MemeDTO> memeDTOs = new ArrayList<>();

        Query q = em.createQuery("SELECT m FROM Meme m WHERE m.title = :default");
        q.setParameter("default", "UserSubmission");
        List<Meme> memes = q.getResultList();

        for (Meme meme : memes) {
            memeDTOs.add(new MemeDTO(meme));
        }
        return memeDTOs;
    }

    public Meme checkIfMemeExists(MemeDTO memeDTO, EntityManager em) {
        Query q = em.createQuery("SELECT m FROM Meme m WHERE m.imageUrl = :url");
        q.setParameter("url", memeDTO.getImageUrl());

        if (q.getResultList().size() > 0) {
            return (Meme) q.getResultList().get(0);
        } else {
            Meme meme = new Meme(memeDTO.getImageUrl(), memeDTO.getTitle());
            if (meme.getTitle() == null) {
                meme.setTitle("none");
            }
            em.getTransaction().begin();
            em.persist(meme);
            em.getTransaction().commit();
            return meme;
        }
    }

    public boolean checkHasUpvoted(Meme meme, User user, EntityManager em) {
        if (user.getUpvotedMemes().contains(meme)) {
            em.getTransaction().begin();
            user.getUpvotedMemes().remove(meme);
            meme.getUpvoters().remove(user);
            em.getTransaction().commit();
            return true;
        } else {
            return false;
        }
    }

    public boolean checkHasDownvoted(Meme meme, User user, EntityManager em) {
        if (user.getDownvotedMemes().contains(meme)) {
            em.getTransaction().begin();
            user.getDownvotedMemes().remove(meme);
            meme.getDownvoters().remove(user);
            em.getTransaction().commit();
            return true;
        } else {
            return false;
        }
    }

    public List<MemeDTO> getAllDownvotedMemes() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery query = em.createQuery("SELECT DISTINCT m FROM Meme m JOIN m.downvoters d", Meme.class);
            List<Meme> memesList = query.getResultList();
            List<MemeDTO> memeDTOsList = new ArrayList<>();
            for (Meme meme : memesList) {
                memeDTOsList.add(new MemeDTO(meme));
            }
            return memeDTOsList;
        } finally {
            em.close();
        }
    }

    public List<MemeDTO> getAllUpvotedMemes() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery query = em.createQuery("SELECT DISTINCT m FROM Meme m JOIN m.upvoters u", Meme.class);
            List<Meme> memesList = query.getResultList();
            List<MemeDTO> memeDTOsList = new ArrayList<>();
            for (Meme meme : memesList) {
                memeDTOsList.add(new MemeDTO(meme));
            }
            return memeDTOsList;
        } finally {
            em.close();
        }
    }

    public List<MemeDTO> getFavoriteMemes(String userName) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Meme> query = em.createQuery("SELECT m FROM Meme m join m.upvoters u WHERE u.username = :username", Meme.class);
            query.setParameter("username", userName);
            List<Meme> result = query.getResultList();
            List<MemeDTO> memeDTOsList = new ArrayList<>();
            for (Meme meme : result) {
                memeDTOsList.add(new MemeDTO(meme));
            }
            return memeDTOsList;
        } finally {
            em.close();
        }
    }

    public MemeDTO reportMeme(ReportDTO reportDTO) throws  MissingInput {

        EntityManager em = emf.createEntityManager();
        hasUserReported(reportDTO,em);

        Report report = new Report(reportDTO.getDescription());
        Meme meme = em.find(Meme.class, reportDTO.getMeme_id());
        User user = em.find(User.class, reportDTO.getUsername());
        Query query = em.createQuery("SELECT s from MemeStatus s where s.statusName = 'Reported'");
        MemeStatus memeStatus = (MemeStatus) query.getSingleResult();

        meme.setMemeStatus(memeStatus);
        report.setMeme(meme);
        report.setUser(user);
        meme.getReportList().add(report);

        try {
            em.getTransaction().begin();
            em.persist(meme);
            em.getTransaction().commit();

        } finally {
            em.close();
        }
        return new MemeDTO(meme);
    }

    public List<MemeDTO> getReportedMemes() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Meme> query = em.createQuery("SELECT m FROM Meme m JOIN m.memeStatus s WHERE s.statusName = 'Reported'", Meme.class);
            List<Meme> reportedMemesList = query.getResultList();
            List<MemeDTO> memeDTOsList = new ArrayList<>();
            for (Meme meme : reportedMemesList) {
                memeDTOsList.add(new MemeDTO(meme));
            }
            return memeDTOsList;
        } finally {
            em.close();
        }
    }
    
    
    public MemeDTO blackListMeme (int id) {
        EntityManager em = emf.createEntityManager();
        Meme meme = em.find(Meme.class, id);
        Query q = em.createQuery("SELECT m FROM MemeStatus m WHERE m.statusName = :statusName");
        q.setParameter("statusName", "Blacklisted");
        MemeStatus memeStatus = (MemeStatus) q.getSingleResult();
        meme.setMemeStatus(memeStatus);
        try {
            em.getTransaction().begin();
            em.persist(meme);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
       return new MemeDTO(meme);
    }
    
    public List<MemeDTO> getBlacklistedMemes() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Meme> query = em.createQuery("SELECT m FROM Meme m JOIN m.memeStatus s WHERE s.statusName = 'Blacklisted'", Meme.class);
            List<Meme> reportedMemesList = query.getResultList();
            List<MemeDTO> memeDTOsList = new ArrayList<>();
            for (Meme meme : reportedMemesList) {
                memeDTOsList.add(new MemeDTO(meme));
            }
            return memeDTOsList;
        } finally {
            em.close();
        }
    }
    
    public MemeDTO dismissMemeReports (int id)  {
        EntityManager em = emf.createEntityManager();
        Meme meme = em.find(Meme.class, id);
        Query q = em.createQuery("SELECT m FROM MemeStatus m WHERE m.statusName = :statusName");
        q.setParameter("statusName", "OK");
        MemeStatus memeStatus = (MemeStatus) q.getSingleResult();
        meme.setMemeStatus(memeStatus);
        try {
            em.getTransaction().begin();
            Query query = em.createQuery("DELETE FROM Report r WHERE r.meme.id = :meme_id");
            query.setParameter("meme_id", id).executeUpdate();
            em.persist(meme);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
       return new MemeDTO(meme);
    
        
    }
    
    
    public void addDefaultStatus(Meme meme, EntityManager em) {
        Query q = em.createQuery("SELECT m FROM MemeStatus m WHERE m.statusName = :default");
        q.setParameter("default", "OK");
        meme.setMemeStatus((MemeStatus) q.getSingleResult());
    }

    public void hasUserReported(ReportDTO reportDTO, EntityManager em) throws MissingInput {
        Query query = em.createQuery("SELECT r from Report r where r.user.username = :username and r.meme.id = :meme_id");
        query.setParameter("username", reportDTO.getUsername());
        query.setParameter("meme_id", reportDTO.getMeme_id());

        if (!query.getResultList().isEmpty()){
            throw new MissingInput("You have already reported this meme");
        }
    }
}
