package org.rogue.datalayer.dto;

import org.rogue.domain.entity.character.Enemy;
import org.rogue.domain.entity.character.Player;
import org.rogue.domain.entity.item.Item;
import org.rogue.domain.entity.item.ItemType;
import org.rogue.domain.entity.level.Tile;
import org.rogue.domain.service.ExploreService.ExploredTiles;
import org.rogue.domain.service.ExploreService.VisibleTiles;

import java.awt.*;
import java.util.List;

/**
 * Data fow view without logic
 */
public class GameStateDto {
    public final int width;
    public final int height;
    private final Item[] items;
    private final ExploredTiles[][] exploredTiles;// то, что видит игрок (с учётом тумана)
    private final Point playerPosition;
    private final Tile[][] tilesMap;
    private final VisibleTiles[][] visibleTiles;
    private final Player player;
    private final int levelNumber;
    private boolean isInventoryOpen;
    private final Item[] inventory;
    private final List<Enemy> enemies;
    private final ItemType inventoryType;
    private final boolean isFogOfWar;

    public GameStateDto(Tile[][] tiles, ExploredTiles[][] exploredTiles, VisibleTiles[][] visibleTiles, int width, int height,
                        Point playerPosition, Item[] items, boolean isInventoryOpen,
                        Item[] inventory, ItemType inventoryType, List<Enemy> enemies,
                        boolean isFogOfWar, Player player, int levelNumber) {
        this.tilesMap = tiles;
        this.exploredTiles = exploredTiles;
        this.visibleTiles = visibleTiles;
        this.width = width;
        this.height = height;
        this.playerPosition = playerPosition;
        this.items = items;
        this.isInventoryOpen = isInventoryOpen;
        this.inventory = inventory;
        this.inventoryType = inventoryType;
        this.enemies = enemies;
        this.isFogOfWar = isFogOfWar;
        this.player = player;
        this.levelNumber = levelNumber;
    }

    public Tile[][] getTilesMap() {
        return tilesMap;
    }

    public ExploredTiles[][] getExploredTiles() {
        return exploredTiles;
    }

    public Point getPlayerPosition() {
        return playerPosition;
    }

    public Item[] getItems() {
        return this.items;
    }

    public boolean isInventoryOpen() {
        return isInventoryOpen;
    }

    public Item[] getInventory() {
        return inventory;
    }

    public ItemType getInventoryType() {
        return this.inventoryType;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public boolean isFogOfWar() {
        return isFogOfWar;
    }

    public VisibleTiles[][] getVisibleTiles() {
        return visibleTiles;
    }

    public Player getPlayer() {
        return player;
    }

    public int getLevelNumber() {
        return levelNumber;
    }
}