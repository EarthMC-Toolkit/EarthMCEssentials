package net.emc.emce.objects;

import lombok.Getter;

// Should be kept up-to-date with OAPI.
public enum QuarterType {
    APARTMENT("Apartment"), // Default type
    INN("Inn"), // Allows bed usage
    STATION("Station"); // Allows vehicle placing and usage
    
    @Getter private final String commonName;
    
    QuarterType(String commonName) {
        this.commonName = commonName;
    }
    
    public String getLowerCase() {
        return name().toLowerCase();
    }
}