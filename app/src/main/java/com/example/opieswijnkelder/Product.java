package com.example.opieswijnkelder;

import java.io.Serializable;

/**
 * Represents a product in the wine cellar inventory.
 * Implements Serializable to allow passing between activities.
 */
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer aantal;  // Quantity
    private String naam;     // Name
    private String vervaldatum;  // Expiry date

    /**
     * Creates a new Product instance.
     * @param aantal The quantity of the product
     * @param naam The name of the product
     * @param vervaldatum The expiry date in format "dd-MM-yyyy"
     * @throws IllegalArgumentException if naam is null or empty
     */
    public Product(Integer aantal, String naam, String vervaldatum) {
        if (naam == null || naam.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        this.aantal = aantal;
        this.naam = naam.trim();
        this.vervaldatum = vervaldatum;
    }

    // Getters and setters
    public Integer getAantal() {
        return aantal;
    }

    public void setAantal(Integer aantal) {
        this.aantal = aantal;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        if (naam == null || naam.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        this.naam = naam.trim();
    }

    public String getVervaldatum() {
        return vervaldatum;
    }

    public void setVervaldatum(String vervaldatum) {
        this.vervaldatum = vervaldatum;
    }
}
