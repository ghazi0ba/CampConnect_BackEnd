package com.example.campconnect_backend.Entities;

public enum EquipmentSubCategory {

    // 1. Shelter & Sleeping
    TENTS(EquipmentCategory.SHELTER_SLEEPING),
    SLEEPING_BAGS(EquipmentCategory.SHELTER_SLEEPING),
    SLEEPING_PADS_AIR_MATTRESSES(EquipmentCategory.SHELTER_SLEEPING),
    HAMMOCKS(EquipmentCategory.SHELTER_SLEEPING),
    CAMPING_PILLOWS(EquipmentCategory.SHELTER_SLEEPING),
    TARPS_GROUND_SHEETS(EquipmentCategory.SHELTER_SLEEPING),

    // 2. Cooking & Food
    PORTABLE_STOVES(EquipmentCategory.COOKING_FOOD),
    GAS_CANISTERS_FUEL(EquipmentCategory.COOKING_FOOD),
    COOKING_SETS_POTS_PANS(EquipmentCategory.COOKING_FOOD),
    UTENSILS(EquipmentCategory.COOKING_FOOD),
    COOLERS_ICE_BOXES(EquipmentCategory.COOKING_FOOD),
    FOOD_STORAGE_CONTAINERS(EquipmentCategory.COOKING_FOOD),

    // 3. Lighting & Power
    FLASHLIGHTS(EquipmentCategory.LIGHTING_POWER),
    HEADLAMPS(EquipmentCategory.LIGHTING_POWER),
    LANTERNS(EquipmentCategory.LIGHTING_POWER),
    BATTERIES(EquipmentCategory.LIGHTING_POWER),
    POWER_BANKS(EquipmentCategory.LIGHTING_POWER),
    SOLAR_CHARGERS(EquipmentCategory.LIGHTING_POWER),

    // 4. Clothing & Footwear
    JACKETS_WATERPROOF_THERMAL(EquipmentCategory.CLOTHING_FOOTWEAR),
    HIKING_PANTS_SHORTS(EquipmentCategory.CLOTHING_FOOTWEAR),
    T_SHIRTS_BREATHABLE(EquipmentCategory.CLOTHING_FOOTWEAR),
    HIKING_BOOTS(EquipmentCategory.CLOTHING_FOOTWEAR),
    SOCKS(EquipmentCategory.CLOTHING_FOOTWEAR),
    HATS_GLOVES(EquipmentCategory.CLOTHING_FOOTWEAR),

    // 5. Backpacks & Storage
    HIKING_BACKPACKS(EquipmentCategory.BACKPACKS_STORAGE),
    TRAVEL_BAGS(EquipmentCategory.BACKPACKS_STORAGE),
    DRY_BAGS(EquipmentCategory.BACKPACKS_STORAGE),
    ORGANIZERS(EquipmentCategory.BACKPACKS_STORAGE),

    // 6. Furniture & Comfort
    CAMPING_CHAIRS(EquipmentCategory.FURNITURE_COMFORT),
    FOLDING_TABLES(EquipmentCategory.FURNITURE_COMFORT),
    COTS_CAMP_BEDS(EquipmentCategory.FURNITURE_COMFORT),
    BLANKETS(EquipmentCategory.FURNITURE_COMFORT),

    // 7. Navigation & Safety
    COMPASSES(EquipmentCategory.NAVIGATION_SAFETY),
    GPS_DEVICES(EquipmentCategory.NAVIGATION_SAFETY),
    MAPS(EquipmentCategory.NAVIGATION_SAFETY),
    FIRST_AID_KITS(EquipmentCategory.NAVIGATION_SAFETY),
    MULTI_TOOLS_KNIVES(EquipmentCategory.NAVIGATION_SAFETY),
    EMERGENCY_KITS(EquipmentCategory.NAVIGATION_SAFETY),

    // 8. Hygiene & Personal Care
    PORTABLE_SHOWERS(EquipmentCategory.HYGIENE_PERSONAL_CARE),
    TOILETRIES_KITS(EquipmentCategory.HYGIENE_PERSONAL_CARE),
    TOWELS(EquipmentCategory.HYGIENE_PERSONAL_CARE),
    TOILET_PAPER_PORTABLE_TOILETS(EquipmentCategory.HYGIENE_PERSONAL_CARE),
    INSECT_REPELLENT(EquipmentCategory.HYGIENE_PERSONAL_CARE),
    SUNSCREEN(EquipmentCategory.HYGIENE_PERSONAL_CARE),

    // 9. Outdoor Activities
    TREKKING_POLES(EquipmentCategory.OUTDOOR_ACTIVITIES),
    CLIMBING_GEAR(EquipmentCategory.OUTDOOR_ACTIVITIES),
    FISHING_GEAR(EquipmentCategory.OUTDOOR_ACTIVITIES),
    BINOCULARS(EquipmentCategory.OUTDOOR_ACTIVITIES),

    // 10. Accessories
    ROPE_PARACORD(EquipmentCategory.ACCESSORIES),
    DUCT_TAPE(EquipmentCategory.ACCESSORIES),
    REPAIR_KITS(EquipmentCategory.ACCESSORIES),
    CARABINERS(EquipmentCategory.ACCESSORIES);

    private final EquipmentCategory category;

    EquipmentSubCategory(EquipmentCategory category) {
        this.category = category;
    }

    public EquipmentCategory getCategory() {
        return category;
    }
}
