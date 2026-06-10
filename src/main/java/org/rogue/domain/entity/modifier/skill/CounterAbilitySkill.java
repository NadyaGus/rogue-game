package org.rogue.domain.entity.modifier.skill;

import org.rogue.domain.entity.modifier.ModifierType;

public class CounterAbilitySkill extends Skill {
    private int numOfCounterAttack;     // сколько раз при получении урона совершит одну контратаку
    private int timeOfCounterAttack;    // сколько ходов будет действовать эффект CounterAttackEffect ("-1" - постоянно)

    public CounterAbilitySkill(int chance, int numOfCounterAttack, int timeOfCounterAttack) {
        super(ModifierType.COUNTER_ABILITY, chance);
        this.numOfCounterAttack = numOfCounterAttack;
        this.timeOfCounterAttack = timeOfCounterAttack;
    }


    public int getNumOfCounterAttack() {
        return numOfCounterAttack;
    }

    public void setNumOfCounterAttack(int numOfCounterAttack) {
        this.numOfCounterAttack = numOfCounterAttack;
    }

    public int getTimeOfCounterAttack() {
        return timeOfCounterAttack;
    }

    public void setTimeOfCounterAttack(int timeOfCounterAttack) {
        this.timeOfCounterAttack = timeOfCounterAttack;
    }
}


