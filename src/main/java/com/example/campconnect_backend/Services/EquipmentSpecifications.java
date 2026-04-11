package com.example.campconnect_backend.Services;



import com.example.campconnect_backend.Entities.Equipment;
import com.example.campconnect_backend.Entities.EquipmentCategory;
import com.example.campconnect_backend.Entities.EquipmentSubCategory;
import org.springframework.data.jpa.domain.Specification;

    public class EquipmentSpecifications {

        public static Specification<Equipment> keywordContains(String keyword) {
            return (root, query, cb) -> {
                if (keyword == null || keyword.isBlank()) return cb.conjunction();
                String like = "%" + keyword.toLowerCase() + "%";
                return cb.or(
                        cb.like(cb.lower(root.get("name")), like),
                        cb.like(cb.lower(root.get("description")), like)
                );
            };
        }

        public static Specification<Equipment> hasCategory(EquipmentCategory category) {
            return (root, query, cb) ->
                    category == null ? cb.conjunction() : cb.equal(root.get("category"), category);
        }

        public static Specification<Equipment> hasSubCategory(EquipmentSubCategory subCategory) {
            return (root, query, cb) ->
                    subCategory == null ? cb.conjunction() : cb.equal(root.get("subCategory"), subCategory);
        }

        public static Specification<Equipment> isAvailable(Boolean available) {
            return (root, query, cb) ->
                    available == null ? cb.conjunction() : cb.equal(root.get("available"), available);
        }

        public static Specification<Equipment> priceGreaterOrEqual(Double minPrice) {
            return (root, query, cb) ->
                    minPrice == null ? cb.conjunction() : cb.ge(root.get("price"), minPrice);
        }

        public static Specification<Equipment> priceLessOrEqual(Double maxPrice) {
            return (root, query, cb) ->
                    maxPrice == null ? cb.conjunction() : cb.le(root.get("price"), maxPrice);
        }
    }


