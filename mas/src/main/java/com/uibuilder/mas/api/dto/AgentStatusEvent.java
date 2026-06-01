package com.uibuilder.mas.api.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

/**
 * Emitted over SSE to inform the frontend of the agent's current execution phase.
 *
 * status values:
 *   STARTED           — phase just began
 *   PAGE_STARTED      — (BUILDER only) starting a specific page
 *   PAGE_COMPLETED    — (BUILDER only) finished a specific page
 *   COMPLETED         — phase finished
 *   RETRY             — validation failed, restarting the full pipeline
 *   FAILED            — all attempts exhausted, pipeline aborted
 *   PIPELINE_COMPLETE — entire pipeline done successfully, stream will close
 */
@Data
@Builder
public class AgentStatusEvent {

    private AgentPhase phase;
    private String status;
    private String message;

    // Populated during BUILDER / PAGE_STARTED / PAGE_COMPLETED
    private Integer currentPageIndex;
    private String currentPageName;
    private Integer totalPages;

    // Populated during RETRY and FAILED
    private Integer attemptNumber;
    private Integer maxAttempts;
    @Singular
    private List<String> violations;
}
