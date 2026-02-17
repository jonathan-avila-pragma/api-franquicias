package co.com.pragma.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FranchiseRequest {
    
    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\s._-]+$", message = "Name contains invalid characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\s.,;:_-]*$", message = "Description contains invalid characters")
    private String description;

    public FranchiseRequest() {
    }

    public FranchiseRequest(String name) {
        this.name = name;
    }

}
