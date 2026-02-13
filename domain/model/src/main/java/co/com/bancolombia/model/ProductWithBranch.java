package co.com.bancolombia.model;

public class ProductWithBranch {
    private Product product;
    private Branch branch;

    public ProductWithBranch() {
    }

    public ProductWithBranch(Product product, Branch branch) {
        this.product = product;
        this.branch = branch;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }
}
