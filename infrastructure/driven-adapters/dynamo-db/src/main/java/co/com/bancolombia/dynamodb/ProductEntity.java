package co.com.bancolombia.dynamodb;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class ProductEntity {
    private String compositeKey;
    private String id;
    private String name;
    private Integer stock;

    public ProductEntity() {
    }

    public ProductEntity(String compositeKey, String id, String name, Integer stock) {
        this.compositeKey = compositeKey;
        this.id = id;
        this.name = name;
        this.stock = stock;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("compositeKey")
    public String getCompositeKey() {
        return compositeKey;
    }

    public void setCompositeKey(String compositeKey) {
        this.compositeKey = compositeKey;
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

    @DynamoDbAttribute("stock")
    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
