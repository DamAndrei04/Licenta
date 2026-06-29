package com.uibuilder.mas.agent.agent.validator.rule;

import com.uibuilder.mas.agent.agent.builder.model.UIComponentTree;

import java.util.List;

/**
 * Interfața pentru regulile de validare. Fiecare regulă efectuează o verificare
 * specifică asupra arborelui de componente și raportează eventualele încălcări.
 */
public interface ValidationRule {

    /**
     * Validează arborele de componente și returnează lista încălcărilor găsite.
     *
     * @param componentTree arborele care trebuie validat
     * @return lista mesajelor de încălcare (goală dacă arborele este valid)
     */
    List<String> validate(UIComponentTree componentTree);

    /**
     * Returnează numele (identificatorul) regulii.
     *
     * @return numele regulii
     */
    String getRuleName();
}
