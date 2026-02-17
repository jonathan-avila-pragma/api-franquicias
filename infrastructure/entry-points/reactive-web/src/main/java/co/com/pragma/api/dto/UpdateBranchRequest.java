package co.com.pragma.api.dto;

public class UpdateBranchRequest extends BaseBranchFields {
    
    public boolean hasAtLeastOneField() {
        return (name != null && !name.trim().isEmpty()) ||
               (address != null && !address.trim().isEmpty()) ||
               (city != null && !city.trim().isEmpty());
    }

}
