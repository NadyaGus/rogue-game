package org.rogue.domain.entity.level;

import org.rogue.domain.entity.character.Enemy;
import org.rogue.domain.entity.item.Item;
import org.rogue.domain.service.ExploreService.ExploreService;

import java.awt.*;
import java.util.List;

public class Level {
    public final int width;
    public final int height;
    private final ExploreService exploreService;
    private Tile[][] tiles;

    private Room[] rooms;
    private Corridor[] corridors;

    private List<Enemy> enemies;
    private Item[] items;

    private Room startingRoom;
    private Room endingRoom;
    private Point exitPoint;

    public Level(int width, int height) {
        this.width = width;
        this.height = height;
        initTiles(width, height);
        exploreService = new ExploreService(tiles);
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    private void initTiles(int width, int height) {
        this.tiles = new Tile[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                this.tiles[x][y] = Tile.EMPTY;
            }
        }
    }

    public void setTile(int x, int y, Tile tile) {
        this.tiles[x][y] = tile;
    }
    public void setRooms(Room[] rooms) {
        this.rooms = rooms;
    }

    public Room[] getRooms() {
        return rooms;
    }

    public void setCorridors(Corridor[] corridors) {
        this.corridors = corridors;
    }

    public Item[] getItems() {
        return items;
    }

    public void setItems(Item[] items) {
        this.items = items;
    }

    public void addItem(Item newItem) {
        Item[] newItems = new Item[items.length + 1];

        System.arraycopy(items, 0, newItems, 0, items.length);
        newItems[items.length] = newItem;

        items = newItems;
    }

    public Room getStartingRoom() {
        return startingRoom;
    }

    public void setStartingRoom(Room startingRoom) {
        this.startingRoom = startingRoom;
    }

    public void setEndingRoom(Room endingRoom) {
        this.endingRoom = endingRoom;

        int x = (endingRoom.getTopLeft().x + endingRoom.getTopRight().x) / 2;
        int y = (endingRoom.getTopLeft().y + endingRoom.getBottomLeft().y) / 2;

        this.exitPoint = new Point(x,y);
    }

    public Point getExitPoint() {
        return exitPoint;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public void setEnemies(List<Enemy> enemies) {
        this.enemies = enemies;
    }

    public ExploreService getExploreService() {
        return exploreService;
    }

    public Corridor[] getCorridors() {
        return corridors;
    }
}
