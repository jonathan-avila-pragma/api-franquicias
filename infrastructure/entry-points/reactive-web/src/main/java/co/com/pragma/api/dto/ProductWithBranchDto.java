package co.com.pragma.api.dto;

import co.com.pragma.model.Product;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * DTO para representar ProductWithBranch sin el campo products en Branch
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductWithBranchDto {
    private Product product;
    private BranchSummaryDto branch;
}
