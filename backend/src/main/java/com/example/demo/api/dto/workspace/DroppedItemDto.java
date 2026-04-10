package com.example.demo.api.dto.workspace;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class DroppedItemDto {
    private String id;
    private String type;
    private Map<String, Object> layout;
    private String parentId;
    private List<String> childrenIds;
    private Map<String, Object> props;
    private Map<String, Object> events;
    private Map<String, Object> state;
}
