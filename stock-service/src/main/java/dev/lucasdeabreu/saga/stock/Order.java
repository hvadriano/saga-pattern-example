package dev.lucasdeabreu.saga.stock;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Order {

    private Long id;

    private String transactionId;

    private Long productId;

    private BigDecimal value;

    private Long quantity;
}