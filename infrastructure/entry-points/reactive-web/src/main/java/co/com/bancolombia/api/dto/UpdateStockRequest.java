package co.com.bancolombia.api.dto;

public class UpdateStockRequest {
    private Integer stock;

    public UpdateStockRequest() {
    }

    public UpdateStockRequest(Integer stock) {
        this.stock = stock;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
