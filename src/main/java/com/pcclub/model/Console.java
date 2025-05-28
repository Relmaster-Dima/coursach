package com.pcclub.model;

import jakarta.persistence.*;

@Entity
@Table(name = "consoles")
public class Console {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private Integer price;
    private String manufacturer;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ConsoleType type;

    @Column(name = "is_available")
    private Boolean isAvailable;

    // Getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ConsoleType getType() { return type; }
    public void setType(ConsoleType type) { this.type = type; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }

    public Integer getPrice() {
        return price;
    }
    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
}
