package com.example.campconnect_backend.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "equipment",
        indexes = {
                @Index(name = "idx_equipment_category", columnList = "category"),
                @Index(name = "idx_equipment_subcategory", columnList = "sub_category"),
                @Index(name = "idx_equipment_price", columnList = "price"),
                @Index(name = "idx_equipment_available", columnList = "available"),
                @Index(name = "idx_equipment_deleted", columnList = "deleted")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_equipment_name", columnNames = "name")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(columnDefinition = "TEXT", length = 5000)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    /**
     * Manual business availability (admin can disable independently of stock)
     */
    @Column(nullable = false)
    private Boolean available;

    /**
     * Inventory fields
     */
    @Column(nullable = false)
    private Integer totalQuantity;

    @Column(nullable = false)
    private Integer reservedQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "sub_category")
    private EquipmentSubCategory subCategory;

    @Column(nullable = false)
    @Builder.Default
    private Boolean deleted = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "equipmentList")
    private List<Order> orders;

    @PrePersist
    @PreUpdate
    private void syncCategoryFromSubCategory() {
        if (subCategory != null) {
            this.category = subCategory.getCategory();
        }
    }

    @Transient
    public int getAvailableQuantity() {
        int total = totalQuantity == null ? 0 : totalQuantity;
        int reserved = reservedQuantity == null ? 0 : reservedQuantity;
        return Math.max(0, total - reserved);
    }

    @Transient
    public boolean isInStock() {
        return getAvailableQuantity() > 0;
    }
}
