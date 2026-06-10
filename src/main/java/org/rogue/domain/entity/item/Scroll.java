package org.rogue.domain.entity.item;

import org.rogue.domain.entity.character.StatType;

import java.util.Random;

public class Scroll extends Item {
    public static final int pointsToBuff = 10;
    private final StatType subtype;
    private final int maxHealth;
    private final int agility;
    private final int strength;

    public Scroll(int difficulty) {
        super(difficulty);
        this.subtype = getRandomType();
        this.maxHealth = this.getSubtype() == StatType.MAX_HP ? pointsToBuff : 0;
        this.agility = this.getSubtype() == StatType.AGILITY ? pointsToBuff : 0;
        this.strength = this.getSubtype() == StatType.STRENGTH ? pointsToBuff : 0;
    }

    public StatType getSubtype() {
        return subtype;
    }

    @Override
    public ItemType getType() {
        return ItemType.SCROLL;
    }

    @Override
    public String getName() {
        return getSubtype().name();
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getAgility() {
        return agility;
    }

    public int getStrength() {
        return strength;
    }

    private StatType getRandomType() {
        StatType[] options = {StatType.AGILITY, StatType.MAX_HP, StatType.STRENGTH};
        Random random = new Random();

        return options[random.nextInt(options.length)];
    }
}
