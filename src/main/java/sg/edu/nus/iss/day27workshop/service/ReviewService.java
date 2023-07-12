package sg.edu.nus.iss.day27workshop.service;

import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.edu.nus.iss.day27workshop.model.Review;
import sg.edu.nus.iss.day27workshop.repository.ReviewRepository;

@Service
public class ReviewService {

    @Autowired
    ReviewRepository repository;

    public Boolean checkIfGameExists(Integer id) {
        return repository.checkIfGameExists(id);
    }

    public Document addReviewToDatabase(Review review) {

        Document reviewDocument = new Document();
        reviewDocument.append("user", review.getName());
        reviewDocument.append("rating", review.getRating());
        reviewDocument.append("comment", review.getComment());
        reviewDocument.append("ID", review.getGameID());
        reviewDocument.append("posted", new Date().toString());
        reviewDocument.append("name", repository.getGameNameByID(review.getGameID()));

        return repository.addReviewToDatabase(reviewDocument);
    }

    public Boolean checkIfReviewExists(String reviewID) {
        return repository.checkIfReviewExistsByID(reviewID);
    }

    public Boolean updateReviewToDatabase(Document toUpdate, String reviewID) {
        toUpdate.append("posted", new Date().toString());
        return repository.addUpdateToReview(toUpdate, reviewID);
    }

    public Document viewLatestReviewByID(String reviewID) {
        Document review = repository.getReviewByID(reviewID);

        if (review.getList("edited", Document.class) == null) {
            return review;
        } else {
            List<Document> allEdits = review.getList("edited", Document.class);
            Document latestComment = allEdits.get(allEdits.size() - 1);

            review.remove("comment");
            review.remove("rating");
            review.remove("posted");

            review.put("comment", latestComment.get("comment"));
            review.put("rating", latestComment.get("rating"));
            review.put("posted", latestComment.get("posted"));
            
            return review;
        }
    }

    public Boolean isReviewEdited(Document review) {
        if (review.getList("edited", Document.class) == null) {
            return false;
        } else {
            return true;
        }
    }

}
