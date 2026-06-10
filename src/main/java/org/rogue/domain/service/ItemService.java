package org.rogue.domain.service;

import org.rogue.domain.entity.character.Enemy;
import org.rogue.domain.entity.character.Player;
import org.rogue.domain.entity.character.StatType;
import org.rogue.domain.entity.game.GameSession;
import org.rogue.domain.entity.game.Statistics;
import org.rogue.domain.entity.item.*;
import org.rogue.domain.entity.level.Level;
import org.rogue.domain.entity.level.Room;
import org.rogue.domain.entity.level.Tile;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ItemService {
    private final GameSession gameSession;
    private final ItemFactory factory = new ItemFactory();
    private Map<Point, Item> itemsMap;
    private final Map<Potion, Integer> potionsTimeMap;

    public ItemService(GameSession gameSession) {
        this.gameSession = gameSession;
        potionsTimeMap = new HashMap<>();
    }

    public Item[] generateItems(Room[] rooms, Room startingRoom, Point exit, int diff) {
        itemsMap = new HashMap<>();
        int difficulty = gameSession.getLevelNumber();
        Random random = new Random();

        int range = (21 - diff) / 2 - 3;
        if (range < 0) range = 0;
        int amount = random.nextInt(range, range + 3);
        Item[] items = new Item[amount];
        ItemType[] types = {ItemType.FOOD, ItemType.SCROLL, ItemType.POTION};

        for (int i = 0; i < amount; i++) {
            if (i == amount - 1 && Math.random() < 0.3) {
                items[i] = factory.createItem(ItemType.WEAPON, difficulty);
            } else {
                items[i] = factory.createItem(types[random.nextInt(types.length)], difficulty);
            }
        }

        calculateItemsPosition(items, rooms, startingRoom, exit);

        return items;
    }

    public void dropGold(Point pos, int value) {
        Gold item = (Gold) factory.createItem(ItemType.GOLD, value);
        item.setPosition(pos);
        itemsMap.put(pos, item);
        gameSession.getLevel().addItem(item);
    }

    private void calculateItemsPosition(Item[] items, Room[] rooms, Room startingRoom, Point exit) {
        int roomIndex = 0;
        Random random = new Random();
        int MAX_ROOMS_AMOUNT = 9;

        for (Item item : items) {
            roomIndex += random.nextInt(1, 3);
            if (roomIndex >= MAX_ROOMS_AMOUNT) roomIndex -= MAX_ROOMS_AMOUNT;
            if (Objects.equals(startingRoom, rooms[roomIndex])) {
                if (roomIndex == MAX_ROOMS_AMOUNT - 1) roomIndex -= 2;
                else roomIndex++;
            }

            Point pos = getRandomSpawnPoint(rooms[roomIndex], itemsMap, exit);

            item.setPosition(pos);
            itemsMap.put(pos, item);
        }
    }

    private Point getRandomSpawnPoint(Room room, Map<Point, Item> itemsMap, Point exit) {
        Random random = new Random();

        int attempts = 0;
        while (attempts < 1000) {
            int x = room.getTopLeft().x + random.nextInt(1, room.width);
            int y = room.getTopLeft().y + random.nextInt(1, room.height);
            Point point = new Point(x, y);
            if (!itemsMap.containsKey(point) &&
                    !Objects.equals(exit, point)) {
                return point;
            }
            attempts++;
        }
        throw new RuntimeException("Не удалось найти свободную точку для спауна предмета");
    }

    public void pickUpItem(Point item) {
        Item currentItem = itemsMap.get(item);
        Level level = gameSession.getLevel();

        if (currentItem != null && level != null) {
            try {
                if (currentItem instanceof Weapon) {
                    pickUpWeapon((Weapon) currentItem);
                } else {
                    gameSession.getPlayer().getBackpack().addItem(currentItem);
                    if (currentItem instanceof Gold)
                        gameSession.getStatistics().addGold(((Gold) currentItem).getValue());
                }

                itemsMap.remove(item);
                level.setItems(Arrays.stream(level.getItems()).filter(i ->
                        !Objects.equals(i, currentItem)).toArray(Item[]::new));
            } catch (IllegalStateException e) {
                System.out.println(e.getMessage());
            }

        }
    }

    public void pickUpWeapon(Weapon weapon) {
        Player player = gameSession.getPlayer();

        if (player.getCurrentWeapon() != null)
            dropWeapon();
        player.setCurrentWeapon(weapon);
    }

    public void dropWeapon() {
        Player player = gameSession.getPlayer();
        Weapon weapon = player.getCurrentWeapon();

        if (weapon != null) {
            Point exit = gameSession.getLevel().getExitPoint();
            List<Enemy> enemyList = gameSession.getLevel().getEnemies();
            Tile[][] levelTiles = gameSession.getLevel().getTiles();
            Point newWeaponPos = null;

            int centerX = player.getPosition().x;
            int centerY = player.getPosition().y;
            int radius = 1;

            while (newWeaponPos == null) {
                for (int dy = -radius; dy <= radius; dy++) {
                    int x = centerX + radius;
                    int y = centerY + dy;
                    if (isValidCell(x, y, exit, enemyList, levelTiles)) {
                        newWeaponPos = new Point(x, y);
                        break;
                    }
                }
                if (newWeaponPos != null) break;

                for (int dx = -radius + 1; dx <= radius - 1; dx++) {
                    int x = centerX + dx;
                    int y = centerY - radius;
                    if (isValidCell(x, y, exit, enemyList, levelTiles)) {
                        newWeaponPos = new Point(x, y);
                        break;
                    }
                }
                if (newWeaponPos != null) break;

                for (int dy = -radius + 1; dy <= radius; dy++) {
                    int x = centerX - radius;
                    int y = centerY + dy;
                    if (isValidCell(x, y, exit, enemyList, levelTiles)) {
                        newWeaponPos = new Point(x, y);
                        break;
                    }
                }
                if (newWeaponPos != null) break;

                for (int dx = -radius + 1; dx <= radius; dx++) {
                    int x = centerX + dx;
                    int y = centerY + radius;
                    if (isValidCell(x, y, exit, enemyList, levelTiles)) {
                        newWeaponPos = new Point(x, y);
                        break;
                    }
                }
                if (newWeaponPos != null) break;

                radius++;
            }

            if (newWeaponPos != null) {
                weapon.setPosition(newWeaponPos);
                itemsMap.put(newWeaponPos, weapon);
                gameSession.getLevel().addItem(weapon);
                player.setCurrentWeapon(null);
            }
        }
    }

    private boolean isEnemyAt(Point point, List<Enemy> enemies) {
        for (Enemy e : enemies) {
            if (e.getPosition().equals(point)) return true;
        }
        return false;
    }

    private boolean isValidCell(int x, int y, Point exit, List<Enemy> enemies, Tile[][] tiles) {
        if (x < 0 || y < 0 || x >= tiles.length || y >= tiles[0].length) {
            return false;
        }
        Point p = new Point(x, y);
        return !Objects.equals(p, exit) &&
                !isEnemyAt(p, enemies) &&
                !itemsMap.containsKey(p) &&
                tiles[x][y] == Tile.FLOOR;
    }

    public void useWeaponFromInventory(int order) {
        Player player = gameSession.getPlayer();
        Weapon oldWeapon = player.getCurrentWeapon();

        if (order == 0) {
            player.setCurrentWeapon(null);

            try {
                if (oldWeapon != null)
                    player.getBackpack().addItem(oldWeapon);
            } catch (IllegalStateException | IllegalArgumentException e) {
                dropWeapon();
            }

        } else {
            Weapon weapon = (Weapon) player.getBackpack().getItemFromCompartment(ItemType.WEAPON, order);
            if (weapon != null) {
                dropWeapon();
                player.setCurrentWeapon(weapon);
                player.getBackpack().deleteItem(weapon);
            }
        }
    }

    // FOOD, POTION, SCROLL
    public void consumeItem(ItemType type, int order) {
        Player player = gameSession.getPlayer();
        Statistics statistics = gameSession.getStatistics();
        Item item = player.getBackpack().getItemFromCompartment(type, order);

        if (item instanceof Food) {
            StatType stat = ((Food) item).getStatType();
            int points = ((Food) item).getHealthPoints();
            player.increaseStats(stat, points);
            statistics.incrementFoodEaten();
        } else if (item instanceof Potion) {
            usePotion((Potion) item);
            statistics.incrementPotionUsed();
        } else if (item instanceof Scroll) {
            StatType stat = ((Scroll) item).getSubtype();
            player.increaseStats(stat, Scroll.pointsToBuff);
            statistics.incrementScrollsUsed();
        }
        gameSession.getPlayer().getBackpack().deleteItem(item);
    }

    private void usePotion(Potion potion) {
        Player player = gameSession.getPlayer();
        StatType stat = potion.getSubtype();

        player.increaseStats(stat, Potion.pointsToBuff);
        potionsTimeMap.put(potion, potion.timeBuff);
    }

    private void dispelPotion(Potion potion) {
        Player player = gameSession.getPlayer();
        player.decreaseStats(potion.getSubtype(), Potion.pointsToBuff);
    }

    public void processPotionTime() {
        potionsTimeMap.entrySet().removeIf(entry -> {
            entry.setValue(entry.getValue() - 1);
            if (entry.getValue() <= 0)
                dispelPotion(entry.getKey());
            return entry.getValue() <= 0;
        });
    }

    public Map<Point, Item> getItemsMap() {
        return itemsMap;
    }
}
