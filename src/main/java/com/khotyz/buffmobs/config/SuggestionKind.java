package com.khotyz.buffmobs.config;

public enum SuggestionKind {
    NONE(null),
    ENTITY_ID("buffmobs.config.menu.entityIdHint"),
    MOD_ID("buffmobs.config.menu.modIdHint"),
    DIMENSION_ID("buffmobs.config.menu.dimensionIdHint"),
    ENTITY_PRESET_MAPPING("buffmobs.config.menu.presetMappingHint"),
    ENTITY_ABILITY_MAPPING("buffmobs.config.menu.abilityMappingHint");

    private final String hintKey;

    SuggestionKind(String hintKey) {
        this.hintKey = hintKey;
    }

    public String hintKey() {
        return hintKey;
    }

    public boolean isEnabled() {
        return this != NONE;
    }
}
