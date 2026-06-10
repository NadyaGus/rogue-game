package org.rogue.domain.entity.modifier.effect;

import org.rogue.domain.entity.modifier.Modifier;
import org.rogue.domain.entity.modifier.ModifierType;

public class EvasionEffect extends Modifier {
    private int numOfDodge;     // количество раз, которые Character может увенуться от атаки

    public EvasionEffect(int duration, int numOfDodge) {
        super(ModifierType.EVASION, duration);
        this.numOfDodge = numOfDodge;
    }

    public int getNumOfDodge() {
        return numOfDodge;
    }

    public void setNumOfDodge(int numOfDodge) {
        this.numOfDodge = numOfDodge;
    }


    public void reduceDodge() {
        if (getNumOfDodge() > 0) numOfDodge--;
    }

    @Override
    public boolean isExpired() {
        return getDuration() == 0 || getNumOfDodge() == 0;
    }


}
