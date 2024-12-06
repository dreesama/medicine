package com.company.medicine.entity;

import io.jmix.core.entity.annotation.SystemLevel;
import io.jmix.core.metamodel.annotation.DependsOnProperties;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.JmixProperty;
import io.jmix.core.metamodel.annotation.Store;
import io.jmix.data.DdlGeneration;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@DdlGeneration(value = DdlGeneration.DbScriptGenerationMode.CREATE_ONLY)
@JmixEntity
@Store(name = "medicinedetails")
@Table(name = "stock")
@Entity
public class Stock {
    @Column(name = "ID", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "deleted")
    private Boolean deleted = false;
    @Column(name = "DATE_ADDED")
    private LocalDateTime dateAdded;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicine_id")
    private Medicine medicine;
    @Column(name = "brand_name")
    private String brandName;
    @Column(name = "active_ingredient_name")
    private String activeIngredientName;
    @Column(name = "active_ingredient_strength")
    private String activeIngredientStrength;
    @Column(name = "dosage_form")
    private String dosageForm;
    @Column(name = "PACKAGE_TYPE")
    private String packageType;
    @Column(name = "UNIT_TYPE")
    private String unitType;
    @Column(name = "units_per_package")
    private Integer unitsPerPackage;
    @Column(name = "package_quantity")
    private Integer packageQuantity;
    @Column(name = "price_per_package", precision = 10, scale = 2)
    private BigDecimal pricePerPackage;
    @Column(name = "price_per_unit", precision = 10, scale = 2)
    private BigDecimal pricePerUnit;
    @Column(name = "total_units")
    private Integer totalUnits;
    @Column(name = "expiration_date")
    @Temporal(TemporalType.DATE)
    private Date expirationDate;
    @Column(name = "STORAGE_LOCATION")
    private String storageLocation;
    @Column(name = "STORAGE_CONDITIONS")
    private String storageConditions;
    @SystemLevel
    @Column(name = "CREATED_BY_ID", updatable = false)
    private UUID createdById;
    @DependsOnProperties({"createdById"})
    @JmixProperty
    @Transient
    private User createdBy;

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDateTime dateAdded) {
        this.dateAdded = dateAdded;
    }
    @PrePersist
    public void setDateAddedOnCreate() {
        if (this.dateAdded == null) {
            this.dateAdded = LocalDateTime.now();
        }
    }
    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }


    public Integer getTotalUnits() {
        return totalUnits;
    }

    public void setTotalUnits(Integer totalUnits) {
        this.totalUnits = totalUnits;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
        this.createdById = createdBy != null ? createdBy.getId() : null;
    }

    public UUID getCreatedById() {
        return createdById;
    }

    public void setCreatedById(UUID createdById) {
        this.createdById = createdById;
    }


    public void setStorageLocation(Location storageLocation) {
        this.storageLocation = storageLocation == null ? null : storageLocation.getId();
    }

    public Location getStorageLocation() {
        return storageLocation == null ? null : Location.fromId(storageLocation);
    }

    public void setStorageConditions(StorageCondition storageConditions) {
        this.storageConditions = storageConditions == null ? null : storageConditions.getId();
    }

    public StorageCondition getStorageConditions() {
        return storageConditions == null ? null : StorageCondition.fromId(storageConditions);
    }

    public void setUnitType(UnitType unitType) {
        this.unitType = unitType == null ? null : unitType.getId();
    }

    public UnitType getUnitType() {
        return unitType == null ? null : UnitType.fromId(unitType);
    }

    public void setPackageType(PackageType packageType) {
        this.packageType = packageType == null ? null : packageType.getId();
    }

    public PackageType getPackageType() {
        return packageType == null ? null : PackageType.fromId(packageType);
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getActiveIngredientName() {
        return activeIngredientName;
    }

    public void setActiveIngredientName(String activeIngredientName) {
        this.activeIngredientName = activeIngredientName;
    }

    public String getActiveIngredientStrength() {
        return activeIngredientStrength;
    }

    public void setActiveIngredientStrength(String activeIngredientStrength) {
        this.activeIngredientStrength = activeIngredientStrength;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    public Integer getUnitsPerPackage() {
        return unitsPerPackage;
    }

    public void setUnitsPerPackage(Integer unitsPerPackage) {
        this.unitsPerPackage = unitsPerPackage;
    }

    public Integer getPackageQuantity() {
        return packageQuantity;
    }

    public void setPackageQuantity(Integer packageQuantity) {
        this.packageQuantity = packageQuantity;
    }

    public BigDecimal getPricePerPackage() {
        return pricePerPackage;
    }

    public void setPricePerPackage(BigDecimal pricePerPackage) {
        this.pricePerPackage = pricePerPackage;
    }

    public BigDecimal getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(BigDecimal pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }


//    @PrePersist
//    @PreUpdate
//    public void updateTotalUnits() {
//        if (this.packageQuantity != null && this.unitsPerPackage != null) {
//            this.totalUnits = this.packageQuantity * this.unitsPerPackage;
//        } else {
//            this.totalUnits = 0;
//        }
//    }
}