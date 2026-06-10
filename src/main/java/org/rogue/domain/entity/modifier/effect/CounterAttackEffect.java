package org.rogue.domain.entity.modifier.effect;

import org.rogue.domain.entity.modifier.Modifier;
import org.rogue.domain.entity.modifier.ModifierType;

public class CounterAttackEffect extends Modifier {
    private boolean restFlag;       // Если TRUE, то не может контратаковать
    private int numOfCounterAttack; // сколько раз при получении урона совершит одну контратаку

    public CounterAttackEffect(int duration, int numOfCounterAttack) {
        super(ModifierType.COUNTER_ATTACK, duration);
        this.restFlag = true;
        this.numOfCounterAttack = numOfCounterAttack;
    }

    public boolean isRestFlag() {
        return restFlag;
    }

    public void setRestFlag(boolean restFlag) {
        this.restFlag = restFlag;
    }

    public int getNumOfCounterAttack() {
        return numOfCounterAttack;
    }

    public void setNumOfCounterAttack(int numOfCounterAttack) {
        this.numOfCounterAttack = numOfCounterAttack;
    }

    @Override
    public boolean isExpired() {
        return getDuration() == 0 || numOfCounterAttack == 0;
    }

    public void reduceNumOfCounterAttack() {
        if (numOfCounterAttack > 0) numOfCounterAttack--;
    }

}


