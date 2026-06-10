package org.rogue.domain.entity.item;

import org.rogue.domain.entity.character.StatType;

public class Food extends Item {
    private final FoodSubtype subtype;
    private final int healthPoints;

    public Food(int difficulty) {
        super(difficulty);
        this.subtype = calculateSubtype();
        this.healthPoints = subtype.getHealthPoints();
    }

    /**
     * max difficulty = 21
     */
    private FoodSubtype calculateSubtype() {
        int difficulty = this.getDifficulty();

        if (difficulty <= 5) {
            return FoodSubtype.APPLE;
        } else if (difficulty <= 10) {
            return Math.random() < 0.5 ? FoodSubtype.APPLE : FoodSubtype.BREAD;
        } else if (difficulty <= 14) {
            return FoodSubtype.BREAD;
        } else if (difficulty <= 18) {
            return Math.random() < 0.5 ? FoodSubtype.BREAD : FoodSubtype.ROAST;
        } else {
            return FoodSubtype.ROAST;
        }
    }

    public FoodSubtype getSubtype() {
        return subtype;
    }

    public int getHealthPoints() {
        return healthPoints;
    }

    @Override
    public ItemType getType() {
        return ItemType.FOOD;
    }

    @Override
    public String getName() {
        return getSubtype().name();
    }

    public StatType getStatType() {
        return StatType.HP;
    }
}