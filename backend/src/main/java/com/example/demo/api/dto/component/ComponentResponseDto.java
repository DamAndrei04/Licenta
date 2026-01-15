package com.example.demo.api.dto.component;

import com.example.demo.api.dto.component.enums.ComponentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComponentResponseDto {

    private Long id;

    private String externalId;

    private Long pageId;

    private Long parentId;

    private List<String> children;

    private ComponentType type;

    private Map<String, Object> props;

    private Map<String, Object> layout;

    private Map<String, Object> events;

    private Map<String, Object> state;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
