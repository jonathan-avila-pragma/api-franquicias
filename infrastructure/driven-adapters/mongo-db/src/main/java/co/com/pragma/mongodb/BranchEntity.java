package co.com.pragma.mongodb;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "branches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BranchEntity {
    @Id
    private String id;
    private String franchiseId;
    private String name;
    private String address;
    private String city;

    public BranchEntity(String franchiseId, String id, String name) {
        this.franchiseId = franchiseId;
        this.id = id;
        this.name = name;
    }
}
