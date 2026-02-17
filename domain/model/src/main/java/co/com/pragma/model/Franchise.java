package co.com.pragma.model;

import java.util.ArrayList;
import java.util.List;

public class Franchise {
    private String id;
    private String name;
    private String description;
    private List<Branch> branches;

    public Franchise() {
        this.branches = new ArrayList<>();
    }

    public Franchise(String id, String name) {
        this.id = id;
        this.name = name;
        this.branches = new ArrayList<>();
    }

    public Franchise(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.branches = new ArrayList<>();
    }

    public Franchise(String id, String name, List<Branch> branches) {
        this.id = id;
        this.name = name;
        this.branches = branches != null ? branches : new ArrayList<>();
    }

    public Franchise(String id, String name, String description, List<Branch> branches) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.branches = branches != null ? branches : new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches != null ? branches : new ArrayList<>();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
