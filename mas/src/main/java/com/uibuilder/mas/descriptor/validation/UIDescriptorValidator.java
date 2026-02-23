package com.uibuilder.mas.descriptor.validation;

import com.uibuilder.mas.descriptor.UIDescriptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates UI descriptors for structural integrity.
 * Isolated validation logic - no business rules.
 */
@Slf4j
@Component
public class UIDescriptorValidator {
    
    public ValidationResult validate(UIDescriptor descriptor) {
        log.debug("Validating UI descriptor");
        List<String> errors = new ArrayList<>();
        
        if (descriptor == null) {
            errors.add("Descriptor cannot be null");
            return new ValidationResult(false, errors);
        }
        
        if (descriptor.getVersion() == null || descriptor.getVersion().isEmpty()) {
            errors.add("Version is required");
        }
        
        if (descriptor.getPages() == null || descriptor.getPages().isEmpty()) {
            errors.add("At least one page is required");
        } else {
            validatePages(descriptor, errors);
        }
        
        boolean isValid = errors.isEmpty();
        log.debug("Validation result: {}, errors: {}", isValid, errors.size());
        
        return new ValidationResult(isValid, errors);
    }
    
    private void validatePages(UIDescriptor descriptor, List<String> errors) {
        descriptor.getPages().forEach((pageId, page) -> {
            if (page.getName() == null || page.getName().isEmpty()) {
                errors.add("Page " + pageId + " must have a name");
            }
            
            if (page.getRoute() == null || !page.getRoute().startsWith("/")) {
                errors.add("Page " + pageId + " must have a valid route starting with '/'");
            }
            
            if (page.getRootIds() == null) {
                errors.add("Page " + pageId + " must have rootIds array (can be empty)");
            }
        });
    }
    
    public record ValidationResult(boolean valid, List<String> errors) {}
}
