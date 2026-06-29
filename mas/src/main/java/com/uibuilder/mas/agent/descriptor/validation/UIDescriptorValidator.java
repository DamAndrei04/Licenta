package com.uibuilder.mas.agent.descriptor.validation;

import com.uibuilder.mas.agent.descriptor.UIDescriptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Validează integritatea structurală a descriptorilor UI (existența versiunii, a paginilor
 * și a câmpurilor obligatorii ale paginilor). Logica de validare este izolată, fără reguli
 * de afaceri.
 */
@Slf4j
@Component
public class UIDescriptorValidator {

    /**
     * Validează un descriptor UI: verifică prezența versiunii și a cel puțin unei pagini,
     * apoi validează fiecare pagină.
     *
     * @param descriptor descriptorul UI care trebuie validat
     * @return rezultatul validării (valid/invalid și lista erorilor)
     */
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
    
    /**
     * Validează paginile unui descriptor: fiecare pagină trebuie să aibă un nume, o rută
     * validă (care începe cu „/”) și un tablou {@code rootIds} (posibil gol).
     *
     * @param descriptor descriptorul ale cărui pagini se validează
     * @param errors lista în care se acumulează mesajele de eroare
     */
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
    
    /**
     * Rezultatul validării unui descriptor: indicatorul de validitate și lista erorilor.
     *
     * @param valid {@code true} dacă descriptorul este valid
     * @param errors lista mesajelor de eroare (goală dacă este valid)
     */
    public record ValidationResult(boolean valid, List<String> errors) {}
}
