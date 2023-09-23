package com.example.opieswijnkelder;

import java.io.Serializable;

public class Product implements Serializable {

    Integer aantal;
    String naam, vervaldatum;

    public Product(Integer aantal, String naam, String vervaldatum) {
        this.aantal = aantal;
        this.naam = naam;
        this.vervaldatum = vervaldatum;
    }
}
