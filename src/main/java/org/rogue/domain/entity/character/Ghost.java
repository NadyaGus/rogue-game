package org.rogue.domain.entity.character;

import java.awt.*;

public class Ghost extends Enemy {
    public static final double VISIBILITY_CHANCE = 0.25;

    public Ghost(int maxHealth, int strength, int agility, Point position,  double hostility) {
        super(maxHealth, strength, agility, position, hostility, EnemyType.GHOST);
        setVisible(false); // изначально невидимый
    }

    @Override
    public int attack() {
        setVisible(true); // при атаке, становится видимым
        return getStrength();
    }

}
