package sg.edu.nus.iss.day27workshop.repository;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

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

}
