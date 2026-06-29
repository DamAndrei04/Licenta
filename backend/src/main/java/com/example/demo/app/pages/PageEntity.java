package com.example.demo.app.pages;

import com.example.demo.app.components.ComponentEntity;
import com.example.demo.app.projects.ProjectEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entitate JPA care reprezintă o pagină persistată în tabela {@code pages}.
 * O pagină aparține unui proiect, are o rută și conține o listă de componente.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pages")
public class PageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private ProjectEntity project;

    @Column(name = "name")
    private String name;

    @Column(name = "route")
    private String route;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "page", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComponentEntity> components = new ArrayList<>();

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
