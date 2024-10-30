package com.company.medicine.entity;

import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import io.jmix.core.metamodel.annotation.Store;
import io.jmix.data.DdlGeneration;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Date;

@DdlGeneration(value = DdlGeneration.DbScriptGenerationMode.DISABLED)
@JmixEntity
@Store(name = "medicinedetails")
@Table(name = "medicine")
@Entity
public class Medicine {
    @Column(name = "id", nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Integer id;

    @Column(name = "active_ingredient_name")
    private String activeIngredientName;

    @Column(name = "active_ingredient_strength", length = 20)
    private String activeIngredientStrength;

    @Column(name = "application_docs_id", length = 20)
    private String applicationDocs;

    @Column(name = "application_docs_date")
    @Temporal(TemporalType.DATE)
    private Date applicationDocsDate;

    @Column(name = "application_docs_type", length = 50)
    private String applicationDocsType;

    @Column(name = "application_docs_url")
    private String applicationDocsUrl;

    @Column(name = "application_number", length = 50)
    private String applicationNumber;

    @InstanceName
    @Column(name = "brand_name")
    private String brandName;

    @Column(name = "dosage_form", length = 100)
    private String dosageForm;

    @Column(name = "marketing_status", length = 50)
    private String marketingStatus;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "product_number", length = 50)
    private String productNumber;

    @Column(name = "reference_drug")
    private Boolean referenceDrug;

    @Column(name = "reference_standard")
    private Boolean referenceStandard;

    @Column(name = "review_priority", length = 20)
    private String reviewPriority;

    @Column(name = "route", length = 50)
    private String route;

    @Column(name = "sponsor_name", length = 100)
    private String sponsorName;

    @Column(name = "submission_class_code_description")
    private String submissionClassCodeDescription;

    @Column(name = "submission_number", length = 50)
    private String submissionNumber;

    @Column(name = "submission_status", length = 20)
    private String submissionStatus;

    @Column(name = "submission_status_date")
    @Temporal(TemporalType.DATE)
    private Date submissionStatusDate;

    @Column(name = "submission_type", length = 50)
    private String submissionType;

    @Column(name = "te_code", length = 10)
    private String teCode;

    public String getTeCode() {
        return teCode;
    }

    public void setTeCode(String teCode) {
        this.teCode = teCode;
    }

    public String getSubmissionType() {
        return submissionType;
    }

    public void setSubmissionType(String submissionType) {
        this.submissionType = submissionType;
    }

    public Date getSubmissionStatusDate() {
        return submissionStatusDate;
    }

    public void setSubmissionStatusDate(Date submissionStatusDate) {
        this.submissionStatusDate = submissionStatusDate;
    }

    public String getSubmissionStatus() {
        return submissionStatus;
    }

    public void setSubmissionStatus(String submissionStatus) {
        this.submissionStatus = submissionStatus;
    }

    public String getSubmissionNumber() {
        return submissionNumber;
    }

    public void setSubmissionNumber(String submissionNumber) {
        this.submissionNumber = submissionNumber;
    }

    public String getSubmissionClassCodeDescription() {
        return submissionClassCodeDescription;
    }

    public void setSubmissionClassCodeDescription(String submissionClassCodeDescription) {
        this.submissionClassCodeDescription = submissionClassCodeDescription;
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public void setSponsorName(String sponsorName) {
        this.sponsorName = sponsorName;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getReviewPriority() {
        return reviewPriority;
    }

    public void setReviewPriority(String reviewPriority) {
        this.reviewPriority = reviewPriority;
    }

    public Boolean getReferenceStandard() {
        return referenceStandard;
    }

    public void setReferenceStandard(Boolean referenceStandard) {
        this.referenceStandard = referenceStandard;
    }

    public Boolean getReferenceDrug() {
        return referenceDrug;
    }

    public void setReferenceDrug(Boolean referenceDrug) {
        this.referenceDrug = referenceDrug;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getMarketingStatus() {
        return marketingStatus;
    }

    public void setMarketingStatus(String marketingStatus) {
        this.marketingStatus = marketingStatus;
    }

    public String getDosageForm() {
        return dosageForm;
    }

    public void setDosageForm(String dosageForm) {
        this.dosageForm = dosageForm;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public String getApplicationDocsUrl() {
        return applicationDocsUrl;
    }

    public void setApplicationDocsUrl(String applicationDocsUrl) {
        this.applicationDocsUrl = applicationDocsUrl;
    }

    public String getApplicationDocsType() {
        return applicationDocsType;
    }

    public void setApplicationDocsType(String applicationDocsType) {
        this.applicationDocsType = applicationDocsType;
    }

    public Date getApplicationDocsDate() {
        return applicationDocsDate;
    }

    public void setApplicationDocsDate(Date applicationDocsDate) {
        this.applicationDocsDate = applicationDocsDate;
    }

    public String getApplicationDocs() {
        return applicationDocs;
    }

    public void setApplicationDocs(String applicationDocs) {
        this.applicationDocs = applicationDocs;
    }

    public String getActiveIngredientStrength() {
        return activeIngredientStrength;
    }

    public void setActiveIngredientStrength(String activeIngredientStrength) {
        this.activeIngredientStrength = activeIngredientStrength;
    }

    public String getActiveIngredientName() {
        return activeIngredientName;
    }

    public void setActiveIngredientName(String activeIngredientName) {
        this.activeIngredientName = activeIngredientName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}