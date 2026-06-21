package com.support.shared.logging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * @author Shared Core Team
 * @version 1.0.0
 */
@Component
public class LoggingMaskingConfig {
    
    /**
     * Flag to enable/disable logging redaction/masking.
     * Can be configured via application.properties: logging.redaction=true/false
     * Default value is true if not specified.
     */
    @Value("${logging.redaction:true}")
    protected Boolean redaction;
    
    /**
     * Initializes the masking configuration after Spring context is loaded.
     * This method is automatically called by Spring after dependency injection.
     */
    @PostConstruct
    public void init() {
        MaskingPatternLayout.setMaskingEnabled(redaction);
        System.out.println("Logging masking initialized: " + (redaction ? "ENABLED" : "DISABLED"));
    }
    
    /**
     * Gets the current redaction/masking status.
     * 
     * @return true if masking is enabled, false otherwise
     */
    public Boolean isRedactionEnabled() {
        return redaction;
    }
    
    /**
     * Sets the redaction/masking status at runtime.
     * 
     * @param enabled true to enable masking, false to disable
     */
    public void setRedactionEnabled(Boolean enabled) {
        this.redaction = enabled;
        MaskingPatternLayout.setMaskingEnabled(enabled);
    }
}

