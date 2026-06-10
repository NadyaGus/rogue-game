package org.rogue.domain.entity.character;

import java.awt.*;

public class Zombie extends Enemy {
    public Zombie(int maxHealth, int strength, int agility, Point position,  double hostility) {
        super(maxHealth, strength, agility, position, hostility, EnemyType.ZOMBIE);
    }

}
