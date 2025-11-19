package com.brewpubs.app;

/**
 * Created by Rajiv Shankar on 11/14/25 @ 12:07 PM.
 */

import org.thymeleaf.Thymeleaf;

/**
 * MODEL LAYER
 * POJO (Plain Old Java Object) to represent a brewery, just holds data - no special behavior (all pojos are models but not vv)
 */
public class Brewery {
    private String name;
    private String address;
    private String signatureBeer;

    public Brewery(String name, String address, String signatureBeer) { // constructor to initialize brewery data
        this.name = name;
        this.address = address;
        this.signatureBeer = signatureBeer;
    }

    // Getters - allow other code to read the data; No setters (we're not changing values after creation)
    // Thymeleaf uses these to access the data in templates
    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getSignatureBeer() {
        return signatureBeer;
    }
}