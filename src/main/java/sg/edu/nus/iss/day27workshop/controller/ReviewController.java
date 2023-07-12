package sg.edu.nus.iss.day27workshop.controller;

import java.util.Date;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import sg.edu.nus.iss.day27workshop.model.Review;
import sg.edu.nus.iss.day27workshop.service.ReviewService;

@RestController
@RequestMapping
public class ReviewController {

    @Autowired
    ReviewService service;

    @PostMapping(path = "/review", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addReview(Review review) {

        if (!service.checkIfGameExists(review.getGameID())) {
            JsonObject errorJson = Json.createObjectBuilder()
                    .add("error", "No game with ID " + review.getGameID() + " found")
                    .build();
            return new ResponseEntity<String>(errorJson.toString(), HttpStatus.NOT_FOUND);
        }

        if (review.getRating() > 10 || review.getRating() < 1) {
            JsonObject errorJson = Json.createObjectBuilder()
                    .add("error", review.getRating() + " is not a valid rating between 1 - 10.")
                    .build();
            return new ResponseEntity<String>(errorJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        JsonObject successJson = Json.createObjectBuilder()
                .add("success",
                        "Your review has been successfully posted with ID "
                                + service.addReviewToDatabase(review).getObjectId("_id") + ".")
                .build();

        return new ResponseEntity<String>(successJson.toString(), HttpStatus.OK);
    }

    @PutMapping(path = "/review/{reviewID}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateReview(@PathVariable String reviewID, @RequestBody Document toUpdate) {

        if (!service.checkIfReviewExists(reviewID)) {
            JsonObject errorJson = Json.createObjectBuilder()
                    .add("error", "No review with ID " + reviewID + " found")
                    .build();
            return new ResponseEntity<String>(errorJson.toString(), HttpStatus.NOT_FOUND);
        }

        if (toUpdate.getInteger("rating") > 10 || toUpdate.getInteger("rating") < 1) {
            JsonObject errorJson = Json.createObjectBuilder()
                    .add("error", toUpdate.getInteger("rating") + " is not a valid rating between 1 - 10.")
                    .build();
            return new ResponseEntity<String>(errorJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (service.updateReviewToDatabase(toUpdate, reviewID)) {
            JsonObject successJson = Json.createObjectBuilder()
                    .add("success", "Review updated successfully.")
                    .build();

            return new ResponseEntity<String>(successJson.toString(), HttpStatus.OK);
        } else {
            JsonObject errorJson = Json.createObjectBuilder()
                    .add("error", "There has been an error updating your review. Please try again.")
                    .build();

            return new ResponseEntity<String>(errorJson.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping(path = "/review/{reviewID}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> viewLatestReview(@PathVariable String reviewID) {

        if (!service.checkIfReviewExists(reviewID)) {
            JsonObject errorJson = Json.createObjectBuilder()
                    .add("error", "No review exists with ID " + reviewID + ".")
                    .build();

            return new ResponseEntity<String>(errorJson.toString(), HttpStatus.NOT_FOUND);
        }

        Document reviewDocument = service.viewLatestReviewByID(reviewID);

        JsonObject reviewJson = Json.createObjectBuilder()
                .add("user", reviewDocument.getString("user"))
                .add("rating", reviewDocument.getInteger("rating"))
                .add("comment", reviewDocument.getString("comment"))
                .add("ID", reviewDocument.getInteger("ID"))
                .add("posted", reviewDocument.getString("posted"))
                .add("name", reviewDocument.getString("name"))
                .add("edited", service.isReviewEdited(reviewDocument))
                .add("timestamp", new Date().toString())
                .build();

        return new ResponseEntity<String>(reviewJson.toString(), HttpStatus.OK);
    }

    @GetMapping(path = "/review/{reviewID}/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> viewReviewHistory(@PathVariable String reviewID) {

        if (!service.checkIfReviewExists(reviewID)) {
            JsonObject errorJson = Json.createObjectBuilder()
                    .add("error", "No review found with ID " + reviewID + ".")
                    .build();

            return new ResponseEntity<String>(errorJson.toString(), HttpStatus.NOT_FOUND);
        }

        Document reviewDocument = service.getReviewByID(reviewID);
        List<Document> editedArray = reviewDocument.getList("edited", Document.class);

        if (editedArray == null) {
            JsonObject reviewJson = Json.createObjectBuilder()
                    .add("user", reviewDocument.getString("user"))
                    .add("rating", reviewDocument.getInteger("rating"))
                    .add("comment", reviewDocument.getString("comment"))
                    .add("ID", reviewDocument.getInteger("ID"))
                    .add("posted", reviewDocument.getString("posted"))
                    .add("name", reviewDocument.getString("name"))
                    .add("timestamp", new Date().toString())
                    .build();

            return new ResponseEntity<String>(reviewJson.toString(), HttpStatus.OK);
        }

        JsonArrayBuilder editedComments = Json.createArrayBuilder();

        for (Document document : editedArray) {
            JsonObject comment = Json.createObjectBuilder()
                    .add("comment", document.getString("comment"))
                    .add("rating", document.getInteger("rating"))
                    .add("posted", document.getString("posted"))
                    .build();

            editedComments.add(comment);
        }

        JsonObject reviewJson = Json.createObjectBuilder()
                .add("user", reviewDocument.getString("user"))
                .add("rating", reviewDocument.getInteger("rating"))
                .add("comment", reviewDocument.getString("comment"))
                .add("ID", reviewDocument.getInteger("ID"))
                .add("posted", reviewDocument.getString("posted"))
                .add("name", reviewDocument.getString("name"))
                .add("edited", editedComments.build())
                .add("timestamp", new Date().toString())
                .build();

        return new ResponseEntity<String>(reviewJson.toString(), HttpStatus.OK);
    }

}
