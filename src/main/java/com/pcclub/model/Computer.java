package com.pcclub.model;

import jakarta.persistence.*;

@Entity
@Table(name = "computers")
public class Computer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @Column(name="cpu_model")
    private String cpuModel;
    @Column(name="gpu_model")
    private String gpuModel;
    @Column(name="ram_gb")
    private Integer ramGb;
    @Column(name="is_available")
    private Boolean isAvailable;
    private Double price;

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCpuModel() { return cpuModel; }
    public void setCpuModel(String cpuModel) { this.cpuModel = cpuModel; }
    public String getGpuModel() { return gpuModel; }
    public void setGpuModel(String gpuModel) { this.gpuModel = gpuModel; }
    public Integer getRamGb() { return ramGb; }
    public void setRamGb(Integer ramGb) { this.ramGb = ramGb; }
    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}
