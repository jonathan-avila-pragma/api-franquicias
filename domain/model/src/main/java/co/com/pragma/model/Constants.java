package co.com.pragma.model;

public final class Constants {
    
    private Constants() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final String FRANCHISE_TABLE_NAME = "Franchises";
    public static final String BRANCH_TABLE_NAME = "Branches";
    public static final String PRODUCT_TABLE_NAME = "Products";
    
    public static final String FRANCHISE_PARTITION_KEY = "FRANCHISE";
    public static final String BRANCH_PARTITION_KEY = "BRANCH";
    public static final String PRODUCT_PARTITION_KEY = "PRODUCT";
    
    public static final String ERROR_FRANCHISE_NOT_FOUND = "Franchise not found";
    public static final String ERROR_BRANCH_NOT_FOUND = "Branch not found";
    public static final String ERROR_PRODUCT_NOT_FOUND = "Product not found";
    public static final String ERROR_INVALID_STOCK = "Stock must be a positive number";
    public static final String ERROR_INVALID_NAME = "Name cannot be empty";
    
    public static final String API_BASE_PATH = "/api/franchises";
    public static final String PATH_ID = "/{id}";
    public static final String PATH_BRANCHES = "/{franchiseId}/branches";
    public static final String PATH_BRANCH_ID = "/{franchiseId}/branches/{branchId}";
    public static final String PATH_PRODUCTS = "/{franchiseId}/branches/{branchId}/products";
    public static final String PATH_PRODUCT_ID = "/{franchiseId}/branches/{branchId}/products/{productId}";
    public static final String PATH_STOCK = "/{franchiseId}/branches/{branchId}/products/{productId}/stock";
    public static final String PATH_MAX_STOCK = "/{franchiseId}/max-stock-products";
    public static final String PATH_FRANCHISE_ID = "/{franchiseId}";
    public static final String PATH_PRODUCT_BY_NAME = "/{franchiseId}/branches/{branchId}/products/name/{productName}";
}
