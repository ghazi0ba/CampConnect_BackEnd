package com.example.campconnect_backend.Entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "equipment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Boolean available;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentSubCategory subCategory;

    @ManyToMany(mappedBy = "equipmentList")
    private List<Order> orders;

    @PrePersist
    @PreUpdate
    private void syncCategoryFromSubCategory() {
        if (subCategory != null) {
            this.category = subCategory.getCategory();
        }
    }
}
