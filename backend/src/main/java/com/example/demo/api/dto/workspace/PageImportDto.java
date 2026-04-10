package com.example.demo.api.dto.workspace;


import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class PageImportDto {
    private String name;
    private String route;
    private Map<String, DroppedItemDto> droppedItems;
    private List<String> rootIds;
    private String selectedId;
}
