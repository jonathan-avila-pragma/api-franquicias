package co.com.pragma.api.dto;

import jakarta.validation.constraints.NotBlank;

public class BranchRequest extends BaseBranchFields {
    
    @NotBlank(message = "Name is required")
    @Override
    public String getName() {
        return super.getName();
    }

    public BranchRequest() {
    }

    public BranchRequest(String name) {
        this.name = name;
    }

    public BranchRequest(String name, String address, String city) {
        this.name = name;
        this.address = address;
        this.city = city;
    }
}
