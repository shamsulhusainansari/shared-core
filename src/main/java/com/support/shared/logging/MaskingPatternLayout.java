package com.support.shared.logging;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * @author Shared Core Team
 * @version 1.0.0
 */
public class MaskingPatternLayout extends PatternLayout {
    
    private static final String MASKING_ENABLE_PROPERTY = "logging.masking.enable";
    private static final String MASKING_ENABLE_ENV = "LOGGING_MASKING_ENABLE";

    private static volatile Boolean isMaskingEnabled = null;
    
    private Pattern multilinePattern;
    private final List<String> maskPatterns = new ArrayList<>();
    
    /**
     * Default constructor that initializes with common sensitive data patterns.
     */
    public MaskingPatternLayout() {
        super();
        initializeDefaultPatterns();
        multilinePattern = Pattern.compile(String.join("|", maskPatterns), Pattern.MULTILINE);
    }
    
    /**
     * Initializes default masking patterns for common sensitive data.
     */
    private void initializeDefaultPatterns() {
        List<String> defaultPatterns = List.of(
                "([\\w.-]+@[\\w.-]+\\.\\w+)",                                    // Email masking
                "\\b\\d{15}\\b",                                                 // Account number (15 digits)
                "\\b\\d{12}\\b",                                                 // CKYC, Aadhaar number (12 digits)
                "\\b\\d{10}\\b",                                                 // Mobile number (10 digits)
                "\\b\\d{8}\\b",                                                  // Customer ID (8 digits)
                "\\b[A-Z][1-9]\\d\\s?\\d{4}[1-9]\\b",                           // Passport number
                "(?<!\\d{2})\\d(?=[\\d-]*\\d-?\\d-?\\d{4})",                    // Card masking, formatted Aadhaar
                "\\b4[0-9]{12}(?:[0-9]{3})?\\b",                                // Ration card
                "\\b[A-Z]{3}[0-9]{7}\\b",                                       // Voter ID
                "\\b(A|B|AB|O)(\\+|-)\\b",                                      // Blood group
                "\\b(([A-Z]{2}[0-9]{2})( )|([A-Z]{2}-[0-9]{2}))((19|20)[0-9][0-9])[0-9]{7}\\b", // Driving license
                "\\b[A-Z]{3}[ABCFGHLJPTF]{1}[A-Z]{1}[0-9]{4}[A-Z]{1}\\b",      // PAN card
                "\\b(?:password|pwd|secret|token|apikey|api_key)\\s*[=:]\\s*[^\\s]+", // Password/Secret patterns
                "\\b[0-9]{3}-[0-9]{2}-[0-9]{4}\\b"                              // SSN (US format)
        );
        maskPatterns.addAll(defaultPatterns);
    }
    
    /**
     * Adds a custom masking pattern.
     * 
     * @param maskPattern Regular expression pattern to mask
     */
    public void addMaskPattern(String maskPattern) {
        if (maskPattern != null && !maskPattern.trim().isEmpty()) {
            maskPatterns.add(maskPattern);
            multilinePattern = Pattern.compile(String.join("|", maskPatterns), Pattern.MULTILINE);
        }
    }
    
    /**
     * Sets multiple custom masking patterns.
     * 
     * @param patterns List of regular expression patterns to mask
     */
    public void setMaskPatterns(List<String> patterns) {
        if (patterns != null && !patterns.isEmpty()) {
            maskPatterns.clear();
            initializeDefaultPatterns();
            maskPatterns.addAll(patterns);
            multilinePattern = Pattern.compile(String.join("|", maskPatterns), Pattern.MULTILINE);
        }
    }
    
    /**
     * Checks if masking is enabled based on configuration.
     * Priority order:
     * 1. Programmatically set value
     * 2. System property
     * 3. Environment variable
     * 4. Default (true)
     * 
     * @return true if masking is enabled, false otherwise
     */
    private static boolean isMaskingEnabled() {
        if (isMaskingEnabled != null) {
            return isMaskingEnabled;
        }
        
        // Check system property
        String sysProp = System.getProperty(MASKING_ENABLE_PROPERTY);
        if (sysProp != null) {
            return Boolean.parseBoolean(sysProp);
        }
        
        // Check environment variable
        String envVar = System.getenv(MASKING_ENABLE_ENV);
        if (envVar != null) {
            return Boolean.parseBoolean(envVar);
        }
        
        // Default to enabled
        return true;
    }
    
    /**
     * Programmatically enable or disable masking globally.
     * 
     * @param enabled true to enable masking, false to disable
     */
    public static void setMaskingEnabled(boolean enabled) {
        isMaskingEnabled = enabled;
    }
    
    /**
     * Resets the masking enabled flag to use configuration-based detection.
     */
    public static void resetMaskingEnabled() {
        isMaskingEnabled = null;
    }
    
    @Override
    public String doLayout(ILoggingEvent event) {
        return maskMessage(super.doLayout(event));
    }
    
    /**
     * Masks sensitive information in the log message.
     * 
     * @param message The original log message
     * @return The masked log message
     */
    private String maskMessage(String message) {
        if (!isMaskingEnabled()) {
            return message;
        }
        
        if (multilinePattern == null || message == null || message.isEmpty()) {
            return message;
        }
        
        StringBuilder sb = new StringBuilder(message);
        Matcher matcher = multilinePattern.matcher(sb);
        
        while (matcher.find()) {
            IntStream.rangeClosed(0, matcher.groupCount()).forEach(group -> {
                if (matcher.group(group) != null) {
                    int startIndex = matcher.start(group);
                    int endIndex = matcher.end(group);
                    int length = endIndex - startIndex;
                    
                    // Calculate how many characters to mask
                    int maskLength;
                    if (length > 8) {
                        // For longer strings, leave last 4 characters visible
                        maskLength = length - 4;
                    } else if (length > 4) {
                        // For medium strings, mask half
                        maskLength = length / 2;
                    } else {
                        // For short strings, mask all but one
                        maskLength = length - 1;
                    }
                    
                    // Apply masking
                    IntStream.range(startIndex, startIndex + maskLength)
                            .forEach(i -> sb.setCharAt(i, 'X'));
                }
            });
        }
        
        return sb.toString();
    }
    
    /**
     * Gets the current list of masking patterns.
     * 
     * @return List of regex patterns used for masking
     */
    public List<String> getMaskPatterns() {
        return new ArrayList<>(maskPatterns);
    }
    
    /**
     * Clears all masking patterns including defaults.
     */
    public void clearMaskPatterns() {
        maskPatterns.clear();
        multilinePattern = null;
    }
}

