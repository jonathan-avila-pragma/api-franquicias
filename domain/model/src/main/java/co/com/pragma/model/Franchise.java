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
public class Franchise {
    private String id;
    private String name;
    private String description;
    @Builder.Default
    private List<Branch> branches = new ArrayList<>();

    public void setBranches(List<Branch> branches) {
        this.branches = branches != null ? branches : new ArrayList<>();
    }
}
