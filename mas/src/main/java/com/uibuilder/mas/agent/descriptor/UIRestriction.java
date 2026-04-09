package com.uibuilder.mas.agent.descriptor;

import lombok.Builder;
import lombok.Value;

/**
 * Represents a constraint or restriction on the UI design.
 * Immutable semantic representation.
 */
@Value
@Builder
public class UIRestriction {
    String id;
    RestrictionType type;
    String description;
    String targetComponentId;
    Object value;
}
