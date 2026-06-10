package org.rogue.domain.entity.item;

public class Weapon extends Item {
    private final WeaponSubtype subtype;
    private final int strength;

    public Weapon(int difficulty) {
        super(difficulty);
        this.subtype = calculateSubtype();
        this.strength = subtype.getWeaponStrength();
    }

    public WeaponSubtype getSubtype() {
        return subtype;
    }

    public int getStrength() {
        return strength;
    }

    @Override
    public ItemType getType() {
        return ItemType.WEAPON;
    }

    @Override
    public String getName() {
        return getSubtype().name();
    }

    private WeaponSubtype calculateSubtype() {
        int difficulty = this.getDifficulty();

        if (difficulty <= 5) {
            return WeaponSubtype.WOODEN_SWORD;
        } else if (difficulty <= 10) {
            return WeaponSubtype.STEEL_SWORD;
        } else {
            return WeaponSubtype.DIAMOND_SWORD;
        }
    }
}
