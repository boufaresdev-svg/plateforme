package com.example.BacK.domain.g_Formation.enumEntity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Converter
public class NiveauFormationConverter implements AttributeConverter<NiveauFormation, String> {

    private static final Logger logger = LoggerFactory.getLogger(NiveauFormationConverter.class);

    @Override
    public String convertToDatabaseColumn(NiveauFormation niveau) {
        if (niveau == null) {
            return null;
        }
        
        // Try storing with lowercase and accents as CHECK constraint might expect this
        String dbValue;
        switch (niveau) {
            case Debutant:
                dbValue = "débutant";
                break;
            case Intermediaire:
                dbValue = "intermédiaire";
                break;
            case Avance:
                dbValue = "avancé";
                break;
            default:
                throw new IllegalArgumentException("Unknown niveau: " + niveau);
        }
        logger.info("🔄 Converting niveau TO database: {} -> {}", niveau, dbValue);
        return dbValue;
    }

    @Override
    public NiveauFormation convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        
        // Handle both uppercase and lowercase variants
        String normalized = dbData.toLowerCase().trim();
        
        switch (normalized) {
            // Standard values
            case "débutant":
            case "debutant":
            case "niveau1":  // Legacy database values
            case "beginner":
            case "junior":
                return NiveauFormation.Debutant;
            case "intermédiaire":
            case "intermediaire":
            case "niveau2":  // Legacy database values
            case "intermediate":
            case "mid-level":
                return NiveauFormation.Intermediaire;
            case "avancé":
            case "avance":
            case "niveau3":  // Legacy database values
            case "advanced":
            case "senior":
                return NiveauFormation.Avance;
            default:
                logger.warn("⚠️ Unknown database value for niveau: '{}', defaulting to Debutant", dbData);
                // Default to Debutant instead of throwing exception to avoid breaking on legacy data
                return NiveauFormation.Debutant;
        }
    }
}
