package co.com.pragma.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Branch {
    private String id;
    private String name;
    private String address;
    private String city;
    @Builder.Default
    private List<Product> products = new ArrayList<>();

    public void setProducts(List<Product> products) {
        this.products = products != null ? products : new ArrayList<>();
    }
}
