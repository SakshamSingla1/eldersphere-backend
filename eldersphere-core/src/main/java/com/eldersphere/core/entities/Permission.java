package com.eldersphere.core.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permissions",
    uniqueConstraints = @UniqueConstraint(name = "uq_permissions_resource_action",
        columnNames = {"resource", "action"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Permission extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String resource;

    @Column(nullable = false, length = 50)
    private String action;

    @Column(columnDefinition = "TEXT")
    private String description;
}
