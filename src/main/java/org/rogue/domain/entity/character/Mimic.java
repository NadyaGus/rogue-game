package org.rogue.domain.entity.character;

import java.awt.*;

public class Mimic extends Enemy {
    private final char disguiseChar; // символ, которым мимик маскируется

    public Mimic(int maxHealth, int strength, int agility, Point position,  double hostility, char disguiseChar) {
        super(maxHealth, strength, agility, position, hostility, EnemyType.MIMIC);
        setVisible(false); // изначально невидимый (отображается как предмет)
        this.disguiseChar = disguiseChar;
    }

    @Override
    public int attack() {
        setVisible(true); // при атаке, становится видимым
        return getStrength();
    }

    public char getDisguiseChar() {
        return disguiseChar;
    }

}
