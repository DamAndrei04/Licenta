package com.example.demo.app.components;

import com.example.demo.api.dto.component.enums.ComponentType;
import com.example.demo.app.pages.PageEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Entitate JPA care reprezintă o componentă UI persistată în tabela {@code components}.
 * O componentă aparține unei pagini, poate avea o componentă părinte și componente
 * copil (structură arborescentă), iar proprietățile, layout-ul, evenimentele și starea
 * sunt stocate ca obiecte JSON.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "components")
public class ComponentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id")
    private String externalId;

    @ManyToOne
    @JoinColumn(name = "page_id", nullable = false)
    private PageEntity page;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private ComponentEntity parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComponentEntity> children = new ArrayList<>();

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private ComponentType type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", name = "props")
    private Map<String, Object> props;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", name = "layout")
    private Map<String, Object> layout;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", name = "events")
    private Map<String, Object> events;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", name = "state")
    private Map<String, Object> state;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    /**
     * Callback JPA apelat înainte de prima persistare; setează data de creare.
     */
    @PrePersist
    private void onCreate(){
        createdAt = LocalDateTime.now();
    }

    /**
     * Callback JPA apelat înainte de fiecare actualizare; setează data ultimei modificări.
     */
    @PreUpdate
    private void onUpdate(){
        updatedAt = LocalDateTime.now();
    }
}
