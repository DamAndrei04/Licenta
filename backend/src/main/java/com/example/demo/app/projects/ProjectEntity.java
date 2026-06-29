package com.example.demo.app.projects;

import com.example.demo.app.pages.PageEntity;
import com.example.demo.app.users.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entitate JPA care reprezintă un proiect persistat în tabela {@code projects}.
 * Un proiect aparține unui utilizator și conține o listă de pagini.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "projects")
public class ProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<PageEntity> pages = new ArrayList<>();


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
