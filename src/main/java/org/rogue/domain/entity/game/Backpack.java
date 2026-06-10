package org.rogue.domain.entity.game;

import org.rogue.domain.entity.item.*;

public class Backpack {
    private final Food[] foodsCompartment;
    private final Potion[] potionsCompartment;
    private final Scroll[] scrollsCompartment;

    private final Weapon[] weaponsCompartment;
    private final int MAX_COMPARTMENT_SIZE = 9;
    private int goldAmount;

    public Backpack() {
        this.foodsCompartment = new Food[MAX_COMPARTMENT_SIZE];
        this.potionsCompartment = new Potion[MAX_COMPARTMENT_SIZE];
        this.scrollsCompartment = new Scroll[MAX_COMPARTMENT_SIZE];
        this.weaponsCompartment = new Weapon[MAX_COMPARTMENT_SIZE];
    }

    public void addItem(Item newItem) throws IllegalStateException {
        ItemType type = newItem.getType();

        if (newItem instanceof Gold goldItem) {
            goldAmount += goldItem.getValue();
            return;
        }

        Item[] compartment = getCompartmentByType(type);

        for (int i = 0; i < MAX_COMPARTMENT_SIZE; i++) {
            if (compartment[i] == null) {
                compartment[i] = newItem;
                return;
            }
        }

        throw new IllegalStateException("No free slot for " + newItem.getType());
    }

    public Item getItemFromCompartment(ItemType type, int order) {
        if (order <= 0) {
            return null;
        }
        Item[] compartment = getCompartmentByType(type);
        if (compartment[order - 1] != null) {
            return compartment[order - 1];
        }
        return null;
    }

    public void deleteItem(Item item) {
        if (item == null) return;

        ItemType type = item.getType();
        Item[] compartment = getCompartmentByType(type);

        for (int i = 0; i < compartment.length; i++) {
            if (compartment[i] != null && compartment[i].getId() == item.getId()) {
                compartment[i] = null;
                return;
            }
        }

    }

    private Item[] getCompartmentByType(ItemType type) throws IllegalArgumentException {
        return switch (type) {
            case FOOD -> foodsCompartment;
            case POTION -> potionsCompartment;
            case SCROLL -> scrollsCompartment;
            case WEAPON -> weaponsCompartment;
            default -> throw new IllegalArgumentException("Unexpected type: " + type);
        };
    }

    public Food[] getFoodsCompartment() {
        return foodsCompartment.clone();
    }

    public Potion[] getPotionsCompartment() {
        return potionsCompartment.clone();
    }

    public Scroll[] getScrollsCompartment() {
        return scrollsCompartment.clone();
    }

    public Weapon[] getWeaponsCompartment() {
        return weaponsCompartment.clone();
    }

    public int getGoldAmount() {
        return goldAmount;
    }
}
