package com.rags.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table (name = "email_log")
public class EmailLog {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column (nullable = false, length = 10)
    private String predictedLabel;

    @Column (nullable = false)
    private double confidence;

    @Column (nullable = false)
    private Instant timestamp;
    
    public EmailLog(){}

    public EmailLog(String content, String predictedLabel, double confidence, Instant timestamp) {
        this.content = content;
        this.predictedLabel = predictedLabel;
        this.confidence = confidence;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPredictedLabel() {
        return predictedLabel;
    }

    public void setPredictedLabel(String predictedLabel) {
        this.predictedLabel = predictedLabel;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
