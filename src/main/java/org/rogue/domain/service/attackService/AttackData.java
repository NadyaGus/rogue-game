package org.rogue.domain.service.attackService;

import org.rogue.domain.entity.character.Character;

public class AttackData {
    private Character attacker;
    private Character defender;
    private int baseDamage;
    private int finalDamage;
    private boolean cancelled;
    private boolean isEvaded;

    public AttackData(Character attacker, Character defender) {
        this.attacker = attacker;
        this.defender = defender;
        this.baseDamage = attacker.attack();
        this.finalDamage = attacker.attack();
        this.cancelled = false;
        this.isEvaded = false;
    }


    public Character getAttacker() {
        return attacker;
    }
    public Character getDefender() {
        return defender;
    }
    public int getBaseDamage() {
        return baseDamage;
    }
    public int getFinalDamage() {
        return finalDamage;
    }
    public boolean isCancelled() {
        return cancelled;
    }
    public boolean isEvaded() {
        return isEvaded;
    }


    public void setAttacker(Character attacker) {
        this.attacker = attacker;
    }
    public void setDefender(Character defender) {
        this.defender = defender;
    }
    public void setBaseDamage(int baseDamage) {
        this.baseDamage = baseDamage;
    }
    public void setFinalDamage(int finalDamage) {
        this.finalDamage = finalDamage;
    }
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
    public void setEvaded(boolean evaded) {
        isEvaded = evaded;
    }
}
