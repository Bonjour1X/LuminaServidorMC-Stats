package com.tuservidor.stats;

public enum ScoreType {
    // PvP y Combate
    KILL(10.0),
    DEATH(-5.0),
    HOSTILE_KILLS(5.0),
    DAMAGE_DEALT(0.5),

    // Movimiento
    DISTANCE_WALKED(0.01),
    DISTANCE_SPRINTED(0.02),
    DISTANCE_SWUM(0.03),
    DISTANCE_BOAT(0.02),
    DISTANCE_HORSE(0.03),

    // Recursos y Crafteo
    BLOCK_MINED(1.0),
    BLOCKS_PLACED(0.5),
    ITEMS_ENCHANTED(5.0),
    WEAPONS_CRAFTED(3.0),
    TOOLS_CRAFTED(3.0),

    // Eventos Especiales
    RAIDS_WON(15.0),
    VILLAGER_TRADES(2.0),
    ANIMALS_BRED(3.0),
    ARCHAEOLOGY(5.0),

    // Tiempo
    PLAYTIME(0.1);

    private final double weight;

    ScoreType(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }
}