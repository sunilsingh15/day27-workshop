package sg.edu.nus.iss.day27workshop.service;

import java.util.Date;

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

}
