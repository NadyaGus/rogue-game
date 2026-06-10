package org.rogue.domain.entity.modifier;

public enum ModifierType {
    // IF duration == -1 {это пассивный скилл/эффект, не имеющий срок жизни в ходах}
    // Skills:
    SLEEP_STRIKE, VAMPIRIC_CLAW, COUNTER_ABILITY,
    // Effects:
    SLEEP, EVASION, COUNTER_ATTACK
}
