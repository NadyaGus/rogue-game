package org.rogue.datalayer.dto;

import org.rogue.domain.entity.game.GameSession;
import org.rogue.domain.entity.item.Item;
import org.rogue.domain.entity.item.ItemType;

public class GameStateMapper {
    private GameSession session;
    private ItemType selectedInventory;

    public GameStateDto toDto(GameSession session, boolean isInventoryOpen, ItemType inventoryTypeOpen, boolean isFogOfWarUsed) {
        this.session = session;
        this.selectedInventory = ItemType.FOOD;

        return new GameStateDto(session.getLevel().getTiles(),
                session.getLevel().getExploreService().getExploredTiles(),
                session.getLevel().getExploreService().getVisibleTiles(),
                session.getLevel().width,
                session.getLevel().height,
                session.getPlayer().getPosition(),
                session.getLevel().getItems(),
                isInventoryOpen,
                processInventory(inventoryTypeOpen),
                this.selectedInventory,
                session.getLevel().getEnemies(),
                isFogOfWarUsed,
                session.getPlayer(),
                session.getLevelNumber()
        );
    }

    private Item[] processInventory(ItemType inventoryTypeOpen) {
        Item[] result;

        switch (inventoryTypeOpen) {
            case POTION -> {
                selectedInventory = ItemType.POTION;
                result = session.getPlayer().getBackpack().getPotionsCompartment();
            }
            case SCROLL -> {
                selectedInventory = ItemType.SCROLL;
                result = session.getPlayer().getBackpack().getScrollsCompartment();
            }
            case WEAPON -> {
                selectedInventory = ItemType.WEAPON;
                result = session.getPlayer().getBackpack().getWeaponsCompartment();
            }
            default -> {
                selectedInventory = ItemType.FOOD;
                result = session.getPlayer().getBackpack().getFoodsCompartment();
            }
        }

        return result;
    }
}
