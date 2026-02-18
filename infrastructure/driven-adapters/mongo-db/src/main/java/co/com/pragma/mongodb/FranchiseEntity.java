package co.com.pragma.mongodb;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "franchises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FranchiseEntity {
    @Id
    private String id;
    private String name;
    private String description;

    public FranchiseEntity(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
