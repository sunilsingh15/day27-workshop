package sg.edu.nus.iss.day27workshop.repository;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.UpdateResult;

@Repository
public class ReviewRepository {

    @Autowired
    private MongoTemplate template;

    public Boolean checkIfGameExists(Integer id) {
        return template.exists(Query.query(Criteria.where("gid").is(id)), "games");
    }

    public Document addReviewToDatabase(Document reviewDocument) {
        return template.insert(reviewDocument, "reviews");
    }

    public String getGameNameByID(Integer id) {
        return template.findOne(Query.query(Criteria.where("gid").is(id)), Document.class, "games").getString("name");
    }

    public Boolean checkIfReviewExistsByID(String reviewID) {
        return template.exists(Query.query(Criteria.where("_id").is(reviewID)), "reviews");
    }

    public Boolean addUpdateToReview(Document update, String reviewID) {
        Document retrievedDoc = template.findOne(Query.query(Criteria.where("_id").is(reviewID)), Document.class,
                "reviews");

        if (retrievedDoc.getList("edited", Document.class) == null) {
            List<Document> editedList = new ArrayList<>();
            editedList.add(update);
            retrievedDoc.append("edited", editedList);
        } else {
            List<Document> editedList = retrievedDoc.getList("edited", Document.class);
            editedList.add(update);
            retrievedDoc.append("edited", editedList);
        }

        UpdateResult result = template.upsert(Query.query(Criteria.where("_id").is(reviewID)),
                Update.fromDocument(retrievedDoc), "reviews");

        return result.wasAcknowledged();
    }

    public Document getReviewByID(String reviewID) {
        return template.findOne(Query.query(Criteria.where("_id").is(reviewID)), Document.class, "reviews");
    }

}
