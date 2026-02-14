package co.com.pragma.api.dto;

public class BranchRequest {
    private String name;

    public BranchRequest() {
    }

    public BranchRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
