package org.rogue.domain.entity.modifier.skill;

import org.rogue.domain.entity.modifier.ModifierType;

public class VampiricClawSkill extends Skill {
    private int percentOfEnemyMaxHealth;     // наносит доп %-урона от максимального здоровья цели

    public VampiricClawSkill(int chance, int percentOfMaxHealth) {
        super(ModifierType.VAMPIRIC_CLAW, chance);
        this.percentOfEnemyMaxHealth = percentOfMaxHealth;
    }

    public int getPercentOfEnemyMaxHealth() {
        return percentOfEnemyMaxHealth;
    }

    public void setPercentOfEnemyMaxHealth(int percentOfEnemyMaxHealth) {
        this.percentOfEnemyMaxHealth = percentOfEnemyMaxHealth;
    }

}
