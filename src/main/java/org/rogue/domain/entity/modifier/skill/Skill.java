package org.rogue.domain.entity.modifier.skill;

import org.rogue.domain.entity.modifier.Modifier;
import org.rogue.domain.entity.modifier.ModifierType;

public abstract class Skill extends Modifier {
    private int chance; // шанс 0-100 (%)

    public Skill(ModifierType type, int chance) {
        super(type, -1);
        this.chance = chance;
    }


    public int getChance() {
        return chance;
    }

    public void setChance(int chance) {
        this.chance = chance;
    }

}
