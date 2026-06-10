package org.rogue.domain.entity.modifier.effect;

import org.rogue.domain.entity.modifier.Modifier;
import org.rogue.domain.entity.modifier.ModifierType;

public class SleepEffect extends Modifier {
    public SleepEffect(int duration) {
        super(ModifierType.SLEEP, duration);
    }

}
