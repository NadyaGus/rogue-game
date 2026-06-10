package org.rogue.domain.entity.character;

import java.awt.*;

public class Ogre extends Enemy {
    public Ogre(int maxHealth, int strength, int agility, Point position,  double hostility) {
        super(maxHealth, strength, agility, position, hostility, EnemyType.OGRE);
    }

}
