package com.uibuilder.mas.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptRequestDto {

    @NotNull
    private String prompt;

    /** Optional. When present, real-time status events are streamed to the
     *  SSE endpoint at GET /agent/status/{sessionId}. */
    private String sessionId;
}
