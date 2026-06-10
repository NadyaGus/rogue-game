package org.rogue.domain.service;

import org.rogue.domain.entity.item.*;

public class ItemFactory {
    /**
     *
     * @param type
     * @param difficulty diff for enemy or value for gold
     * @return
     */
    public Item createItem(ItemType type, int difficulty) {
        Item item = null;

        switch (type) {
            case GOLD -> item = new Gold(difficulty);
            case FOOD -> item = new Food(difficulty);
            case POTION -> item = new Potion(difficulty);
            case SCROLL -> item = new Scroll(difficulty);
            default -> item = new Weapon(difficulty);
        }

        return item;
    }
}
