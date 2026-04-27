package com.example.campconnect_backend.Services;

import com.example.campconnect_backend.Entities.Equipment;
import com.example.campconnect_backend.Entities.EquipmentCategory;
import com.example.campconnect_backend.Entities.EquipmentSubCategory;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class EquipmentSpecifications {

    public static Specification<Equipment> keywordContains(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;
            String pattern = "%" + keyword.trim().toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }

    public static Specification<Equipment> hasCategory(EquipmentCategory category) {
        return (root, query, cb) -> category == null ? null : cb.equal(root.get("category"), category);
    }

    public static Specification<Equipment> hasSubCategory(EquipmentSubCategory subCategory) {
        return (root, query, cb) -> subCategory == null ? null : cb.equal(root.get("subCategory"), subCategory);
    }

    public static Specification<Equipment> isAvailable(Boolean available) {
        return (root, query, cb) -> available == null ? null : cb.equal(root.get("available"), available);
    }

    public static Specification<Equipment> inStock(Boolean inStock) {
        return (root, query, cb) -> {
            if (inStock == null) return null;

            // force Integer expression type
            var total = root.get("totalQuantity").as(Integer.class);
            var reserved = root.get("reservedQuantity").as(Integer.class);
            var availableQtyExpr = cb.diff(total, reserved);

            return inStock
                    ? cb.gt(availableQtyExpr, 0)
                    : cb.le(availableQtyExpr, 0);
        };
    }


    public static Specification<Equipment> priceGreaterOrEqual(BigDecimal minPrice) {
        return (root, query, cb) -> minPrice == null ? null : cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    public static Specification<Equipment> priceLessOrEqual(BigDecimal maxPrice) {
        return (root, query, cb) -> maxPrice == null ? null : cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }

    public static Specification<Equipment> isDeleted(Boolean deleted) {
        return (root, query, cb) -> deleted == null ? null : cb.equal(root.get("deleted"), deleted);
    }
}
