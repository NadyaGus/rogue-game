package org.rogue.domain.entity.character;

import java.awt.*;

public class SnakeMage extends Enemy {
    public SnakeMage(int maxHealth, int strength, int agility, Point position,  double hostility) {
        super(maxHealth, strength, agility, position, hostility, EnemyType.SNAKE_MAGE);
    }

}
