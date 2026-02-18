package co.com.pragma.api.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseBranchFields {
    
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\s._-]*$", message = "Name contains invalid characters")
    protected String name;

    @Size(max = 200, message = "Address must not exceed 200 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\s.,#-]*$", message = "Address contains invalid characters")
    protected String address;

    @Size(max = 100, message = "City must not exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s.-]*$", message = "City contains invalid characters")
    protected String city;
}
