package org.rogue.domain.entity.character;

import java.awt.*;

public class Vampire extends Enemy {
    public Vampire(int maxHealth, int strength, int agility, Point position,  double hostility) {
        super(maxHealth, strength, agility, position, hostility, EnemyType.VAMPIRE);
    }

}
