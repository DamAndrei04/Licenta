package com.example.demo.api.dto.component;

import com.example.demo.api.dto.component.enums.ComponentType;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;


@Data
@Builder
public class ComponentRequestDto {

    @Size(max = 255)
    @NotNull
    private String externalId;

    private String parentExternalId;

    @NotNull
    private ComponentType type;

    private Map<String, Object> props;

    @NotNull
    private Map<String, Object> layout;

    private Map<String, Object> events;

    private Map<String, Object> state;

}
