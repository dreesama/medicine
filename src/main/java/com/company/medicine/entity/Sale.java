package com.company.medicine.entity;

import io.jmix.core.DeletePolicy;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.entity.annotation.OnDeleteInverse;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@JmixEntity
@Table(name = "SALE", indexes = {
        @Index(name = "IDX_SALE_CASHIER", columnList = "CASHIER_ID")
})

@Entity
public class Sale {

    @Id
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    private UUID id = UUID.randomUUID();

    @Column(name = "SALE_DATE")
    private LocalDateTime saleDate;

    @Column(name = "TOTAL_AMOUNT", precision = 19, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "PAYMENT_METHOD")
    private String paymentMethod;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleItem> saleItems = new ArrayList<>();
    @Column(name = "DISCOUNT_AMOUNT", precision = 19, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;
    @Column(name = "TAX_AMOUNT", precision = 19, scale = 2)
    private BigDecimal taxAmount = BigDecimal.ZERO;
    @OnDeleteInverse(DeletePolicy.UNLINK)
    @JoinColumn(name = "CASHIER_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    private User cashier;

    public User getCashier() {
        return cashier;
    }

    public void setCashier(User cashier) {
        this.cashier = cashier;
    }


    // Getters and setters for all fields

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<SaleItem> getSaleItems() {
        return saleItems;
    }

    public void setSaleItems(List<SaleItem> saleItems) {
        this.saleItems = saleItems;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }


}
