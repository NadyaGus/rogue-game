package org.rogue.domain.entity.game;

public class Statistics {
    private int goldCollected;
    private int deepestLevel = 1;

    private int enemiesDefeated;
    private int hitsDealt;
    private int hitsTaken;
    private int steps = -1;

    private int foodEaten;
    private int potionUsed;
    private int scrollsUsed;

    public void addGold(int amount) {
        goldCollected += amount;
    }

    public void incrementLevel() {
        deepestLevel++;
    }

    public void incrementEnemiesDefeated() {
        enemiesDefeated++;
    }

    public void incrementHitsDealt() {
        hitsDealt++;
    }

    public void incrementHitsTake() {
        hitsTaken++;
    }

    public void incrementSteps() {
        steps++;
    }

    public void incrementFoodEaten() {
        foodEaten++;
    }

    public void incrementPotionUsed() {
        potionUsed++;
    }

    public void incrementScrollsUsed() {
        scrollsUsed++;
    }

    public int getGoldCollected() {
        return goldCollected;
    }

    public int getDeepestLevel() {
        return deepestLevel;
    }

    public int getEnemiesDefeated() {
        return enemiesDefeated;
    }

    public int getHitsDealt() {
        return hitsDealt;
    }

    public int getHitsTaken() {
        return hitsTaken;
    }

    public int getSteps() {
        return steps;
    }

    public int getFoodEaten() {
        return foodEaten;
    }

    public int getPotionUsed() {
        return potionUsed;
    }

    public int getScrollsUsed() {
        return scrollsUsed;
    }
}
