package sg.edu.nus.iss.day27workshop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import sg.edu.nus.iss.day27workshop.model.Review;
import sg.edu.nus.iss.day27workshop.service.ReviewService;

@RestController
@RequestMapping
public class ReviewController {

    @Autowired
    ReviewService service;

    @PostMapping(path = "/review", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
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

}
