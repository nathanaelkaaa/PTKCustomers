package com.example.springapi.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderDTO {

    private int idCustomer;
    private int idProduct;
    private int quantity;
    private double total;

    public OrderDTO() {
    }

    public OrderDTO(int idCustomer, int idProduct, int quantity, double total) {
        this.idCustomer = idCustomer;
        this.idProduct = idProduct;
        this.quantity = quantity;
        this.total = total;
    }
}
