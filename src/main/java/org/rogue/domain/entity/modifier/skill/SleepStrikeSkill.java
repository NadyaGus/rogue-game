package org.rogue.domain.entity.modifier.skill;

import org.rogue.domain.entity.modifier.ModifierType;

public class SleepStrikeSkill extends Skill {
    private int sleepDuration;      // на сколько ходов усыпить врага

    public SleepStrikeSkill(int chance, int sleepDuration) {
        super(ModifierType.SLEEP_STRIKE, chance);
        this.sleepDuration = sleepDuration;
    }

    public int getSleepDuration() {
        return sleepDuration;
    }

    public void setSleepDuration(int sleepDuration) {
        this.sleepDuration = sleepDuration;
    }

}
