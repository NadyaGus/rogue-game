package org.rogue.domain.entity.item;

import org.rogue.domain.entity.character.StatType;

import java.util.Random;

public class Potion extends Item {
    private final StatType subtype;
    public final int timeBuff = 90;
    public final static int pointsToBuff = 5;

    public Potion(int difficulty) {
        super(difficulty);
        this.subtype = getRandomType();
    }

    public StatType getSubtype() {
        return subtype;
    }

    @Override
    public ItemType getType() {
        return ItemType.POTION;
    }

    @Override
    public String getName() {
        return getSubtype().name();
    }

    private StatType getRandomType() {
        StatType[] options = {StatType.AGILITY, StatType.MAX_HP, StatType.STRENGTH};
        Random random = new Random();

        return options[random.nextInt(options.length)];
    }
}
