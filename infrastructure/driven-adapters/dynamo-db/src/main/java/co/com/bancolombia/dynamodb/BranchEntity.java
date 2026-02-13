package co.com.bancolombia.dynamodb;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class BranchEntity {
    private String franchiseId;
    private String id;
    private String name;

    public BranchEntity() {
    }

    public BranchEntity(String franchiseId, String id, String name) {
        this.franchiseId = franchiseId;
        this.id = id;
        this.name = name;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("franchiseId")
    public String getFranchiseId() {
        return franchiseId;
    }

    public void setFranchiseId(String franchiseId) {
        this.franchiseId = franchiseId;
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDbAttribute("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
