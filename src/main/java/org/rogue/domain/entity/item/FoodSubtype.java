package org.rogue.domain.entity.item;

public enum FoodSubtype {
    APPLE(15), BREAD(30), ROAST(45);
    private final int healthPoints;

    FoodSubtype(int healthPoints) {
        this.healthPoints = healthPoints;
    }

    public int getHealthPoints() {
        return healthPoints;
    }
}
