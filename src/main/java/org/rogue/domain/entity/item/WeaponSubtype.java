package org.rogue.domain.entity.item;

public enum WeaponSubtype {
    WOODEN_SWORD(10), STEEL_SWORD(20), DIAMOND_SWORD(30);
    private final int strength;

    WeaponSubtype(int strength) {
        this.strength = strength;
    }

    public int getWeaponStrength() {
        return strength;
    }
}
