package sg.edu.nus.iss.day27workshop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    private String name;
    private String comment;
    private Integer rating;
    private Integer gameID;
    
}
